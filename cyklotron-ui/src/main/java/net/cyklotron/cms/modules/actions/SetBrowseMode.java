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
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: SetBrowseMode.java,v 1.1 2005-01-24 04:34:15 pablo Exp $
 */
public class SetBrowseMode extends BaseCMSAction
{
    /**
     * @param structureService
     * @param cmsDataFactory
     */
    public SetBrowseMode(Logger logger, StructureService structureService, 
        CmsDataFactory cmsDataFactory, StyleService styleService)
    {
        super(logger, structureService, cmsDataFactory);
    }

    
    /** 
     * 
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        if(parameters.isDefined("mode"))
        {
            String mode = parameters.get("mode");
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            if(!mode.equals("time_travel"))
            {
                cmsData.setDate(new Date());
                httpContext.setSessionAttribute(CMS_DATE_KEY, null);
            }
            cmsData.setBrowseMode(mode);
        }
    }
}
