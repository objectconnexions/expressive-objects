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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.breadcrumbs.BreadCrumbsTrail;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.breadcrumbs.Files;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.breadcrumbs.ListFiles;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.debug.DebugUsersView;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.debug.ErrorDetails;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.debug.ErrorMessage;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.debug.ErrorReference;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display.DateFormatString;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display.IeInclude;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display.InitializeLocalization;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display.MarkdownField;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display.Section;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display.SectionContainer;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display.ShortObjectType;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.HelpLink;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.History;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.VersionNumber;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.action.ActionButton;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.action.ActionForm;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.action.ActionLink;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.action.Methods;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.action.Parameter;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.action.RunAction;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.action.Services;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.collection.Collection;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.debug.DebugAccessCheck;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.debug.DebugCollectionView;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.debug.DebugObjectView;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.debug.DebuggerLink;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.debug.Diagnostics;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.debug.Log;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.debug.LogLevel;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.debug.Members;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.debug.ShowDebug;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.debug.Specification;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.debug.ThrowException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.AddMessage;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.AddWarning;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.Errors;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.Feedback;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.FieldLabel;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.FieldValue;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.GetField;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.IncludeObject;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.ListView;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.LongFormView;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.Messages;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.SelectedObject;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.ShortFormView;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.TableBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.TableCell;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.TableEmpty;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.TableHeader;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.TableRow;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.TableView;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.Title;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.Warnings;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.edit.EditObject;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.edit.FormEntry;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.edit.FormField;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.edit.HiddenField;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.edit.RadioListField;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.edit.Selector;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.field.ExcludeField;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.field.IncludeField;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.field.LinkField;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.logon.Logoff;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.logon.Logon;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.logon.RestrictAccess;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.logon.Secure;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.logon.User;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.BlockDefine;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.BlockUse;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.Commit;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.ContentTag;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.CookieValue;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.DefaultValue;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.EditLink;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.EndSession;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.Forward;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.GetCookie;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.Import;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.InitializeFromCookie;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.InitializeFromResult;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.Localization;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.Mark;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.NewActionLink;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.ObjectLink;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.PageTitle;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.Redirect;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.RemoveElement;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.ScopeTag;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.SetCookie;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.SetCookieFromField;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.SetFieldFromCookie;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.SetLocalization;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.SimpleButton;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.StartSession;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.TemplateTag;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.UniqueId;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.Unless;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.Variable;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple.When;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.value.ActionName;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.value.CountElements;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.value.ElementType;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.value.FieldName;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.value.ParameterName;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.value.TitleString;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.value.Type;

public class ProcessorLookup {
    private final Map<String, ElementProcessor> swfElementProcessors = new HashMap<String, ElementProcessor>();

