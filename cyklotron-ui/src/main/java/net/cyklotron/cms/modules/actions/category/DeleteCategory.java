package net.cyklotron.cms.modules.actions.category;

import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.CategoryException;
import net.cyklotron.cms.category.CategoryResource;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DeleteCategory.java,v 1.1 2005-01-24 04:33:58 pablo Exp $
 */
public class DeleteCategory
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
   
        CategoryResource category = getCategory(data);
        try
        {
            categoryService.deleteCategory(category, subject);
            parameters.remove("cat_id");
        }
        catch(CategoryException e)
        {
            templatingContext.put("result","exception");
            log.error("CategoryException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.delete");
    }
}
