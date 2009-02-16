package net.cyklotron.cms.modules.actions.search;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Action for index optimisation.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: Optimise.java,v 1.5 2007-02-25 14:15:27 pablo Exp $
 */
public class Optimise extends BaseSearchAction
{
    public Optimise(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SearchService searchService)
    {
        super(logger, structureService, cmsDataFactory, searchService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        
        Subject subject = coralSession.getUserSubject();

        IndexResource index = getIndex(coralSession, parameters);
        try
        {
            searchService.getIndexingFacility().optimize(index);
        }
        catch(SearchException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            logger.error("problem optimising index '"+index.getIdString()+"'", e);
            return;
        }
        templatingContext.put("result","optimised_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("search"))
        {
            logger.debug("Application 'search' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.search.index.modify");
    }
}
