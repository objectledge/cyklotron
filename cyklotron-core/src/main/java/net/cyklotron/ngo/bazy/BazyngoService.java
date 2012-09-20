package net.cyklotron.ngo.bazy;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;

import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

public class BazyngoService
{    
    private static final String SITE_NAME_KEY = "siteName";
    public static final String BAZYNGO_SITE_FILES_ROOT_DIR = "org_files";
    
    private SiteResource site;
    public BazyngoService(Logger logger, final Configuration config,
        final ServletContext servletContext, SiteService siteService, CoralSessionFactory coralSessionFactory,
        FilesService filesService)
        throws ConfigurationException, ServletException, SiteException, FilesException
    {
        final CoralSession coralSession = coralSessionFactory.getCurrentSession();
        site = siteService.getSite(coralSession, config.getChild(SITE_NAME_KEY).getValue());
        //filesService.createRootDirectory(coralSession, site, BAZYNGO_SITE_FILES_ROOT_DIR, true, null);
    }
    

    public SiteResource getSite()
    {
        return site;
    }

}
