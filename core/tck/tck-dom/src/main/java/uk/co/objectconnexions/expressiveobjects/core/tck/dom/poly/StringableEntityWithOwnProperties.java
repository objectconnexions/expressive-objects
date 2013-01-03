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

package uk.co.objectconnexions.expressiveobjects.core.tck.dom.poly;

import uk.co.objectconnexions.expressiveobjects.applib.AbstractDomainObject;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.MemberOrder;

public class StringableEntityWithOwnProperties extends AbstractDomainObject implements Stringable, Empty {
    
    public String title() {
        return string;
    }

    // {{ String type
    private String string;

    @Override
    public String getString() {
        return string;
    }

    public void setString(final String string) {
        this.string = string;
    }

    // }}

    // {{ Special
    private String special;

    public String getSpecial() {
        return special;
    }

    public void setSpecial(final String special) {
        this.special = special;
    }

    // }}

    // {{ Integer
    private Integer integerValue;

    @MemberOrder(sequence = "1")
    public Integer getInteger() {
        return integerValue;
    }

    public void setInteger(final Integer integerValue) {
        this.integerValue = integerValue;
    }
    // }}

}
