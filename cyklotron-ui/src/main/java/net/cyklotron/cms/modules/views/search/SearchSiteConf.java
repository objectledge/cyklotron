package net.cyklotron.cms.modules.views.search;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;

/**
 * A screen for configuring search component.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchSiteConf.java,v 1.3 2005-01-25 11:24:16 pablo Exp $
 */
public class SearchSiteConf extends PoolList
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        super.prepare(data, context);
        
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
        Set poolNames = new HashSet(Arrays.asList(componentConfig.getStrings("poolNames")));
        templatingContext.put("selected_pools", poolNames);
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        if(cmsData.getNode() != null)
        {
            return cmsData.getNode().canModify(coralSession.getUserSubject());
        }
        else
        {
            return checkAdministrator(coralSession);
        }
    }
}
