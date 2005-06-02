package net.cyklotron.cms.modules.actions.periodicals;

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
 * @version $Id: BasePeriodicalsAction.java,v 1.4 2005-06-02 11:15:01 pablo Exp $
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
                PublicationTimeResource ptr = PublicationTimeResourceImpl.createPublicationTimeResource(coralSession, "" + i, periodical);
                ptr.setDayOfWeek(ptd.getDayOfWeek());
                ptr.setDayOfMonth(ptd.getDayOfMonth());
                ptr.setHour(ptd.getHour());
                ptr.update();
            }
        }
        catch (EntityInUseException e)
        {
            throw new ProcessingException("Exception occured during publication times update", e);
        }
    }

    public boolean checkAccessRights(Context context) throws ProcessingException
    {
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
