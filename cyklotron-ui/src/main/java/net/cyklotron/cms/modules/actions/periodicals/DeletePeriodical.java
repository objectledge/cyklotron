package net.cyklotron.cms.modules.actions.periodicals;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResourceImpl;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Periodical delete action.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: DeletePeriodical.java,v 1.4 2005-03-08 10:52:53 pablo Exp $
 */
public class DeletePeriodical
    extends BasePeriodicalsAction
{

    public DeletePeriodical(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PeriodicalsService periodicalsService,
        SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, periodicalsService, siteService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();

		long periodicalId = parameters.getLong("periodical_id", -1);
		if(periodicalId == -1)
		{
			throw new ProcessingException("Periodical id couldn't be found");
		}
			
		try
		{
			PeriodicalResource periodical = PeriodicalResourceImpl.getPeriodicalResource(coralSession, periodicalId);
			Resource[] publicationTimes = coralSession.getStore().getResource(periodical);
			for(int i = 0; i < publicationTimes.length; i++)
			{
				coralSession.getStore().deleteResource(publicationTimes[i]);		
			}
    	    coralSession.getStore().deleteResource(periodical);
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            logger.error("problem adding a periodical", e);
            return;
        }
	}

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return true;
    }
}
