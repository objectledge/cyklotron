package net.cyklotron.cms.modules.actions.statistics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.DateAttributeHandler;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.modules.views.statistics.RecentlyUpdated;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: StoreRecentlyUpdatedConfiguration.java,v 1.1.2.3 2005-08-09 04:30:01 rafal Exp $
 */
public class StoreRecentlyUpdatedConfiguration extends BaseCMSAction
{
    private PreferencesService preferencesService;
    
    /**
     * @param structureService
     * @param cmsDataFactory
     */
    public StoreRecentlyUpdatedConfiguration(Logger logger, StructureService structureService, 
        CmsDataFactory cmsDataFactory, StyleService styleService, PreferencesService preferencesService)
    {
        super(logger, structureService, cmsDataFactory);
        this.preferencesService = preferencesService;
    }

    
    /** 
     * 
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Parameters systemPreferences = preferencesService.getSystemPreferences(coralSession);
        systemPreferences.set(RecentlyUpdated.CONFIGURATION_PREFIX+"update_start",parameters.getLong("update_start"));
        systemPreferences.set(RecentlyUpdated.CONFIGURATION_PREFIX+"update_end",parameters.getLong("update_end"));
        systemPreferences.set(RecentlyUpdated.CONFIGURATION_PREFIX+"offset",parameters.getInt("offset"));
        systemPreferences.set(RecentlyUpdated.CONFIGURATION_PREFIX+"selected_site_id",
            parameters.getLongs("selected_site_id"));
        systemPreferences.set(RecentlyUpdated.CONFIGURATION_PREFIX+"range",parameters.getBoolean("range"));
    }
    
    /**
     * @{inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        return checkAdministrator(context);
    }
}
