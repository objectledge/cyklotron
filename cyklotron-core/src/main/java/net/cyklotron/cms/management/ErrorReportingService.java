package net.cyklotron.cms.management;

import java.util.Date;
import java.util.Locale;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.jcontainer.dna.Configuration;
import org.objectledge.mail.LedgeMessage;
import org.objectledge.mail.MailSystem;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StringUtils;

/**
 * Sends error reports by email.
 * <p>
 * This service is a singleton companion to {@link net.cyklotorn.cms.modules.actions.ReportError}
 * action. Reports are sent only if the user clicks appropriate link, so frequency of received
 * reports can be used as a metric of user frustration with the particular bug.
 * </p>
 * <p>
 * The layout of error reports can be customized by overriding
 * /templates/mail/management/ErrorReport_PLAIN_*locale*.vt templates in the application's working
 * directory. Make sure template in the locale selected in the service's configuration is available.
 * </p>
 * 
 * @author rafal
 * @since 2.7
 */
public class ErrorReportingService
{
    /** TO header of report messages */
    private final String reportRecipient;

    /** FROM header of report messages */
    private final String reportSender;

    /** MailSystem component */
    private final MailSystem mailSystem;

    /** Locale of the messages */
    private final Locale reportLocale;

    public ErrorReportingService(Configuration config, MailSystem mailSystem)
    {
        this.mailSystem = mailSystem;
        reportRecipient = config.getChild("ReportRecipient").getValue(null);
        reportSender = config.getChild("ReportSender").getValue(reportRecipient);
        reportLocale = StringUtils.getLocale(config.getChild("ReportLocale").getValue("en_US"));
    }

    public void reportError(String requestMarker, String time, String url, String stackTrace,
        String additionalInfo)
        throws TemplateNotFoundException, MessagingException, MergingException
    {
        if(reportRecipient != null)
        {
            LedgeMessage message = mailSystem.newMessage();
            TemplatingContext context = message.getContext();
            context.put("requestMarker", requestMarker);
            context.put("time", time);
            context.put("url", url);
            context.put("stackTrace", stackTrace);
            context.put("additionalInfo", additionalInfo);
            message.setTemplate(reportLocale, "PLAIN", "management/ErrorReport");
            message.getMessage().setFrom(new InternetAddress(reportSender));
            message.getMessage().setRecipient(Message.RecipientType.TO,
                new InternetAddress(reportRecipient));
            message.getMessage().setSentDate(new Date());
            message.send();
        }
    }
}
