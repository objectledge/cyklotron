package net.cyklotron.cms.modules.actions.fixes;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ConvertCategoryQuery3.java,v 1.4 2007-11-18 21:24:37 rafal Exp $
 */
public class ConvertCategoryQuery3
    extends BaseCMSAction
{
    private CategoryQueryService categoryQueryService;
    
    public ConvertCategoryQuery3(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory)
    {
        super(logger, structureService, cmsDataFactory);
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            CoralSchema cS = coralSession.getSchema();
            
            ResourceClass catQueryResClass =
                cS.getResourceClass(CategoryQueryResource.CLASS_NAME);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException(e);
        }
    }  
    
    /**
     * @{inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        return false;
    }
    
    
}
