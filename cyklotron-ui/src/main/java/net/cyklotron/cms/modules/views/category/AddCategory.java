package net.cyklotron.cms.modules.views.category;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.CategoryInfoTool;
import net.cyklotron.cms.category.CategoryResource;

/**
 * Screen for category addition.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: AddCategory.java,v 1.1 2005-01-24 04:34:27 pablo Exp $
 */
public class AddCategory extends BaseCategoryScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        if(parameters.get("cat_id").isDefined())
        {
            CategoryResource category = getCategory(data);
            templatingContext.put("category", category);
        }
        templatingContext.put("category_tool", new CategoryInfoTool(data));
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.add");
    }
}
