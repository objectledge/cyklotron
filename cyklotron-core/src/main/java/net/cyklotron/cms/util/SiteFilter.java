package net.cyklotron.cms.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.table.TableFilter;

/**
 * This is a filter for filtering resources upon their owner site,
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SiteFilter.java,v 1.3 2005-02-09 22:20:08 rafal Exp $
 */
public class SiteFilter
    implements TableFilter<Resource>
{
    private Set<SiteResource> acceptedSites;

    public SiteFilter(SiteResource[] acceptedSites)
    {
        this.acceptedSites = new HashSet<SiteResource>(Arrays.asList(acceptedSites));
    }
    
    public SiteFilter(CoralSession coralSession, String[] siteNames, SiteService siteService)
        throws SiteException
    {
        acceptedSites = new HashSet<SiteResource>(siteNames.length);
        for(int i=0; i<siteNames.length; i++)
        {
            acceptedSites.add(siteService.getSite(coralSession, siteNames[i]));
        }
    }

    public boolean accept(Resource object)
    {
        SiteResource site = CmsTool.getSite(object);
        return (site != null) && acceptedSites.contains(site);
    }
    
}
