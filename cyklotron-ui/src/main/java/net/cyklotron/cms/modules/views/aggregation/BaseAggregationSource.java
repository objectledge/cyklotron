package net.cyklotron.cms.modules.views.aggregation;

import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsComponentData;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;


/**
 * The base screen assember for aggregation source screens.
 *
 * @author <a href="mailto:zwierzem@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseAggregationSource.java,v 1.5 2005-02-08 22:06:36 rafal Exp $
 */
public abstract class BaseAggregationSource extends BaseCMSScreen
{
    /** finder service */
    protected MVCFinder mvcFinder;
    
    public BaseAggregationSource(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        MVCFinder mvcFinder, TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.mvcFinder = mvcFinder;
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // prepare component config in cms data
        CmsData cmsData = getCmsData();
        String instanceName = parameters.get("component_instance",null);
        String compApp = getComponentApplication();
        String compClass = getComponentClass();
        CmsComponentData componentData = cmsData.nextComponent(instanceName, compApp, compClass);

        // execute component
        
        //TODO
        /**
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
         */
        // setup context variables
        templatingContext.put("instance", instanceName);
        templatingContext.put("mode", parameters.get("mode",null));
        // TODO: set a link.self link
    }
    
    protected String getComponentApplication()
    {
        return "cms";
    }

    protected abstract String getComponentClass();

    /** This method must work in a same way that it works during site browsing.
     * @see net.cyklotron.cms.modules.views.BaseSkinableScreen
     * @throws ProcessingException
     * @return true if the access is granted.
     */
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        if(templatingContext.containsKey("stackTrace"))
        {
            return true;
        }
        if(isNodeDefined())
        {
            return getNode().canView(context, coralSession.getUserSubject(), new Date());
        }
        else
        {
            return true;
        }
    }
}
