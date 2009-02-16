package net.cyklotron.cms.modules.views.security;

import java.util.HashSet;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.RoleAssignment;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;

public class MemberAssignments
    extends BaseRoleScreen
{
    private static String TABLE_NAME = "cms.security.MemberAssignments";



    public MemberAssignments(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SecurityService securityService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        securityService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            templatingContext.put("roles", getRoleTable(coralSession, site, i18nContext));
            templatingContext.put("path_tool", new PathTool(site));
            long subjectId = parameters.getLong("subject_id");
            Subject subject = coralSession.getSecurity().getSubject(subjectId);
            templatingContext.put("subject", subject);
            templatingContext.put("assigned", getAssignedRoles(subject));
        }
        catch(Exception e)
        {
            throw new ProcessingException("data access failed", e);
        }
    }

    public Set getAssignedRoles(Subject subject)
    {
        Set roles = new HashSet();
        RoleAssignment assignments[] = subject.getRoleAssignments();
        for(int i=0; i<assignments.length; i++)
        {
            roles.add(assignments[i].getRole());
        }
        return roles;
    }

    protected String getTableName()
    {
        return TABLE_NAME;
    }
}
