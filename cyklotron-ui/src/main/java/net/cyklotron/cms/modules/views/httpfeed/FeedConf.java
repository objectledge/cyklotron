package net.cyklotron.cms.modules.views.httpfeed;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.httpfeed.FeedViewConfiguration;
import net.cyklotron.cms.httpfeed.HttpFeedService;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * Screen for http feed component configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FeedConf.java,v 1.4 2005-01-26 09:00:32 pablo Exp $
 */
public class FeedConf extends FeedList
{
    
    public FeedConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, HttpFeedService httpFeedService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        httpFeedService);
        // TODO Auto-generated constructor stub
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        super.process(parameters, mvcContext, templatingContext, httpContext, i18nContext, coralSession);
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
        FeedViewConfiguration feedConf = new FeedViewConfiguration(componentConfig);
        templatingContext.put("feed_conf", feedConf);
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        CmsData cmsData = getCmsData();
        if(cmsData.getNode() != null)
        { 
            return cmsData.getNode().canModify(context, coralSession.getUserSubject());
        }
        else
        {
            // check privileges necessary for configuring global components
            return checkAdministrator(coralSession);
        }
    }
}
