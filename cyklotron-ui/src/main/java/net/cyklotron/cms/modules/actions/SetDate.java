package net.cyklotron.cms.modules.actions;

import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: SetDate.java,v 1.1 2005-01-24 04:34:15 pablo Exp $
 */
public class SetDate
    extends BaseCMSAction
{
    public SetDate(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory)
    {
        super(logger, structureService, cmsDataFactory);
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        long time = parameters.getLong("time", 0);
        if(time != 0)
        {
            Date date = new Date(time);
            cmsData.setDate(date);
            httpContext.setSessionAttribute(CMS_DATE_KEY, date);
        }
        else
        {
            cmsData.setDate(new Date());
            httpContext.setSessionAttribute(CMS_DATE_KEY, null);
        }
    }
}
