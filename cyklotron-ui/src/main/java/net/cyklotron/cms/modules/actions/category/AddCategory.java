package net.cyklotron.cms.modules.actions.category;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: AddCategory.java,v 1.4 2005-05-19 04:42:29 zwierzem Exp $
 */
public class AddCategory
    extends BaseCategoryAction
{
    
    public AddCategory(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryService categoryService,
        IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, categoryService, integrationService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        String name = parameters.get("name","");
        String description = parameters.get("description","");
        String uiStyle = parameters.get("uiStyle","");
        Pattern illegalNamePattern = Pattern.compile("[()]");
        
        if(name.equals(""))
        {
            if(parameters.isDefined("res_class_id"))
            {
                templatingContext.put("selected_res_id", Arrays.asList(parameters
                    .getStrings("res_class_id")));
            }
            templatingContext.put("description", description);
            templatingContext.put("result","category_name_empty");
            return;
        }
        if(illegalNamePattern.matcher(name).find())
        {
            if(parameters.isDefined("res_class_id"))
            {
                templatingContext.put("selected_res_id", Arrays.asList(parameters
                    .getStrings("res_class_id")));
            }
            templatingContext.put("name", name);
            templatingContext.put("description", description);
            templatingContext.put("result", "invalid_category_name");
            return;
        }

        try
        {
            // get parent node for category
            Resource parent;
            if(parameters.isDefined("cat_id"))
            {
                parent = getCategory(coralSession, parameters);
            }
            else
            {
                SiteResource site = null;
                if(parameters.isDefined("site_id"))
                {
                    site = getSite(context);
                }
                parent = categoryService.getCategoryRoot(coralSession, site);
            }

            ResourceClassResource[] resourceClasses = getResourceClasses(coralSession, parameters);

            CategoryResource newCategory =
                categoryService.addCategory(coralSession, name, description, parent, 
                						resourceClasses, 
                						uiStyle);
            parameters.set("cat_id", newCategory.getIdString());
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            logger.error("add category",e);
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        templatingContext.put("result","added_successfully");
        mvcContext.setView("category.CategoryList");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.category.add");
    }
}
