package net.cyklotron.cms.modules.actions.documents;

import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.structure.BaseUpdatePreferences;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;

/**
 * Saves configuration for resource list component configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateRecommendDocumentConfiguration.java,v 1.1 2005-01-24 04:34:39 pablo Exp $
 */
public class UpdateRecommendDocumentConfiguration
	extends BaseUpdatePreferences
{
	public void modifyNodePreferences(RunData data, Parameters conf)
		  throws ProcessingException
	{
	    long naviId = parameters.getLong("target", -1);
	    if(naviId == -1)
	    {
	    	throw new ProcessingException("parameter not found");
	    }
	    try
	    {
	    	NavigationNodeResource node = NavigationNodeResourceImpl.
	    		getNavigationNodeResource(coralSession, naviId);
			NavigationNodeResource homePage = getHomePage(context);
			String path = node.getPath().substring(homePage.getPath().length());	    		
			conf.set("recommend_document_path", path);	    		
	    }
	    catch(Exception e)
	    {
	    	throw new ProcessingException("Exception: ",e);
	    }

    }
}
