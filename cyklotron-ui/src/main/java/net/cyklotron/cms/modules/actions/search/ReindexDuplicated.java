package net.cyklotron.cms.modules.actions.search;

import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.SearchException;

/**
 * Action for reindexing duplicate resources in indexes.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ReindexDuplicated.java,v 1.1 2005-01-24 04:34:07 pablo Exp $
 */
public class ReindexDuplicated
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
            searchService.getIndexingFacility().reindexDuplicated(index);
        }
        catch(SearchException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
            log.error("problem reindexing duplicate resources in index '"+index.getIdString()+"'", e);
            return;
        }
        templatingContext.put("result","indexed_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.search.index.modify");
    }
}
