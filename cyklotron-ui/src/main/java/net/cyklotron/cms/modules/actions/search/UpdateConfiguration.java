package net.cyklotron.cms.modules.actions.search;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.search.RootResource;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;

/**
 * Updates search application configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateConfiguration.java,v 1.4 2005-03-08 10:53:37 pablo Exp $
 */
public class UpdateConfiguration extends BaseSearchAction
{
    public UpdateConfiguration(Logger logger, StructureService structureService,
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
        
        SiteResource site = getSite(context);
        try 
        {
            String path = parameters.get("searchNodePath","");
            NavigationNodeResource searchNode = null;
            if(!path.equals(""))
            {
                Resource parent = structureService.getRootNode(coralSession, site).getParent();
                Resource[] ress = coralSession.getStore().getResourceByPath(parent.getPath()+path);
                if(ress.length == 1)
                {
                    searchNode = (NavigationNodeResource)ress[0];
                }
                else if(ress.length > 1)
                {
                    throw new ProcessingException("multiple nodes with path="+path);
                }
                else // length == 0
                {
                    templatingContext.put("result", "no_node_with_given_path");
                    return;
                }
            }
            
            RootResource searchRoot = searchService.getSearchRoot(coralSession, site);
            searchRoot.setSearchNode(searchNode);
            searchRoot.update();
        }
        catch(SearchException e)
        {
            throw new ProcessingException("cannot get search root", e);
        }
        catch(StructureException e)
        {
            throw new ProcessingException("cannot get search node", e);
        }
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.search.configure");
    }
}
