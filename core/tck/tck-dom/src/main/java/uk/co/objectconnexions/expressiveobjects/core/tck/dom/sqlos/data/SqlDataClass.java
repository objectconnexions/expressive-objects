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

package uk.co.objectconnexions.expressiveobjects.core.tck.dom.sqlos.data;

import java.util.ArrayList;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.AbstractDomainObject;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.MemberOrder;
import uk.co.objectconnexions.expressiveobjects.applib.value.Color;
import uk.co.objectconnexions.expressiveobjects.applib.value.Date;
import uk.co.objectconnexions.expressiveobjects.applib.value.DateTime;
import uk.co.objectconnexions.expressiveobjects.applib.value.Image;
import uk.co.objectconnexions.expressiveobjects.applib.value.Money;
import uk.co.objectconnexions.expressiveobjects.applib.value.Password;
import uk.co.objectconnexions.expressiveobjects.applib.value.Percentage;
import uk.co.objectconnexions.expressiveobjects.applib.value.Time;
import uk.co.objectconnexions.expressiveobjects.applib.value.TimeStamp;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.scalars.PrimitiveValuedEntity;

/**
 * The SqlDataClass is a test class used in the sql-persistor integration tests.
 * It's properties are stored and retrieved using the persistor, to confirm
 * accurate persistence behaviour.
 * 
 * Each property here requires an equivalent test in
 * {@link SqlIntegrationTestCommon}
 * 
 * @author Kevin Meyer
 * 
 */

public class SqlDataClass extends AbstractDomainObject {
    
    public String title() {
        return string;
    }

    // {{ String
    private String string;

    public String getString() {
        return string;
    }

    public void setString(final String string) {
        this.string = string;
    }

    // }}

    // {{ Expressive Objects Date
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    // }}

    // {{ java.sql Date
    private java.sql.Date sqlDate;

    @MemberOrder(sequence = "1")
    public java.sql.Date getSqlDate() {
        return sqlDate;
    }

    public void setSqlDate(final java.sql.Date sqlDate) {
        this.sqlDate = sqlDate;
    }

    /**/

    // }}

    // {{ Expressive Objects Money
    private Money money;

    @MemberOrder(sequence = "1")
    public Money getMoney() {
        return money;
    }

    public void setMoney(final Money money) {
        this.money = money;
    }

    // }}

    // {{ Expressive Objects DateTime
    private DateTime dateTime;

    @MemberOrder(sequence = "1")
    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(final DateTime dateTime) {
        this.dateTime = dateTime;
    }

    // }}

    // {{ Expressive Objects TimeStamp
    private TimeStamp timeStamp;

    @MemberOrder(sequence = "1")
    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(final TimeStamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    // }}

    // {{ Expressive Objects Time
    private Time time;

    @MemberOrder(sequence = "1")
    public Time getTime() {
        return time;
    }

    public void setTime(final Time time) {
        this.time = time;
    }

    // }}

    // {{ Expressive Objects Color
    private Color color;

    @MemberOrder(sequence = "1")
    public Color getColor() {
        return color;
    }

    public void setColor(final Color color) {
        this.color = color;
    }

    // }}

    // {{ Expressive Objects Image
    private Image image;

    @MemberOrder(sequence = "1")
    public Image getImage() {
        return image;
    }

    public void setImage(final Image image) {
        this.image = image;
    }
    // }}

    // {{ Expressive Objects Password
    private Password password;

    @MemberOrder(sequence = "1")
    public Password getPassword() {
        return password;
    }

    public void setPassword(final Password password) {
        this.password = password;
    }

    // }}

    // {{ Expressive Objects Percentage
    private Percentage percentage;

    @MemberOrder(sequence = "1")
    public Percentage getPercentage() {
        return percentage;
    }

    public void setPercentage(final Percentage percentage) {
        this.percentage = percentage;
    }

    // }}

    
    //
    // References to other entities
    //
    
    // {{ SimpleClasses
    public List<SimpleClass> simpleClasses1 = new ArrayList<SimpleClass>();

    @MemberOrder(sequence = "1")
    public List<SimpleClass> getSimpleClasses1() {
        return simpleClasses1;
    }

    public void setSimpleClasses1(final List<SimpleClass> simpleClasses) {
        this.simpleClasses1 = simpleClasses;
    }

    public void addToSimpleClasses1(final SimpleClass simpleClass) {
        // check for no-op
        if (simpleClass == null || getSimpleClasses1().contains(simpleClass)) {
            return;
        }
        // associate new
        getSimpleClasses1().add(simpleClass);
        // additional business logic
        onAddToSimpleClasses1(simpleClass);
    }

    public void removeFromSimpleClasses1(final SimpleClass simpleClass) {
        // check for no-op
        if (simpleClass == null || !getSimpleClasses1().contains(simpleClass)) {
            return;
        }
        // dissociate existing
        getSimpleClasses1().remove(simpleClass);
        // additional business logic
        onRemoveFromSimpleClasses1(simpleClass);
    }

    protected void onAddToSimpleClasses1(final SimpleClass simpleClass) {
    }

    protected void onRemoveFromSimpleClasses1(final SimpleClass simpleClass) {
    }

    // }}

    // {{ SimpleClasses2
    /**/
    private List<SimpleClass> simpleClasses2 = new ArrayList<SimpleClass>();

    @MemberOrder(sequence = "1")
    public List<SimpleClass> getSimpleClasses2() {
        return simpleClasses2;
    }

    public void setSimpleClasses2(final List<SimpleClass> simpleClasses) {
        this.simpleClasses2 = simpleClasses;
    }

    public void addToSimpleClasses2(final SimpleClass simpleClass) {
        // check for no-op
        if (simpleClass == null || getSimpleClasses2().contains(simpleClass)) {
            return;
        }
        // associate new
        getSimpleClasses2().add(simpleClass);
        // additional business logic
        onAddToSimpleClasses2(simpleClass);
    }

    public void removeFromSimpleClasses2(final SimpleClass simpleClass) {
        // check for no-op
        if (simpleClass == null || !getSimpleClasses2().contains(simpleClass)) {
            return;
        }
        // dissociate existing
        getSimpleClasses2().remove(simpleClass);
        // additional business logic
        onRemoveFromSimpleClasses2(simpleClass);
    }

    protected void onAddToSimpleClasses2(final SimpleClass simpleClass) {
    }

    protected void onRemoveFromSimpleClasses2(final SimpleClass simpleClass) {
    }

    /**/
    // }}

    // {{ SimpleClassTwo
    private SimpleClassTwo simplyClassTwo;

    public SimpleClassTwo getSimpleClassTwo() {
        return simplyClassTwo;
    }

    public void setSimpleClassTwo(final SimpleClassTwo simpleClassTwo) {
        this.simplyClassTwo = simpleClassTwo;
    }

    // }}

    // {{ PrimitiveValuedEntityMax
    private PrimitiveValuedEntity pveMax;

    public PrimitiveValuedEntity getPrimitiveValuedEntityMax() {
        return pveMax;
    }

    public void setPrimitiveValuedEntityMax(final PrimitiveValuedEntity pveMax) {
        this.pveMax = pveMax;
    }

    // }}

    // {{ PrimitiveValuedEntityMin
    private PrimitiveValuedEntity pveMin;

    public PrimitiveValuedEntity getPrimitiveValuedEntityMin() {
        return pveMin;
    }

    public void setPrimitiveValuedEntityMin(final PrimitiveValuedEntity pveMin) {
        this.pveMin = pveMin;
    }

    // }}
}
