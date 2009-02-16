package net.cyklotron.cms.modules.views.appearance.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcontainer.dna.Logger;
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
import net.cyklotron.cms.style.ComponentSocketResource;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.LayoutResourceImpl;
import net.cyklotron.cms.style.StyleService;

/**
 *
 */
public class EditLayout
    extends BaseAppearanceScreen
{
    
    public EditLayout(org.objectledge.context.Context context, Logger logger,
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
        LayoutResource layout = null;
        try 
        {
            layout = LayoutResourceImpl.getLayoutResource(coralSession,layoutId);
            templatingContext.put("layout",layout);
            if(parameters.isDefined("socket_count"))
            {
                int count = parameters.getInt("socket_count");
                ArrayList sockets = new ArrayList(count);
                for(int i=1; i<=count; i++)
                {
                    sockets.add(parameters.get("socket_"+i,""));
                }
                templatingContext.put("sockets", sockets);
            }
            else
            {
                ComponentSocketResource[] sockets = styleService.getSockets(coralSession, layout);
                List socketList = new ArrayList();
                for(int i=0; i<sockets.length; i++)
                {
                    socketList.add(sockets[i].getName());
                }
                Collections.sort(socketList);
                templatingContext.put("sockets", socketList);
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to load layout information",e);
        }
    }
}

