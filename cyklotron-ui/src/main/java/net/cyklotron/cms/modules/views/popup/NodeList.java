package net.cyklotron.cms.modules.views.popup;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.modules.views.structure.BaseNodeListScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;



public class NodeList
    extends BaseNodeListScreen
{
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
                homePage = structureService.getRootNode(site);
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

        prepareTableState(data, context, homePage, currentNode);
    }

    protected String getStateName(RunData data)
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

