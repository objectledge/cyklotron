package net.cyklotron.cms;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;

import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 * A data object used to encapsulate CMS runtime data.
 *
 * @author <a href="mailto:zwierzem@caltha.pl">Damian Gajda</a>
 * @version $Id: CmsDataFactory.java,v 1.4 2007-02-25 12:12:05 pablo Exp $
 */
public class CmsDataFactory
    implements CmsConstants
{
    /** The {@link Logger} */
    private Logger logger;
    /** resource service */
    private CoralSession resourceService;
    /** structure service */
    private StructureService structureService;
    /** preferences service */
    private PreferencesService preferencesService;
    /** site service */
    private SiteService siteService;
    /** user manager */
    private UserManager userManager;
    /** integration manager */
    private IntegrationService integrationService;
    
    public CmsDataFactory(Logger logger, StructureService structureService, 
        PreferencesService preferencesService, SiteService siteService,
        UserManager userManager, IntegrationService integrationService)
    {
        this.logger = logger;
        this.structureService = structureService;
        this.preferencesService = preferencesService;
        this.siteService = siteService;
        this.userManager = userManager;
        this.integrationService = integrationService;
    }
    
    public CmsData getCmsData(Context context)
        throws ProcessingException
    {
        TemplatingContext tContext = (TemplatingContext)
            context.getAttribute(TemplatingContext.class);
        CmsData cmsData = (CmsData)(tContext.get(CMS_DATA_KEY));
        if(cmsData == null)
        {
            cmsData = new CmsData(context, logger, structureService, 
                preferencesService, siteService, userManager, integrationService);
            tContext.put(CMS_DATA_KEY, cmsData);
            if(cmsData.getNode() != null) // Remove this block after CmsData is widely used
            {
                // store values in the context
                tContext.put("node", cmsData.getNode());
                tContext.put("home_page_node", cmsData.getHomePage());
                tContext.put("site", cmsData.getSite());
            }
        }
        return cmsData;
    }
    
    public void removeCmsData(Context context)
        throws ProcessingException
    {
        TemplatingContext tContext = (TemplatingContext)
        context.getAttribute(TemplatingContext.class);
            tContext.remove(CMS_DATA_KEY);
    }
    
    /**
     * static metod to retrieve cms data previously created
     * 
     * it can returns null!!!
     */
    public static CmsData getCmsDataIfExists(Context context)
        throws ProcessingException
    {
        TemplatingContext tContext = (TemplatingContext)
            context.getAttribute(TemplatingContext.class);
        return (CmsData)(tContext.get(CMS_DATA_KEY));
    }
}
