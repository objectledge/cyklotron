package net.cyklotron.cms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.labeo.Labeo;
import net.labeo.services.resource.Resource;

import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * This is a filter for filtering resources upon their owner site,
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SiteFilter.java,v 1.1 2005-01-12 20:44:32 pablo Exp $
 */
public class SiteFilter
    implements net.labeo.services.table.TableFilter
{
    private Set acceptedSites;

    public SiteFilter(SiteResource[] acceptedSites)
    {
        this.acceptedSites = new HashSet(Arrays.asList(acceptedSites));
    }
    
    public SiteFilter(String[] siteNames)
        throws SiteException
    {
        SiteService siteService = (SiteService)(Labeo.getBroker().
            getService(SiteService.SERVICE_NAME));
        List sites = new ArrayList();
        for(int i=0; i<siteNames.length; i++)
        {
            sites.add(siteService.getSite(siteNames[i]));
        }
        acceptedSites = new HashSet(sites);
    }

    public boolean accept(Object object)
    {
        if(!(object instanceof Resource))
        {
            return false;
        }

        SiteResource site = CmsTool.getSite((Resource)object);

        return (site != null) && acceptedSites.contains(site);
    }
    
}
