package net.cyklotron.cms.modules.components.structure;

import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationConfiguration;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

/**
 * Basic tree navigation component.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: TreeNavigation.java,v 1.3 2005-03-08 10:54:55 pablo Exp $
 */

public class TreeNavigation extends CacheableNavigation
{
    public TreeNavigation(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        TableStateManager tableStateManager,SiteService siteService, StructureService structureService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder,
                        tableStateManager,siteService, structureService);
        
    }
    
    protected void setConfigParameters(TableState state, NavigationConfiguration naviConf,
                                      NavigationNodeResource currentNode)
    {
        // - - - - - - - NAVIGATION TYPE parameters

        // PARAMETER: ViewType - this one is not configurable for Tree navigation
        state.setTreeView(true);

        // PARAMETER: expansionType - depends on a type of a navigation
        state.clearExpanded();

        // TODO: maybe we should add root node expanding - there is a problem with tree navigations
        //      when they have a root node defined somewhere else than is current node 
        state.setExpanded(currentNode.getIdString());

        List expandedList = currentNode.getParentNavigationNodes(true);
        for(int i = 0; i < expandedList.size(); i++)
        {
            state.setExpanded(((NavigationNodeResource)expandedList.get(i)).getIdString());
        }
    }
}
