/*
 * Created on Nov 7, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.actions.mail;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.mail.LedgeMessage;
import org.objectledge.mail.MailSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import java.util.Date;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

/**
 * @author <a href="pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id$
 */
public class SendSimpleFormEmail
    extends BaseCMSAction
{
    protected MailSystem mailService;
    
    public SendSimpleFormEmail(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, MailSystem mailSystem)
    {
        super(logger, structureService, cmsDataFactory);
        this.mailService = mailSystem;        
    }
    
        /**
     * {@inheritdoc}
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) 
        throws ProcessingException
    {
        try
        {
            String subject = parameters.get("subject","no-subject");
            String from = parameters.get("from",mailService.getSystemAddress());
            String template = parameters.get("template", "SimpleFormEmail");
            String[] emails = parameters.getStrings("to");
            if(emails.length == 0)
            {
                templatingContext.put("result", "address_missing");
                return;
            }
            InternetAddress[] addresses = new InternetAddress[emails.length];
            for(int i=0; i < emails.length; i++)
            {
                addresses[i]=new InternetAddress(emails[i]);
            }
            String[] paramNames = parameters.getParameterNames();
            for(int i = 0; i < paramNames.length; i++)
            {
                if(paramNames[i].startsWith("param_"))
                {
                    if(parameters.isDefined("req_"+paramNames[i]) &&
                       parameters.get(paramNames[i]).equals(""))
                    {
                        templatingContext.put("result","required_missing");
                        templatingContext.put("param_name", paramNames[i]);
                        return;
                    }
                }
            }
            LedgeMessage message = mailService.newMessage();
            I18nContext i18nContext = I18nContext.getI18nContext(context);
            message.setTemplate(i18nContext.getLocale(), "PLAIN", "mail/"+template);
            message.getContext().put("parameters", parameters);
            message.getMessage().setSentDate(new Date());
            message.getMessage().setFrom(new InternetAddress(from));
            message.getMessage().setSubject(subject);
            message.getMessage().setRecipients(Message.RecipientType.TO, addresses);
            byte[] bytes = message.getMessageBytes();
            System.out.println(new String(bytes));
            message.send(true);
            templatingContext.put("result","email_sent");
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
    }
    
    public boolean checkAccessRights(Context context) throws ProcessingException
    {
        return true;
    }  
    
    /**
     * @{inheritDoc}
     */
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return false;
    }

    @Override
    public boolean requiresSecureChannel(Context context)
        throws Exception
    {
        return false;
    }
  
    
}
