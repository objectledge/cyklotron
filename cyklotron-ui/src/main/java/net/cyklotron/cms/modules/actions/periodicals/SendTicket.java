/*
 * Created on Nov 7, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.actions.periodicals;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.mail.LedgeMessage;
import org.objectledge.mail.MailSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.confirmation.EmailConfirmationService;
import net.cyklotron.cms.documents.LinkRenderer;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PeriodicalsSubscriptionService;
import net.cyklotron.cms.periodicals.PeriodicalsTemplatingService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

/**
 * @author <a href="rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: SendTicket.java,v 1.6 2006-05-16 09:48:00 rafal Exp $
 */
public class SendTicket
    extends BasePeriodicalsAction
{
    protected MailSystem mailService;

    private final PeriodicalsSubscriptionService periodicalsSubscriptionService;

    private final EmailConfirmationService emailConfirmationService;

    private final PeriodicalsTemplatingService periodicalsTemplatingService;

    public SendTicket(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PeriodicalsService periodicalsService,
        PeriodicalsSubscriptionService periodicalsSubscriptionService,
        PeriodicalsTemplatingService periodicalsTemplatingService, SiteService siteService,
        MailSystem mailSystem, EmailConfirmationService emailConfirmationService)
    {
        super(logger, structureService, cmsDataFactory, periodicalsService, siteService);
        this.periodicalsSubscriptionService = periodicalsSubscriptionService;
        this.periodicalsTemplatingService = periodicalsTemplatingService;
        this.mailService = mailSystem;
        this.emailConfirmationService = emailConfirmationService;
    }

    /**
     * {@inheritdoc}
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            String email = parameters.get("email", "");
            templatingContext.put("email", email);
            boolean subscribe = parameters.getBoolean("subscribe", false);
            templatingContext.put("subscribe", new Boolean(subscribe));
            String items = "";
            String[] selected = parameters.getStrings("selected");
            Set selectedSet = new HashSet();
            for(int i = 0; i < selected.length; i++)
            {
                items += selected[i];
                selectedSet.add(selected[i]);
                if(i < selected.length - 1)
                {
                    items += " ";
                }
            }
            templatingContext.put("selected", selectedSet);
            if(parameters.getBoolean("subscribe", true) && items.length() == 0)
            {
                templatingContext.put("result", "no_periodicals_selected");
                return;
            }
            if(email.length() == 0)
            {
                templatingContext.put("result", "address_missing");
                return;
            }
            String cookie = periodicalsSubscriptionService.createSubscriptionRequest(coralSession,
                getSite(context), email, subscribe ? items : null);

            NavigationNodeResource node = getNode(context);
            LinkRenderer linkRenderer = periodicalsService.getLinkRenderer();
            String ticketVariant = parameters.get("ticket_variant", "default");
            Template template = periodicalsTemplatingService.getConfirmationTicketTemplate(node
                .getSite(), ticketVariant);
            if(template == null)
            {
                I18nContext i18nContext = I18nContext.getI18nContext(context);
                template = periodicalsTemplatingService
                    .getDefaultConfirmationTicketTemplate(i18nContext.getLocale());                
            }

            emailConfirmationService.sendConfirmationRequest(cookie, periodicalsService
                .getFromAddress(), email, node, template, "PLAIN", linkRenderer, coralSession);

            templatingContext.put("result", "ticket_sent");
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return true;
    }

    /**
     * @{inheritDoc
     */
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return false;
    }

}
