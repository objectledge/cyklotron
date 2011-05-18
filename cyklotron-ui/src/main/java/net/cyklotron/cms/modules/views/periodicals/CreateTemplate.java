/*
 * Created on Oct 29, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.periodicals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PeriodicalsTemplatingService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;


/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CreateTemplate
    extends BasePeriodicalsScreen
{
    private final PeriodicalsTemplatingService periodicalsTemplatingService;

    public CreateTemplate(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        PeriodicalsService periodicalsService, PeriodicalsTemplatingService periodicalsTemplatingService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        periodicalsService);
        this.periodicalsTemplatingService = periodicalsTemplatingService;
        
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
        String rendererName = parameters.get("renderer");
        try
        {
            templatingContext.put("renderer", rendererName);
            SiteResource site = getSite();
            String[] variants = periodicalsTemplatingService.getTemplateVariants(site, rendererName);
            templatingContext.put("variants", Arrays.asList(variants));
            Map locales = new HashMap();
            List provided = periodicalsTemplatingService.getDefaultTemplateLocales(rendererName);
            Iterator i = provided.iterator();
            while(i.hasNext())
            {
                Locale l = (Locale)i.next();
                locales.put(l, l.toString());
            }
            templatingContext.put("locales", locales);
        }
        catch(Exception e)
        {
            throw new ProcessingException(e);
        }
    }
}
