package net.cyklotron.cms.modules.views.periodicals;

import org.jcontainer.dna.Logger;
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
import net.cyklotron.cms.periodicals.EmailPeriodicalsRootResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * Configuration for subscriptins screen for email periodicals subscriptions. 
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ConfigureEmailPeriodicals.java,v 1.5 2006-05-08 10:01:29 rafal Exp $
 */
public class ConfigureEmailPeriodicals 
    extends BasePeriodicalsScreen
{
    
    
    public ConfigureEmailPeriodicals(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, PeriodicalsService periodicalsService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        periodicalsService);
        
    }
    /* 
     * (overriden)
     */
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
    	CmsData cmsData = cmsDataFactory.getCmsData(context);
    	try
        {
            EmailPeriodicalsRootResource root =
            	periodicalsService.getEmailPeriodicalsRoot(coralSession, cmsData.getSite());
            templatingContext.put("subscription_node", root.getSubscriptionNode());
            templatingContext.put("preview_recipient", root.getPreviewRecipient());
        }
        catch (PeriodicalsException e)
        {
        	throw new ProcessingException("cannot get email periodicals root", e);
        }
    }
}
