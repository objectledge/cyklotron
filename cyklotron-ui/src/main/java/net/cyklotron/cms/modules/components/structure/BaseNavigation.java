package net.cyklotron.cms.modules.components.structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsComponentData;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationConfiguration;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.table.NavigationTableModel;
import net.cyklotron.cms.util.ProtectedValidityViewFilter;

/**
 * Base class for Cyklotron CMS navigations.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseNavigation.java,v 1.7 2005-12-28 16:31:36 rafal Exp $
 */

public abstract class BaseNavigation extends SkinableCMSComponent
{
    /** table service */
    protected TableStateManager tableStateManager;

    /** structure service */
    protected StructureService structureService;

    public BaseNavigation(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        TableStateManager tableStateManager, StructureService structureService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.tableStateManager = tableStateManager;
        this.structureService = structureService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        CmsComponentData componentData = cmsData.getComponent();
        Parameters componentConfig = componentData.getConfiguration();

        NavigationConfiguration naviConf = new NavigationConfiguration(componentConfig);

        // get currently selected node
        NavigationNodeResource currentNode = cmsData.getNode();
        
        if(currentNode == null)
        {
            componentError(context, "Not within a site");
            return;
        }
        
        // - - - - 0. Set navigation header
        // setup header
        templatingContext.put("header", naviConf.getHeader());

        // - - - - 1. Get navigation root
        NavigationNodeResource naviRoot = getNavigationRoot(coralSession, cmsData, context, naviConf);
        if(naviRoot == null)
        {
            // WARN: An error occured;
            return;
        }
        
        // - - - - 2. Set state parameters

        // State parameters are being set either once - from configuration
        // - or every time navigation is being rendered - from runtime parameters.
        // Available parameters are:
        //   - visible levels number                      (configuration only)
        //   - root visibility                            (configuration only)
        //   - sort column name                           (configuration only)
        //   - filters                                    (configuration only)
        //   - root id                                    (runtime or configuration)
        //   - nodes expansion state upon navigation type (runtime)

        // TODO: cache rendered navigation

        String instanceName = componentData.getInstanceName();
        TableState state = tableStateManager.getState(context, getTableStateName(currentNode, instanceName));

        // multiple selection is used to visualise path from site's root (home page) to current node

        // - - - - - - - CONFIGURATION parameters
        state.setShowRoot(naviConf.getShowRoot());
        state.setMaxVisibleDepth(naviConf.getLevels());
        state.setSortColumnName(naviConf.getSortColumn());
        state.setAscSort(naviConf.getSortDir());

        // PARAMETER: Filters - CONFIGURATION
        /*
         * TODO: Filtr w konfiguracji zapisany bylby za pomoca 2 wartosci:
         *      - nazwa filtru
         *      - string definiujacy jego konfiguracje
         *
         *      StructureService (?) powinien umiec zwracac kolekcje filtrow
         *      na podstawie takiej konfiguracji.
         */
        
        // - - - - - - - RUNTIME parameters
        state.setRootId(naviRoot.getIdString());
        
        Set selectedNodes = new HashSet();
        selectedNodes.add(currentNode);
        List selectedList = currentNode.getParentNavigationNodes(true);
        selectedNodes.addAll(selectedList);
        templatingContext.put("pathNodes", selectedNodes);

        // - - - - - - - SUBCLASS parameters
        setConfigParameters(state, naviConf, currentNode);

        // - - - - 3. create model and TableTool and display :)
        TableModel model = getTableModel(coralSession, i18nContext, naviConf, currentNode, naviRoot);
        try
        {
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedValidityViewFilter(coralSession, cmsData, cmsData.getUserData().getSubject()));
            TableTool helper = new TableTool(state, filters,  model);
            templatingContext.put("table",helper);
        }
        catch(TableException e)
        {
            throw new ProcessingException("Cannot create TableTool", e);
        }
    }

    protected TableModel getTableModel(CoralSession coralSession, I18nContext i18nContext, NavigationConfiguration naviConf,
                                               NavigationNodeResource currentNode, NavigationNodeResource naviRoot)
        throws ProcessingException
    {
        return new NavigationTableModel(coralSession, i18nContext.getLocale());
    }

    protected abstract void setConfigParameters(TableState state, NavigationConfiguration naviConf,
                                               NavigationNodeResource currentNode);

    protected String getTableStateName(NavigationNodeResource currentNode, String instanceName)
    {
        return "net.cyklotron.cms.structure.navigation";
    }

    /** Gets navigation root. */
    private NavigationNodeResource getNavigationRoot(CoralSession coralSession, CmsData cmsData, Context context,
        NavigationConfiguration naviConf)
    throws ProcessingException
    {
        NavigationNodeResource currentNode = cmsData.getNode();
        NavigationNodeResource naviRoot = null;

        // CASE A: Navigation root selected by it's path.
        //      This is a static navigation - ie. not dependent on currentNode.
        //      It may contain current node, but may also not.
        if(naviConf.getRootConfigType().equals("rootPath"))
        {
            if(naviConf.getRootPath().equals(""))
            {
                componentError(context, "Selected navigation's root path is empty");
                return null;
            }
            
            Resource homePageParent = cmsData.getHomePage().getParent();
            Resource[] temp = coralSession.getStore()
                            .getResourceByPath(homePageParent.getPath()+naviConf.getRootPath());
            if(temp.length == 1)
            {
                naviRoot = (NavigationNodeResource)(temp[0]);
            }
            else
            {
                componentError(context, "Could not find a navigation root node with path '"+
                naviConf.getRootPath()+"'");
                return null;
            }
        }
        // CASE B: Navigation root selected relatively to current node by selecting it's level.
        //      This navigation changes it's root dynamically depending on a
        //      selected node (currentNode). It is suitable for building layout
        //      blocks like "navigation of site level 1", "navigation of site level 2" etc.
        else if(naviConf.getRootConfigType().equals("rootLevel"))
        {
            if(naviConf.getRootLevel() < 0)
            {
                componentError(context, "Absolute root level cannot be lower then zero");
                return null;
            }
            
            // current node's level sanity check - this is only suitable for
            // abslutely selected root node level
            int currentNodeLevel = currentNode.getLevel();
            if(currentNodeLevel < naviConf.getRootLevel())
            {
                componentError(context, "Selected navigation node is on a higher level than navigation's root");
                return null;
            }

            // get the root node
            Resource node = currentNode;
            while(node != null && node instanceof NavigationNodeResource)
            {
                if(((NavigationNodeResource)node).getLevel() == naviConf.getRootLevel())
                {
                    break;
                }
                node = node.getParent();
            }

            if(node == null || !(node instanceof NavigationNodeResource))
            {
                componentError(context, "Could not find a navigation root node");
                return null;
            }

            naviRoot = (NavigationNodeResource)node;
        }
        // CASE C: Navigation root selected relatively to current node by selecting it's RELATIVE
        //      level. This navigation changes it's root dynamically depending on a
        //      selected node (currentNode). It is suitable for building layout
        //      blocks like "parent level navigation", "current level navigation" etc.
        else if(naviConf.getRootConfigType().equals("relativeRootLevel"))
        {
            if(naviConf.getRelativeRootLevel() > 0)
            {
                componentError(context, "Relative root level cannot be higher then zero");
                return null;
            }
            
            // get the root node
            Resource node = currentNode;
            int i = 0;
            while(node != null && node instanceof NavigationNodeResource)
            {
                if(i == naviConf.getRelativeRootLevel())
                {
                    break;
                }
                i--;
                node = node.getParent();
            }

            if(node == null || !(node instanceof NavigationNodeResource)
               || i != naviConf.getRelativeRootLevel())
            {
                componentError(context, "Could not find a navigation root node");
                return null;
            }

            naviRoot = (NavigationNodeResource)node;
        }
        else
        {
            componentError(context, "Unknown navigation root configuration type '"+naviConf.getRootConfigType()+"'");
            return null;
        }
        
        return naviRoot;
    }
}
