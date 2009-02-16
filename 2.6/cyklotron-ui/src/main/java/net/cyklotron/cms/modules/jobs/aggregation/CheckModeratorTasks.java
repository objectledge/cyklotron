package net.cyklotron.cms.modules.jobs.aggregation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.i18n.I18n;
import org.objectledge.mail.LedgeMessage;
import org.objectledge.mail.MailSystem;
import org.objectledge.scheduler.Job;

import net.cyklotron.cms.aggregation.AggregationConstants;
import net.cyklotron.cms.aggregation.AggregationNode;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.aggregation.ImportResource;
import net.cyklotron.cms.aggregation.RecommendationResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * A job that checks whether any tasks appear in task list.
 *
 */
public class CheckModeratorTasks extends Job
{
    // instance variables ////////////////////////////////////////////////////

    /** logging facility */
    private Logger log;

    /** mail service */
    private MailSystem mailSystem;

	/** i18 service */
	private I18n i18n;

    private CoralSessionFactory sessionFactory;
    
    private SiteService siteService;
    
    private AggregationService aggregationService;
    
    // initialization ///////////////////////////////////////////////////////

    /**
     *
     */
    public CheckModeratorTasks(Logger logger, MailSystem mailSystem,
        I18n i18n, CoralSessionFactory sessionFactory, SiteService siteService,
        AggregationService aggregationService)
    {            
        this.log = logger;
        this.mailSystem = mailSystem;
        this.i18n = i18n;
        this.sessionFactory = sessionFactory;
        this.siteService = siteService;
        this.aggregationService = aggregationService;
    }

    // Job interface ////////////////////////////////////////////////////////

    /**
     * Performs the mainteance.
     */
    public void run(String[] args)
    {
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            checkMessages(coralSession);
        }
        finally
        {
            coralSession.close();
        }
    }

    private void checkMessages(CoralSession coralSession)
    {
        try
        {
			Locale locale = new Locale("pl","PL");
            Map<String, Set<SiteResource>> receipients = new HashMap<String, Set<SiteResource>>();
            Map<SiteResource, List> pendingMap = new HashMap<SiteResource, List>();
            Map<SiteResource, List> submittedMap = new HashMap<SiteResource, List>();
            Map<SiteResource, List> changedMap = new HashMap<SiteResource, List>();
            
            SiteResource[] availableSites = siteService.getSites(coralSession);
            for(SiteResource site: availableSites)
            {
                RecommendationResource[] pending = aggregationService.
                    getPendingRecommendations(coralSession, site);
                ArrayList<RecommendationResource> pendingList = new ArrayList<RecommendationResource>(pending.length);
                for(RecommendationResource res: pending)
                {
                    pendingList.add(res);
                }
                RecommendationResource[] submitted = aggregationService.
                    getSubmittedRecommendations(coralSession, site, coralSession.getUserSubject());
                ArrayList<RecommendationResource> submittedList = new ArrayList<RecommendationResource>(submitted.length);
                for(RecommendationResource res: submitted)
                {
                    submittedList.add(res);
                }
                ImportResource[] imports = aggregationService.getImports(coralSession, site);
                ArrayList<ImportResource> changed = new ArrayList<ImportResource>(imports.length);
                for (int i = 0; i < imports.length; i++)
                {
                    if(imports[i].getState(coralSession) == AggregationConstants.IMPORT_MODIFIED)
                    {
                        changed.add(imports[i]);
                    }
                }
                if(pendingList.size() > 0 || submittedList.size() > 0 || changed.size() > 0)
                {
                    pendingMap.put(site, pendingList);
                    submittedMap.put(site, submittedList);
                    changedMap.put(site, changed);
                    AggregationNode node = aggregationService.getAggregationRoot(coralSession, site, false);
                    if(node == null)
                    {
                        continue;
                    }
                    String replyTo = node.getReplyTo();
                    if (replyTo != null && replyTo.length() > 0)
                    {
                        StringTokenizer st = new StringTokenizer(replyTo, "\n", false);
                        while (st.hasMoreTokens())
                        {
                            String email = st.nextToken();
                            Set<SiteResource> sites = (Set<SiteResource>)receipients.get(email);
                            if (sites == null)
                            {
                                sites = new HashSet<SiteResource>();
                                receipients.put(email, sites);
                            }
                            sites.add(site);
                        }
                    }
                }
            
            }
            
            // map = email -> map(forum->set(messages)) 
            
            Date date = new Date();
            Iterator i = receipients.keySet().iterator();
            while (i.hasNext())
            {
                String email = (String)i.next();
                Set<SiteResource> sites = receipients.get(email);
                try
                {
                    LedgeMessage msg = mailSystem.newMessage();
                    msg.setTemplate(locale, "PLAIN", "aggregation/ModeratorReminder");
                    msg.getContext().put("sites", sites);
                    msg.getContext().put("pendingMap", pendingMap);
                    msg.getContext().put("submittedMap", submittedMap);
                    msg.getContext().put("changedMap", changedMap);
                    msg.getMessage().setSubject(i18n.get(locale, "messages.aggregation.moderator_tasks_subject"));                     
                    msg.getMessage().setFrom(new InternetAddress(mailSystem.getSystemAddress()));
                    msg.getMessage().setRecipient(Message.RecipientType.TO, new InternetAddress(email));
                    msg.getMessage().setSentDate(date);
                    msg.send(false);
                }
                catch (Exception e)
                {
					log.error("Filed to send message to: "+email);
                    log.error("Exception occured", e);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Exception occured", e);
        }
    }

}
