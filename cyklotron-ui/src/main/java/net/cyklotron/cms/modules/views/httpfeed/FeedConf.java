package net.cyklotron.cms.modules.views.httpfeed;

import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.httpfeed.FeedViewConfiguration;

/**
 * Screen for http feed component configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FeedConf.java,v 1.2 2005-01-24 10:27:02 pablo Exp $
 */
public class FeedConf extends FeedList
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        super.prepare(data, context);
        
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
        FeedViewConfiguration feedConf = new FeedViewConfiguration(componentConfig);
        templatingContext.put("feed_conf", feedConf);
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        if(cmsData.getNode() != null)
        { 
            return cmsData.getNode().canModify(coralSession.getUserSubject());
        }
        else
        {
            // check privileges necessary for configuring global components
            return checkAdministrator(coralSession);
        }
    }
}
