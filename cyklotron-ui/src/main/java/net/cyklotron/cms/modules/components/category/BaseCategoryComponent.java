package net.cyklotron.cms.modules.components.category;

import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.site.SiteService;

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

    public BaseCategoryComponent()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME))
                                .getFacility(CategoryService.LOGGING_FACILITY);
        categoryService = (CategoryService)broker.getService(CategoryService.SERVICE_NAME);
        siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
    }
}
