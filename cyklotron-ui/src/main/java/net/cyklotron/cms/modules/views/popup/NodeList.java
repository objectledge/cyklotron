package net.cyklotron.cms.modules.views.popup;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
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
import net.cyklotron.cms.modules.views.structure.BaseNodeListScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;



public class NodeList
    extends BaseNodeListScreen
{
    public NodeList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService);
        // TODO Auto-generated constructor stub
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    { 
        NavigationNodeResource homePage;
        long rootId = parameters.getLong("root", -1);
        if(rootId == -1)
        {
            SiteResource site;
            try
            {
                CmsData cmsData = getCmsData();
                site = cmsData.getSite();
                if(site == null)
                {
                    site = cmsData.getGlobalComponentsDataSite();
                }
                if(site == null)
                {
                    throw new ProcessingException("No site selected");
                }                
                homePage = structureService.getRootNode(coralSession, site);
            }
            catch(StructureException e)
            {
                throw new ProcessingException("failed to lookup site root node");
            }
        }
        else
        {
            try
            {
                homePage = (NavigationNodeResource)coralSession.getStore().getResource(rootId);
            }
            catch (EntityDoesNotExistException e)
            {
                throw new ProcessingException("invalid root parameter", e);
            }
        }
        
        long selectedId = parameters.getLong("selected", -1);
        Resource selected = null;
        if(selectedId != -1)
        {
            try
            {
                selected = coralSession.getStore().getResource(selectedId);
                templatingContext.put("selected", selected);
            }
            catch(EntityDoesNotExistException e)
            {
                throw new ProcessingException("Cannot retrieve a node", e);
            }
        }
        NavigationNodeResource currentNode = (NavigationNodeResource)selected;

        prepareTableState(context, homePage, currentNode);
    }

    protected String getStateName()
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        SiteResource site = cmsData.getSite();
        if(site == null)
        {
            site = cmsData.getGlobalComponentsDataSite();
        }
        if(site == null)
        {
            throw new ProcessingException("no site selected");
        }
        return "cms:screens:popup,NodeList:"+site.getIdString();
    }
}

