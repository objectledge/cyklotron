package net.cyklotron.cms.modules.actions.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.structure.BaseUpdatePreferences;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.ComponentDataCacheService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * Saves configuration for resource list component configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateRecommendDocumentConfiguration.java,v 1.4 2005-03-08 10:51:57 pablo Exp $
 */
public class UpdateRecommendDocumentConfiguration
	extends BaseUpdatePreferences
{
    
    public UpdateRecommendDocumentConfiguration(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService,
        PreferencesService preferencesService, SiteService siteService,
        ComponentDataCacheService componentDataCacheService)
    {
        super(logger, structureService, cmsDataFactory, styleService, preferencesService,
                        siteService, componentDataCacheService);
        
    }
	public void modifyNodePreferences(Context context, Parameters conf, Parameters parameters, CoralSession coralSession)
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
