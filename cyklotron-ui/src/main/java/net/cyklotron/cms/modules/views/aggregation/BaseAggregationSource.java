package net.cyklotron.cms.modules.views.aggregation;

import java.util.Date;

import net.cyklotron.cms.CmsComponentData;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.labeo.Labeo;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.FinderService;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.webcore.Assembler;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.TemplateComponent;


/**
 * The base screen assember for aggregation source screens.
 *
 * @author <a href="mailto:zwierzem@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseAggregationSource.java,v 1.2 2005-01-24 10:27:09 pablo Exp $
 */
public abstract class BaseAggregationSource extends BaseCMSScreen
{
    /** finder service */
    protected FinderService finderService;
    
    public BaseAggregationSource()
    {
        finderService = (FinderService)(Labeo.getBroker().getService(FinderService.SERVICE_NAME));
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        // prepare component config in cms data
        CmsData cmsData = getCmsData();
        String instanceName = parameters.get("component_instance",null);
        String compApp = getComponentApplication(data);
        String compClass = getComponentClass(data);
        CmsComponentData componentData = cmsData.nextComponent(instanceName, compApp, compClass);

        // execute component
        try
        {
            TemplateComponent component = (TemplateComponent)(finderService.findAssembler(
                Assembler.COMPONENT, data, compApp, compClass));
            component.prepare(data, context);
        }
        catch(NotFoundException e)
        {
            throw new ProcessingException("could not find a provided component", e);
        }

        // setup context variables
        templatingContext.put("instance", instanceName);
        templatingContext.put("mode", parameters.get("mode",null));
        // TODO: set a link.self link
    }
    
    protected String getComponentApplication(RunData data)
    {
        return data.getApplication();
    }

    protected abstract String getComponentClass(RunData data);

    /** This method must work in a same way that it works during site browsing.
     * @see net.cyklotron.cms.modules.views.BaseSkinableScreen
     * @param data
     * @throws ProcessingException
     * @return
     */
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        if(data.getContext().containsKey("stackTrace"))
        {
            return true;
        }
        if(isNodeDefined())
        {
            return getNode().canView(coralSession.getUserSubject(), new Date());
        }
        else
        {
            return true;
        }
    }
}
