package net.cyklotron.cms.structure;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.webcore.ProcessingException;

/**
 * Utility methods for structure applications and CMS core.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: StructureUtil.java,v 1.1 2005-01-12 20:44:33 pablo Exp $
 */
public class StructureUtil
{
	/**
	 * Returns node with a given id.
	 */
	public static NavigationNodeResource getNode(ResourceService resourceService, long node_id)
		throws ProcessingException
	{
		NavigationNodeResource node = null;

		try
		{
			Resource naviNodeRes = resourceService.getStore().getResource(node_id);
			if(naviNodeRes instanceof NavigationNodeResource)
			{
				node = (NavigationNodeResource)naviNodeRes;
			}
			else
			{
				String msg = "Resource with a given id="+node_id+" is not a navigation node";
				throw new ProcessingException(msg);
			}
		}
		catch (EntityDoesNotExistException e)
		{
			String msg = "Navigation node with id="+node_id+" does not exist";
			throw new ProcessingException(msg, e);
		}

		return node;
	}
}
