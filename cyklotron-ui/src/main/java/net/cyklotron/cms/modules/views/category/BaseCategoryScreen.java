package net.cyklotron.cms.modules.views.category;

import net.cyklotron.cms.category.CategoryConstants;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.CategoryUtil;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.site.SiteService;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

/**
 * The base screen assember for category management application.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseCategoryScreen.java,v 1.2 2005-01-24 10:27:14 pablo Exp $
 */
public abstract class BaseCategoryScreen extends BaseCMSScreen implements CategoryConstants, Secure
{
    /** logging facility */
    protected Logger log;

    /** category service */
    protected CategoryService categoryService;
    
	/** site service */
	protected SiteService siteService;

    public BaseCategoryScreen()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("navi");
        categoryService = (CategoryService)broker.getService(CategoryService.SERVICE_NAME);
        siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
    }

    public CategoryResource getCategory(CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
        return CategoryUtil.getCategory(coralSession, data);
    }

    /**
     * Checks if the current user has the specific permission on the current category.
     */
    public boolean checkPermission(Context context, CoralSession coralSession, String permissionName)
        throws ProcessingException
    {
        return CategoryUtil.checkPermission(coralSession, data, permissionName);
    }
}
