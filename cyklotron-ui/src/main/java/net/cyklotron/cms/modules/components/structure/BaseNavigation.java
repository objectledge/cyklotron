package net.cyklotron.cms.modules.components.structure;

import java.util.ArrayList;
import java.util.List;

import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Resource;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsComponentData;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.structure.NavigationConfiguration;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.table.NavigationTableModel;
import net.cyklotron.cms.util.ProtectedValidityViewFilter;

/**
 * Base class for Cyklotron CMS navigations.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseNavigation.java,v 1.2 2005-01-25 11:24:27 pablo Exp $
 */

public abstract class BaseNavigation extends SkinableCMSComponent
{
    /** table service */
    protected TableService tableService;

    /** structure service */
    protected StructureService structureService;

    public BaseNavigation()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME))
                                .getFacility(StructureService.LOGGING_FACILITY);
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        structureService = (StructureService)broker.getService(StructureService.SERVICE_NAME);
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
        NavigationNodeResource naviRoot = getNavigationRoot(cmsData, context, naviConf);
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
        TableState state = tableService.getGlobalState(data, getTableStateName(currentNode, instanceName));

        // multiple selection is used to visualise path from site's root (home page) to current node
        state.setMultiSelect(true);

        // - - - - - - - CONFIGURATION parameters
        state.setShowRoot(naviConf.getShowRoot());
        state.setMaxVisibleDepth(naviConf.getLevels());
        state.setSortColumnName(naviConf.getSortColumn());
        state.setSortDir(naviConf.getSortDir());

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

        state.clearSelected();
        state.setSelected(currentNode.getIdString());
        List selectedList = currentNode.getParentNavigationNodes(true);
        for(int i = 0; i < selectedList.size(); i++)
        {
            state.setSelected(((NavigationNodeResource)selectedList.get(i)).getIdString());
        }

        // - - - - - - - SUBCLASS parameters
        setConfigParameters(state, naviConf, currentNode);

        // - - - - 3. create model and TableTool and display :)
        TableModel model = getTableModel(data, naviConf, currentNode);
        try
        {
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedValidityViewFilter(cmsData, cmsData.getUserData().getSubject()));
            TableTool helper = new TableTool(state, model, filters);
            templatingContext.put("table",helper);
        }
        catch(TableException e)
        {
            throw new ProcessingException("Cannot create TableTool", e);
        }
    }

    protected TableModel getTableModel(RunData data, NavigationConfiguration naviConf,
                                               NavigationNodeResource currentNode)
        throws ProcessingException
    {
        return new NavigationTableModel(i18nContext.getLocale()());
    }

    protected abstract void setConfigParameters(TableState state, NavigationConfiguration naviConf,
                                               NavigationNodeResource currentNode);

    protected String getTableStateName(NavigationNodeResource currentNode, String instanceName)
    {
        return "net.cyklotron.cms.structure.navigation";
    }

    /** Gets navigation root. */
    private NavigationNodeResource getNavigationRoot(CmsData cmsData, Context context,
        NavigationConfiguration naviConf)
    throws ProcessingException
    {
        NavigationNodeResource currentNode = cmsData.getNode(context);
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
