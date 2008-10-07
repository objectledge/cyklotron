package net.cyklotron.cms.modules.actions.periodicals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResourceData;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PublicationTimeData;
import net.cyklotron.cms.periodicals.PublicationTimeResource;
import net.cyklotron.cms.periodicals.PublicationTimeResourceImpl;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BasePeriodicalsAction.java,v 1.9 2008-10-07 14:47:54 rafal Exp $
 */
public abstract class BasePeriodicalsAction extends BaseCMSAction
{
    /** structure service */
    protected SiteService siteService;

    /** aggregation service */
    protected PeriodicalsService periodicalsService;

    
    public BasePeriodicalsAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PeriodicalsService periodicalsService,
        SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory);
        this.periodicalsService = periodicalsService;
        this.siteService = siteService;
    }

    public void updatePublicationTimes(CoralSession coralSession, PeriodicalResourceData periodicalData, PeriodicalResource periodical)
        throws ProcessingException
    {
        try
        {
            Resource[] resources = coralSession.getStore().getResource(periodical);
            for (int i = 0; i < resources.length; i++)
            {
                coralSession.getStore().deleteResource(resources[i]);
            }
            List publicationTimes = periodicalData.getPublicationTimes();
            for (int i = 0; i < publicationTimes.size(); i++)
            {
                PublicationTimeData ptd = (PublicationTimeData)publicationTimes.get(i);
                try
                {
                    PublicationTimeResource ptr = PublicationTimeResourceImpl
                        .createPublicationTimeResource(coralSession, "" + i, periodical);
                    ptr.setDayOfWeek(ptd.getDayOfWeek());
                    ptr.setDayOfMonth(ptd.getDayOfMonth());
                    ptr.setHour(ptd.getHour());
                    ptr.update();
                }
                catch(Exception e)
                {
                    throw new ProcessingException("unexpected exception", e);
                }
            }
        }
        catch (EntityInUseException e)
        {
            throw new ProcessingException("Exception occured during publication times update", e);
        }
    }
    
    protected String sortAddresses(String addresses)
    {
        return sortAddresses(Arrays.asList(addresses.split("\\s+")));
    }
    
    protected String sortAddresses(List<String> addresses)
    {
        Collections.sort(addresses, new Comparator<String>() {
            public int compare(String o1, String o2)
            {
                String[] a1 = o1.split("@");
                String[] a2 = o2.split("@");
                if(a1.length == 2 && a2.length == 2) // both well formed local_part@host_part 
                {
                    int hostCmp = a1[1].compareTo(a2[1]);
                    if(hostCmp != 0) // sort by host_part first
                    {
                        return hostCmp;
                    }
                    else // then by local_part
                    {
                        return a1[0].compareTo(a2[0]);
                    }
                }
                else if(a1.length == 1 && a2.length == 1) // both malformed 1 token, sort by full image
                {
                    return o1.compareTo(o2);
                }
                else // one is malformed, bring it to the front
                {
                    return a1.length - a2.length;
                }
            }
        });        
        StringBuilder buff = new StringBuilder();
        for(String s : addresses)
        {
            buff.append(s);
            buff.append("\n");
        }
        return buff.toString();
    }

    public boolean checkAccessRights(Context context) throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("periodicals"))
        {
            logger.debug("Application 'periodicals' not enabled in site");
            return false;
        }
        try
        {
            CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
            SiteResource site = getSite(context);
            Resource root = periodicalsService.getApplicationRoot(coralSession, site);
            Permission permission = coralSession.getSecurity().
            getUniquePermission("cms.periodicals.administer");
            return coralSession.getUserSubject().hasPermission(root, permission);
        }
        catch(Exception e)
        {
            logger.error("failed to check permissions for periodical action");
            return false;
        }
    }
}
