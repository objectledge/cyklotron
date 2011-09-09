package net.cyklotron.cms.modules.actions.periodicals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.periodicals.EmailPeriodicalResource;
import net.cyklotron.cms.periodicals.EmailPeriodicalResourceImpl;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

public class UpdateSubscribers
    extends BasePeriodicalsAction
{

    public UpdateSubscribers(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PeriodicalsService periodicalsService,
        SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, periodicalsService, siteService);
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        long periodicalId = parameters.getLong("periodical_id", -1);
        if(periodicalId == -1)
        {
            throw new ProcessingException("Periodical id couldn't be found");
        }
        try
        {
            EmailPeriodicalResource periodical = EmailPeriodicalResourceImpl.getEmailPeriodicalResource(coralSession, periodicalId);
            List<String> subscribers1 = new ArrayList<String>(Arrays.asList(periodical.getAddresses().split("\\s+")));
            List<String> subscribers2 = new ArrayList<String>(Arrays.asList(parameters.get("addresses", "").split("\\s+")));
            boolean subscribe = parameters.getBoolean("subscribe", false);
            if(subscribe)
            {
                subscribers1.addAll(subscribers2); 
            }
            else
            {
                subscribers1.removeAll(subscribers2);
            }
            periodical.setAddresses(sortAddresses(subscribers1));
            periodical.update();
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            logger.error("problem adding a periodical", e);
            return;
        }

    }

}
