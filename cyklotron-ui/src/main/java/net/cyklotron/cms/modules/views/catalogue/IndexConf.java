package net.cyklotron.cms.modules.views.catalogue;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.catalogue.CatalogueConfigResource;
import net.cyklotron.cms.catalogue.CatalogueConfigResourceImpl;
import net.cyklotron.cms.catalogue.CatalogueService;
import net.cyklotron.cms.catalogue.IndexCard;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * Configuration view for catalogue index screen
 * 
 * @author rafal
 */
public class IndexConf
    extends BaseCMSScreen
{
    private final CatalogueService catalogueService;

    public IndexConf(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager, CatalogueService catalogueService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.catalogueService = catalogueService;
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        Parameters screenConfig = getScreenConfig();
        if(screenConfig.get("cid","").length() > 0)
        {
            try
            {
                long cid = screenConfig.getLong("cid");
                CatalogueConfigResource config = CatalogueConfigResourceImpl
                    .getCatalogueConfigResource(coralSession, cid);
                templatingContext.put("selectedCatalogue", config);
            }
            catch(Exception e)
            {
                throw new ProcessingException(e);
            }
        }
        Resource configRoot = catalogueService.getConfigRoot(getCmsData().getSite(), coralSession);
        templatingContext.put("availableCatalogues", configRoot.getChildren());
        templatingContext.put("sortColumn", screenConfig.get("sortColumn", "TITLE"));
        templatingContext.put("sortAsc", screenConfig.get("sortAsc", "true"));
        templatingContext.put("pageSize", screenConfig.get("pageSize", "20"));
        templatingContext.put("propertyOrder", IndexCard.Property.ORDER);
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("catalogue"))
        {
            logger.debug("Application 'search' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        if(cmsData.getNode() != null)
        {
            return cmsData.getNode().canModify(coralSession, coralSession.getUserSubject());
        }
        else
        {
            return checkAdministrator(coralSession);
        }
    }
}
