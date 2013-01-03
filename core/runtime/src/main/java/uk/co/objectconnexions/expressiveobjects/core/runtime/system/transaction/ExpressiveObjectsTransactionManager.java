/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction;

import static uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Ensure.ensureThatArg;
import static uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Ensure.ensureThatState;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import java.util.List;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.components.SessionScopedComponent;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.ObjectPersistenceException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PersistenceCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.TransactionalResource;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSession;

public class ExpressiveObjectsTransactionManager implements SessionScopedComponent {


    private static final Logger LOG = Logger.getLogger(ExpressiveObjectsTransactionManager.class);

    private final EnlistedObjectDirtying objectPersistor;
    private final TransactionalResource transactionalResource;

    private int transactionLevel;
    
    private ExpressiveObjectsSession session;

    /**
     * Holds the current or most recently completed transaction.
     */
    private ExpressiveObjectsTransaction transaction;


    // ////////////////////////////////////////////////////////////////
    // constructor
    // ////////////////////////////////////////////////////////////////

    public ExpressiveObjectsTransactionManager(final EnlistedObjectDirtying objectPersistor, final TransactionalResource objectStore) {
        this.objectPersistor = objectPersistor;
        this.transactionalResource = objectStore;
    }
    
    
    public TransactionalResource getTransactionalResource() {
        return transactionalResource;
    }
    
    // ////////////////////////////////////////////////////////////////
    // open, close
    // ////////////////////////////////////////////////////////////////

    @Override
    public void open() {
        ensureThatState(session, is(notNullValue()), "session is required");
    }

    @Override
    public void close() {
        if (getTransaction() != null) {
            try {
                abortTransaction();
            } catch (final Exception e2) {
                LOG.error("failure during abort", e2);
            }
        }
        session = null;
    }

    // //////////////////////////////////////////////////////
    // current transaction (if any)
    // //////////////////////////////////////////////////////

    /**
     * The current transaction, if any.
     */
    public ExpressiveObjectsTransaction getTransaction() {
        return transaction;
    }

    public int getTransactionLevel() {
        return transactionLevel;
    }


    
    /**
     * Convenience method returning the {@link UpdateNotifier} of the
     * {@link #getTransaction() current transaction}.
     */
    protected UpdateNotifier getUpdateNotifier() {
        return getTransaction().getUpdateNotifier();
    }

    /**
     * Convenience method returning the {@link MessageBroker} of the
     * {@link #getTransaction() current transaction}.
     */
    protected MessageBroker getMessageBroker() {
        return getTransaction().getMessageBroker();
    }

    
    // ////////////////////////////////////////////////////////////////
    // Transactional Execution
    // ////////////////////////////////////////////////////////////////

    /**
     * Run the supplied {@link Runnable block of code (closure)} in a
     * {@link ExpressiveObjectsTransaction transaction}.
     * 
     * <p>
     * If a transaction is {@link ExpressiveObjectsContext#inTransaction() in progress}, then
     * uses that. Otherwise will {@link #startTransaction() start} a transaction
     * before running the block and {@link #endTransaction() commit} it at the
     * end. If the closure throws an exception, then will
     * {@link #abortTransaction() abort} the transaction.
     */
    public void executeWithinTransaction(final TransactionalClosure closure) {
        final boolean initiallyInTransaction = inTransaction();
        if (!initiallyInTransaction) {
            startTransaction();
        }
        try {
            closure.preExecute();
            closure.execute();
            closure.onSuccess();
            if (!initiallyInTransaction) {
                endTransaction();
            }
        } catch (final RuntimeException ex) {
            closure.onFailure();
            if (!initiallyInTransaction) {
                // temp TODO fix swallowing of exception
                // System.out.println(ex.getMessage());
                // ex.printStackTrace();
                try {
                    abortTransaction();
                } catch (final Exception e) {
                    LOG.error("Abort failure after exception", e);
                    // System.out.println(e.getMessage());
                    // e.printStackTrace();
                    throw new ExpressiveObjectsTransactionManagerException("Abort failure: " + e.getMessage(), ex);
                }
            }
            throw ex;
        }
    }

