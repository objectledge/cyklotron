package net.cyklotron.cms.modules.components.category;

import org.jcontainer.dna.Logger;
import org.objectledge.cache.CacheFactory;
import org.objectledge.context.Context;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;

/**
 * This component displays lists of document resources assigned to queried categories.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentResourceList.java,v 1.2 2005-01-25 11:24:10 pablo Exp $
 */
public class DocumentResourceList
extends BaseResourceList
{
    public DocumentResourceList(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        CategoryService categoryService, SiteService siteService,
        TableStateManager tableStateManager, CategoryQueryService categoryQueryService,
        CacheFactory cacheFactory, IntegrationService integrationService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder, categoryService,
                        siteService, tableStateManager, categoryQueryService, cacheFactory,
                        integrationService);
    }
	/* (non-Javadoc)
	 * @see net.cyklotron.cms.modules.components.category.BaseResourceList#getResourceList()
	 */
	protected net.cyklotron.cms.category.components.BaseResourceList getResourceList()
	{
		return new net.cyklotron.cms.category.components.DocumentResourceList(context, integrationService, cmsDataFactory,
            categoryQueryService, siteService);
	}
}
