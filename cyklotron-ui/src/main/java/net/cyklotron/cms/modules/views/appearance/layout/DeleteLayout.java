package net.cyklotron.cms.modules.views.appearance.layout;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.query.MalformedQueryException;
import net.labeo.services.resource.query.QueryResults;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.LayoutResourceImpl;

/**
 *
 */
public class DeleteLayout
    extends BaseAppearanceScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        long layoutId = parameters.getLong("layout_id", -1);
        if(layoutId == -1)
        {
            throw new ProcessingException("layout id couldn't be found");
        }
        try 
        {
            LayoutResource layout = LayoutResourceImpl.getLayoutResource(coralSession,layoutId);
            QueryResults res = coralSession.getQuery().executeQuery(
                "FIND RESOURCE FROM cms.style.level WHERE layout = "+layout.getIdString());
            int count = res.getArray(1).length;
            templatingContext.put("usage_quantity",new Integer(count));
            if( count > 0)
            {
                templatingContext.put("in_use",new Boolean(true));
            }
            else
            {
                templatingContext.put("in_use",new Boolean(false));
            }

            templatingContext.put("layout",layout);
        }
        catch (EntityDoesNotExistException e)
        {
            log.error("Exception :",e);
            throw new ProcessingException("resource doesn't exist",e);
        }
        catch (MalformedQueryException e)
        {
            log.error("Exception :",e);
            throw new ProcessingException("bad query",e);
        }
    }
}
