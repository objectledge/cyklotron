/*
 */
package net.cyklotron.cms.modules.components.forum;

import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.ForumException;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.ForumService;
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
        // TODO Auto-generated constructor stub
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
        if(getNode() == null)
        {
            componentError(context, "No node selected");
            return null;
        }
        NavigationNodeResource node = getNode();
        try
        {
            ForumResource forum = forumService.getForum(coralSession, getSite(context));
            return forumService.getDiscussion(coralSession, forum, "comments/"+node.getIdString());
        }
        catch(ForumException e)
        {
            throw new ProcessingException("failed to retrieve forum information", e);
        }
    }
}
