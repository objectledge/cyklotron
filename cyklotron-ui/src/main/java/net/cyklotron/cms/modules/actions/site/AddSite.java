package net.cyklotron.cms.modules.actions.site;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteResourceImpl;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddSite.java,v 1.3 2005-01-25 07:48:02 pablo Exp $
 */
public class AddSite
    extends BaseSiteAction
{
    protected UserManager userManager;
    
    public AddSite(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
        SiteService siteService, UserManager userManager)
    {
        super(logger, structureService, cmsDataFactory, siteService);
        this.userManager = userManager;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        
        String name = parameters.get("name","");
        String description = parameters.get("description","");

        long templateId = parameters.getLong("template_id", -1);
        String owner = parameters.get("owner",null);
        String dn = null;
        if(owner == null || owner.length() == 0)
        {
            templatingContext.put("result","owner_name_empty");
        }
        else
        {
            try
            {
                dn = userManager.getUserByLogin(owner).getName();
            }
            catch(Exception e)
            {
                templatingContext.put("result","owner_name_invalid");
            }
        }
        if(templateId == -1)
        {
            templatingContext.put("result","site_template_empty");
        }
        if(name.equals(""))
        {
            templatingContext.put("result","site_name_empty");
        }
        if(dn != null && name.length() > 0 && templateId > 0)
        {
            try
            {
                SiteResource template = SiteResourceImpl.getSiteResource(coralSession,
                                                                         templateId);
                Subject ownerSubject = coralSession.getSecurity().getSubject(dn);
                SiteResource site = ss.createSite(coralSession, template, name, description,
                                                  ownerSubject);
                parameters.set("site_id", site.getIdString());
            }
            catch(Exception e)
            {
                templatingContext.put("result","exception");
                logger.error("AddSite",e);
                templatingContext.put("trace", new StackTrace(e));
            }
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("site,AddSite");
            templatingContext.put("name", name);
            templatingContext.put("description", description);
            templatingContext.put("template_id", new Long(templateId));
            templatingContext.put("owner", owner);
        }
        else
        {
            templatingContext.put("result","added_successfully");
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }
}
