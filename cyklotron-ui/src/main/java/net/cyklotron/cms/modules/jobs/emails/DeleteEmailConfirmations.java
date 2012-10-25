package net.cyklotron.cms.modules.jobs.emails;

import java.util.Arrays;

import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.confirmation.ConfirmationRequestException;
import net.cyklotron.cms.confirmation.EmailConfirmationService;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.scheduler.Job;

public class DeleteEmailConfirmations extends Job
{
    private CoralSessionFactory sessionFactory;
    private EmailConfirmationService emailConfirmationService;
    // initialization ///////////////////////////////////////////////////////
    
    /**
     *
     */
    public DeleteEmailConfirmations(CoralSessionFactory sessionFactory, EmailConfirmationService emailConfirmationService)
    {
        this.emailConfirmationService = emailConfirmationService;
        this.sessionFactory = sessionFactory;
    }

    // Job interface ////////////////////////////////////////////////////////
    
    /**
     * Performs the mainteance.
     */
    public void run(String[] arguments)
    {
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            String[] res = arguments[0].split("\\,\\s*");       
            emailConfirmationService.deleteNotConfirmedEmailConfirmationRequests(coralSession, res[0], res[1]);
        }
        catch(ConfirmationRequestException e)
        {
            new ConfirmationRequestException("Failed to delete email confirmation requests");
        }
        finally
        {
            coralSession.close();
        }
    }
}
