package net.cyklotron.cms.modules.components.structure;

import java.util.List;

import org.objectledge.table.TableConstants;
import org.objectledge.table.TableState;

import net.cyklotron.cms.structure.NavigationConfiguration;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Basic tree navigation component.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: TreeNavigation.java,v 1.1 2005-01-24 04:35:20 pablo Exp $
 */

public class TreeNavigation extends CacheableNavigation
{
    protected void setConfigParameters(TableState state, NavigationConfiguration naviConf,
                                      NavigationNodeResource currentNode)
    {
        // - - - - - - - NAVIGATION TYPE parameters

        // PARAMETER: ViewType - this one is not configurable for Tree navigation
        state.setViewType(TableConstants.VIEW_AS_TREE);

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
