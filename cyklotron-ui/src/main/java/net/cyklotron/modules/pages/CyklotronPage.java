package net.cyklotron.modules.pages;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.modules.views.BaseCoralView;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;

/**
 * The custom page class for Cyklotron system.
 */
public class CyklotronPage
    extends BaseCoralView
{
    /** logging facility */
    private Logger log;
    
    private CmsDataFactory cmsDataFactory;

    public CyklotronPage(Context context, Logger logger, CmsDataFactory cmsDataFactory)
    {
        super(context);
        log = logger;
        this.cmsDataFactory = cmsDataFactory;
    }

    // TODO do we need it!
    
    /**
     * Sets up the environment before rendering the page.
     *
     * @param data the run data
     */
    public void process(Parameters parameters, TemplatingContext templatingContext, 
         MVCContext mvcContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {

        // if there was a problem - allow to get to error layout
        if(templatingContext.get("stackTrace") != null)
        {
            return;
        }
        else
        {
            // setup the node and site context variables (useful editing the site)
            CmsData cmsData = cmsDataFactory.getCmsData(context);

            // Check if we are just browsing the sites (x parameter is
            // defined, or site_id is defined with no view)
            boolean x_p = parameters.isDefined("x");
            boolean n_p = parameters.isDefined("node_id");
            boolean s_p = parameters.isDefined("site_id");
            String view = parameters.get("view","");
            boolean v_p = !(view.length() == 0 || view.equals("Default"));
            
            // this one here is not for the feeble of the heart...
            /**
            if(x_p || (!x_p && !n_p && s_p && !v_p))
            // site browsing
            {
                 // setup CMS page template
                try
                {
                    data.setPageTemplate("CmsSitePage");
                    cmsScreenSetup(data);
                }
                catch(NotFoundException e)
                {
                    throw new ProcessingException("Cannot find CMS page template", e);
                }
            }
            else
            // admin interface
            {
                SiteResource site = cmsData.getSite();
                if(site != null)
                {
                    LinkTool link = (LinkTool)context.get("link");
                    templatingContext.put("link", link.set("site_id",site.getIdString()));
                }
            }
            */
        }
    }
    
    public void cmsScreenSetup()
    	throws ProcessingException
    {
        /**
        CmsData cmsData = cmsDataFactory.getCmsData(context);
    	Parameters conf = cmsData.getPreferences();
		
    	String app = "cms";
    	String screen = "CmsDefault";

        String[] instances = conf.getSubset("component.").getSubsetNames();
		for(int i=0; i<instances.length; i++)
		{
			if(conf.get("component."+instances[i]+".class","").equals("EmbeddedScreen"))
			{
                app = conf.get("screen.app",app);
                screen = conf.get("screen.class",screen);
			}
		}

        data.setApplication(app);
        try
        {
            data.setScreenAssembler(screen);
        }
        catch(NotFoundException e)
        {
            throw new ProcessingException("screen "+screen+" does not exist in application "+app);
        }
        
        // configure skin preview
        String skinKey = SkinService.PREVIEW_KEY_PREFIX + cmsData.getSite().getName();
        if(data.getGlobalContext().hasAttribute(skinKey))
        {
            cmsData.setSkinName((String)data.getGlobalContext().getAttribute(skinKey));
        }
        */
    }
}
