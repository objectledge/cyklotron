package net.cyklotron.cms.modules.actions.search;

import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.SearchException;

/**
 * Action for incrementral index cleanup - deletes deleted resources still existing in the index.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DeleteDeleted.java,v 1.2 2005-01-24 10:27:13 pablo Exp $
 */
public class DeleteDeleted
    extends BaseSearchAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        IndexResource index = getIndex(data);
        try
        {
            searchService.getIndexingFacility().deleteDeleted(index);
        }
        catch(SearchException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            log.error("problem incrementally cleaning up index '"+index.getIdString()+"'", e);
            return;
        }
        templatingContext.put("result","cleaned_up_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.search.index.modify");
    }
}
