package net.cyklotron.cms.modules.actions.periodicals;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.periodicals.EmailPeriodicalsRootResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;

/**
 * Email periodicals app configuration update action.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateEmailPeriodicalsConfiguration.java,v 1.4 2005-03-08 10:52:53 pablo Exp $
 */
public class UpdateEmailPeriodicalsConfiguration
    extends BasePeriodicalsAction
{
    public UpdateEmailPeriodicalsConfiguration(Logger logger, StructureService structureService,
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
		CmsData cmsData = cmsDataFactory.getCmsData(context);

		NavigationNodeResource subscriptionNode = null;
		String subscriptionPath = parameters.get("subscription_node_path","");
		if(subscriptionPath.length() > 0)
		{
			SiteResource site = cmsData.getSite();
			try
			{
				Resource structure = structureService.getRootNode(coralSession, site).getParent();
				Resource[] res = coralSession.getStore().getResourceByPath(structure, subscriptionPath);
				if(res.length > 0)
				{
					subscriptionNode = (NavigationNodeResource)res[0];
				}
			}
			catch(StructureException e)
			{
				throw new ProcessingException("failed to locate root node for site", e);
			}
		}

		try
		{
			EmailPeriodicalsRootResource root =
				periodicalsService.getEmailPeriodicalsRoot(coralSession, cmsData.getSite());
			root.setSubscriptionNode(subscriptionNode);
			root.update();				
		}
		catch (PeriodicalsException e)
		{
			throw new ProcessingException("cannot get email periodicals root", e);
        }
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return true;
    }
}
