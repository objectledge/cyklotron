package net.cyklotron.cms.modules.components.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationConfiguration;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

/**
 * List navigation component, which shows the list of documents from the whole site/sectoin
 * structure.
 * Number of visible documents is configured.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ListNavigation.java,v 1.4 2005-03-08 10:54:55 pablo Exp $
 */
public class ListNavigation extends CacheableNavigation
{
    
    
    public ListNavigation(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        TableStateManager tableStateManager, StructureService structureService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder,
                        tableStateManager, structureService);
        
    }
    protected void setConfigParameters(TableState state, NavigationConfiguration naviConf,
                                      NavigationNodeResource currentNode)
    {
        // - - - - - - - CONFIGURATION parameters
        state.setCurrentPage(1);
        state.setPageSize(naviConf.getMaxNodesNumber());
        
        // PARAMETER: ViewType - this one has no sense for List navigation
        state.setTreeView(false);
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
