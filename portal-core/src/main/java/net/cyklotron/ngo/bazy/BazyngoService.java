package net.cyklotron.ngo.bazy;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;

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

    public BazyngoService(Logger logger, final Configuration config, SiteService siteService,
        CoralSessionFactory coralSessionFactory, FilesService filesService)
        throws ConfigurationException, SiteException, FilesException
    {
        final CoralSession coralSession = coralSessionFactory.getCurrentSession();
        site = siteService.getSite(coralSession, config.getChild(SITE_NAME_KEY).getValue());
    }

    public SiteResource getSite()
    {
        return site;
    }

}
