package net.cyklotron.cms.modules.views.category;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryConstants;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.CategoryUtil;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;

/**
 * The base screen assember for category management application.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseCategoryScreen.java,v 1.3 2005-01-26 05:23:29 pablo Exp $
 */
public abstract class BaseCategoryScreen 
    extends BaseCMSScreen 
    implements CategoryConstants
{
    /** category service */
    protected CategoryService categoryService;
    
	/** site service */
	protected SiteService siteService;

    protected IntegrationService integrationService;
    
    public BaseCategoryScreen(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryService categoryService,
        SiteService siteService, IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.categoryService = categoryService;
        this.siteService = siteService;
        this.integrationService = integrationService;
    }

    public CategoryResource getCategory(CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
        return CategoryUtil.getCategory(coralSession, parameters);
    }

    /**
     * Checks if the current user has the specific permission on the current category.
     */
    public boolean checkPermission(Context context, CoralSession coralSession, String permissionName)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        return CategoryUtil.checkPermission(coralSession, parameters, permissionName);
    }
}
