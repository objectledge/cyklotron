package net.cyklotron.cms.modules.actions.fixes;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.SecurityException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

public class CYKLO416
    extends BaseCMSAction
{
    public CYKLO416(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory)
    {
        super(logger, structureService, cmsDataFactory);
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject allSubjects[] = coralSession.getSecurity().getSubject();
        Role registered = coralSession.getSecurity().getUniqueRole("cms.registered");
        try
        {
            int updated = 0;
            for (Subject subject : allSubjects)
            {
                if(subject.getId() != Subject.ROOT && subject.getId() != Subject.ANONYMOUS
                    && !subject.hasRole(registered))
                {
                    coralSession.getSecurity().grant(registered, subject, false);
                    updated++;
                }
            }
            templatingContext.put("result", "success");
            templatingContext.put("info", "granted cms.registed role to " + updated + " subjects");
        }
        catch(SecurityException e)
        {
            throw new ProcessingException("internal error", e);
        }
    }

    /**
     * @{inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        return (coralSession.getUserSubject().getId() == Subject.ROOT);
    }
}
