/*
 * Created on Oct 29, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.actions.periodicals;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PeriodicalsTemplatingService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DeleteTemplate extends BasePeriodicalsAction
{    
    private final PeriodicalsTemplatingService periodicalsTemplatingService;

    public DeleteTemplate(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PeriodicalsService periodicalsService,
        PeriodicalsTemplatingService periodicalsTemplatingService, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, periodicalsService, siteService);
        this.periodicalsTemplatingService = periodicalsTemplatingService;
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
    {
        SiteResource site = getSite(context);
        String renderer = parameters.get("renderer");
        String name = parameters.get("name");
        try
        {
            periodicalsTemplatingService.deleteTemplateVariant(site, renderer, name);
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("periodicals.EditTemplate");
        }
        else
        {
            templatingContext.put("result","deleted_successfully");
        }
    }
}
