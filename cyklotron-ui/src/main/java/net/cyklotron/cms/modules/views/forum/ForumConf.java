package net.cyklotron.cms.modules.views.forum;

import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Screen;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.forum.ForumException;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;


/**
 * The discussion list screen class.
 */
public class ForumConf
    extends BaseForumScreen
    implements Secure
{
    public Screen route(RunData data)
        throws NotFoundException, ProcessingException
    {
        try
        {
            CmsData cmsData = getCmsData();
            NavigationNodeResource node = cmsData.getNode();
            Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
            String instance = parameters.get("component_instance","");
            
            httpContext.setSessionAttribute(FROM_COMPONENT,new Boolean(true));
            httpContext.setSessionAttribute(COMPONENT_INSTANCE,instance);
            if(node != null)
            {
                httpContext.setSessionAttribute(COMPONENT_NODE, node.getIdObject());
            }

            SiteResource site = cmsData.getSite();
            if(site == null)
            {
                site = cmsData.getGlobalComponentsDataSite();          
            }
            if(site == null)
            {
                throw new ProcessingException("No site selected");
            }
            ForumResource forumResource = forumService.getForum(site);
            parameters.set("fid",forumResource.getIdString());
            long did = componentConfig.get("did").asLong(-1);
            parameters.set("did",did);
            mvcContext.setView("forum,DiscussionList");
            return (Screen)data.getScreenAssembler();
        }
        catch(ForumException e)
        {
            throw new ProcessingException("ForumException",e);
        }
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        NavigationNodeResource node = getNode();
        String instance = parameters.get("component_instance","");
        httpContext.setSessionAttribute(FROM_COMPONENT,new Boolean(true));
        httpContext.setSessionAttribute(COMPONENT_INSTANCE,instance);
        httpContext.setSessionAttribute(COMPONENT_NODE,node.getIdObject());
    }
}
