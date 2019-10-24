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

package uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.query;

import java.io.IOException;

import uk.co.objectconnexions.expressiveobjects.core.commons.encoding.DataInputExtended;
import uk.co.objectconnexions.expressiveobjects.core.commons.encoding.DataOutputExtended;
import uk.co.objectconnexions.expressiveobjects.core.commons.encoding.Encodable;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceQuery;

public abstract class PersistenceQueryAbstract implements PersistenceQuery, Encodable {

    private final ObjectSpecification specification;

    public PersistenceQueryAbstract(final ObjectSpecification specification) {
        this.specification = specification;
        initialized();
    }

    protected PersistenceQueryAbstract(final DataInputExtended input) throws IOException {
        final String specName = input.readUTF();
        specification = getSpecificationLoader().loadSpecification(specName);
        initialized();
    }

    @Override
    public void encode(final DataOutputExtended output) throws IOException {
        output.writeUTF(specification.getFullIdentifier());
    }

    private void initialized() {
        // nothing to do
    }

    // ///////////////////////////////////////////////////////
    //
    // ///////////////////////////////////////////////////////

    @Override
    public ObjectSpecification getSpecification() {
        return specification;
    }

    // ///////////////////////////////////////////////////////
    // equals, hashCode
    // ///////////////////////////////////////////////////////

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PersistenceQueryAbstract other = (PersistenceQueryAbstract) obj;
        if (specification == null) {
            if (other.specification != null) {
                return false;
            }
        } else if (!specification.equals(other.specification)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + 1231;
        result = PRIME * result + ((specification == null) ? 0 : specification.hashCode());
        return result;
    }

    // ///////////////////////////////////////////////////////
    // Dependencies (from context)
    // ///////////////////////////////////////////////////////

    protected static SpecificationLoaderSpi getSpecificationLoader() {
        return ExpressiveObjectsContext.getSpecificationLoader();
    }

}
