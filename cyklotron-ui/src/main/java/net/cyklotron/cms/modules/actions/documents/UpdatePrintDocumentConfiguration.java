package net.cyklotron.cms.modules.actions.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.structure.BaseUpdatePreferences;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * Saves configuration for print document component configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdatePrintDocumentConfiguration.java,v 1.3 2005-01-25 03:22:23 pablo Exp $
 */
public class UpdatePrintDocumentConfiguration
	extends BaseUpdatePreferences
{
    
    public UpdatePrintDocumentConfiguration(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService,
        PreferencesService preferencesService, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, styleService, preferencesService,
                        siteService);
    }
    
    public void modifyNodePreferences(Context context, Parameters conf, Parameters parameters, CoralSession coralSession)
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
