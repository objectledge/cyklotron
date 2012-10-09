package net.cyklotron.cms.ngodatabase.organizations;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.coral.session.CoralSession;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

public abstract class UpdatedDocumentsProvider
{
    private final SiteService siteService;

    public UpdatedDocumentsProvider(SiteService siteService)
    {
        this.siteService = siteService;
    }

    public Date offsetDate(Date date, int offsetDays)
    {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -offsetDays);
        return cal.getTime();
    }

    public SiteResource[] getSites(Configuration config, CoralSession coralSession)
        throws SiteException, ConfigurationException
    {
        Configuration[] siteConfigElm = config.getChildren("site");
        SiteResource[] sites = new SiteResource[siteConfigElm.length];
        for(int i = 0; i < siteConfigElm.length; i++)
        {
            sites[i] = siteService.getSite(coralSession, siteConfigElm[i].getValue());
            if(sites[i] == null)
            {
                throw new ConfigurationException("site " + siteConfigElm[i].getValue()
                    + " not found", siteConfigElm[i].getPath(), siteConfigElm[i].getLocation());
            }
        }
        return sites;
    }

    public abstract List<DocumentNodeResource> queryDocuments(SiteResource[] sites, Date startDate,
        Date endDate, long organizationId, CoralSession coralSession);
}
