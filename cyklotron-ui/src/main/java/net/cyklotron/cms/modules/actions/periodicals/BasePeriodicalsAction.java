package net.cyklotron.cms.modules.actions.periodicals;

import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResourceData;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PublicationTimeData;
import net.cyklotron.cms.periodicals.PublicationTimeResource;
import net.cyklotron.cms.periodicals.PublicationTimeResourceImpl;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BasePeriodicalsAction.java,v 1.3 2005-01-25 07:15:00 pablo Exp $
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
        return checkAdministrator(context);
    }
}
