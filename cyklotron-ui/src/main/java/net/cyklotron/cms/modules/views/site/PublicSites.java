package net.cyklotron.cms.modules.views.site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;

public class PublicSites extends BaseCMSScreen
{
    private SiteService siteService;
    private StructureService structureService;
    private UserManager userManager;
    
    public PublicSites(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SiteService siteService,
        StructureService structureService, UserManager userManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.siteService = siteService;
        this.structureService = structureService;
        this.userManager = userManager;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) throws ProcessingException
    {
        try
        {
            SiteResource[] sites = siteService.getSites(coralSession);
            List siteList = new ArrayList(sites.length);
            Map rootNodeMap = new HashMap(sites.length);
            Subject anonymous = coralSession.getSecurity().getSubject(Subject.ANONYMOUS);
            for (int i = 0; i < sites.length; i++)
            {
                try
                {
                    NavigationNodeResource rootNode = structureService.getRootNode(coralSession, sites[i]);

                    if (rootNode.canView(coralSession, anonymous))
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
            Collections.sort(siteList, new NameComparator(i18nContext.getLocale()));
            templatingContext.put("sites", siteList);
            templatingContext.put("rootNodes", rootNodeMap);
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to list sites", e);
        }
    }
}
