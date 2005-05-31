package net.cyklotron.cms.category.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

/**
 * Utility functions for category query screens and actions.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryUtil.java,v 1.5 2005-05-31 17:11:52 pablo Exp $
 */
public class CategoryQueryUtil
{
	public static String QUERY_PARAM = "query_id";
	public static String QUERY_POOL_PARAM = "query_pool_id";
	
    public static CategoryQueryPoolResource getPool(CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
		long id = parameters.getLong(QUERY_POOL_PARAM,-1);
		if(id == -1)
		{
			throw new ProcessingException("the parameter '"+QUERY_POOL_PARAM+"' is not defined");
		}

		try
		{
			return CategoryQueryPoolResourceImpl.getCategoryQueryPoolResource(coralSession, id);
		}
		catch(EntityDoesNotExistException e)
		{
			throw new ProcessingException("pool resource does not exist", e);
		}
    }

	public static CategoryQueryResource getQuery(CoralSession coralSession, Parameters parameters)
		throws ProcessingException
	{
		long id = parameters.getLong(QUERY_PARAM,-1);
		if(id == -1)
		{
			throw new ProcessingException("the parameter '"+QUERY_PARAM+"' is not defined");
		}

		try
		{
			return CategoryQueryResourceImpl.getCategoryQueryResource(coralSession, id);
		}
		catch(EntityDoesNotExistException e)
		{
			throw new ProcessingException("query resource does not exist", e);
		}
	}

    public static String getNames(CoralSession coralSession, ResourceSelectionState selection, String state)
        throws ProcessingException
    {
        try
        {
            Map requiredMap = selection.getIds(state);
            StringBuilder sb = new StringBuilder();
            Iterator i = requiredMap.keySet().iterator();
            while (i.hasNext())
            {
                Long id = (Long)i.next();
                Resource res = coralSession.getStore().getResource(id.longValue());
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
	public static boolean checkPermission(CoralSession coralSession, Parameters parameters,
										  String permissionName)
		throws ProcessingException
	{
		try
		{
			long id;
			if(parameters.isDefined(QUERY_PARAM))
			{
				id = parameters.getLong(QUERY_PARAM);
			}
			else if(parameters.isDefined(QUERY_POOL_PARAM))
			{
				id = parameters.getLong(QUERY_POOL_PARAM);
			}
			else if(parameters.isDefined("site_id"))
			{
				id = parameters.getLong("site_id");
			}
			else
			{
				id = -1;
			}

			Resource res = coralSession.getStore().getResource(id);
			Permission permission = coralSession.getSecurity().
				getUniquePermission(permissionName);
			return coralSession.getUserSubject().hasPermission(res, permission);
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
		StringBuilder buf = new StringBuilder(items.length() / AVG_PATH_LENGTH);
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

        if(state != S5_ERROR && buf.length() > 0) // add last numeric id
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
		StringBuilder buf = new StringBuilder(items.length * AVG_PATH_LENGTH);
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
