package net.cyklotron.cms.files;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.event.EventWhiteboard;
import org.objectledge.filesystem.FileSystem;
import org.picocontainer.Startable;

import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteDestructionValve;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Files Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: FilesListener.java,v 1.5 2005-05-31 17:11:21 pablo Exp $
 */
public class FilesListener
extends BaseSiteListener
implements SiteCreationListener, SiteDestructionValve, Startable
{
    /** files service */
    private FilesService filesService;

    private FileSystem fileSystem;
    
    public FilesListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, EventWhiteboard eventWhiteboard,
        FilesService filesService, FileSystem fileSystem)
    {
        super(logger, sessionFactory, cmsSecurityService, eventWhiteboard);
        this.filesService = filesService;
        this.fileSystem = fileSystem;
        eventWhiteboard.addListener(SiteCreationListener.class,this,null);
    }

    /**
     * {@inheritDoc}
     */
    public void start()
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop()
    {
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

    /**
     * {@inheritDoc}
     */
    public void clearApplication(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        Resource[] res = coralSession.getStore().getResource(site, "files");
        if(res.length == 0)
        {
            return;
        }
        Resource root = res[0];
        res = coralSession.getStore().getResource(root);
        for(Resource r: res)
        {
            if(r instanceof RootDirectoryResource)
            {
                String path = filesService.getPath((DirectoryResource)r);
                if(fileSystem.exists(path))
                {
                    fileSystem.deleteRecursive(path);
                }
            }
        }
        deleteSiteNode(coralSession,root);
    }

    

    public void clearSecurity(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        Resource[] res = coralSession.getStore().getResource(site, "files");
        if(res.length == 0)
        {
            return;
        }
        FilesMapResource root = (FilesMapResource)res[0];
        root.setAdministrator(null);
        root.setVisitor(null);
        root.update();
    }
}
