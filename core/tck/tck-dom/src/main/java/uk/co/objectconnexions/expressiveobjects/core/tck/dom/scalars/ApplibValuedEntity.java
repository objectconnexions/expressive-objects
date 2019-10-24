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

package uk.co.objectconnexions.expressiveobjects.core.tck.dom.scalars;

import uk.co.objectconnexions.expressiveobjects.applib.AbstractDomainObject;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.MemberOrder;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.ObjectType;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Optional;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Title;
import uk.co.objectconnexions.expressiveobjects.applib.value.Color;
import uk.co.objectconnexions.expressiveobjects.applib.value.Date;
import uk.co.objectconnexions.expressiveobjects.applib.value.DateTime;
import uk.co.objectconnexions.expressiveobjects.applib.value.Image;
import uk.co.objectconnexions.expressiveobjects.applib.value.Money;
import uk.co.objectconnexions.expressiveobjects.applib.value.Password;
import uk.co.objectconnexions.expressiveobjects.applib.value.Percentage;
import uk.co.objectconnexions.expressiveobjects.applib.value.Time;
import uk.co.objectconnexions.expressiveobjects.applib.value.TimeStamp;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Discriminator("APLV")
@javax.jdo.annotations.Query(
        name="jdkv_findByStringProperty", language="JDOQL",  
        value="SELECT FROM uk.co.objectconnexions.expressiveobjects.tck.dom.scalars.ApplibValuedEntity WHERE stringProperty == :i")
@ObjectType("APLV")
public class ApplibValuedEntity extends AbstractDomainObject {

    
    // {{ StringProperty (also title, pk)
    private String stringProperty;

    @javax.jdo.annotations.PrimaryKey
    @Title
    @Optional
    @MemberOrder(sequence = "1")
    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(final String description) {
        this.stringProperty = description;
    }

    // }}

    
    
    // {{ DateProperty
    @javax.jdo.annotations.Persistent
    private Date dateProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public Date getDateProperty() {
        return dateProperty;
    }

    public void setDateProperty(final Date dateProperty) {
        this.dateProperty = dateProperty;
    }

    // }}

    // {{ ColorProperty
    @javax.jdo.annotations.NotPersistent
    private Color colorProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public Color getColorProperty() {
        return colorProperty;
    }

    public void setColorProperty(final Color colorProperty) {
        this.colorProperty = colorProperty;
    }

    // }}

    // {{ DateTimeProperty
    @javax.jdo.annotations.NotPersistent
    private DateTime dateTimeProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public DateTime getDateTimeProperty() {
        return dateTimeProperty;
    }

    public void setDateTimeProperty(final DateTime dateTimeProperty) {
        this.dateTimeProperty = dateTimeProperty;
    }

    // }}

    // {{ ImageProperty
    @javax.jdo.annotations.NotPersistent
    private Image imageProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public Image getImageProperty() {
        return imageProperty;
    }

    public void setImageProperty(final Image imageProperty) {
        this.imageProperty = imageProperty;
    }

    // }}

    // {{ MoneyProperty
    @javax.jdo.annotations.NotPersistent
    private Money moneyProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public Money getMoneyProperty() {
        return moneyProperty;
    }

    public void setMoneyProperty(final Money moneyProperty) {
        this.moneyProperty = moneyProperty;
    }

    // }}

    // {{ PasswordProperty
    @javax.jdo.annotations.NotPersistent
    private Password passwordProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public Password getPasswordProperty() {
        return passwordProperty;
    }

    public void setPasswordProperty(final Password passwordProperty) {
        this.passwordProperty = passwordProperty;
    }

    // }}

    // {{ PercentageProperty
    @javax.jdo.annotations.NotPersistent
    private Percentage percentageProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public Percentage getPercentageProperty() {
        return percentageProperty;
    }

    public void setPercentageProperty(final Percentage percentageProperty) {
        this.percentageProperty = percentageProperty;
    }

    // }}

    // {{ TimeProperty
    @javax.jdo.annotations.NotPersistent
    private Time timeProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public Time getTimeProperty() {
        return timeProperty;
    }

    public void setTimeProperty(final Time timeProperty) {
        this.timeProperty = timeProperty;
    }

    // }}

    // {{ TimeStampProperty
    @javax.jdo.annotations.NotPersistent
    private TimeStamp timeStampProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public TimeStamp getTimeStampProperty() {
        return timeStampProperty;
    }

    public void setTimestampProperty(final TimeStamp timestampProperty) {
        this.timeStampProperty = timestampProperty;
    }
    // }}

}
