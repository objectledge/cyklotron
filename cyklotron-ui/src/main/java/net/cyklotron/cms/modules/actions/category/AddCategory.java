package net.cyklotron.cms.modules.actions.category;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.site.SiteResource;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: AddCategory.java,v 1.1 2005-01-24 04:33:58 pablo Exp $
 */
public class AddCategory
    extends BaseCategoryAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        String name = parameters.get("name","");
        String description = parameters.get("description","");
        if(name.equals(""))
        {
            templatingContext.put("result","category_name_empty");
            return;
        }

        try
        {
            // get parent node for category
            Resource parent;
            if(parameters.get("cat_id").isDefined())
            {
                parent = getCategory(data);
            }
            else
            {
                SiteResource site = null;
                if(parameters.get("site_id").isDefined())
                {
                    site = getSite(context);
                }
                parent = categoryService.getCategoryRoot(site);
            }

            ResourceClassResource[] resourceClasses = getResourceClasses(data);

            CategoryResource newCategory =
                categoryService.addCategory(name, description, parent, subject, resourceClasses);
            parameters.set("cat_id", newCategory.getIdString());
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            log.error("add category",e);
            templatingContext.put("trace",StringUtils.stackTrace(e));
            return;
        }
        templatingContext.put("result","added_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.add");
    }
}
