package net.cyklotron.cms.modules.components.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.search.RootResource;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Search site component.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchSite.java,v 1.4 2005-04-15 06:49:53 pablo Exp $
 */
public class SearchSite
    extends SkinableCMSComponent
{
    /** search service */
    protected SearchService searchService;

    public SearchSite(org.objectledge.context.Context context, Logger logger,
        Templating templating, CmsDataFactory cmsDataFactory, SkinService skinService,
        MVCFinder mvcFinder, SearchService searchService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.searchService = searchService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        if(getSite(context) == null)
        {
            componentError(context, "No site selected");
            return;
        }
        SiteResource site = getSite(context);

        // get search node for redirecting the search results view
        try
        {
            RootResource searchRoot = searchService.getSearchRoot(coralSession, site);
            NavigationNodeResource searchNode = searchRoot.getSearchNode();
            if(searchNode == null)
            {
                componentError(context, "no search node defined for site "
                    +site.getName());
                return;
            }
            else if(searchNode.getSite() != site)
            {
                componentError(context, "search node for site "+site.getName()
                    +" defined in site "+searchNode.getSite().getName());
                return;
            }
            templatingContext.put("search_node", searchNode);
        }
        catch(SearchException e)
        {
            componentError(context, "cannot get search root for site "+site.getName());
            return;
        }

        // get index pools available for this site
        Resource poolsParent = null;
        try
        {
            poolsParent = searchService.getPoolsRoot(coralSession, site);
        }
        catch(SearchException e)
        {
            componentError(context, "cannot get pools root for site "+site.getName());
            return;
        }

        Parameters componentConfig = getConfiguration();
        String[] poolNames = componentConfig.getStrings("poolNames");

        List pools = new ArrayList();
        for(int i = 0; i < poolNames.length; i++)
        {
            String poolName = poolNames[i];
            Resource[] ress = coralSession.getStore().getResource(poolsParent, poolName);
            if(ress.length == 1)
            {
                // TODO: maybe we should check the resource class
                pools.add(ress[0]);
            }
            else if(ress.length > 1)
            {
                componentError(context, "multiple pools named "+poolName);
                return;
            }
        }
        Collections.sort(pools, new NameComparator(i18nContext.getLocale()));
        templatingContext.put("pools",pools);
    }
}
