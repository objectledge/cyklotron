package net.cyklotron.cms.modules.actions.fixes;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.generic.NodeResourceImpl;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.link.LinkListener;
import net.cyklotron.cms.banner.BannerListener;
import net.cyklotron.cms.poll.PollListener;
import net.cyklotron.cms.search.SearchListener;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.aggregation.AggregationListener;
import net.cyklotron.cms.modules.actions.BaseCMSAction;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class InstallAll
    extends UninstallAll
{
    /* 
     * (overriden)
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) 
        throws ProcessingException
    {
        try
        {
            Subject subject = coralSession.getUserSubject();
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
            LinkListener linkListener = new LinkListener();
            PollListener pollListener = new PollListener();
            BannerListener bannerListener = new BannerListener();
            SearchListener searchListener = new SearchListener();
            AggregationListener aggregationListener = new AggregationListener();
            for (int i = 0; i < sites.length; i++)
            {
                SiteResource site = (SiteResource)sites[i];
                Role admin = site.getAdministrator();
                if(admin == null)
                {
                    Role[] admins = coralSession.getSecurity().getRole("cms.site.administrator."+site.getName());
                    if(admins.length == 1)
                    {
                        admin = admins[0];
                    }
                    else
                    {
                        admin = coralSession.getSecurity().createRole("cms.site.administrator."+site.getName());
                    }
                }
                site.setAdministrator(admin);
                site.update(subject);
                if(!site.getTemplate())
                {
                    linkListener.createSite(null, sites[i].getName());
                    pollListener.createSite(null, sites[i].getName());
                    bannerListener.createSite(null, sites[i].getName());
                    searchListener.createSite(null, sites[i].getName());
                    aggregationListener.createSite(null, sites[i].getName());
                }
            }        
        }
        catch(Exception e)
        {
            log.error("Error on install: try again!!!",e);
            execute(data);
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
