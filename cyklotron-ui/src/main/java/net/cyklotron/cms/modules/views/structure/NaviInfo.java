package net.cyklotron.cms.modules.views.structure;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;

/**
 * Navigation node information screen.
 */
public class NaviInfo
    extends BaseNodeListScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        NavigationNodeResource homePage = getHomePage();
        NavigationNodeResource currentNode;
        if(parameters.get("action","").equals("structure,AddNode"))
        {
            try
            {
                currentNode = (NavigationNodeResource)coralSession.getStore().
                      getResource(parameters.get("node_id").asLong());
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

        prepareTableState(data, context, homePage, currentNode);

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
                log.error(msg, e);
                throw new ProcessingException(msg, e);
            }
        }
        else
        {
            templatingContext.put("clipboard","false");
        }
    }

    protected String getStateName(RunData data)
        throws ProcessingException
    {
        SiteResource site = getSite();
        return "cms:screens:structure,NaviInfo:"+site.getIdString();
    }
    
    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        SiteResource site = getSite();
        return coralSession.getUserSubject().hasRole(site.getTeamMember()) || checkAdministrator(coralSession);
    }
}
