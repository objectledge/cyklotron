package net.cyklotron.cms.modules.actions.appearance.style;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleResourceImpl;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddStyle.java,v 1.1 2005-01-24 04:34:43 pablo Exp $
 */
public class AddStyle
    extends BaseAppearanceAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        String name = parameters.get("name","");
        String description = parameters.get("description","");
        if(name.equals(""))
        {
            templatingContext.put("result", "required_field_missing");
            data.setView("appearance,AddStyle");
            return;
        }
        long parentId = parameters.getLong("style_id", -1);
        if (parentId == -1)
        {
            throw new ProcessingException("style id could not be found");
        }
        StyleResource parent = null;

        SiteResource site = getSite(context);
        try
        {
            parent = StyleResourceImpl.getStyleResource(coralSession,parentId);
        }
        catch (EntityDoesNotExistException e)
        {
            throw new ProcessingException("Style resource doesn't exist",e);
        }
        try
        {
            StyleResource style = styleService.
                addStyle(name, description, site, parent, coralSession.getUserSubject());
            parameters.set("style_id", style.getIdString());
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            log.error("failed to add style", e);
            templatingContext.put("trace", StringUtils.stackTrace(e));
            return;
        }
        templatingContext.put("result","added_successfully");
    }
}
