package net.cyklotron.cms.modules.views.forum;

import net.labeo.services.resource.Resource;
import net.labeo.webcore.LinkTool;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.MessageResource;

/**
 * The forum search result screen class.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ForumSearchResult.java,v 1.2 2005-01-24 10:27:30 pablo Exp $
 */
public class ForumSearchResult
    extends BaseForumScreen
{
    /**
     * Builds the screen contents.
     *
     * <p>Redirecto to forum application</p>
     */
    public String build(RunData data)
        throws ProcessingException
    {
        LinkTool link = data.getLinkTool();
        long rid = parameters.getLong("res_id", -1);
        if(rid == -1)
        {
            throw new ProcessingException("Resource id not found");
        }
        try
        {
            Resource resource = coralSession.getStore().getResource(rid);
            if(!(resource instanceof DiscussionResource) &&
               !(resource instanceof MessageResource))
            {
                throw new ProcessingException("Class of the resource '"+resource.getResourceClass().getName()+
                                              "' is does not belong to forum application");
            }
            if(resource instanceof DiscussionResource)
            {
                Resource node = ((DiscussionResource)resource).getForum().getForumNode();
                if(node == null)
                {
                    throw new ProcessingException("Section forum not configured in forum application");
                }
                link = link.set("x",node.getIdString()).set("did",rid).set("state","Messages").unset("view");
            }
            if(resource instanceof MessageResource)
            {
                Resource node = ((MessageResource)resource).getDiscussion().getForum().getForumNode();
                if(node == null)
                {
                    throw new ProcessingException("Section forum not configured in forum application");
                }
                link = link.set("x",node.getIdString()).set("mid",rid).set("state","Message").unset("view");
            }
            data.sendRedirect(link.toString());
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured during redirecting...",e);
        }
        return null;
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return true;
    }
    
}
