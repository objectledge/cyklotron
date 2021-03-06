package net.cyklotron.cms.search;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.document.DateTools;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

/**
 * Utility functions for search application.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchUtil.java,v 1.4 2005-06-10 13:25:45 zwierzem Exp $
 */
public class SearchUtil
{
    public static final long DATE_MILLIS_DIVIDER = 60000L;  
    public static final long DATE_MAX_TIME_MILLIS = (Integer.MAX_VALUE) * DATE_MILLIS_DIVIDER;

    public static String dateToString(Date date)
    {
        return date == null ? null : DateTools.dateToString(date, DateTools.Resolution.SECOND);
    }

    public static Date dateFromString(String string)
        throws ParseException
    {
        return DateTools.stringToDate(string);
    }

    public static IndexResource getIndex(CoralSession coralSession, Parameters parameters)
    throws ProcessingException
    {
        long index_id = parameters.getLong("index_id",-1);
        if(index_id == -1)
        {
            throw new ProcessingException("the parameter index_id is not defined");
        }

        try
        {
            return IndexResourceImpl.getIndexResource(coralSession, index_id);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("index resource does not exist", e);
        }
    }

    public static PoolResource getPool(CoralSession coralSession, Parameters parameters)
    throws ProcessingException
    {
        long pool_id = parameters.getLong("pool_id",-1);
        if(pool_id == -1)
        {
            throw new ProcessingException("the parameter pool_id is not defined");
        }

        try
        {
            return PoolResourceImpl.getPoolResource(coralSession, pool_id);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("pool resource does not exist", e);
        }
    }
    
    public static ExternalPoolResource getExternalPool(CoralSession coralSession, Parameters parameters)
    throws ProcessingException
    {
        long pool_id = parameters.getLong("pool_id",-1);
        if(pool_id == -1)
        {
            throw new ProcessingException("the parameter pool_id is not defined");
        }

        try
        {
            return ExternalPoolResourceImpl.getExternalPoolResource(coralSession, pool_id);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("external pool resource does not exist", e);
        }
    }

    /**
     * Checks if the current user has the specific permission on the current search resource.
     */
    public static boolean checkPermission(CoralSession coralSession, Parameters parameters,
                                          String permissionName)
        throws ProcessingException
    {
        try
        {
            long id;
            if(parameters.isDefined("index_id"))
            {
                id = parameters.getLong("index_id");
            }
            else if(parameters.isDefined("pool_id"))
            {
                id = parameters.getLong("pool_id");
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

    public static Set getIndexableResources(CoralSession coralSession, Logger log,
        Set resourcesIds)
    {
        Set resources = new HashSet();
        for (Iterator iter = resourcesIds.iterator(); iter.hasNext();)
        {
            Long id = (Long)iter.next();
            try
            {
                Resource res = coralSession.getStore().getResource(id.longValue());
                if(res instanceof IndexableResource)
                {
                    resources.add(res);
                }
            }
            catch(EntityDoesNotExistException e)
            {
                log.warn("Search: could not get the resource #"+id+" for indexing", e);
            }
        }
        return resources;
    }
}
