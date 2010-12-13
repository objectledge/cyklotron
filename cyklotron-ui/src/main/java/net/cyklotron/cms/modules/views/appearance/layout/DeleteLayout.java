package net.cyklotron.cms.modules.views.appearance.layout;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.LayoutResourceImpl;
import net.cyklotron.cms.style.StyleService;

/**
 *
 */
public class DeleteLayout
    extends BaseAppearanceScreen
{
    
    
    public DeleteLayout(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StyleService styleService, SkinService skinService,
        IntegrationService integrationService, Templating templating)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, styleService,
                        skinService, integrationService, templating);
        
    }
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
            logger.error("Exception :",e);
            throw new ProcessingException("resource doesn't exist",e);
        }
        catch (MalformedQueryException e)
        {
            logger.error("Exception :",e);
            throw new ProcessingException("bad query",e);
        }
    }
}
