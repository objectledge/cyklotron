package net.cyklotron.ngo.bazy;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;

import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

public class BazyngoService
{
    
    private static final String SITE_NAME_KEY = "siteName";
    private SiteResource site;
    public BazyngoService(Logger logger, final Configuration config,
        final ServletContext servletContext, SiteService siteService, CoralSessionFactory coralSessionFactory)
        throws ConfigurationException, ServletException, SiteException
    {
        site = siteService.getSite(coralSessionFactory.getCurrentSession(), config.getChild(SITE_NAME_KEY).getValue());
    }
    

    public SiteResource getSite()
    {
        return site;
    }

}
