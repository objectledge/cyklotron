package net.cyklotron.cms.modules.components.site;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.components.BaseCMSComponent;
import net.cyklotron.cms.site.SiteService;

/**
 * Site UI navigation component.
 */
public class Navi
    extends BaseCMSComponent
{
    private SiteService siteService;

    
    
    public Navi(org.objectledge.context.Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SiteService siteService)
    {
        super(context, logger, templating, cmsDataFactory);
        this.siteService = siteService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        List sites = Arrays.asList(siteService.getSites(coralSession));
        Collections.sort(sites, new NameComparator(i18nContext.getLocale()));
        templatingContext.put("sites", sites);
    }
}
