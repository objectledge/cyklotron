package net.cyklotron.cms.modules.components.forum;

import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;

/**
 * The discussion list screen class.
 */
public abstract class BaseForumComponent
    extends SkinableCMSComponent
{
    protected TableService tableService = null;

    protected ForumService forumService = null;

    public BaseForumComponent()
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        forumService = (ForumService)broker.getService(ForumService.SERVICE_NAME);
    }
}
