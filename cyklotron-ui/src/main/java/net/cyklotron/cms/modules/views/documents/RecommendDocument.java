package net.cyklotron.cms.modules.views.documents;

import java.util.Arrays;

import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;

/**
 * Stateful screen for recomend document application.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawe� Potempski</a>
 * @version $Id: RecommendDocument.java,v 1.1 2005-01-24 04:34:59 pablo Exp $
 */
public class RecommendDocument
    extends BaseSkinableScreen
{
    /** logging facility */
    protected Logger log;
    
    public RecommendDocument()
    {
        super();
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility("documents");
    }

	public String getState(RunData data)
		throws ProcessingException
	{
		return parameters.get("state","Default");
	}

    public void prepareDefault(RunData data, Context context)
        throws ProcessingException
    {
        SiteResource site = getSite();
        try
        {
            templatingContext.put("styles", Arrays.asList(styleService.getStyles(site)));
            long parent_node_id = parameters.getLong("parent_node_id", -1);
            if(parent_node_id == -1)
            {
                templatingContext.put("parent_node",getHomePage());
            }
            else
            {
                templatingContext.put("parent_node",NavigationNodeResourceImpl.getNavigationNodeResource(coralSession,parent_node_id));
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("Screen Error "+e);
        }
    }
    
	public void prepareClosePopup(RunData data, Context context)
		throws ProcessingException
	{		
	}

}
