package net.cyklotron.cms.modules.views.category;

import java.util.Set;

import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.CategoryException;
import net.cyklotron.cms.category.CategoryResource;

/**
 * Screen for category editing.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FixCategoryAssignments.java,v 1.3 2005-01-25 11:23:54 pablo Exp $
 */
public class FixCategoryAssignments extends BaseCategoryScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        CategoryResource category = getCategory(coralSession, parameters);
        templatingContext.put("category", category);
        try
        {
            Set removedResources = categoryService.fixCategoryAssignments(category);
            templatingContext.put("resources", removedResources);
        }
        catch(CategoryException e)
        {
            templatingContext.put("result","exception");
            log.error("CategoryException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.modify");
    }
}
