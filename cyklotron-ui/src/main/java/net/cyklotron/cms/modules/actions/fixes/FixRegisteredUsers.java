package net.cyklotron.cms.modules.actions.fixes;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * 
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawe� Potempski</a>
 * @version $Id: FixRegisteredUsers.java,v 1.1 2005-01-24 04:34:29 pablo Exp $
 */
public class FixRegisteredUsers
    extends BaseCMSAction
{
	protected Logger log;
	
	public FixRegisteredUsers()
    {
		log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("fixes");
	}
	
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        try
        {
            Role role = coralSession.getSecurity().getUniqueRole("cms.registered");
            Subject[] subjects = coralSession.getSecurity().getSubject();
            for(int i = 0; i < subjects.length; i++)
            {
                if(subjects[i].getName().equals("uid=anonymous,ou=people,o=ngo,c=pl") || 
                   subjects[i].getName().equals("uid=root,ou=people,o=ngo,c=pl"))
                {
                    // do not grant anonymous nor root
                }
                else
                {
                    try
                    {
                        coralSession.getSecurity().grant(role, subjects[i], false, coralSession.getUserSubject());
                    }
                    catch(Exception e)
                    {
                        log.error("failed to grant registered role to user "+subjects[i].getName(), e);
                    }
                }
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
    }
}
