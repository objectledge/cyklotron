package net.cyklotron.cms.modules.views.aggregation;

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

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.aggregation.AggregationNode;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * A screen for forum application configuration.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawe� Potempski</a>
 * @version $Id: EditConfiguration.java,v 1.1 2005-09-04 12:51:42 pablo Exp $
 */
public class EditConfiguration 
    extends BaseAggregationScreen
{
    
    public EditConfiguration(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        SiteService siteService, TableStateManager tableStateManager,
        AggregationService aggregationService, SecurityService securityService)
    {
        super(context, logger, preferencesService, cmsDataFactory, 
            siteService, aggregationService, securityService, tableStateManager);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try 
        {
            SiteResource site = getSite();
            AggregationNode aggregationNode = aggregationService.getAggregationRoot(coralSession, site, true);
            templatingContext.put("aggregationNode",aggregationNode);
        }
        catch(Exception e)
        {
            throw new ProcessingException("cannot get forum root", e);
        }
    }
}
