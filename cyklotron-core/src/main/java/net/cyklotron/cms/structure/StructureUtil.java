package net.cyklotron.cms.structure;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.pipeline.ProcessingException;

/**
 * Utility methods for structure applications and CMS core.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: StructureUtil.java,v 1.2 2005-01-18 13:20:48 pablo Exp $
 */
public class StructureUtil
{
	/**
	 * Returns node with a given id.
	 */
	public static NavigationNodeResource getNode(CoralSession coralSession, long node_id)
		throws ProcessingException
	{
		NavigationNodeResource node = null;

		try
		{
			Resource naviNodeRes = coralSession.getStore().getResource(node_id);
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
