package net.cyklotron.cms.util;

import org.objectledge.coral.store.Resource;
import org.objectledge.table.TableFilter;

/**
 * This is a filter for filtering resources upon their paths,
 * also it accepts the application node if it's needed.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: CmsPathFilter.java,v 1.3 2005-05-30 00:18:34 zwierzem Exp $
 */
public class CmsPathFilter
    implements TableFilter
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
