package net.cyklotron.cms.modules.views.catalogue;

import java.util.List;
import java.util.Locale;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.catalogue.CatalogueConfigResourceImpl;
import net.cyklotron.cms.catalogue.IndexCard;
import net.cyklotron.cms.catalogue.IndexCardTableModel;
import net.cyklotron.cms.catalogue.CatalogueService;
import net.cyklotron.cms.catalogue.CatalogueConfigResource;
import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

public class Index
    extends BaseSkinableScreen
{
    private final CatalogueService catalogueService;

    public Index(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, StructureService structureService,
        StyleService styleService, SkinService skinService, MVCFinder mvcFinder,
        TableStateManager tableStateManager, CatalogueService catalogueService)
    {
        super(context, logger, preferencesService, cmsDataFactory, structureService, styleService,
                        skinService, mvcFinder, tableStateManager);
        this.catalogueService = catalogueService;
    }

    public void prepareDefault(Context context)
        throws ProcessingException
    {
        try
        {
            TemplatingContext templatingContext = context.getAttribute(TemplatingContext.class);
            CoralSession coralSession = context.getAttribute(CoralSession.class);
            I18nContext i18nContext = context.getAttribute(I18nContext.class);
            CmsData cmsData = getCmsData();
            SiteResource site = cmsData.getSite();
            Parameters screenConfig = cmsData.getEmbeddedScreenConfig();
            if(screenConfig.get("cid","").length() > 0)
            {
                long cid = screenConfig.getLong("cid");
                CatalogueConfigResource config = CatalogueConfigResourceImpl
                    .getCatalogueConfigResource(coralSession, cid);

                Parameters parameters = context.getAttribute(RequestParameters.class);
                if(config.isCategoryDefined() && config.isSearchPoolDefined())
                {
                    templatingContext.put("applicationConfigured", "true");
                    Locale locale = i18nContext.getLocale();
                    List<IndexCard> index;
                    String query;
                    if(parameters.isDefined("query") && (query = parameters.get("query")).length() > 0)
                    {
                        templatingContext.put("query", query);
                        index = catalogueService.search(query, config, coralSession, locale);
                    }
                    else
                    {
                        index = catalogueService.getAllItems(config, coralSession, locale);
                    }
                    IndexCardTableModel tableModel = new IndexCardTableModel(index, locale);
                    TableState tableState = tableStateManager.getState(context,
                        "screen:catalogue.Index:" + cmsData.getNode().getIdString());
                    if(tableState.isNew())
                    {
                        tableState.setTreeView(false);
                        tableState.setSortColumnName(screenConfig.get("sortColumn", "TITLE"));
                        tableState.setAscSort(screenConfig.getBoolean("sortAsc", true));
                        tableState.setPageSize(screenConfig.getInt("pageSize", 20));
                    }
                    TableTool<IndexCard> tableTool = new TableTool<IndexCard>(tableState, null,
                                    tableModel);
                    templatingContext.put("table", tableTool);
                }
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("internal error", e);
        }
    }
}
