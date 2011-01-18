package net.cyklotron.cms.modules.views.library;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

public class Index extends BaseSkinableScreen
{
    private final SearchService searchService;

    public Index(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, StructureService structureService,
        StyleService styleService, SkinService skinService, MVCFinder mvcFinder,
        TableStateManager tableStateManager, SearchService searchService)
    {
        super(context, logger, preferencesService, cmsDataFactory, structureService, styleService,
                        skinService, mvcFinder, tableStateManager);
        this.searchService = searchService;
    }

    public void prepareDefault(Context context)
    {
        TemplatingContext teplatingContext = context.getAttribute(TemplatingContext.class);
        Parameters parameters = context.getAttribute(RequestParameters.class); 
    }
}
