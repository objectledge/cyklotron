package net.cyklotron.cms.modules.components.forum;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.skins.SkinService;

/**
 * The discussion list screen class.
 */
public abstract class BaseForumComponent
    extends SkinableCMSComponent
{
    protected TableStateManager tableStateManager;

    protected ForumService forumService;

    public BaseForumComponent(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        TableStateManager tableStateManager, ForumService forumService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.tableStateManager = tableStateManager;
        this.forumService = forumService;
    }
}
