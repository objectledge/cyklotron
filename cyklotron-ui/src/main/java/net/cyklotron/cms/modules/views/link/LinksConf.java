package net.cyklotron.cms.modules.views.link;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.link.PoolResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.StructureService;


/**
 *
 */
public class LinksConf
    extends BaseLinkScreen
{
    

    public LinksConf(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        LinkService linkService, StructureService structureService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, linkService,
                        structureService);
        
    }
    
    
    
    
    /* (non-Javadoc)
     * @see net.cyklotron.cms.modules.views.BaseCMSScreen#process(org.objectledge.parameters.Parameters, org.objectledge.web.mvc.MVCContext, org.objectledge.templating.TemplatingContext, org.objectledge.web.HttpContext, org.objectledge.i18n.I18nContext, org.objectledge.coral.session.CoralSession)
     */
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession) throws org.objectledge.pipeline.ProcessingException
    {
    }

    /**
     * {@inheritDoc}
     */
    public String route(String thisViewName)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        TemplatingContext templatingContext =
            TemplatingContext.getTemplatingContext(context);
        String instance = parameters.get("component_instance","");
        long nodeId = parameters.getLong("node_id", -1);
        httpContext.setSessionAttribute(FROM_COMPONENT,new Boolean(true));
        httpContext.setSessionAttribute(COMPONENT_INSTANCE,instance);
        if(nodeId != -1)
        {
            httpContext.setSessionAttribute(COMPONENT_NODE,new Long(nodeId));
        }
        try
        {
            Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
            long pid = componentConfig.getLong("pid",-1);
            if(pid != -1)
            {
                try
                {
                    Resource pool = coralSession.getStore().getResource(pid);
                    if(pool instanceof PoolResource)
                    {
                        parameters.set("pid",pid);
                        return "link.EditPool";
                    }
                }
                catch(EntityDoesNotExistException e)
                {
                    // something wrong with pid in configuration 
                    // probably pints to not existing resource or not pool
                    // resource, simply process to pool list screen to choose
                    // correct pool.
                }
            }
            LinkRootResource linkRoot = getLinkRoot(coralSession);
            parameters.set("lsid",linkRoot.getIdString());
            return "link.PoolList";
        }
        catch(Exception e)
        {
            throw new ProcessingException("Link exception", e);
        }
    }
}

