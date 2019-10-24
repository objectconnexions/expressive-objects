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
package uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db.file.server;

class LogRange {
    private long first = Long.MAX_VALUE;
    private long last = 0;

    public long getFirst() {
        return first;
    }

    public long getLast() {
        return last;
    }

    public boolean noLogFile() {
        return first == Long.MAX_VALUE && last == 0;
    }

    public void add(final long sequence) {
        last = Math.max(last, sequence);
        first = Math.min(first, sequence);
    }
}
