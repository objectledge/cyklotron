package net.cyklotron.cms.modules.actions.fixes;

import java.util.HashSet;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.schema.AttributeClass;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.schema.SchemaIntegrityException;
import org.objectledge.coral.schema.UnknownAttributeException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ConvertCategoryQuery2.java,v 1.3 2005-04-04 11:37:53 rafal Exp $
 */
public class ConvertCategoryQuery2
    extends BaseCMSAction
{
    private CategoryQueryService categoryQueryService;
    
    public ConvertCategoryQuery2(Logger logger, StructureService structureService,
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

            AttributeDefinition queryDef = catQueryResClass.getAttribute("query");
            cS.deleteAttribute(catQueryResClass, queryDef);
            AttributeDefinition def = catQueryResClass.getAttribute("requiredCategoryPaths");
            cS.deleteAttribute(catQueryResClass, def);
            def = catQueryResClass.getAttribute("optionalCategoryPaths");
            cS.deleteAttribute(catQueryResClass, def);

            AttributeClass textAttributeClass =  cS.getAttributeClass("text");
            queryDef = cS.createAttribute("query", textAttributeClass, null, 0);
            cS.addAttribute(catQueryResClass, queryDef, null);

            QueryResults results = coralSession.getQuery().
                executeQuery("FIND RESOURCE FROM "+CategoryQueryResource.CLASS_NAME);
            Resource[] nodes = results.getArray(1);
            HashSet<CategoryQueryResource> unfixed = new HashSet<CategoryQueryResource>(
                            nodes.length/100+1); 
            for(int i = 0; i < nodes.length; i++)
            {
                CategoryQueryResource res = (CategoryQueryResource)nodes[i];
                try
                {
                    if(res.isLongQueryDefined())
                    {
                        res.set(queryDef, res.getLongQuery());
                        res.update();
                    }
                }
                catch(UnknownAttributeException e)
                {
                    unfixed.add(res);
                }
                catch(ModificationNotPermitedException e)
                {
                    unfixed.add(res);
                }
                catch(ValueRequiredException e)
                {
                    unfixed.add(res);
                }
            }
            templatingContext.put("unfixedQueries", unfixed);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException(e);
        }
        catch(MalformedQueryException e)
        {
            throw new ProcessingException(e);
        }
        catch(SchemaIntegrityException e)
        {
            throw new ProcessingException(e);
        }
        catch(ValueRequiredException e)
        {
            throw new ProcessingException(e);
        }
    }
}
