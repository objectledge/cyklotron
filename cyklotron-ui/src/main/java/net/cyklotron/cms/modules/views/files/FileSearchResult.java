package net.cyklotron.cms.modules.views.files;

import net.labeo.services.resource.Resource;
import net.labeo.webcore.LinkTool;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesTool;
import net.cyklotron.cms.site.SiteResource;

/**
 * Search result screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FileSearchResult.java,v 1.3 2005-01-25 11:23:57 pablo Exp $
 */
public class FileSearchResult
    extends BaseFilesScreen
{
    /**
     * Builds the screen contents.
     *
     * <p>Redirecto to file application or download</p>
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
            if(!(resource instanceof FileResource) && !(resource instanceof DirectoryResource))
            {
                throw new ProcessingException("Class of the resource '"+resource.getResourceClass().getName()+
                                              "' is does not belong to files application");
            }
            if(resource instanceof FileResource)
            {
                FilesTool filesTool = (FilesTool)templatingContext.get("files");
                data.sendRedirect(filesTool.getLink(resource));
            }
            if(resource instanceof DirectoryResource)
            {
                SiteResource site = CmsTool.getSite(resource);
                if(site != null)
                {
                    LinkTool link = data.getLinkTool();
                    data.sendRedirect(link.unset("view").set("site_id", site.getIdString()).toString());
                }
                else
                {
                    throw new ProcessingException("Directory resource outside the cms branch");
                }
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured during redirecting...",e);
        }
        return null;
    }

    public boolean checkAccessRights(Context context)
    {
        return true;
    }
}

