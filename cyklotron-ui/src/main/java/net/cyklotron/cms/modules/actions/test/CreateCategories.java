package net.cyklotron.cms.modules.actions.test;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.modules.actions.category.BaseCategoryAction;
import net.cyklotron.cms.site.SiteResource;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CreateCategories.java,v 1.1 2005-01-24 04:34:32 pablo Exp $
 */
public class CreateCategories
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
            
            int levels = parameters.getInt("levels", 1);
            int childcount = parameters.getInt("childcount", 1);
            createCats(levels, childcount, 1, name, newCategory, subject, new ResourceClassResource[0]);
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

    private void createCats(
        int levels,
        int childcount,
        int l,
        String name,
        CategoryResource parent,
        Subject subject,
        ResourceClassResource[] resClasses)
        throws Exception
    {
        if(l >= levels)
        {
            return;
        }
        for(int c = 0; c < childcount; c++)
        {
            String catName = name + "." + l +"."+ (c+1);
            CategoryResource childCat =
                categoryService.addCategory(catName, null, parent, subject, resClasses);
            createCats(levels, childcount, l+1, name, childCat, subject, resClasses);
        }                
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkAdministrator(context, coralSession);
    }
}
