package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display;

import java.util.Locale;
import java.util.TimeZone;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserLocalization;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.util.MethodsUtils;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.logon.User;

import com.ibexis.common.SystemClock;

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
        User user = (User) currentUser.getObject();
        Locale locale = user.forLocale();
        TimeZone timeZone = user.forTimeZone();
        ExpressiveObjectsContext.getUserProfile().setLocalization(new UserLocalization(locale, timeZone));

        context.addVariable("user-language", user.getLanguageCode(), Scope.SESSION);
        context.addVariable("user-time-zone", timeZone.getID(), Scope.SESSION);

        int offset = timeZone.getOffset(SystemClock.currentTime().toDate().getTime()) / 3600000;
        context.addVariable("my-time-offset", "" + offset, Scope.SESSION);
    }

}

