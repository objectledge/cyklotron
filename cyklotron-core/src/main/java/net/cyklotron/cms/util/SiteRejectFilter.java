package net.cyklotron.cms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.table.TableFilter;

/**
 * This is a filter for filtering resources upon their owner site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SiteRejectFilter.java,v 1.2 2005-01-19 08:24:15 pablo Exp $
 */
public class SiteRejectFilter implements TableFilter
{
    private Set rejectedSites;

    public SiteRejectFilter(SiteResource[] rejectedSites)
    {
        this.rejectedSites = new HashSet(Arrays.asList(rejectedSites));
    }
    
    public SiteRejectFilter(CoralSession coralSession, String[] siteNames, SiteService siteService)
        throws SiteException
    {
        List sites = new ArrayList();
        for(int i=0; i<siteNames.length; i++)
        {
            sites.add(siteService.getSite(coralSession, siteNames[i]));
        }
        rejectedSites = new HashSet(sites);
    }

    public boolean accept(Object object)
    {
        if(!(object instanceof Resource))
        {
            return false;
        }

        SiteResource site = CmsTool.getSite((Resource)object);

        return (site != null) && !rejectedSites.contains(site);
    }
    
}
