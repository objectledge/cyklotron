package net.cyklotron.cms.modules.actions.category;

import java.util.ArrayList;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryConstants;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.CategoryUtil;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseCategoryAction.java,v 1.2 2005-01-24 10:27:04 pablo Exp $
 */
public abstract class BaseCategoryAction 
    extends BaseCMSAction 
    implements CategoryConstants
{
    /** category service */
    protected CategoryService categoryService;
    /** Integration service for information on resource classes */
    protected IntegrationService integrationService;
   
    public BaseCategoryAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryService categoryService,
        IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory);
        this.categoryService = categoryService;
        this.integrationService = integrationService;
    }

    public CategoryResource getCategory(CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
        return CategoryUtil.getCategory(coralSession, parameters);
    }

    public ResourceClassResource[] getResourceClasses(CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
        ResourceClassResource[] resClasses;
        if(parameters.isDefined("res_class_id"))
        {
            try
            {
                ArrayList resClassesTemp = new ArrayList();
                long[] resClassIds = parameters.getLongs("res_class_id");
                for(int i=0; i<resClassIds.length; i++)
                {
                    Resource resClass = coralSession.getStore().getResource(resClassIds[i]);
                    if(resClass instanceof ResourceClassResource)
                    {
                        resClassesTemp.add(resClass);
                    }
                    else
                    {
                        // WARN: Malicious users
                        throw new ProcessingException(
                            "Cannot link arbitrary resources as resource class resources");
                    }
                }
                
                resClasses = new ResourceClassResource[resClassesTemp.size()];
                for(int i=0; i < resClasses.length; i++)
                {
                    resClasses[i] = (ResourceClassResource)(resClassesTemp.get(i));
                }
            }
            catch(EntityDoesNotExistException e)
            {
                throw new ProcessingException("Cannot link not existing resource class resources");
            }
        }
        else
        {
            resClasses = new ResourceClassResource[0];
        }
        return resClasses;
    }

    /**
     * Checks if the current user has the specific permission on the current category.
     */
    public boolean checkPermission(Context context, CoralSession coralSession, String permissionName)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        return CategoryUtil.checkPermission(coralSession, parameters, permissionName);
    }
}


