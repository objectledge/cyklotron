/*
 */
package net.cyklotron.cms.modules.components.forum;

import java.util.HashMap;
import java.util.Map;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.ForumException;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class NodeComments 
    extends Forum
{
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
    
    protected DiscussionResource getDiscussion(RunData data, Context context, boolean errorOnNull)
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
            ForumResource forum = forumService.getForum(getSite(context));
            return forumService.getDiscussion(forum, "comments/"+node.getIdString());
        }
        catch(ForumException e)
        {
            throw new ProcessingException("failed to retrieve forum information", e);
        }
    }
}
