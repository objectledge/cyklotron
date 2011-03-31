/*
 */
package net.cyklotron.cms.modules.components.forum;

import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentAliasResource;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.ForumException;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class NodeComments 
    extends Forum
{
    public NodeComments(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        TableStateManager tableStateManager, ForumService forumService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder,
                        tableStateManager, forumService);
        
    }
    public static final String COMPONENT_NAME = "cms:component:forum,NodeComments";

    private static Map stateMap = new HashMap();

    static
    {
        stateMap.put("dl", "MessageList");
        stateMap.put("ml", "MessageList");
        stateMap.put("m", "Message");
        stateMap.put("am", "AddMessage");
    }

    public String getComponentName()
    {
        return COMPONENT_NAME; 
    }

    public Map getStateMap()
    {
        return stateMap;
    }
    
    protected DiscussionResource getDiscussion(HttpContext httpContext, CoralSession coralSession, Context context, boolean errorOnNull)
        throws ProcessingException
    {

        Parameters parameters = RequestParameters.getRequestParameters(context);
        CmsData cmsData = cmsDataFactory.getCmsData(context);    
        NavigationNodeResource node = getContextNode(coralSession, cmsData, parameters);
        if(node == null)
        {
            componentError(context, "No node selected");
            return null;
        }
        try
        {
            if(node instanceof DocumentAliasResource)
            {
                node = ((DocumentAliasResource)node).getOriginalDocument();
            }
            ForumResource forum = getForum(coralSession, node, getSite(context));
            return forumService.getDiscussion(coralSession, forum, "comments/"+node.getIdString());
        }
        catch(ForumException e)
        {
            throw new ProcessingException("failed to retrieve forum information", e);
        }
    }
   
    
    private NavigationNodeResource getContextNode(CoralSession coralSession, CmsData cmsData, Parameters parameters)
    {    
        NavigationNodeResource node;
        if(parameters.isDefined("doc_id"))
        {
            try
            {
                long doc_id = parameters.getLong("doc_id", -1L);
                node = (DocumentNodeResource)coralSession.getStore().getResource(doc_id);
                // check if subject can view this node.
                if(!node.canView(coralSession, coralSession.getUserSubject(),cmsData.getDate()))
                {
                    node = cmsData.getNode();
                }
                return node;
            }
            catch(EntityDoesNotExistException e)
            {
                return cmsData.getNode();
            }
        }
        else
        {
            return cmsData.getNode();
        } 
    }
    
    
    private ForumResource getForum(CoralSession coralSession,
        NavigationNodeResource node, SiteResource site)
        throws ForumException
    {
        ForumResource forum;
        try
        {
            forum = forumService.getForum(coralSession, node.getSite());
        }
        catch(ForumException e)
        {
            forum = forumService.getForum(coralSession, site);
        }
        return forum;
    }
}
