package net.cyklotron.cms.modules.actions.site;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.RoleAssignment;
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
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateSite.java,v 1.4 2005-03-09 09:59:30 pablo Exp $
 */
public class UpdateSite
    extends BaseSiteAction
{
    protected UserManager userManager;
    
    public UpdateSite(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
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
        
        Subject subject = coralSession.getUserSubject();
        String description = parameters.get("description","");
        String owner = parameters.get("owner","");
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
        if(dn != null)
        {
            try
            {
                SiteResource site = getSite(context);
                if(!site.getDescription().equals(description))
                {
                    site.setDescription(description);
                    site.update();
                }
                Subject root = coralSession.getSecurity().getSubject(Subject.ROOT);
                Subject newOwner = coralSession.getSecurity().getSubject(dn);
                Subject oldOwner = site.getOwner();
                if(!oldOwner.equals(newOwner))
                {
                    if(!newOwner.hasRole(site.getTeamMember()))
                    {
                        coralSession.getSecurity().
                            grant(site.getTeamMember(), newOwner, true);
                    }
                    coralSession.getSecurity().
                        grant(site.getAdministrator(), newOwner, false);
                    
                    RoleAssignment[] roleAssignments = site.getAdministrator().getRoleAssignments();
                    for(int i = 0; i < roleAssignments.length; i++)
                    {
                        if(roleAssignments[i].getSubject().equals(oldOwner))
                        {
                            coralSession.getSecurity().
                                revoke(site.getAdministrator(), oldOwner);
                        }
                    }
                    coralSession.getStore().setOwner(site, newOwner);
                }
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
            mvcContext.setView("site.EditSite");
            templatingContext.put("owner", owner);
            templatingContext.put("description", description);
        }
        else
        {
            templatingContext.put("result","updated_successfully");
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return coralSession.getUserSubject().hasRole(getSite(context).getAdministrator());
    }
}
