package net.cyklotron.cms.modules.views.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 *
 */
public class EditNode
    extends BaseStructureScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        SiteResource site = getSite();

        try
        {
            templatingContext.put("styles", Arrays.asList(styleService.getStyles(site)));
        }
        catch (Exception e)
        {
            log.error("Exception :",e);
            throw new ProcessingException("failed to lookup available styles", e);
        }
        List priorities = new ArrayList();
        for(int i = 0; i < 10; i++)
        {
        	priorities.add(new Integer(i));
        }
        templatingContext.put("priorities", priorities);
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return getCmsData().getNode().canModify(coralSession.getUserSubject());
    }
}
