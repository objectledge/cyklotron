package net.cyklotron.cms.modules.actions.search;

import net.labeo.Labeo;
import net.labeo.services.ServiceBroker;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.search.RootResource;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;

/**
 * Updates search application configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateConfiguration.java,v 1.1 2005-01-24 04:34:07 pablo Exp $
 */
public class UpdateConfiguration extends BaseSearchAction
{
    /** structure service */
    protected StructureService structureService;

    public UpdateConfiguration()
    {
        ServiceBroker broker = Labeo.getBroker();
        structureService = (StructureService)broker.getService(StructureService.SERVICE_NAME);
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
    throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        
        SiteResource site = getSite(context);
        try 
        {
            String path = parameters.get("searchNodePath","");
            NavigationNodeResource searchNode = null;
            if(!path.equals(""))
            {
                Resource parent = structureService.getRootNode(site).getParent();
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
            
            RootResource searchRoot = searchService.getSearchRoot(site);
            searchRoot.setSearchNode(searchNode);
            searchRoot.update(subject);
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

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.search.configure");
    }
}
