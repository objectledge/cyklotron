/*
 * Created on Nov 5, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.periodicals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.periodicals.EmailPeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.SubscriptionRequestResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Subscriptions
    extends BaseSkinableScreen
{
    /** Periodicals service */
    protected PeriodicalsService periodicalsService;    


    public Subscriptions(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        StructureService structureService, StyleService styleService, SkinService skinService,
        MVCFinder mvcFinder, TableStateManager tableStateManager, PeriodicalsService periodicalsService)
    {
        super(context, logger, preferencesService, cmsDataFactory, structureService, styleService,
                        skinService, mvcFinder, tableStateManager);
        this.periodicalsService = periodicalsService;
    }
    
    public void prepareDefault(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        try
        {
            EmailPeriodicalResource[] periodicals = periodicalsService.
                getEmailPeriodicals(coralSession, getSite());
            List list = Arrays.asList(periodicals);
            Collections.sort(list, new NameComparator(i18nContext.getLocale()));                
            templatingContext.put("periodicals", list);
        }
        catch(PeriodicalsException e)
        {
            throw new ProcessingException("failed to retrieve data", e);
        }
    }
    
    public void prepareTicketSent(Context context)
        throws ProcessingException
    {
        // does nothing
    }
    
    public void prepareInvalidTicket(Context context)
    {
        // does nothing
    }
    
    public void prepareEdit(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        try
        {
            String cookie = parameters.get("cookie");
            templatingContext.put("cookie", cookie);
            SiteResource site = getSite();
            SubscriptionRequestResource req = periodicalsService.getSubscriptionRequest(coralSession, cookie);
            templatingContext.put("email", req.getEmail());
            List periodicals = Arrays.asList(periodicalsService.getEmailPeriodicals(coralSession, site));
            List selectedList = Arrays.asList(periodicalsService.getSubscribedEmailPeriodicals(coralSession, site, req.getEmail()));
            Set selected = new HashSet(selectedList);
            templatingContext.put("periodicals", periodicals);
            templatingContext.put("selected", selected);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);
        }
    }
    
    public void prepareConfirm(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        try
        {
            String cookie = parameters.get("cookie");
            templatingContext.put("cookie", cookie);
            SubscriptionRequestResource req = periodicalsService.getSubscriptionRequest(coralSession, cookie);
            templatingContext.put("email", req.getEmail());
            StringTokenizer st = new StringTokenizer(req.getItems(), " ");
            List selected = new ArrayList();
            while (st.hasMoreTokens())
            {
                long periodicalId = Long.parseLong(st.nextToken());
                try
                {
                    Resource periodical = coralSession.getStore().getResource(periodicalId);
                    selected.add(periodical);
                }
                catch(EntityDoesNotExistException e)
                {
                    // periodical was deleted, ignore
                }
            }
            templatingContext.put("selected", selected);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);
        }
    }
    
    public String getState()
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        
        if("ticket_sent".equals(templatingContext.get("result")))
        {
            return "TicketSent";
        }
        String cookie = parameters.get("cookie","");
        if(cookie.length() > 0)
        {
            try
            {
                SubscriptionRequestResource req = periodicalsService.getSubscriptionRequest(coralSession, cookie);
                if (req == null)
                {
                    return "InvalidTicket";
                }
                else if (req.getItems() == null)
                {
                    return "Edit";
                }
                else
                {
                    return "Confirm";
                }
            }
            catch(Exception e)
            {
                throw new ProcessingException("failed to validate cookie", e);
            }
        }
        return "Default";
    }
}
