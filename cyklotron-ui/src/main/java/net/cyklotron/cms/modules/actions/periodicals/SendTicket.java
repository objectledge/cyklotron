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

import net.labeo.Labeo;
import net.labeo.services.mail.LabeoMessage;
import net.labeo.services.mail.MailService;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * @author <a href="rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: SendTicket.java,v 1.1 2005-01-24 04:34:14 pablo Exp $
 */
public class SendTicket
    extends BasePeriodicalsAction
{
    protected MailService mailService;
    
    public SendTicket()
    {
        mailService = (MailService)Labeo.getBroker().
            getService(MailService.SERVICE_NAME);
    }

    /**
     * {@inheritdoc}
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) 
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        try
        {
            String email = parameters.get("email","");
            templatingContext.put("email", email);
            boolean subscribe = parameters.getBoolean("subscribe", false);
            templatingContext.put("subscribe", new Boolean(subscribe));
            String items = "";
            String[] selected = parameters.getStrings("selected");
            Set selectedSet = new HashSet();
            for (int i = 0; i < selected.length; i++)
            {
                items += selected[i];
                selectedSet.add(selected[i]);
                if(i < selected.length - 1)
                {
                    items += " ";
                }
            }
            templatingContext.put("selected", selectedSet);
            if(parameters.get("subscribe").asBoolean(true) && items.length() == 0)
            {
                templatingContext.put("result", "no_periodicals_selected");
                return;
            }
            if(email.length() == 0)
            {
                templatingContext.put("result", "address_missing");
                return;
            }
            String cookie = periodicalsService.createSubsriptionRequest(getSite(context), email, subscribe ? items : null);
            LabeoMessage message = mailService.newMessage();
            message.getContext().put("cookie", cookie);
            message.getContext().put("link", periodicalsService.getLinkRenderer());
            message.getContext().put("site", getSite(context));
            message.getContext().put("node", getNode(context));
            message.setTemplate(data, "PLAIN", "periodicals/ticket");
            message.getMessage().setSentDate(new Date());
            message.getMessage().setFrom(new InternetAddress(periodicalsService.getFromAddress()));
            message.getMessage().setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.send();
            templatingContext.put("result","ticket_sent");
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
    }
    
    public boolean checkAccess(RunData data) throws ProcessingException
    {
        return true;
    }  
    
    public boolean requiresLogin(RunData data)
            throws ProcessingException
    {
        return false;
    }
  
}
