/*
 */
package net.cyklotron.cms.modules.actions.forum;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.generic.NodeResourceImpl;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.forum.ForumListener;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class InstallForum extends BaseCMSAction
{
    /* 
     * (overriden)
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException, NotFoundException
    {
        try
        {
            coralSession.getSchema().getResourceClass("cms.forum.node");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("forum data model not installed", e);
        }
        
        Resource[] sites = coralSession.getStore().
            getResourceByPath("/cms/sites/*");
        Resource p;    
        Resource[] res;
        // make sure applications nodes exist
        for (int i = 0; i < sites.length; i++)
        {
            p = sites[i];
            res = coralSession.getStore().getResource(p, "applications");
            if(res.length == 0)
            {
                try
                {
                    NodeResourceImpl.createNodeResource(coralSession, "applications",
                        p, coralSession.getUserSubject());
                }
                catch(ValueRequiredException e)
                {
                    throw new ProcessingException("unexpected exception", e);
                }
            }
            else if(res.length > 1)
            {
                throw new ProcessingException("multiple applications nodes in "+sites[i].getPath());
            }
        }
        // check for existing forums
        for (int i = 0; i < sites.length; i++)
        {
            p = sites[i];
            res = coralSession.getStore().getResource(p, "applications");
            p = res[0];
            res = coralSession.getStore().getResource(p, "forum");
            if(res.length > 0)
            {
                throw new ProcessingException("forum already installed in "+sites[i].getPath());
            }
        }         
        ForumListener helper = new ForumListener();
        // create forum data
        for (int i = 0; i < sites.length; i++)
        {
        	SiteResource site = (SiteResource)sites[i];
        	if(!site.getTemplate())
        	{
        	    helper.createSite(null, sites[i].getName());
        	}
        }        
    }

    /* 
     * (overriden)
     */
    public boolean checkAccess(RunData data) throws ProcessingException
    {
        return checkAdministrator(context, coralSession);
    }
}
