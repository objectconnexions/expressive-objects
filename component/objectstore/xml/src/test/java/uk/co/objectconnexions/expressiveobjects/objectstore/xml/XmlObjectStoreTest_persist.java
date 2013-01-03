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

package uk.co.objectconnexions.expressiveobjects.objectstore.xml;

import java.io.File;
import java.io.FilenameFilter;

import uk.co.objectconnexions.expressiveobjects.core.integtestsupport.tck.ObjectStoreContractTest_persist;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.PersistenceMechanismInstaller;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.files.Files;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.files.Files.Recursion;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.XmlPersistenceMechanismInstaller;

public class XmlObjectStoreTest_persist extends ObjectStoreContractTest_persist {

    @Override
    protected PersistenceMechanismInstaller createPersistenceMechanismInstaller() {
        return new XmlPersistenceMechanismInstaller();
    }

    protected void resetPersistenceStore() {
        Files.deleteFiles("xml/objects", Files.and(endsWithXml(), notServicesXml()), Recursion.DO_RECURSE);
    }

    private static FilenameFilter notServicesXml() {
        return new FilenameFilter() {
            
            @Override
            public boolean accept(File parentDirectory, String fileName) {
                return !fileName.equals("services.xml");
            }
        };
    }

    private static FilenameFilter endsWithXml() {
        return Files.filterFileNameExtension(".xml");
    }

}
