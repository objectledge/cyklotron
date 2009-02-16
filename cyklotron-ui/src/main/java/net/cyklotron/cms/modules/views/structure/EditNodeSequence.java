package net.cyklotron.cms.modules.views.structure;

import java.util.HashMap;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;


/**
 * The add post screen class
 */
public class EditNodeSequence
    extends BaseStructureScreen
{
    
    public EditNodeSequence(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        NavigationNodeResource node = getNode();

        Resource[] children = coralSession.getStore().getResource(node);
        HashMap childrenMap = new HashMap();
        for(int i = 0; i < children.length; i++)
        {
            childrenMap.put(children[i].getIdObject(), children[i]);
        }
        List childrenIds = (List)httpContext.getSessionAttribute(CURRENT_SEQUENCE);
        if(childrenIds == null)
        {
            throw new ProcessingException("Sequence list couldn't be found in session context");
        }
        templatingContext.put("childrenIds",childrenIds);
        templatingContext.put("childrenMap",childrenMap);
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        try
        {
            NavigationNodeResource node = getNode();
            Permission permission = coralSession.getSecurity().
                getUniquePermission("cms.structure.move");
            return coralSession.getUserSubject().hasPermission(node, permission);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check security", e);
        }
    }
}
