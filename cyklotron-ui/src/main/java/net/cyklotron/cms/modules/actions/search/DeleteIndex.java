package net.cyklotron.cms.modules.actions.search;

import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.SearchException;

/**
 * Action for deleting indexes.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DeleteIndex.java,v 1.1 2005-01-24 04:34:07 pablo Exp $
 */
public class DeleteIndex extends BaseSearchAction
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
            searchService.deleteIndex(index, subject);
        }
        catch(SearchException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
            log.error("problem deleting the index '"+index.getIdString()+"'", e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.search.index.delete");
    }
}
