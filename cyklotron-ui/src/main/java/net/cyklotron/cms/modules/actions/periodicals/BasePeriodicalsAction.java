package net.cyklotron.cms.modules.actions.periodicals;

import java.util.List;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResourceData;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PublicationTimeData;
import net.cyklotron.cms.periodicals.PublicationTimeResource;
import net.cyklotron.cms.periodicals.PublicationTimeResourceImpl;
import net.cyklotron.cms.site.SiteService;
import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BasePeriodicalsAction.java,v 1.1 2005-01-24 04:34:14 pablo Exp $
 */
public abstract class BasePeriodicalsAction extends BaseCMSAction implements Secure
{
    /** logging facility */
    protected Logger log;

    /** structure service */
    protected SiteService siteService;

    /** aggregation service */
    protected PeriodicalsService periodicalsService;

    public BasePeriodicalsAction()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(PeriodicalsService.LOGGING_FACILITY);
        periodicalsService = (PeriodicalsService)broker.getService(PeriodicalsService.SERVICE_NAME);
    }

    public void updatePublicationTimes(RunData data, PeriodicalResourceData periodicalData, PeriodicalResource periodical, Subject subject)
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
                PublicationTimeResource ptr = PublicationTimeResourceImpl.createPublicationTimeResource(coralSession, "" + i, periodical, subject);
                ptr.setDayOfWeek(ptd.getDayOfWeek());
                ptr.setDayOfMonth(ptd.getDayOfMonth());
                ptr.setHour(ptd.getHour());
                ptr.update(subject);
            }
        }
        catch (EntityInUseException e)
        {
            throw new ProcessingException("Exception occured during publication times update", e);
        }
        catch (ValueRequiredException e)
        {
            throw new ProcessingException("Exception occured during publication times update", e);
        }
    }

    public boolean checkAccess(RunData data) throws ProcessingException
    {
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }
}
