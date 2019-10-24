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

package uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db.mongo;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.version.ConcurrencyException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.NoSqlCommandContext;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db.StateWriter;
import org.apache.log4j.Logger;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;


public class MongoClientCommandContext implements NoSqlCommandContext {

    private static final Logger LOG = Logger.getLogger(MongoClientCommandContext.class);
    private final DB db;

    public MongoClientCommandContext(DB db) {
        this.db = db;
    }

    @Override
    public void start() {}

    @Override
    public void end() {}

    @Override
    public StateWriter createStateWriter(final ObjectSpecId objectSpecId) {
        return new MongoStateWriter(db, objectSpecId);
    }

    @Override
    public void delete(final ObjectSpecId objectSpecId, final String mongoId, final String version, final Oid oid) {
        final DBCollection instances = db.getCollection(objectSpecId.asString());
        final DBObject object = instances.findOne(mongoId);
        if (!object.get(PropertyNames.VERSION).equals(version)) {
            throw new ConcurrencyException("Could not delete object of different version", oid);
        }
        instances.remove(object);
        LOG.info("removed " + oid);
    }

    @Override
    public void insert(final StateWriter writer) {
        ((MongoStateWriter) writer).flush();
    }

    @Override
    public void update(final StateWriter writer) {
        ((MongoStateWriter) writer).flush();
    }

}
