package net.cyklotron.cms.modules.actions.search;

import java.util.ArrayList;
import java.util.List;

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
import net.cyklotron.cms.search.IndexResourceData;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;

/**
 * Action for adding indexes.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AddIndex.java,v 1.9 2007-11-18 21:25:05 rafal Exp $
 */
public class AddIndex
    extends BaseSearchAction
{
    
    
    public AddIndex(Logger logger, StructureService structureService,
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

        IndexResourceData indexData = IndexResourceData.getData(httpContext, null);
        indexData.update(parameters);
        
        if(indexData.getName().equals(""))
        {
            templatingContext.put("result", "name_empty");
            return;
        }
        if(!coralSession.getStore().isValidResourceName(indexData.getName()))
        {
            templatingContext.put("result", "name_invalid");
            return;
        }

        SiteResource site = getSite(context);
        try
        {
            if(coralSession.getStore()
                .getResource(searchService.getIndexesRoot(coralSession, site), indexData.getName()).length > 0)
            {
                templatingContext.put("result","cannot_add_indexes_with_the_same_name");
                return;
            }

            IndexResource index = searchService.createIndex(coralSession, site, indexData.getName());
            index.setDescription(indexData.getDescription());
            index.setPublic(indexData.getPublic());

			index.update();

			// setup branches
            List resources = new ArrayList(indexData.getBranchesSelectionState()
                .getEntities(coralSession, "recursive").keySet());
            searchService.setIndexedBranches(coralSession, index, resources);
            
            resources = new ArrayList(indexData.getBranchesSelectionState()
                .getEntities(coralSession, "local").keySet());
            searchService.setIndexedNodes(coralSession, index, resources);
            
            // WARN: VERY IMPORTANT!! - 
            // on ledge is not so important!!!!!!
            //searchService.updateBranchesAndNodesXRef();
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("problem adding an index for site '"+site.getName()+"'", e);
            return;
        }
		IndexResourceData.removeData(httpContext, null);
        mvcContext.setView("search.IndexList");
        templatingContext.put("result","added_successfully");
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
        return checkPermission(context, coralSession, "cms.search.index.add");
    }
}