    public void init() {
        addElementProcessor(new ActionLink());
        addElementProcessor(new ActionButton());
        addElementProcessor(new ActionForm());
        addElementProcessor(new ActionName());
        addElementProcessor(new AddMessage());
        addElementProcessor(new AddWarning());
        addElementProcessor(new BlockDefine());
        addElementProcessor(new BlockUse());
        addElementProcessor(new BreadCrumbsTrail());
        addElementProcessor(new History());
        addElementProcessor(new Collection());
        addElementProcessor(new Commit());
        addElementProcessor(new ContentTag());
        addElementProcessor(new CountElements());
        addElementProcessor(new DateFormatString());
        addElementProcessor(new Diagnostics());
        addElementProcessor(new DebugAccessCheck());
        addElementProcessor(new DebugCollectionView()); 
        addElementProcessor(new DebuggerLink());
        addElementProcessor(new DebugObjectView()); 
        addElementProcessor(new DebugUsersView());
        addElementProcessor(new DefaultValue());
        addElementProcessor(new EditLink());
        addElementProcessor(new EditObject());
        addElementProcessor(new ElementType());
        addElementProcessor(new Errors());
        addElementProcessor(new ErrorDetails()); 
        addElementProcessor(new ErrorMessage()); 
        addElementProcessor(new ErrorReference());
        addElementProcessor(new ExcludeField());
        addElementProcessor(new Feedback());
        addElementProcessor(new FieldLabel());
        addElementProcessor(new FieldName());
        addElementProcessor(new FieldValue());
        addElementProcessor(new ListFiles());
        addElementProcessor(new FormField());
        addElementProcessor(new FormEntry());
        addElementProcessor(new Forward());
        addElementProcessor(new GetField());
        addElementProcessor(new HelpLink());
        addElementProcessor(new HiddenField());
        addElementProcessor(new IeInclude());
        addElementProcessor(new Import());
        addElementProcessor(new IncludeObject());
        addElementProcessor(new IncludeField());
        addElementProcessor(new InitializeFromCookie());
        addElementProcessor(new InitializeFromResult());
        addElementProcessor(new InitializeLocalization());
        addElementProcessor(new Log());
        addElementProcessor(new LogLevel());
        addElementProcessor(new Logon());
        addElementProcessor(new Logoff());
        addElementProcessor(new LongFormView());
        addElementProcessor(new LinkField());
        addElementProcessor(new ListView());
        addElementProcessor(new NewActionLink());
        addElementProcessor(new Mark());
        addElementProcessor(new MarkdownField());
        addElementProcessor(new Members());
        addElementProcessor(new Messages());
        addElementProcessor(new Methods());
        addElementProcessor(new ObjectLink());
        addElementProcessor(new PageTitle());
        addElementProcessor(new Parameter());
        addElementProcessor(new ParameterName());
        addElementProcessor(new RadioListField());
        addElementProcessor(new Redirect());
        addElementProcessor(new RemoveElement());
        addElementProcessor(new VersionNumber());
        addElementProcessor(new RunAction());
        addElementProcessor(new RestrictAccess());
        addElementProcessor(new ScopeTag());
        addElementProcessor(new Secure());
        addElementProcessor(new Section());
        addElementProcessor(new SectionContainer());
        addElementProcessor(new SelectedObject());
        addElementProcessor(new Selector());
        addElementProcessor(new Services());
        addElementProcessor(new ShortFormView());
        addElementProcessor(new ShortObjectType());
        addElementProcessor(new ShowDebug());
        addElementProcessor(new SimpleButton());
        addElementProcessor(new Specification());
        addElementProcessor(new TableCell());
        addElementProcessor(new TableView());
        addElementProcessor(new TableBuilder());
        addElementProcessor(new TableEmpty());
        addElementProcessor(new TableRow());
        addElementProcessor(new TableHeader());
        addElementProcessor(new TemplateTag());
        addElementProcessor(new Title());
        addElementProcessor(new TitleString());
        addElementProcessor(new ThrowException());
        addElementProcessor(new Type());
        addElementProcessor(new User());
        addElementProcessor(new Unless());
        addElementProcessor(new UniqueId());
        addElementProcessor(new Variable());
        addElementProcessor(new Warnings());
        addElementProcessor(new When());

        addElementProcessor(new StartSession());
        addElementProcessor(new EndSession());

        addElementProcessor(new CookieValue());
        addElementProcessor(new SetCookie());
        addElementProcessor(new GetCookie());
        addElementProcessor(new SetCookieFromField());
        addElementProcessor(new SetFieldFromCookie());
        
        // new, alpha, processors
        addElementProcessor(new Localization());
        addElementProcessor(new SetLocalization());
    }

    public void addElementProcessor(final ElementProcessor action) {
        swfElementProcessors.put("SWF:" + action.getName().toUpperCase(), action);
    }

    public void debug(final DebugBuilder debug) {
        debug.startSection("Recognised tags");
        final Iterator<String> it2 = new TreeSet<String>(swfElementProcessors.keySet()).iterator();
        while (it2.hasNext()) {
            final String name = it2.next();
            debug.appendln(name.toLowerCase(), swfElementProcessors.get(name));
        }
        debug.endSection();
    }

    public ElementProcessor getFor(final String name) {
        return swfElementProcessors.get(name);
    }

}
