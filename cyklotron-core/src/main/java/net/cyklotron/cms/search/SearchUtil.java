package net.cyklotron.cms.search;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.cyklotron.cms.CmsTool;

import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Utility functions for search application.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchUtil.java,v 1.1 2005-01-12 20:44:36 pablo Exp $
 */
public class SearchUtil
{
    public static final long DATE_MILLIS_DIVIDER = 60000L;  
    public static final long DATE_MAX_TIME_MILLIS = ((long)Integer.MAX_VALUE) * DATE_MILLIS_DIVIDER;  

    public static String dateToString(Date date)
    {
    	if(date == null)
    	{
    		return null;
    	}
        long longValue = date.getTime();
        if(longValue > DATE_MAX_TIME_MILLIS)
        {
            longValue = DATE_MAX_TIME_MILLIS;
        }
        int intValue = (int)(longValue/DATE_MILLIS_DIVIDER);
        return Integer.toString(intValue);
    }

    public static Date dateFromString(String string)
    throws ParseException
    {
        return new Date(Long.parseLong(string) * DATE_MILLIS_DIVIDER);
    }   
    
    public static IndexResource getIndex(ResourceService resourceService, RunData data)
    throws ProcessingException
    {
        long index_id = data.getParameters().get("index_id").asLong(-1);
        if(index_id == -1)
        {
            throw new ProcessingException("the parameter index_id is not defined");
        }

        try
        {
            return IndexResourceImpl.getIndexResource(resourceService, index_id);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("index resource does not exist", e);
        }
    }

    public static PoolResource getPool(ResourceService resourceService, RunData data)
    throws ProcessingException
    {
        long pool_id = data.getParameters().get("pool_id").asLong(-1);
        if(pool_id == -1)
        {
            throw new ProcessingException("the parameter pool_id is not defined");
        }

        try
        {
            return PoolResourceImpl.getPoolResource(resourceService, pool_id);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("pool resource does not exist", e);
        }
    }
    
    public static ExternalPoolResource getExternalPool(ResourceService resourceService, RunData data)
    throws ProcessingException
    {
        long pool_id = data.getParameters().get("pool_id").asLong(-1);
        if(pool_id == -1)
        {
            throw new ProcessingException("the parameter pool_id is not defined");
        }

        try
        {
            return ExternalPoolResourceImpl.getExternalPoolResource(resourceService, pool_id);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("external pool resource does not exist", e);
        }
    }

    /**
     * Checks if the current user has the specific permission on the current search resource.
     */
    public static boolean checkPermission(ResourceService resourceService, RunData data,
                                          String permissionName)
        throws ProcessingException
    {
        try
        {
            long id;
            if(data.getParameters().get("index_id").isDefined())
            {
                id = data.getParameters().get("index_id").asLong();
            }
            else if(data.getParameters().get("pool_id").isDefined())
            {
                id = data.getParameters().get("pool_id").asLong();
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

    public static Set getResources(ResourceService resourceService, LoggingFacility log,
        Set resourcesIds)
    {
        Set resources = new HashSet();
        for (Iterator iter = resourcesIds.iterator(); iter.hasNext();)
        {
            Long id = (Long)iter.next();
            try
            {
                resources.add(resourceService.getStore().getResource(id.longValue()));
            }
            catch(EntityDoesNotExistException e)
            {
                log.warning("Search: could not get the resource #"+id+" for indexing", e);
            }
        }
        return resources;
    }
}
