package net.cyklotron.cms.modules.views.site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.labeo.Labeo;
import net.labeo.services.authentication.AuthenticationService;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.table.NameComparator;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;

public class PublicSites extends BaseCMSScreen
{
    private SiteService siteService;
    private StructureService structureService;
    private Subject anonymous;

    public PublicSites() throws Exception
    {
        siteService = (SiteService)Labeo.getBroker().getService(SiteService.SERVICE_NAME);
        structureService = (StructureService)Labeo.getBroker().getService(StructureService.SERVICE_NAME);
        AuthenticationService authenticationService = (AuthenticationService)Labeo.getBroker().getService(AuthenticationService.SERVICE_NAME);
        CoralSession coralSession = (CoralSession)Labeo.getBroker().getService(CoralSession.SERVICE_NAME);
        anonymous = coralSession.getSecurity().getSubject(authenticationService.getAnonymousUser().getName());
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) throws ProcessingException
    {
        try
        {
            SiteResource[] sites = siteService.getSites();
            List siteList = new ArrayList(sites.length);
            Map rootNodeMap = new HashMap(sites.length);
            for (int i = 0; i < sites.length; i++)
            {
                try
                {
                    NavigationNodeResource rootNode = structureService.getRootNode(sites[i]);
                    if (rootNode.canView(anonymous))
                    {
                        siteList.add(sites[i]);
                        rootNodeMap.put(sites[i], rootNode);
                    }
                }
                catch(StructureException e)
                {
                    siteList.add(sites[i]);
                }
            }
            Collections.sort(siteList, new NameComparator(i18nContext.getLocale()()));
            templatingContext.put("sites", siteList);
            templatingContext.put("rootNodes", rootNodeMap);
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to list sites", e);
        }
    }
}
