package net.cyklotron.cms.modules.views.category;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.components.BaseResourceListConfiguration;
import net.cyklotron.cms.category.components.DocumentResourceListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;

/**
 * Configuration screen for DocumentResourceList component.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentResourceListComponentConf.java,v 1.2 2005-01-26 05:23:29 pablo Exp $
 */
public class DocumentResourceListComponentConf extends BaseResourceListComponentConf
{
    
    public DocumentResourceListComponentConf(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryService categoryService,
        SiteService siteService, IntegrationService integrationService,
        CategoryQueryService categoryQueryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        categoryService, siteService, integrationService, categoryQueryService);
        // TODO Auto-generated constructor stub
    }
    /* (non-Javadoc)
     * @see net.cyklotron.cms.modules.screens.category.BaseResourceListComponentConf#getConfig(net.labeo.webcore.RunData)
     */
    protected BaseResourceListConfiguration getConfig() throws ProcessingException
    {
		return DocumentResourceListConfiguration.getConfig(context);
    }
}
