package net.cyklotron.cms.modules.views.test;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.CategoryInfoTool;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.modules.views.category.BaseCategoryScreen;

/**
 * Screen for batch category creation.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CreateCategories.java,v 1.3 2005-01-25 11:24:18 pablo Exp $
 */
public class CreateCategories extends BaseCategoryScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        if(parameters.isDefined("cat_id"))
        {
            CategoryResource category = getCategory(coralSession, parameters);
            templatingContext.put("category", category);
        }
        templatingContext.put("category_tool", new CategoryInfoTool(data));
    }
}
