package net.cyklotron.cms.modules.actions.category;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryException;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.BaseLinkResourceImpl;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateCategory.java,v 1.5 2005-06-13 11:08:34 rafal Exp $
 */
public class UpdateCategory
    extends BaseCategoryAction
{
    
    public UpdateCategory(Logger logger, StructureService structureService,
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
        Long linkId = parameters.getLong("link_id",-1L);
        String uiStyle = parameters.get("uiStyle","");
        Pattern illegalNamePattern = Pattern.compile("[()]");
        if(name.equals(""))
        {
            templatingContext.put("result","category_name_empty");
            return;
        }
        if(illegalNamePattern.matcher(name).find())
        {
            templatingContext.put("result", "invalid_category_name");
            return;
        }
   
        CategoryResource category = getCategory(coralSession, parameters);
        Resource parent = category.getParent();
        ResourceClassResource[] resourceClasses = getResourceClasses(coralSession, parameters);

        try
        {
            BaseLinkResource link = null;
            if(linkId!=-1L){
                link = BaseLinkResourceImpl.getBaseLinkResource(coralSession, linkId);
            }
            categoryService.updateCategory(coralSession, category, name, description, link, parent,
                resourceClasses, uiStyle); 
        }
        catch(CategoryException e)
        {
            templatingContext.put("result","exception");
            logger.error("CategoryException: ",e);
            return;
        }
        catch(InvalidResourceNameException e)
        {
            templatingContext.put("result","invalid_name");
            return;
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","invalid_link");
            logger.error("CategoryException: ",e);
            return;
        }
        templatingContext.put("result","updated_successfully");
        mvcContext.setView("category.CategoryList");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.category.modify");
    }
}
