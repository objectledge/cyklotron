package net.cyklotron.cms.modules.components.structure;

import org.objectledge.table.TableConstants;
import org.objectledge.table.TableState;

import net.cyklotron.cms.structure.NavigationConfiguration;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * List navigation component, which shows the list of documents from the whole site/sectoin
 * structure.
 * Number of visible documents is configured.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ListNavigation.java,v 1.1 2005-01-24 04:35:20 pablo Exp $
 */
public class ListNavigation extends CacheableNavigation
{
    protected void setConfigParameters(TableState state, NavigationConfiguration naviConf,
                                      NavigationNodeResource currentNode)
    {
        // - - - - - - - CONFIGURATION parameters
        state.setCurrentPage(1);
        state.setPageSize(naviConf.getMaxNodesNumber());
        
        // PARAMETER: ViewType - this one has no sense for List navigation
        state.setViewType(TableConstants.VIEW_AS_LIST);
    }
    
    /**
     * ListNavigation must have a different name to avoid state clash because of paging used in this
     * navigation (paging is not used in other navigations).
     */
    protected String getTableStateName(NavigationNodeResource currentNode, String instanceName)
    {
        return super.getTableStateName(currentNode, instanceName)+".list";
    }
}
