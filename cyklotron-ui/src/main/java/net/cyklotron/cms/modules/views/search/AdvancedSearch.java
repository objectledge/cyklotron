package net.cyklotron.cms.modules.views.search;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.Instantiator;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.SearchService;

public class AdvancedSearch
    extends Search
{

    public AdvancedSearch(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SearchService searchService, Instantiator instantiator,
        IntegrationService integrationService, CategoryQueryService categoryQueryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, searchService,
                        instantiator, integrationService, categoryQueryService);
    }

}
