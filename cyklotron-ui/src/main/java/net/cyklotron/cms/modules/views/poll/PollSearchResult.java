package net.cyklotron.cms.modules.views.poll;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.poll.PollResource;

/**
 * The poll search result screen class.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PollSearchResult.java,v 1.3 2005-01-25 11:24:03 pablo Exp $
 */
public class PollSearchResult
    extends BasePollScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        long rid = parameters.getLong("res_id", -1);
        if(rid == -1)
        {
            throw new ProcessingException("Resource id not found");
        }
        try
        {
            Resource resource = coralSession.getStore().getResource(rid);
            if(!(resource instanceof PollResource))
            {
                throw new ProcessingException("Class of the resource '"+resource.getResourceClass().getName()+
                                              "' is does not belong to poll application");
            }
            templatingContext.put("resource", resource);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Resource not found",e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        long rid = parameters.getLong("res_id", -1);
        if(rid == -1)
        {
            throw new ProcessingException("Resource id not found");
        }
        try
        {
            Resource resource = coralSession.getStore().getResource(rid);
            if(resource instanceof PollResource)
            {
                return ((PollResource)resource).canView(coralSession.getUserSubject());
            }
            return true;
        }
        catch(Exception e)
        {
            log.error("Exception during access rights checking",e);
            return false;
        }
    }
}
