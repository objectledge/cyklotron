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
 * @version $Id: FixCategoryAssignments.java,v 1.1 2005-01-24 04:34:27 pablo Exp $
 */
public class FixCategoryAssignments extends BaseCategoryScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        CategoryResource category = getCategory(data);
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
            templatingContext.put("trace", StringUtils.stackTrace(e));
            return;
        }
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.modify");
    }
}
