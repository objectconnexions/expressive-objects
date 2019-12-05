package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display;

import java.util.Locale;
import java.util.TimeZone;

import uk.co.objectconnexions.expressiveobjects.applib.clock.Clock;
import uk.co.objectconnexions.expressiveobjects.applib.user.Localized;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserLocalization;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ScimpiException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.util.MethodsUtils;

public class InitializeLocalization extends AbstractElementProcessor {

    public String getName() {
        return "init-localization";
    }

    public void process(Request request) {
        RequestContext context = request.getContext();
        String userId = (String) context.getVariable("_user");
        if (userId == null) {
            return;
        }

        ObjectAdapter currentUser = MethodsUtils.findObject(context, userId);
        if (currentUser.getObject() instanceof Localized) {
            Localized user = (Localized) currentUser.getObject();
            Locale locale = user.forLocale();
            TimeZone timeZone = user.forTimeZone();
            ExpressiveObjectsContext.getUserProfile().setLocalization(new UserLocalization(locale, timeZone));
    
            String languageCode = LocalizationUtils.codeForLanguage(user.forLocale().getLanguage());
            context.addVariable("user-language", languageCode, Scope.SESSION);
            context.addVariable("user-time-zone", timeZone.getID(), Scope.SESSION);
    
            int offset = timeZone.getOffset(Clock.getTime()) / 3600000;
            context.addVariable("user-time-offset", "" + offset, Scope.SESSION);
        } else {
            throw new ScimpiException("_user was not a Localized object");
        }
    }

}

