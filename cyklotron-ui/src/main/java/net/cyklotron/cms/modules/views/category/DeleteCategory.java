package net.cyklotron.cms.modules.views.category;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.CategoryInfoTool;
import net.cyklotron.cms.category.CategoryResource;

/**
 * Screen showing available categories, presented as tree.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DeleteCategory.java,v 1.3 2005-01-25 11:23:54 pablo Exp $
 */
public class DeleteCategory extends BaseCategoryScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        CategoryResource category = getCategory(coralSession, parameters);
        templatingContext.put("category", category);
        templatingContext.put("category_tool", new CategoryInfoTool(data));
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.delete");
    }
}
