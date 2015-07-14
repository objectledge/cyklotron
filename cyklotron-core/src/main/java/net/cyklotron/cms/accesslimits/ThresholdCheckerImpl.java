package net.cyklotron.cms.accesslimits;

import java.net.InetAddress;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.coral.entity.AmbigousEntityNameException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.mail.LedgeMessage;
import org.objectledge.mail.MailSystem;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.ratelimit.impl.AccessListRegistry;
import org.objectledge.web.ratelimit.impl.HitTable.Hit;
import org.objectledge.web.ratelimit.impl.ThresholdChecker;

public class ThresholdCheckerImpl
    implements ThresholdChecker
{
    private static final String CONFIG_RES_PATH = "/cms/accesslimits/notifications";

    private AccessListRegistry accessListRegistry;

    private MailSystem mailSystem;

    private NotificationsConfigResource config;

    private Logger log;

    public ThresholdCheckerImpl(AccessListRegistry accessListRegistry, MailSystem mailSystem,
        CoralSessionFactory coralSessionFactory, Logger log)
    {
        this.accessListRegistry = accessListRegistry;
        this.mailSystem = mailSystem;
        this.log = log;
        try(CoralSession coralSession = coralSessionFactory.getRootSession())
        {
            config = (NotificationsConfigResource)coralSession.getStore().getUniqueResourceByPath(
                CONFIG_RES_PATH);
        }
        catch(EntityDoesNotExistException | AmbigousEntityNameException e)
        {
            throw new ComponentInitializationError(e);
        }
    }

    @Override
    public boolean isThresholdExceeded(InetAddress address, Hit hit)
    {
        if(hit.getHits() > config.getThreshold())
        {
            if(!accessListRegistry.anyContains(address))
            {
                sendNotification(address, hit);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    private void sendNotification(InetAddress address, Hit hit)
    {
        try
        {
            LedgeMessage msg = mailSystem.newMessage();
            msg.setTemplate(StringUtils.getLocale(config.getLocale()), "HTML",
                "accesslimits/Notification");
            msg.getMessage().setFrom(new InternetAddress(mailSystem.getSystemAddress()));
            msg.getMessage().setRecipient(Message.RecipientType.TO,
                new InternetAddress(config.getRecipient()));
            msg.getContext().put("baseURL", config.getBaseURL());
            msg.getContext().put("address", address);
            msg.getContext().put("hit", hit);
            msg.send(false);
        }
        catch(TemplateNotFoundException | MessagingException | MergingException e)
        {
            log.error("failed to send access limits violation notification", e);
        }
    }
}
