package net.cyklotron.cms.category.components;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;

import org.objectledge.context.Context;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;

/**
 * Provides default parameter values for resource list configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HoldingResourceListConfiguration.java,v 1.3 2005-01-19 12:33:01 pablo Exp $
 */
public class HoldingResourceListConfiguration
extends DocumentResourceListConfiguration
{
    public static String KEY = "cms.category.prioritized_resource_list.configuration";

    public static ResourceListConfiguration getConfig(Context context)
        throws ProcessingException
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        HoldingResourceListConfiguration currentConfig = (HoldingResourceListConfiguration)
            httpContext.getSessionAttribute(KEY);
        if(currentConfig == null)
        {
            CmsData cmsData = CmsDataFactory.getCmsDataIfExists(context);
            if(cmsData == null)
            {
                currentConfig = new HoldingResourceListConfiguration(new Date());
            }
            else
            {
                currentConfig = new HoldingResourceListConfiguration(cmsData.getDate());
            }
            httpContext.setSessionAttribute(KEY, currentConfig);
        }
        return currentConfig;
    }

    public static void removeConfig(Context context)
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        httpContext.removeSessionAttribute(KEY);
    }

	public HoldingResourceListConfiguration(Date currentDate)
	{
		super();
		this.currentDate = currentDate;
	}

	private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
    protected Date currentDate;
	protected Map weightMap = new HashMap(512);
	protected Map dateMap = new HashMap(512);

	public static String HELD_RESOURCES_PARAM_KEY = "heldResources";

	/** Short initialisation used during component preparation. */
	public void shortInit(Parameters componentConfig)
	{
		String[] heldRes = componentConfig.getStrings(HELD_RESOURCES_PARAM_KEY);
		initMaps(heldRes, currentDate);
		super.shortInit(componentConfig);
	}

	/** Initialisation used during component configuration. Initialises resource selection state. */
	public void init(Parameters componentConfig)
	{
		String[] heldRes = componentConfig.getStrings(HELD_RESOURCES_PARAM_KEY);
		initMaps(heldRes, null);
		super.init(componentConfig);
	}

	public static final String RESOURCE_VISIBLE_PARAM = "resource-visible"; 

	/** Updates the config after a form post during configuration. */
	public void update(CmsData cmsData, Parameters params)
	    throws ProcessingException
	{
		currentDate = cmsData.getDate();
		// get visible resources id's
		if(params.isDefined(RESOURCE_VISIBLE_PARAM))
		{
			Set visibleResourceIds = ResourceSelectionState.getIds(params, RESOURCE_VISIBLE_PARAM);
			// remove visibleParamName because it was already used
			//    - this is for actions it will block another state modifications for current request
			params.remove(RESOURCE_VISIBLE_PARAM);

			for(Iterator i=visibleResourceIds.iterator(); i.hasNext();)
			{
				// get id
				Long id = (Long)i.next();
				
				// take care of weight
				int weight = params.getInt("resource-weight-"+id.toString(),-1);
				weightMap.put(id, new Integer(weight));
				
				// take care of date if there is a need
				long time = params.getLong("resource-date-"+id.toString(),-1L);
				if(time != -1L)
				{
					dateMap.put(id, new Date(time));
				}
			}
		}
		super.update(cmsData, params);
	}

	public Set removeHeldResources(Date date)
	{
		Set removedIds = new HashSet();
		for (Iterator iter = dateMap.keySet().iterator(); iter.hasNext();)
        {
			Long id = (Long) iter.next();
            Date resDate = (Date) dateMap.get(id);
            if(resDate.before(date))
            {
            	removedIds.add(id);
            }
        }
		for (Iterator iter = removedIds.iterator(); iter.hasNext();)
		{
			Long id = (Long) iter.next();
			dateMap.remove(id);
			weightMap.remove(id);
		}
		return removedIds;
	}

    // getters -------------------------------------------------------------------------------------

	public String[] getHeldResources()
	{
		ArrayList params = new ArrayList(dateMap.size());
		
		StringBuffer buf = new StringBuffer(100);
		int i = 0; 
		for (Iterator iter = dateMap.keySet().iterator(); iter.hasNext(); i++)
		{
			Long id = (Long) iter.next();
			Date resDate = (Date) dateMap.get(id);
			Integer weight = (Integer) weightMap.get(id);
			if(weight.intValue() != -1)
			{
				buf.setLength(0);
				buf.append(id.toString()).append(',');
				buf.append(dateFormat.format(resDate)).append(',');
				buf.append(weight.toString());

				params.add(buf.toString());
			}
		}
		return (String[]) params.toArray(new String[params.size()]);
	}

	// public API ----------------------------------------------------------------------------------

	public boolean hasResource(Resource res)
	{
        return weightMap.containsKey(res.getIdObject());
	}

	public int getWeight(Resource res)
	{
        Integer weight = (Integer)weightMap.get(res.getIdObject());
        if(weight != null)
        {
            return weight.intValue();
        }
        return -1;
	}
    
	public Date getValidityDate(Resource res)
	{
        return (Date)dateMap.get(res.getIdObject());
	}

	public String getValidityDateAsString(Resource res)
	{
		Date date = getValidityDate(res);
		if(date != null)
		{
			return dateFormat.format(getValidityDate(res));
		}
		else
		{
			return "";
		}
	}

	
	// implementation -----------------------------------------------------------------------------
	
	protected void initMaps(String[] heldRes, Date currentDate1)
	{
		weightMap.clear();
		dateMap.clear();

		String[] record = new String[3];
		for (int i = 0; i < heldRes.length; i++)
		{
			StringTokenizer tokenizer = new StringTokenizer(heldRes[i], ",");
			for (int j = 0; tokenizer.hasMoreTokens(); j++)
			{
				record[j] = tokenizer.nextToken();
			}
			try
			{
				Long id = new Long(record[0]);
				Date validityDate = dateFormat.parse(record[1]);
				Integer weight = new Integer(record[2]);
				if(currentDate1 == null || currentDate1.before(validityDate))
				{
					weightMap.put(id, weight);
					dateMap.put(id, validityDate);
				}
			}
			catch (ParseException e)
			{
				// ignore bad records
			}
			catch (NumberFormatException e)
			{
				// ignore bad records
			}
		}
	}
}
