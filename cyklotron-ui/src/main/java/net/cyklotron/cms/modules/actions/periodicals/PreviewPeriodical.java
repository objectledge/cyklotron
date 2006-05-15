/*
 * Created on Oct 28, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.actions.periodicals;

import java.util.List;

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
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.periodicals.EmailPeriodicalResource;
import net.cyklotron.cms.periodicals.EmailPeriodicalsRootResource;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResourceImpl;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PreviewPeriodical extends BasePeriodicalsAction
{
    
    
    public PreviewPeriodical(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PeriodicalsService periodicalsService,
        SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, periodicalsService, siteService);
        
    }
    // inherit doc
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) 
        throws ProcessingException
    {
        try
        {
            long periodicalId = parameters.getLong("periodical_id");
            PeriodicalResource periodical = null;
            periodical =
                PeriodicalResourceImpl.getPeriodicalResource(coralSession, periodicalId);
            templatingContext.put("periodical", periodical);
            String recipient = null;
            if(periodical instanceof EmailPeriodicalResource)
            {
                templatingContext.put("isEmail", true);
                EmailPeriodicalsRootResource emailRoot = (EmailPeriodicalsRootResource)periodical.getParent();
                recipient = emailRoot.getPreviewRecipient();
                if(recipient != null && recipient.trim().length() == 0)
                {
                    recipient = null;
                }
                templatingContext.put("recipient", recipient);
            }
            List<FileResource> results = periodicalsService.publishNow(coralSession, periodical,
                false, recipient != null, recipient);
            templatingContext.put("results", results);
            mvcContext.setView("periodicals.PreviewPeriodical");            
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }        
    }
}
