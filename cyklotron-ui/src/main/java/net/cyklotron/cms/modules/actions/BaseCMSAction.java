package net.cyklotron.cms.modules.actions;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.modules.actions.BaseCoralAction;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsConstants;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseCMSAction.java,v 1.1 2005-01-24 04:34:15 pablo Exp $
 */
public abstract class BaseCMSAction
    extends BaseCoralAction
    implements CmsConstants
{
    /** structure service */
    protected StructureService structureService;
    
    /** cms data factory */
    protected CmsDataFactory cmsDataFactory;
    
    protected Logger logger;

    public BaseCMSAction(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory)
    {
        super();
        this.logger = logger; 
        this.structureService = structureService;
        this.cmsDataFactory = cmsDataFactory;
    }

    public final void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        execute(context, parameters, mvcContext, templatingContext, httpContext, coralSession);
    }
    
    public abstract void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException;
    
    
    /** Returns current requests CmsData. 
     * @param context TODO*/
    public CmsData getCmsData(Context context)
    throws ProcessingException
    {
        return cmsDataFactory.getCmsData(context);
    }

    /** TODO: Remove after CmsData is widely used 
     * @param context TODO*/
    public boolean isNodeDefined(Context context)
        throws ProcessingException
    {
        return getCmsData(context).isNodeDefined();
    }

    /** TODO: Remove after CmsData is widely used 
     * @param context TODO*/
    public NavigationNodeResource getNode(Context context)
        throws ProcessingException
    {
        return getCmsData(context).getNode();
    }

    /** TODO: Remove after CmsData is widely used */
    public SiteResource getSite(Context context)
        throws ProcessingException
    {
        return getCmsData(context).getSite();
    }

    /** TODO: Remove after CmsData is widely used */
    public NavigationNodeResource getHomePage(Context context)
        throws ProcessingException
    {
        return getCmsData(context).getHomePage();
    }

    /**
     * Checks if the current user has the specific permission on the current
     * node.
     */
    public boolean checkPermission(Context context, CoralSession coralSession, String permissionName)
        throws ProcessingException
    {
        try
        {
            NavigationNodeResource node = getNode(context);
            Permission permission = coralSession.getSecurity().
                getUniquePermission(permissionName);
            return coralSession.getUserSubject().hasPermission(node, permission);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check security", e);
        }
    }

    /**
     * Checks if the current user has administrative privileges on the current
     * site.
     */
    public boolean checkAdministrator(Context context, CoralSession coralSession)
        throws ProcessingException
    {
        return getCmsData(context).checkAdministrator(coralSession);
    }
}