    /**
     * Run the supplied {@link Runnable block of code (closure)} in a
     * {@link ExpressiveObjectsTransaction transaction}.
     * 
     * <p>
     * If a transaction is {@link ExpressiveObjectsContext#inTransaction() in progress}, then
     * uses that. Otherwise will {@link #startTransaction() start} a transaction
     * before running the block and {@link #endTransaction() commit} it at the
     * end. If the closure throws an exception, then will
     * {@link #abortTransaction() abort} the transaction.
     */
    public <Q> Q executeWithinTransaction(final TransactionalClosureWithReturn<Q> closure) {
        final boolean initiallyInTransaction = inTransaction();
        if (!initiallyInTransaction) {
            startTransaction();
        }
        try {
            closure.preExecute();
            final Q retVal = closure.execute();
            closure.onSuccess();
            if (!initiallyInTransaction) {
                endTransaction();
            }
            return retVal;
        } catch (final RuntimeException ex) {
            closure.onFailure();
            if (!initiallyInTransaction) {
                abortTransaction();
            }
            throw ex;
        }
    }

    public boolean inTransaction() {
        return getTransaction() != null && !getTransaction().getState().isComplete();
    }

    // ////////////////////////////////////////////////////////////////
    // create transaction, + hooks
    // ////////////////////////////////////////////////////////////////

    /**
     * Creates a new transaction and saves, to be accessible in
     * {@link #getTransaction()}.
     */
    protected final ExpressiveObjectsTransaction createTransaction() {
        return this.transaction = createTransaction(createMessageBroker(), createUpdateNotifier());
    }


    /**
     * The provided {@link MessageBroker} and {@link UpdateNotifier} are
     * obtained from the hook methods ( {@link #createMessageBroker()} and
     * {@link #createUpdateNotifier()}).
     * 
     * @see #createMessageBroker()
     * @see #createUpdateNotifier()
     */
    protected ExpressiveObjectsTransaction createTransaction(final MessageBroker messageBroker, final UpdateNotifier updateNotifier) {
        ensureThatArg(messageBroker, is(not(nullValue())));
        ensureThatArg(updateNotifier, is(not(nullValue())));

        return new ExpressiveObjectsTransaction(this, messageBroker, updateNotifier, getTransactionalResource());
    }
    

    // //////////////////////////////////////////////////////
    // start, flush, abort, end
    // //////////////////////////////////////////////////////

    public synchronized void startTransaction() {

        boolean noneInProgress = false;
        if (getTransaction() == null || getTransaction().getState().isComplete()) {
            noneInProgress = true;

            createTransaction();
            transactionLevel = 0;
            transactionalResource.startTransaction();
        }

        transactionLevel++;

        if (LOG.isDebugEnabled()) {
            LOG.debug("startTransaction: level " + (transactionLevel - 1) + "->" + (transactionLevel) + (noneInProgress ? " (no transaction in progress or was previously completed; transaction created)" : ""));
        }
    }

    public synchronized boolean flushTransaction() {

        if (LOG.isDebugEnabled()) {
            LOG.debug("flushTransaction");
        }

        if (getTransaction() != null) {
            objectPersistor.objectChangedAllDirty();
            getTransaction().flush();
        }
        return false;
    }

