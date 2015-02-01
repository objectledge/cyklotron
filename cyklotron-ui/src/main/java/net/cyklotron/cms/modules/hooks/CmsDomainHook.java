package net.cyklotron.cms.modules.hooks;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;

import net.cyklotron.cms.NodeNotFoundException;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;

public class CmsDomainHook
    implements Valve
{
    /** site service */
    private SiteService siteService;

    private Context context;

    private PreferencesService preferencesService;

    public CmsDomainHook(SiteService siteService, PreferencesService preferencesService,
        Context context)
    {
        this.siteService = siteService;
        this.preferencesService = preferencesService;
        this.context = context;
    }

    /**
     * @inheritDoc{
     */
    public void process(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        try
        {
            // if /view or /x is defined, do not interfere
            final String serverName = httpContext.getRequest().getServerName();
            if(!parameters.isDefined("view") && !parameters.isDefined("x"))
            {
                // if / of the server is requested, check if a corresponding virtual host is defined
                final String servletPath = httpContext.getRequest().getServletPath();
                if(servletPath == null || servletPath.equals("/"))
                {
                    NavigationNodeResource homePage = siteService.getDefaultNode(coralSession,
                        serverName);
                    // redirect to virtual host's home page
                    if(homePage != null)
                    {
                        parameters.set("x", homePage.getIdString());
                        parameters.set("app", "cms");
                    }
                    else
                    {
                        throw new NodeNotFoundException("Page not found");
                    }
                }
                // request contains unrecognized path info (no /x of /view present)
                else
                {
                    SiteResource site = siteService.getSiteByAlias(coralSession, serverName);
                    if(site == null)
                    {
                        site = getDefaultSite();
                    }
                    parameters.set("site_id", site.getIdString());
                    throw new NodeNotFoundException("Page not found");
                }
            }
        }
        catch(SiteException e)
        {
            throw new ProcessingException(e);
        }
    }

    private SiteResource getDefaultSite()
        throws SiteException
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        Parameters systemPreferences = preferencesService.getSystemPreferences(coralSession);
        String globalComponentsDataSiteName = systemPreferences.get("globalComponentsData", null);
        return globalComponentsDataSiteName != null ? siteService.getSite(coralSession,
            globalComponentsDataSiteName) : null;
    }
}
