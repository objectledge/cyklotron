package net.cyklotron.cms.modules.views.search;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * A screen for configuring search screen.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchSiteScreenConf.java,v 1.3 2005-01-25 11:24:17 pablo Exp $
 */
public class SearchSiteScreenConf extends PoolList
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        super.prepare(data, context);
        
        Parameters config = prepareScreenConfig(data);
        Set poolNames = new HashSet(Arrays.asList(config.getStrings("poolNames")));
        templatingContext.put("selected_pools", poolNames);
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return getCmsData().getNode().canModify(coralSession.getUserSubject());
    }
}
