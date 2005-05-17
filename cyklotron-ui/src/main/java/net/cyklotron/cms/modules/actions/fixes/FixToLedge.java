package net.cyklotron.cms.modules.actions.fixes;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

/**
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FixToLedge.java,v 1.2 2005-05-17 06:01:00 zwierzem Exp $
 */
public class FixToLedge
    extends BaseCMSAction
{
    public FixToLedge(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory)
    {
        super(logger, structureService, cmsDataFactory);
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        fixResClass(coralSession, "integration.resource_class",
            new String[]
            {"view", "aggregationUpdateAction", "aggregationCopyAction", "relatedQuickAddView"});
        fixResClass(coralSession, "integration.component",
            new String[] {"componentName", "configurationView", "aggregationSourceView"});
        fixResClass(coralSession, "integration.screen",
            new String[] {"screenName", "configurationView"});
    }

    private void fixResClass(CoralSession coralSession, String resClassName,
        String[] attributeNames)
        throws ProcessingException
    {
        QueryResults results;
        try
        {
            results = coralSession.getQuery().
                executeQuery("FIND RESOURCE FROM "+resClassName);
        }
        catch(MalformedQueryException e)
        {
            throw new ProcessingException("cannot get '"+resClassName+"' resources", e);
        }
        Resource[] nodes = results.getArray(1);
        for(Resource node : nodes)
        {
            boolean update = false;
            for (String attrName : attributeNames)
            {
                AttributeDefinition attrDef = node.getResourceClass().getAttribute(attrName);
                if(node.isDefined(attrDef))
                {
                    String value = (String) node.get(attrDef);
                    if(value != null && value.indexOf(',') > 0)
                    {
                        value = value.replace(',','.');
                        try
                        {
                            node.set(attrDef, value);
                        }
                        catch(Exception e)
                        {
                            logger.error("FixToLedge: Could not update attribute '"+attrName
                                +"' for resource "+node.toString(), e);
                        }
                        update = true;
                    }
                }
            }
            
            if(update)
            {
                node.update();
            }
        }
    }
}
