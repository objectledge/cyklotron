package net.cyklotron.cms.category.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.resource.util.ResourceSelectionState;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsTool;

/**
 * Utility functions for category query screens and actions.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryUtil.java,v 1.2 2005-01-18 17:38:20 pablo Exp $
 */
public class CategoryQueryUtil
{
	public static String QUERY_PARAM = "query_id";
	public static String QUERY_POOL_PARAM = "query_pool_id";
	
    public static CategoryQueryPoolResource getPool(CoralSession resourceService, RunData data)
        throws ProcessingException
    {
		long id = data.getParameters().get(QUERY_POOL_PARAM).asLong(-1);
		if(id == -1)
		{
			throw new ProcessingException("the parameter '"+QUERY_POOL_PARAM+"' is not defined");
		}

		try
		{
			return CategoryQueryPoolResourceImpl.getCategoryQueryPoolResource(resourceService, id);
		}
		catch(EntityDoesNotExistException e)
		{
			throw new ProcessingException("pool resource does not exist", e);
		}
    }

	public static CategoryQueryResource getQuery(CoralSession resourceService, RunData data)
		throws ProcessingException
	{
		long id = data.getParameters().get(QUERY_PARAM).asLong(-1);
		if(id == -1)
		{
			throw new ProcessingException("the parameter '"+QUERY_PARAM+"' is not defined");
		}

		try
		{
			return CategoryQueryResourceImpl.getCategoryQueryResource(resourceService, id);
		}
		catch(EntityDoesNotExistException e)
		{
			throw new ProcessingException("query resource does not exist", e);
		}
	}

    public static String getNames(CoralSession resourceService, ResourceSelectionState selection, String state)
        throws ProcessingException
    {
        try
        {
            Map requiredMap = selection.getIds(state);
            StringBuffer sb = new StringBuffer();
            Iterator i = requiredMap.keySet().iterator();
            while (i.hasNext())
            {
                Long id = (Long)i.next();
                Resource res = resourceService.getStore().getResource(id.longValue());
                sb.append(res.getName()).append(' ');
            }
            if(sb.length() > 1)
            {
                sb.setLength(sb.length() - 1);
            }
            return sb.toString();
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to extract paths", e);
        }
    }

	/**
	 * Checks if the current user has the specific permission on the current category query resource.
	 */
	public static boolean checkPermission(CoralSession resourceService, RunData data,
										  String permissionName)
		throws ProcessingException
	{
		try
		{
			long id;
			if(data.getParameters().get(QUERY_PARAM).isDefined())
			{
				id = data.getParameters().get(QUERY_PARAM).asLong();
			}
			else if(data.getParameters().get(QUERY_POOL_PARAM).isDefined())
			{
				id = data.getParameters().get(QUERY_POOL_PARAM).asLong();
			}
			else if(data.getParameters().get("site_id").isDefined())
			{
				id = data.getParameters().get("site_id").asLong();
			}
			else
			{
				id = -1;
			}

			Resource res = resourceService.getStore().getResource(id);
			Permission permission = resourceService.getSecurity().
				getUniquePermission(permissionName);
			return CmsTool.getSubject(data).hasPermission(res, permission);
		}
		catch(Exception e)
		{
			throw new ProcessingException("failed to check security", e);
		}
	}

	private static final int S1_BEFORE_PATH = 0;
	private static final int S2_IN_PATH = 1;
    private static final int S6_IN_ID = 6;
	private static final int S3_ESCAPE_IN_PATH = 2;
	private static final int S4_AFTER_PATH = 3;
	private static final int S5_ERROR = 4;

	private static final int AVG_PATH_LENGTH = 30;

	public static String[] splitCategoryIdentifiers(String items)
	{
		if(items == null || items.length() == 0)
		{
			return new String[0];
		}
		List paths = new ArrayList();
		StringBuffer buf = new StringBuffer(items.length() / AVG_PATH_LENGTH);
		int state = S1_BEFORE_PATH;
		for (int i = 0; i < items.length(); i++)
        {
        	char c = items.charAt(i);
        	switch(state)
        	{
        		case S1_BEFORE_PATH:
        			if (c == '\'')
        			{
        				state = S2_IN_PATH;
        			}
                    else if (c >= '0' && c <= '9')
                    {
                        buf.append(c);
                        state = S6_IN_ID;
                    }
        			else if (c != ' ')
        			{
        				state = S5_ERROR;
        			}
        		break;
        		
        		case S2_IN_PATH:
					if (c == '\\')
					{
						state = S3_ESCAPE_IN_PATH;
					}
					else if (c == '\'')
					{
						state = S4_AFTER_PATH;
						paths.add(buf.toString());
						buf.setLength(0);
					}
					else
					{
						buf.append(c);
					}
				break;

        		case S3_ESCAPE_IN_PATH:
					state = S2_IN_PATH;
					buf.append(c);
				break;

        		case S4_AFTER_PATH:
					if (c == ',')
					{
						state = S1_BEFORE_PATH;
					}
					else if (c != ' ')
					{
						state = S5_ERROR;
					}
				break;
				
                case S6_IN_ID:
                    if (c >= '0' && c <= '9')
                    {
                        buf.append(c);
                    }
                    else
                    {
                        paths.add(buf.toString());
                        buf.setLength(0);
                        if (c == ',')
                        {
                            state = S1_BEFORE_PATH;
                        }
                        else if (c == ' ')
                        {
                            state = S4_AFTER_PATH;
                        }
                        else
                        {
                            state = S5_ERROR;
                        }
                    }
                break;

                default:
					state = S5_ERROR;
        	}

			if(state == S5_ERROR)
			{
				break; // WARN: avoid parsing further - this is a silent failure        	
			}
        }

        if(state != S5_ERROR) // add last numeric id
        {
            paths.add(buf.toString());
        }
		
		return (String[]) paths.toArray(new String[paths.size()]);
	}

	public static String joinCategoryIdentifiers(String[] items)
	{
		if(items == null || items.length == 0)
		{
			return null;
		}
		StringBuffer buf = new StringBuffer(items.length * AVG_PATH_LENGTH);
		for (int i = 0; i < items.length; i++)
		{
			String item = items[i];
			for (int j = 0; j < item.length(); j++)
            {
				char c = item.charAt(j);
				if(c == ',')
				{
					buf.append('\\');
				}
				buf.append(c);
            }
			buf.append(',');
		}
		buf.setLength(buf.length()-1);
		return buf.toString();
	}
}
