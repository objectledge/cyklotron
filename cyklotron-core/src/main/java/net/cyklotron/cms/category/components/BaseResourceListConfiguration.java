package net.cyklotron.cms.category.components;

import net.labeo.services.resource.ResourceService;
import net.labeo.services.table.TableConstants;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.ParameterContainer;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * This is a base class for classes that provide default parameter values for categorized
 * resource lists' configurations.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseResourceListConfiguration.java,v 1.1 2005-01-12 20:45:00 pablo Exp $
 */
public abstract class BaseResourceListConfiguration
{
    /** The header string for this component. */
    private String header;
    /** The name of a sorting column for resource list. */
    private String sortColumn;
    /** The direction of sorting for resource list. */
    private int sortDir;
    /** Maximal number of resources visible in list. */
    private int maxResNumber;
    /** Number of seconds the calculated resource list will be kept in the cache. */
    private int cacheInterval;

    
    /** <code>true</code> if the config object was created during current request. */
    protected boolean newConfig;

    public BaseResourceListConfiguration()
    {
        newConfig = true;
    }

    public boolean isNew()
    {
        return newConfig;
    }

	/** Short initialisation used during component preparation. */
	public void shortInit(Configuration componentConfig)
	{
		setParams(componentConfig);
		// config was modified
		newConfig = false;
	}

    /** Initialisation used during component configuration. */
    public void init(Configuration componentConfig, ResourceService resourceService)
    {
        setParams(componentConfig);
        // config was modified
        newConfig = false;
    }

    /** Updates the config after a form post during configuration. */
    public void update(RunData data)
    throws ProcessingException
    {
        ParameterContainer params = data.getParameters();
        setParams(params);
        // config was modified
        newConfig = false;
    }

    /** Thi one sets basic parameters from parameter container - either from component config or
     * current request, MUST be called before any other modification in init, shortInit or update
     */
    protected void setParams(ParameterContainer params)
    {
        header = params.get("header").asString("");

        sortColumn = params.get("listSortColumn").asString("index.title");
        sortDir = params.get("listSortDir").asInt(TableConstants.SORT_ASC);

        maxResNumber = params.get("maxResNumber").asInt(0);
        cacheInterval = params.get("cacheInterval").asInt(0);
    }
    
    // getters /////////////////////////////////////////////////////////////////////////////////////

    public String getHeader()
    {
        return header;
    }

    public String getSortColumn()
    {
        return sortColumn;
    }

    public int getSortDir()
    {
        return sortDir;
    }

    /** Returns a maximal number of resources visible in resource list.
     * @return Value of property maxResNumber.
     */
    public int getMaxResNumber()
    {
        return maxResNumber;
    }

    public int getCacheInterval()
    {
        return cacheInterval;
    }
}
