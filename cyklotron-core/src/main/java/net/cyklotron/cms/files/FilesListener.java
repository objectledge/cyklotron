package net.cyklotron.cms.files;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;

import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Files Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: FilesListener.java,v 1.4 2005-03-23 07:53:18 rafal Exp $
 */
public class FilesListener
extends BaseSiteListener
implements SiteCreationListener
{
    /** files service */
    private FilesService filesService;

    public FilesListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, FilesService filesService)
    {
        super(logger, sessionFactory, cmsSecurityService);
        this.filesService = filesService;
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
    public void createSite(SiteService siteService, String template, String name)
    {
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            SiteResource site = siteService.getSite(coralSession, name);
            FilesMapResource filesRoot = filesService.getFilesRoot(coralSession, site);
            Role filesAdministrator = cmsSecurityService.createRole(coralSession, site.getAdministrator(),
                "cms.files.administrator",filesRoot);
            Role filesVisitor = cmsSecurityService.createRole(coralSession, filesAdministrator,
                "cms.files.visitor", filesRoot);
            filesRoot.setAdministrator(filesAdministrator);
            filesRoot.setVisitor(filesVisitor);
            filesRoot.update();

            DirectoryResource publicDirectory =
                filesService.createRootDirectory(coralSession, site, "public", true, null);
            publicDirectory.setDescription("Public directory");
            publicDirectory.update();
            DirectoryResource protectedDirectory =
                filesService.createRootDirectory(coralSession, site, "protected", false, null);
            protectedDirectory.setDescription("Protected directory");
            protectedDirectory.update();
        }
        catch(Exception e)
        {
            log.error("Listener Exception",e);
        }
        finally
        {
            coralSession.close();
        }
    }
}
