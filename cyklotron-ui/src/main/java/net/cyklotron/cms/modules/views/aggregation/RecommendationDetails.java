/*
 */
package net.cyklotron.cms.modules.views.aggregation;

import java.util.Arrays;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.aggregation.RecommendationResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteService;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class RecommendationDetails 
    extends BaseAggregationScreen
{
    
    public RecommendationDetails(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        SiteService siteService, AggregationService aggregationService,
        SecurityService securityService, TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, siteService, aggregationService,
                        securityService, tableStateManager);
        
    }
    /* 
     * (overriden)
     */
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
        try
        {
            long recId = parameters.getLong("rec");
            RecommendationResource rec =
                (RecommendationResource)coralSession.getStore().getResource(recId);
            templatingContext.put("rec", rec);
            templatingContext.put("comments", Arrays.asList(aggregationService.getComments(coralSession, rec)));
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to retrieve data", e);
        } 
    }
}
