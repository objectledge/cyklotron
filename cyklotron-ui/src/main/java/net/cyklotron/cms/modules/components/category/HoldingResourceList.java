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
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;

/**
 * This component displays lists of hand-prioritzed resources assigned to queried categories.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HoldingResourceList.java,v 1.3 2005-01-27 04:59:00 pablo Exp $
 */
public class HoldingResourceList
extends BaseResourceList
{
    public HoldingResourceList(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        CategoryService categoryService, SiteService siteService,
        TableStateManager tableStateManager, CategoryQueryService categoryQueryService,
        CacheFactory cacheFactory, IntegrationService integrationService,
        PreferencesService preferencesService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder, categoryService,
                        siteService, tableStateManager, categoryQueryService, cacheFactory,
                        integrationService, preferencesService);
        // TODO Auto-generated constructor stub
    }
    /* (non-Javadoc)
     * @see net.cyklotron.cms.modules.components.category.BaseResourceList#getResourceList()
     */
    protected net.cyklotron.cms.category.components.BaseResourceList getResourceList()
    {
		return new net.cyklotron.cms.category.components.HoldingResourceList(context, integrationService, cmsDataFactory,
            categoryQueryService, siteService);
    }
}
