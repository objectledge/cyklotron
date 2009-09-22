package net.cyklotron.cms.modules.views.syndication;

import java.io.IOException;
import java.util.List;

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
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.syndication.IncomingFeedResource;
import net.cyklotron.cms.syndication.IncomingFeedResourceData;
import net.cyklotron.cms.syndication.IncomingFeedUtil;
import net.cyklotron.cms.syndication.SyndicationService;

/**
 * Editing an incoming feed.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EditIncomingFeed.java,v 1.3 2007-11-18 21:24:50 rafal Exp $
 */
public class EditIncomingFeed extends BaseSyndicationScreen
{
    public EditIncomingFeed(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SyndicationService syndicationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
            syndicationService);
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession) throws org.objectledge.pipeline.ProcessingException
    {
        IncomingFeedResource feed = null;
        if(parameters.isDefined(IncomingFeedUtil.FEED_ID_PARAM))
        {
            feed = getIncomingFeed(coralSession, parameters);
            templatingContext.put("feed", feed);
        }
        if(parameters.getBoolean("fromList", false))
        {
            IncomingFeedResourceData.removeData(httpContext, feed);
        }
        // WARN: feed may be null -> creation of a new feed
        IncomingFeedResourceData feedData = IncomingFeedResourceData.getData(httpContext, feed);
        templatingContext.put("feed_data", feedData);
        
        List templates;
        try
        {
            templates = syndicationService.getIncomingFeedsManager().getTransformationTemplates();
        }
        catch(IOException e)
        {
            throw new ProcessingException(e);
        }
        templatingContext.put("templates", templates);
    }

    public boolean checkAccessRights(Context context)
    throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("syndication"))
        {
            logger.debug("Application 'syndication' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.syndication.infeed.modify");
    }
}
