package net.cyklotron.cms.util;

import net.labeo.services.resource.Resource;

/**
 * This is a filter for filtering resources upon their paths,
 * also it accepts the application node if it's needed.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: CmsPathFilter.java,v 1.1 2005-01-12 20:44:32 pablo Exp $
 */
public class CmsPathFilter
    implements net.labeo.services.table.TableFilter
{
    protected String[] paths;
    
    protected String applicationPath;

    public CmsPathFilter(Resource root, String[] acceptedPaths)
    {
    	String basePath = root.getPath();
		paths = new String[acceptedPaths.length];

        for(int i=0; i<acceptedPaths.length; i++)
        {
        	// define application path if is on the way to any accepted path
        	if(applicationPath == null && acceptedPaths[i].indexOf("applications")!=-1)
        	{
        	    applicationPath = basePath + "/applications";
        	}
        	String accPath = acceptedPaths[i];
            if(accPath.charAt(accPath.length()-1) == '/')
            {
                accPath = accPath.substring(0, accPath.length()-1);
            }

            if(accPath.charAt(0) == '/')
            {
                paths[i] = accPath;
            }
            else
            {
                paths[i] = basePath + '/' + accPath;
            }
        }
    }

    public boolean accept(Object object)
    {
        if(!(object instanceof Resource))
        {
            return false;
        }

        // TODO: Add sort of a dictionary for fast path comparation
        // maybe a tree of names with termination/acceptation field
        String path = ((Resource)object).getPath(); 
        for(int i=0; i<paths.length; i++)
        {
            if(path.startsWith(paths[i]))
            {
                return true;
            }
        }
        if(applicationPath != null && path.equals(applicationPath))
        {
        	return true;
        }
        return false;
    }
}