    /**
     * Ends the transaction if nesting level is 0.
     */
    public synchronized void endTransaction() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("endTransaction: level " + (transactionLevel) + "->" + (transactionLevel - 1));
        }

        transactionLevel--;
        if (transactionLevel == 0) {

            //
            // TODO: granted, this is some fairly byzantine coding.  but I'm trying to account for different types
            // of object store implementations that could start throwing exceptions at any stage.
            // once the contract/API for the objectstore is better tied down, hopefully can simplify this...
            //
            
            List<ExpressiveObjectsException> exceptions = this.getTransaction().getExceptionsIfAny();
            if(exceptions.isEmpty()) {
            
                if (LOG.isDebugEnabled()) {
                    LOG.debug("endTransaction: committing");
                }
                
                objectPersistor.objectChangedAllDirty();
                
                // just in case any additional exceptions were raised...
                exceptions = this.getTransaction().getExceptionsIfAny();
            }
            
            if(exceptions.isEmpty()) {
                getTransaction().commit();
                
                // in case any additional exceptions were raised...
                exceptions = this.getTransaction().getExceptionsIfAny();
            }
            
            if(exceptions.isEmpty()) {
                transactionalResource.endTransaction();
                
                // just in case any additional exceptions were raised...
                exceptions = this.getTransaction().getExceptionsIfAny();
            }
            
            if(!exceptions.isEmpty()) {
                
                if (LOG.isDebugEnabled()) {
                    LOG.debug("endTransaction: aborting instead, " + exceptions.size() + " exception(s) have been raised");
                }
                abortTransaction();
                
                // just in case any additional exceptions were raised...
                exceptions = this.getTransaction().getExceptionsIfAny();
                
                throw exceptionToThrowFrom(exceptions);
            }
            
        } else if (transactionLevel < 0) {
            LOG.error("endTransaction: transactionLevel=" + transactionLevel);
            transactionLevel = 0;
            throw new IllegalStateException(" no transaction running to end (transactionLevel < 0)");
        }
    }


    private ExpressiveObjectsException exceptionToThrowFrom(List<ExpressiveObjectsException> exceptions) {
        if(exceptions.size() == 1) {
            return exceptions.get(0);
        } 
        final StringBuilder buf = new StringBuilder();
        for (ExpressiveObjectsException ope : exceptions) {
            buf.append(ope.getMessage()).append("\n");
        }
        return new ExpressiveObjectsException(buf.toString());
    }
    

    public synchronized void abortTransaction() {
        if (getTransaction() != null) {
            getTransaction().abort();
            transactionLevel = 0;
            transactionalResource.abortTransaction();
        }
    }

    public void addCommand(final PersistenceCommand command) {
        getTransaction().addCommand(command);
    }

    // //////////////////////////////////////////////////////////////
    // Hooks
    // //////////////////////////////////////////////////////////////



    
    /**
     * Overridable hook, used in
     * {@link #createTransaction(MessageBroker, UpdateNotifier)
     * 
     * <p> Called when a new {@link ExpressiveObjectsTransaction} is created.
     */
    protected MessageBroker createMessageBroker() {
        return new MessageBrokerDefault();
    }

    /**
     * Overridable hook, used in
     * {@link #createTransaction(MessageBroker, UpdateNotifier)
     * 
     * <p> Called when a new {@link ExpressiveObjectsTransaction} is created.
     */
    protected UpdateNotifier createUpdateNotifier() {
        return new UpdateNotifierDefault();
    }

    // ////////////////////////////////////////////////////////////////
    // helpers
    // ////////////////////////////////////////////////////////////////

    protected void ensureTransactionInProgress() {
        ensureThatState(getTransaction() != null && !getTransaction().getState().isComplete(), is(true), "No transaction in progress");
    }

    protected void ensureTransactionNotInProgress() {
        ensureThatState(getTransaction() != null && !getTransaction().getState().isComplete(), is(false), "Transaction in progress");
    }


    // //////////////////////////////////////////////////////
    // debugging
    // //////////////////////////////////////////////////////

    public void debugData(final DebugBuilder debug) {
        debug.appendln("Transaction", getTransaction());
    }

    // ////////////////////////////////////////////////////////////////
    // Dependencies (injected)
    // ////////////////////////////////////////////////////////////////

    /**
     * The owning {@link ExpressiveObjectsSession}.
     * 
     * <p>
     * Will be non-<tt>null</tt> when {@link #open() open}ed, but <tt>null</tt>
     * if {@link #close() close}d .
     */
    public ExpressiveObjectsSession getSession() {
        return session;
    }

    /**
     * Should be injected prior to {@link #open() opening}
     */
    public void setSession(final ExpressiveObjectsSession session) {
        this.session = session;
    }



    
}
