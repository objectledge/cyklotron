package net.cyklotron.cms.modules.components.category;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.templating.Templating;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;

/**
 * The base component class for category components.
 */
public abstract class BaseCategoryComponent
	extends SkinableCMSComponent
{
    /** category service */
    protected CategoryService categoryService;

    /** site service */
    protected SiteService siteService;
    
    public BaseCategoryComponent(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        CategoryService categoryService, SiteService siteService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.categoryService = categoryService;
        this.siteService = siteService;
    }
}
