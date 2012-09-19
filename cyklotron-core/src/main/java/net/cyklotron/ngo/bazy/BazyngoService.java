package net.cyklotron.ngo.bazy;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;

import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

public class BazyngoService
{
    
    private static final String SITE_NAME = "siteName";
    private SiteResource site;
    public BazyngoService(Logger logger, final Configuration config,
        final ServletContext servletContext, SiteService siteService, CoralSession coralSession)
        throws ConfigurationException, ServletException, SiteException
    {
        site = siteService.getSite(coralSession, config.getAttribute(SITE_NAME));
    }
    

    public SiteResource getSite()
    {
        return site;
    }

}
