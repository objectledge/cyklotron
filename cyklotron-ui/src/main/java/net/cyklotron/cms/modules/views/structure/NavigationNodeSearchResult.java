package net.cyklotron.cms.modules.views.structure;

import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.mvc.tools.LinkTool;
/**
 * Navigation node information screen.
 */
public class NavigationNodeSearchResult
    extends TemplateScreen
{
    /**
     * Builds the screen contents.
     *
     * <p>Redirecto to section</p>
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
        link = link.set("x",rid).unset("view");
        try
        {
            data.sendRedirect(link.toString());
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured during redirecting...",e);
        }
        return null;
    }

}
