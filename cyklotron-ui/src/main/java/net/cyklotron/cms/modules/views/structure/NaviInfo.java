package net.cyklotron.cms.modules.views.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * Navigation node information screen.
 */
public class NaviInfo
    extends BaseNodeListScreen
{
    
    public NaviInfo(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        NavigationNodeResource homePage = getHomePage();
        NavigationNodeResource currentNode;
        if(parameters.get("action","").equals("structure,AddNode"))
        {
            try
            {
                currentNode = (NavigationNodeResource)coralSession.getStore().
                      getResource(parameters.getLong("node_id"));
            }
            catch(Exception e)
            {
                throw new ProcessingException("invalid node id", e);
            }
        }
        else
        {
            currentNode = getNode();
        }

        prepareTableState(context, homePage, currentNode);

       // TODO: Check if we need to have it?
        Long clipId = (Long)httpContext.getSessionAttribute(CLIPBOARD_KEY);
        if(clipId != null)
        {
            try
            {
                NavigationNodeResource clipboardNode = NavigationNodeResourceImpl
                    .getNavigationNodeResource(coralSession, clipId.longValue());
                templatingContext.put("clipboard", "true");
                templatingContext.put("clipboard_node", clipboardNode);
                templatingContext.put("clipboard_mode", httpContext.getSessionAttribute(CLIPBOARD_MODE));
            }
            catch (EntityDoesNotExistException e)
            {
                String msg = "Navigation node with id="+clipId.longValue()
                    +" stored in clipboard cannot be retrieved";
                logger.error(msg, e);
                throw new ProcessingException(msg, e);
            }
        }
        else
        {
            templatingContext.put("clipboard","false");
        }
    }

    protected String getStateName()
        throws ProcessingException
    {
        SiteResource site = getSite();
        return "cms:screens:structure,NaviInfo:"+site.getIdString();
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        SiteResource site = getSite();
        return coralSession.getUserSubject().hasRole(site.getTeamMember()) || checkAdministrator(coralSession);
    }
}
