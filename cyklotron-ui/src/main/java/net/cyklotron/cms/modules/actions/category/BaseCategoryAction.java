package net.cyklotron.cms.modules.actions.category;

import java.util.ArrayList;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;

import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.category.CategoryConstants;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.CategoryUtil;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.modules.actions.BaseCMSAction;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseCategoryAction.java,v 1.1 2005-01-24 04:33:58 pablo Exp $
 */
public abstract class BaseCategoryAction extends BaseCMSAction implements CategoryConstants, Secure
{
    /** logging facility */
    protected Logger log;

    /** category service */
    protected CategoryService categoryService;
    /** Integration service for information on resource classes */
    protected IntegrationService integrationService;
   
    public BaseCategoryAction()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME))
                .getFacility(CategoryService.LOGGING_FACILITY);
        categoryService = (CategoryService)broker.getService(CategoryService.SERVICE_NAME);
        integrationService = (IntegrationService)broker.getService(IntegrationService.SERVICE_NAME);
    }

    public CategoryResource getCategory(RunData data)
        throws ProcessingException
    {
        return CategoryUtil.getCategory(coralSession, data);
    }

    public ResourceClassResource[] getResourceClasses(RunData data)
        throws ProcessingException
    {
        ResourceClassResource[] resClasses;
        if(parameters.get("res_class_id").isDefined())
        {
            try
            {
                ArrayList resClassesTemp = new ArrayList();

                Parameter[] resClassIds = parameters.getArray("res_class_id");
                for(int i=0; i<resClassIds.length; i++)
                {
                    Resource resClass = coralSession.getStore().getResource(resClassIds[i].asLong());
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
        return CategoryUtil.checkPermission(coralSession, data, permissionName);
    }
}


