package net.cyklotron.cms.modules.views.search;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
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
 * A screen for configuring search screen.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchSiteScreenConf.java,v 1.9 2008-10-30 17:46:03 rafal Exp $
 */
public class SearchSiteScreenConf extends PoolList
{
    
    public SearchSiteScreenConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SearchService searchService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, searchService);
        
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        super.process(parameters, mvcContext, templatingContext, httpContext, i18nContext, coralSession);
        Parameters config = getScreenConfig();
        Set poolNames = new HashSet(Arrays.asList(config.getStrings("poolNames")));
        templatingContext.put("selected_pools", poolNames);

        try
        {
            long requiredQueryPool = config.getLong("required_query_pool_id", -1);
            if(requiredQueryPool != -1)
            {
                templatingContext.put("required_query_pool",
                    coralSession.getStore().getResource(requiredQueryPool));
            }
            long optionalQueryPool = config.getLong("optional_query_pool_id", -1);
            if(optionalQueryPool != -1)
            {
                templatingContext.put("optional_query_pool",
                    coralSession.getStore().getResource(optionalQueryPool));
            }
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Exception occurred", e);
        }
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("search"))
        {
            logger.debug("Application 'search' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return getCmsData().getNode().canModify(coralSession, coralSession.getUserSubject());
    }
}
