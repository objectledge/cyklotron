package net.cyklotron.cms.modules.views.link;

import net.labeo.services.resource.Resource;
import net.labeo.webcore.LinkTool;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.CmsLinkResource;
import net.cyklotron.cms.link.ExternalLinkResource;

/**
 * The link search result screen class.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: LinksSearchResult.java,v 1.2 2005-01-24 10:27:19 pablo Exp $
 */
public class LinksSearchResult
    extends BaseLinkScreen
{
    /**
     * Builds the screen contents.
     *
     * <p>Redirecto to link target</p>
     */
    public String build(RunData data)
        throws ProcessingException
    {
        try
        {
            long rid = parameters.getLong("res_id", -1);
            if(rid == -1)
            {
                throw new ProcessingException("Resource id not found");
            }
            Resource resource = coralSession.getStore().getResource(rid);
            if(!(resource instanceof BaseLinkResource))
            {
                throw new ProcessingException("Class of the resource '"+resource.getResourceClass().getName()+
                                              "' is does not belong to link application");
            }
            if(resource instanceof CmsLinkResource)
            {
                LinkTool link = data.getLinkTool();
                link = link.unset("view").set("x",((CmsLinkResource)resource).getNode().getIdString());
                data.sendRedirect(link.toString());
            }
            if(resource instanceof ExternalLinkResource)
            {
                data.sendRedirect(((ExternalLinkResource)resource).getTarget());
            }
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
