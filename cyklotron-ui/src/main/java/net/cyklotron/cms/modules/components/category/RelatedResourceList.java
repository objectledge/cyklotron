package net.cyklotron.cms.modules.components.category;


/**
 * This component displays lists of resources assigned to categories assigned to current document
 * node. Category list is filtered upon this component's configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: RelatedResourceList.java,v 1.1 2005-01-24 04:35:10 pablo Exp $
 */
public class RelatedResourceList
extends BaseResourceList
{
	/* (non-Javadoc)
	 * @see net.cyklotron.cms.modules.components.category.BaseResourceList#getResourceList()
	 */
	protected net.cyklotron.cms.category.components.BaseResourceList getResourceList()
	{
		return new net.cyklotron.cms.category.components.RelatedResourceList(categoryQueryService,
			categoryService);
	}
}
