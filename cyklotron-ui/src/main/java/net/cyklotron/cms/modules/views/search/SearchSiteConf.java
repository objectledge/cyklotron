package net.cyklotron.cms.modules.views.search;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.SearchService;

/**
 * A screen for configuring search component.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchSiteConf.java,v 1.5 2005-03-08 11:08:42 pablo Exp $
 */
public class SearchSiteConf extends PoolList
{
    
    public SearchSiteConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SearchService searchService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, searchService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        super.process(parameters, mvcContext, templatingContext, httpContext, i18nContext, coralSession);
        
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
        Set poolNames = new HashSet(Arrays.asList(componentConfig.getStrings("poolNames")));
        templatingContext.put("selected_pools", poolNames);
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        CmsData cmsData = getCmsData();
        if(cmsData.getNode() != null)
        {
            return cmsData.getNode().canModify(context, coralSession.getUserSubject());
        }
        else
        {
            return checkAdministrator(coralSession);
        }
    }
}
