package net.cyklotron.cms.modules.components.category;

/**
 * This component displays lists of resources assigned to queried categories.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ResourceList.java,v 1.1 2005-01-24 04:35:10 pablo Exp $
 */
public class ResourceList
extends BaseResourceList
{
	/* (non-Javadoc)
	 * @see net.cyklotron.cms.modules.components.category.BaseResourceList#getResourceList()
	 */
	protected net.cyklotron.cms.category.components.BaseResourceList getResourceList()
	{
		return new net.cyklotron.cms.category.components.ResourceList(
				coralSession, categoryQueryService);
	}
}
