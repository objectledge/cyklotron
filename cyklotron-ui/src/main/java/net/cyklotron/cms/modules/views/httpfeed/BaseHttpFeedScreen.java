package net.cyklotron.cms.modules.views.httpfeed;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.httpfeed.HttpFeedResource;
import net.cyklotron.cms.httpfeed.HttpFeedService;
import net.cyklotron.cms.httpfeed.HttpFeedUtil;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * Http feed application base screen.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseHttpFeedScreen.java,v 1.2 2005-01-26 09:00:32 pablo Exp $
 */
public abstract class BaseHttpFeedScreen 
    extends BaseCMSScreen
{
    /** http feed service */
    protected HttpFeedService httpFeedService;

    public BaseHttpFeedScreen(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, HttpFeedService httpFeedService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.httpFeedService = httpFeedService;
    }

    public HttpFeedResource getFeed()
    throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        return HttpFeedUtil.getFeed(coralSession, parameters);
    }
}
