package net.cyklotron.cms.modules.jobs.emails;

import net.cyklotron.cms.confirmation.ConfirmationRequestException;
import net.cyklotron.cms.confirmation.EmailConfirmationService;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.scheduler.Job;

public class DeleteEmailConfirmations
    extends Job
{
    private CoralSessionFactory sessionFactory;

    private EmailConfirmationService emailConfirmationService;
    
    private Logger logger;

    // initialization ///////////////////////////////////////////////////////

    /**
     *
     */
    public DeleteEmailConfirmations(CoralSessionFactory sessionFactory, Logger logger,
        EmailConfirmationService emailConfirmationService)
    {
        this.emailConfirmationService = emailConfirmationService;
        this.sessionFactory = sessionFactory;
        this.logger = logger;
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
            emailConfirmationService.deleteNotConfirmedRequests(coralSession,
                Integer.parseInt(res[0]), Integer.parseInt(res[1]));
        }
        catch(ConfirmationRequestException e)
        {
            logger.error("Failed to delete email confirmation requests");        
        }
        finally
        {
            coralSession.close();
        }
    }
}
