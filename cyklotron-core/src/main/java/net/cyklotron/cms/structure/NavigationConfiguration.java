package net.cyklotron.cms.structure;

import net.labeo.services.table.TableConstants;
import net.labeo.util.configuration.Configuration;

/**
 * Provides default parameter values for navigation's configurations.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: NavigationConfiguration.java,v 1.1 2005-01-12 20:44:33 pablo Exp $
 */
public class NavigationConfiguration
{
    /** Configured component's class name, as stored in configuration.
     * Example: <code>structure,BreadCrumbNavigation</code>.
     */
    private String type;

    private String header;
    
    private String rootConfigType;
    /**
     * <code>relativeRootLevel</code> is used to calculate rootId for the navigation by
     * moving upwards from currently selected node.
     */
    private int relativeRootLevel;
    /**
     * <code>rootLevel</code> is used to calculate rootId for the navigation by
     * selecting a node which lies on an abslutely selected level on a path from structure root
     * to currently selected node.
     */
    private int rootLevel;
    /**
     * <code>rootPath</code> is used to calculate rootId for the navigation by
     * selecting a node has a selected path.
     */
    private String rootPath;
    
    private int levels;
    private boolean showRoot;
    private String sortColumn;
    private int sortDir;
    private int viewType;
    private int maxNodesNumber;

    public NavigationConfiguration(Configuration componentConfig)
    {
        type = componentConfig.get("class").asString("");

        header = componentConfig.get("header").asString("");
        
        rootConfigType = componentConfig.get("rootConfigType").asString("rootLevel");
        // relativeRootLevel or rootLevel or rootPath - (rootId is calculated on runtime)
        relativeRootLevel = componentConfig.get("relativeRootLevel").asInt(0);
        rootLevel = componentConfig.get("rootLevel").asInt(0);
        rootPath = componentConfig.get("rootPath").asString("");
        
        // number of visible levels, zero is 'no limits'
        levels = componentConfig.get("levels").asInt(0);
        // show or hide root
        showRoot = componentConfig.get("showRoot").asBoolean(true);
        // Sorting
        sortColumn = componentConfig.get("naviSortColumn").asString("sequence");
        sortDir = componentConfig.get("naviSortDir").asInt(TableConstants.SORT_ASC);
        // PARAMETER: Filters

        viewType = componentConfig.get("viewType").asInt(TableConstants.VIEW_AS_TREE);
        maxNodesNumber = componentConfig.get("maxNodesNumber").asInt(0);
    }


    /** Getter for property type.
     * @return Value of property type.
     *
     */
    public String getType()
    {
        return type;
    }

    /** Getter for property header.
     * @return Value of property header.
     *
     */
    public String getHeader()
    {
        return header;
    }
    
    /** Getter for property rootConfigType.
     * @return Value of property rootConfigType.
     *
     */
    public java.lang.String getRootConfigType()
    {
        return rootConfigType;
    }
    
    /** Getter for property relativeRootLevel.
     * @return Value of property relativeRootLevel.
     *
     */
    public int getRelativeRootLevel()
    {
        return relativeRootLevel;
    }
    
    /** Getter for property rootLevel.
     * @return Value of property rootLevel.
     *
     */
    public int getRootLevel()
    {
        return rootLevel;
    }

    /** Getter for property rootPath.
     * @return Value of property rootPath.
     *
     */
    public String getRootPath()
    {
        return rootPath;
    }

    /** Getter for property sortColumn.
     * @return Value of property sortColumn.
     *
     */
    public String getSortColumn()
    {
        return sortColumn;
    }

    /** Getter for property sortDir.
     * @return Value of property sortDir.
     *
     */
    public int getSortDir()
    {
        return sortDir;
    }

    /** Getter for property showRoot.
     * @return Value of property showRoot.
     *
     */
    public boolean getShowRoot()
    {
        return showRoot;
    }

    /** Getter for property levels.
     * @return Value of property levels.
     *
     */
    public int getLevels()
    {
        return levels;
    }

    /** Getter for property viewType.
     * @return Value of property viewType.
     *
     */
    public int getViewType()
    {
        return viewType;
    }
    
    /** Getter for property maxNodesNumber.
     * @return Value of property maxNodesNumber.
     *
     */
    public int getMaxNodesNumber()
    {
        return maxNodesNumber;
    }
}
