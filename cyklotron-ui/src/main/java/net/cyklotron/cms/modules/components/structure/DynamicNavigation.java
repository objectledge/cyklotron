package net.cyklotron.cms.modules.components.structure;

import java.util.List;

import org.objectledge.table.TableConstants;
import org.objectledge.table.TableState;

import net.cyklotron.cms.structure.NavigationConfiguration;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Dynamic navigation component - behaves just like any other ordinary
 * tree or list view. For instance it may set ViewType dynamically.
 * <p><i>WARNING</i> - This component is not really suitable for site
 * visitor layouts. It is more suitable for administrators, because
 * it does not support caching.</p>
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DynamicNavigation.java,v 1.1 2005-01-24 04:35:20 pablo Exp $
 */

public class DynamicNavigation extends BaseNavigation
{
    protected void setConfigParameters(TableState state, NavigationConfiguration naviConf,
                                      NavigationNodeResource currentNode)
    {
        // - - - - - - - NAVIGATION TYPE parameters

        // PARAMETER: ViewType - this one is also dynamic for this type of navigation
        if(state.isNew())
        {
            state.setViewType(TableConstants.VIEW_AS_TREE);
        }

        // PARAMETER: expansionType - depends on a type of a navigation
        state.setExpanded(currentNode.getIdString());

        List expandedList = currentNode.getParentNavigationNodes(true);
        for(int i = 0; i < expandedList.size(); i++)
        {
            state.setExpanded(((NavigationNodeResource)expandedList.get(i)).getIdString());
        }
    }
    
    protected String getTableStateName(NavigationNodeResource currentNode, String instanceName)
    {
        return super.getTableStateName(currentNode, instanceName)+instanceName;
    }
}
