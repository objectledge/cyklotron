package net.cyklotron.cms.modules.actions.category;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.CategoryException;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.integration.ResourceClassResource;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateCategory.java,v 1.1 2005-01-24 04:33:58 pablo Exp $
 */
public class UpdateCategory
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
   
        CategoryResource category = getCategory(data);
        Resource parent = category.getParent();
        ResourceClassResource[] resourceClasses = getResourceClasses(data);

        try
        {
            categoryService.updateCategory(category, name, description,
                                           parent, subject, resourceClasses); 
        }
        catch(CategoryException e)
        {
            templatingContext.put("result","exception");
            log.error("CategoryException: ",e);
            return;
        }
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.modify");
    }
}
