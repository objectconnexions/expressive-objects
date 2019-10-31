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
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package dom.audit;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Hidden;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Immutable;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.MemberOrder;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Programmatic;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Title;
import uk.co.objectconnexions.expressiveobjects.applib.bookmarks.Bookmark;
import uk.co.objectconnexions.expressiveobjects.applib.bookmarks.BookmarkHolder;
import uk.co.objectconnexions.expressiveobjects.applib.value.DateTime;

@Immutable
public class AuditEntry implements BookmarkHolder {

    // {{ TimestampUtc (property)
    private Long timestampEpoch;

    @Hidden
    public Long getTimestampEpoch() {
        return timestampEpoch;
    }

    public void setTimestampEpoch(final Long timestampEpoch) {
        this.timestampEpoch = timestampEpoch;
    }
    // }}
    
    // {{ Timestamp (property)
    @Title(sequence="1")
    @MemberOrder(sequence = "1")
    public DateTime getTimestamp() {
        return timestampEpoch != null? new DateTime(timestampEpoch): null;
    }

    // }}
    
    // {{ User (property)
    private String user;

    @MemberOrder(sequence = "2")
    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }
    // }}

    // {{ ObjectType (property)
    private String objectType;

    @Title(sequence="3", prepend=":")
    @MemberOrder(sequence = "3")
    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(final String objectType) {
        this.objectType = objectType;
    }
    // }}

    // {{ Identifier (property)
    private String identifier;

    @MemberOrder(sequence = "4")
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }
    // }}
    
    // {{ PreValue (property)
    private String preValue;

    @MemberOrder(sequence = "5")
    public String getPreValue() {
        return preValue;
    }

    public void setPreValue(final String preValue) {
        this.preValue = preValue;
    }
    // }}

    // {{ PostValue (property)
    private String postValue;

    @MemberOrder(sequence = "6")
    public String getPostValue() {
        return postValue;
    }

    public void setPostValue(final String postValue) {
        this.postValue = postValue;
    }
    // }}

    // {{ bookmark (action)
    @Override
    @Programmatic
    public Bookmark bookmark() {
        return new Bookmark(getObjectType(), getIdentifier());
    }
    // }}
}
