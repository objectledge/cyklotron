package net.cyklotron.cms.modules.actions.periodicals;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.periodicals.EmailPeriodicalsRootResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Email periodicals app configuration update action.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateEmailPeriodicalsConfiguration.java,v 1.2 2005-01-24 10:27:17 pablo Exp $
 */
public class UpdateEmailPeriodicalsConfiguration
    extends BasePeriodicalsAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

		CmsData cmsData = cmsDataFactory.getCmsData(context);

		NavigationNodeResource subscriptionNode = null;
		String subscriptionPath = parameters.get("subscription_node_path","");
		if(subscriptionPath.length() > 0)
		{
			SiteResource site = cmsData.getSite();
			try
			{
				Resource structure = structureService.getRootNode(site).getParent();
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
				periodicalsService.getEmailPeriodicalsRoot(cmsData.getSite());
			root.setSubscriptionNode(subscriptionNode);
			root.update(subject);				
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
