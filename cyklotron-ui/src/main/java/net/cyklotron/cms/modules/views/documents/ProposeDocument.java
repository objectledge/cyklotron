package net.cyklotron.cms.modules.views.documents;

import java.util.Arrays;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * Stateful screen for propose document application.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawe� Potempski</a>
 * @version $Id: ProposeDocument.java,v 1.7 2007-11-18 21:25:26 rafal Exp $
 */
public class ProposeDocument
    extends BaseSkinableDocumentScreen
{
    public ProposeDocument(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        StructureService structureService, StyleService styleService, SkinService skinService,
        MVCFinder mvcFinder, TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, structureService, styleService,
                        skinService, mvcFinder, tableStateManager);
        
    }

    public String getState()
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        return parameters.get("state","AddDocument");
    }
    
    public void prepareAddDocument(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        SiteResource site = getSite();
        try
        {
            templatingContext.put("styles", Arrays.asList(styleService.getStyles(coralSession, site)));
            long parent_node_id = parameters.getLong("parent_node_id", -1);
            if(parent_node_id == -1)
            {
                templatingContext.put("parent_node",getHomePage());
            }
            else
            {
                templatingContext.put("parent_node",NavigationNodeResourceImpl.getNavigationNodeResource(coralSession,parent_node_id));
            }
            prepareCategories(context, true);
        }
        catch(Exception e)
        {
            screenError(getNode(), context, "Screen Error "+e);
        }
    }
    
    public void prepareResult(Context context)
        throws ProcessingException
    {
        // does nothing
    }
}
