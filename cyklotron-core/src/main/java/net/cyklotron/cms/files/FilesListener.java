package net.cyklotron.cms.files;

import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteResource;
import net.labeo.Labeo;
import net.labeo.services.resource.Role;

/**
 * Files Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: FilesListener.java,v 1.1 2005-01-12 20:44:42 pablo Exp $
 */
public class FilesListener
extends BaseSiteListener
implements SiteCreationListener
{
    /** files service */
    private FilesService filesService;

    protected synchronized void init()
    {
        if(!initialized)
        {
            filesService = (FilesService)Labeo.getBroker().getService(FilesService.SERVICE_NAME);
            super.init();
        }
    }

    //  --------------------       listeners implementation  ----------------------

    /**
     * Called when a new site is created.
     *
     * <p>The method will be called after the site Resources are successfully
     * copied from the template.</p>
     *
     * @param template the site template name.
     * @param name the site name.
     */
    public void createSite(String template, String name)
    {
        init();
        try
        {
            SiteResource site = siteService.getSite(name);
            FilesMapResource filesRoot = filesService.getFilesRoot(site);
            Role filesAdministrator = cmsSecurityService.createRole(site.getAdministrator(),
                "cms.files.administrator",filesRoot, rootSubject);
            Role filesVisitor = cmsSecurityService.createRole(filesAdministrator,
                "cms.files.visitor", filesRoot, rootSubject);
            filesRoot.setAdministrator(filesAdministrator);
            filesRoot.setVisitor(filesVisitor);
            filesRoot.update(rootSubject);

            DirectoryResource publicDirectory =
                filesService.createRootDirectory(site, "public", true, null, rootSubject);
            publicDirectory.setDescription("Public directory");
            publicDirectory.update(rootSubject);
            DirectoryResource protectedDirectory =
                filesService.createRootDirectory(site, "protected", false, null, rootSubject);
            protectedDirectory.setDescription("Protected directory");
            protectedDirectory.update(rootSubject);
        }
        catch(Exception e)
        {
            log.error("Listener Exception",e);
        }
    }
}
