package net.cyklotron.cms.modules.actions.documents;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.modules.actions.structure.BaseUpdatePreferences;
import net.labeo.services.resource.Resource;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Saves configuration for print document component configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdatePrintDocumentConfiguration.java,v 1.1 2005-01-24 04:34:39 pablo Exp $
 */
public class UpdatePrintDocumentConfiguration
	extends BaseUpdatePreferences
{
	public void modifyNodePreferences(RunData data, Parameters conf)
		  throws ProcessingException
	{
	    String path = parameters.get("printNodePath");
	    if(path == null)
	    {
	    	throw new ProcessingException("parameter not found");
	    }
	    
	    CmsData cmsData = cmsDataFactory.getCmsData(context);
	    try
	    {
	    	Resource parent = cmsData.getHomePage().getParent();
    	   	Resource[] nodes = coralSession.getStore().getResourceByPath(parent, path);
    	   	if(nodes.length != 1)
    	   	{
				throw new Exception("Cannot find resource with path '"+parent.getPath()+path+"'");
    	   	}
	    }
	    catch(Exception e)
	    {
	    	throw new ProcessingException("Exception: ",e);
	    }
		conf.set("printNodePath", path);	    		
    }
}
