package net.cyklotron.cms.periodicals.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import pl.caltha.encodings.HTMLEntityEncoder;

import net.labeo.LabeoRuntimeException;
import net.labeo.services.BaseService;
import net.labeo.services.InitializationError;
import net.labeo.services.ServiceBroker;
import net.labeo.services.file.FileService;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.mail.LabeoMessage;
import net.labeo.services.mail.MailService;
import net.labeo.services.pool.Factory;
import net.labeo.services.pool.PoolService;
import net.labeo.services.pool.Recyclable;
import net.labeo.services.resource.AmbigousNameException;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.query.QueryResults;
import net.labeo.services.templating.Template;
import net.labeo.services.templating.TemplateNotFoundException;
import net.labeo.services.templating.TemplatingService;
import net.labeo.services.webcore.WebcoreService;
import net.labeo.util.ObjectUtils;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;

import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.documents.LinkRenderer;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.files.RootDirectoryResource;
import net.cyklotron.cms.periodicals.EmailPeriodicalResource;
import net.cyklotron.cms.periodicals.EmailPeriodicalsRootResource;
import net.cyklotron.cms.periodicals.EmailPeriodicalsRootResourceImpl;
import net.cyklotron.cms.periodicals.PeriodicalRenderer;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsNodeResource;
import net.cyklotron.cms.periodicals.PeriodicalsNodeResourceImpl;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PublicationTimeResource;
import net.cyklotron.cms.periodicals.SubscriptionRequestResource;
import net.cyklotron.cms.periodicals.SubscriptionRequestResourceImpl;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * A generic implementation of the periodicals service.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PeriodicalsServiceImpl.java,v 1.1 2005-01-12 20:44:44 pablo Exp $
 */
public class PeriodicalsServiceImpl 
    extends BaseService 
    implements PeriodicalsService
{
    // constants ////////////////////////////////////////////////////////////
    
    /** serverName parameter key. */
    public static final String SERVER_NAME_KEY = "server";

    /** port parameter key. */
    public static final String PORT_KEY = "port";
    
    /** port default value. */
    public static final int PORT_DEFAULT = 80;
    
    /** context parameter key. */
    public static final String CONTEXT_KEY = "context";
    
    /** context parameter default value. */
    public static final String CONTEXT_DEFAULT = "/";
    
    /** servletAndApp parameter key. */
    public static final String SERVLET_AND_APP_KEY = "servletAndApp";
    
    /** servletAndApp defaault value. */
    public static final String SERVLET_AND_APP_DEFAULT = "labeo/app/cms/";
    
    /** messages from parameter key. */
    public static final String MESSAGES_FROM_KEY = "messagesFrom";
    
    // instance variables ///////////////////////////////////////////////////
    
    /** resource service */
    private ResourceService resourceService;
    
    /** category query service. */
    private CategoryQueryService categoryQueryService;
    
    /** pool service. */
    private PoolService poolService;
    
    /** cms files service */
    private FilesService cmsFilesService;
    
    /** mail service */
    private MailService mailService;
    
    /** file service. */
    private FileService fileService;
    
    /** webcore service. */
    private WebcoreService webcoreService;

    /** templating service. */
    private TemplatingService templatingService;
    
    /** site service. */
    private SiteService siteService;
    
    /** log */
    private LoggingFacility log;
    
    /** renderer classes */
    private Map rendererClass = new HashMap();

    /** root subject */
    private Subject rootSubject;
    
    /** template encoding in the FS */
    private String templateEncoding;

    /** default server name used when no site alias is selected. */
    private String serverName;

    /** server port to use. "80" by default. */
    private int port;
    
    /** context name. "/" by default. */
    private String context;
    
    /** servlet name and application bit. "labeo/app/cms/" by default. */
    private String servletAndApp;
    
    /** messages from address. */
    private String messagesFrom;

    /** pseudo-random number generator */
    private Random random;
    
    public void init()
    {
        resourceService = (ResourceService)broker.getService(ResourceService.SERVICE_NAME);
        categoryQueryService = 
            (CategoryQueryService)broker.getService(CategoryQueryService.SERVICE_NAME);
        poolService = (PoolService)broker.getService(PoolService.SERVICE_NAME);
        cmsFilesService = (FilesService)broker.getService(FilesService.SERVICE_NAME);
        mailService = (MailService)broker.getService(MailService.SERVICE_NAME);
        fileService = (FileService)broker.getService(FileService.SERVICE_NAME);
        templatingService = (TemplatingService)broker.getService(TemplatingService.SERVICE_NAME);
        templateEncoding = templatingService.getTemplateEncoding();
        webcoreService = (WebcoreService)broker.getService(WebcoreService.SERVICE_NAME);
        siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility(PeriodicalsService.LOGGING_FACILITY);
        try
        {
            rootSubject = resourceService.getSecurity().getSubject(Subject.ROOT);
        }
        catch (EntityDoesNotExistException e)
        {
            throw new InitializationError("Could not find the root subject", e);
        }
        Configuration renderersConfig = getConfiguration().getSubset("renderer.");
        String[] rendererNames = renderersConfig.getSubsetNames(); 
        for (int i = 0; i < rendererNames.length; i++)
        {
            String renderer = rendererNames[i];
            Configuration config = renderersConfig.getSubset(renderer+".");
            String cn = config.get("classname").asString(); 
            Class cl;
            try
            {
                cl = ObjectUtils.loadClass(cn);
            }
            catch(LabeoRuntimeException e)
            {
                throw new InitializationError(e.getMessage(), e.getRootCause());
            }
            if(poolService.getFactory(cl) == null)
            {
                // WARNING this is broken due to pool service design flaw!
                // do not use renderer's configs until it's fixed.
                poolService.registerFactory(getFactory(cl, config, broker, log));
            }
            rendererClass.put(renderer, ObjectUtils.loadClass(cn));
        }
        serverName = getConfiguration().get(SERVER_NAME_KEY).asString(); // no default
        port = getConfiguration().get(PORT_KEY).asInt(PORT_DEFAULT);
        context = getConfiguration().get(CONTEXT_KEY).asString(CONTEXT_DEFAULT);
        servletAndApp = getConfiguration().get(SERVLET_AND_APP_KEY).
            asString(SERVLET_AND_APP_DEFAULT);
        messagesFrom = getConfiguration().get(MESSAGES_FROM_KEY).
            asString("noreply@"+serverName);
        random = new Random();
    }

    /**
     * List the periodicals existing in the site.
     * 
     * @param site the site.
     * @return array of periodicals.
     */
    public PeriodicalResource[] getPeriodicals(SiteResource site) throws PeriodicalsException
    {
        PeriodicalsNodeResource periodicalsRoot = getPeriodicalsRoot(site);
        Resource[] resources = resourceService.getStore().getResource(periodicalsRoot);
        PeriodicalResource[] periodicals = new PeriodicalResource[resources.length];
        System.arraycopy(resources, 0, periodicals, 0, resources.length);
        return periodicals;
    }

    /**
     * List the email periodicals existing in the site.
     * 
     * @param site the site.
     * @return array of periodicals.
     */
    public EmailPeriodicalResource[] getEmailPeriodicals(SiteResource site) throws PeriodicalsException
    {
        PeriodicalsNodeResource periodicalsRoot = getEmailPeriodicalsRoot(site);
        Resource[] resources = resourceService.getStore().getResource(periodicalsRoot);
        EmailPeriodicalResource[] periodicals = new EmailPeriodicalResource[resources.length];
        System.arraycopy(resources, 0, periodicals, 0, resources.length);
        return periodicals;
    }

	/**
	 * Return the root node for periodicals
	 * 
	 * @param site the site.
	 * @return the periodicals root.
	 */
    public PeriodicalsNodeResource getPeriodicalsRoot(SiteResource site) throws PeriodicalsException
    {
        PeriodicalsNodeResource applicationRoot = getApplicationRoot(site);
        Resource[] res = resourceService.getStore().getResource(applicationRoot, "periodicals");
        if (res.length == 0)
        {
            try
            {
                return PeriodicalsNodeResourceImpl.createPeriodicalsNodeResource(resourceService, "periodicals", applicationRoot, rootSubject);
            }
            catch (ValueRequiredException e)
            {
                throw new PeriodicalsException("faild to create periodicals root node", e);
            }
        }
        else
        {
            return (PeriodicalsNodeResource)res[0];
        }
    }

	/**
	 * Return the root node for email periodicals
	 * 
	 * @param site the site.
	 * @return the periodicals root.
	 */
    public EmailPeriodicalsRootResource getEmailPeriodicalsRoot(SiteResource site) throws PeriodicalsException
    {
        PeriodicalsNodeResource applicationRoot = getApplicationRoot(site);
        Resource[] res = resourceService.getStore().getResource(applicationRoot, "email_periodicals");
        if (res.length == 0)
        {
            try
            {
                return EmailPeriodicalsRootResourceImpl.createEmailPeriodicalsRootResource(resourceService, "email_periodicals", applicationRoot, rootSubject);
            }
            catch (ValueRequiredException e)
            {
                throw new PeriodicalsException("faild to create periodicals root node", e);
            }
        }
        else
        {
            return (EmailPeriodicalsRootResource)res[0];
        }
    }

    /**
     * Return the root node for email periodicals
     * 
     * @param site the site.
     * @return the periodicals root.
     */
    public PeriodicalsNodeResource getSubscriptionChangeRequestsRoot(SiteResource site) throws PeriodicalsException
    {
        PeriodicalsNodeResource applicationRoot = getApplicationRoot(site);
        Resource[] res = resourceService.getStore().getResource(applicationRoot, "requests");
        if (res.length == 0)
        {
            try
            {
                return PeriodicalsNodeResourceImpl.createPeriodicalsNodeResource(resourceService, "requests", applicationRoot, rootSubject);
            }
            catch (ValueRequiredException e)
            {
                throw new PeriodicalsException("faild to create requests root node", e);
            }
        }
        else
        {
            return (PeriodicalsNodeResource)res[0];
        }
    }


    // mail from address ////////////////////////////////////////////////////
    
    // inherit doc
    public String getFromAddress()
    {
        return messagesFrom;
    }

    // interit doc
    public synchronized String createSubsriptionRequest(SiteResource site, String email, String items)
        throws PeriodicalsException
    {
        Resource root = getSubscriptionChangeRequestsRoot(site);
        String cookie;
        Resource[] res;
        do
        {
            cookie = getRandomCookie();
            res = resourceService.getStore().getResource(root, cookie);
        }
        while(res.length > 0);
        try
        {
            SubscriptionRequestResource request = SubscriptionRequestResourceImpl.
                createSubscriptionRequestResource(resourceService, cookie, root, email, rootSubject);
            request.setItems(items);
            request.update(rootSubject);
        }
        catch(Exception e)
        {
            throw new PeriodicalsException("failed to create subscription request record", e);
        }
        return cookie;
    }

    protected String getRandomCookie()
    {
        String cookie = "0000000000000000".concat(Long.toString(random.nextLong(), 16));
        return cookie.substring(cookie.length()-16, cookie.length());
    }

    // inherit doc    
    public synchronized SubscriptionRequestResource getSubscriptionRequest(String cookie)
        throws PeriodicalsException
    {
        Resource[] res = resourceService.getStore().getResourceByPath(
            "/cms/sites/*/applications/periodicals/requests/"+cookie);
        if(res.length > 0)
        {
            return (SubscriptionRequestResourceImpl)res[0];
        }
        else
        {
            return null;
        }
    }
    
    // inherit doc
    public synchronized void discardSubscriptionRequest(String cookie)
        throws PeriodicalsException
    {
        SubscriptionRequestResource r = getSubscriptionRequest(cookie);
        if(r != null)
        {
            try
            {
                resourceService.getStore().deleteResource(r);
            }
            catch(EntityInUseException e)
            {
                throw new PeriodicalsException("failed to delete subscription change request", e);
            }
        }
        }

    public EmailPeriodicalResource[] getSubscribedEmailPeriodicals(SiteResource site, String email)
        throws PeriodicalsException
    {
        EmailPeriodicalResource[] periodicals = getEmailPeriodicals(site);
        List temp = new ArrayList();
        for (int i = 0; i < periodicals.length; i++)
        {
            EmailPeriodicalResource periodical = periodicals[i];
            if(periodical.getAddresses().indexOf(email) >= 0)
            {
                temp.add(periodical);
            }
        }
        EmailPeriodicalResource[] result = new EmailPeriodicalResource[temp.size()];
        temp.toArray(result);
        return result;
    }

     // renderers & templates ///////////////////////////////////////////////
    
    // inherit doc
    public String[] getRendererNames()
    {
        String[] result = new String[rendererClass.size()];
        rendererClass.keySet().toArray(result);
        return result;
    }
    
    // inheirt doc
    public void publishNow(PeriodicalResource periodical)
        throws PeriodicalsException
    {
        Date time = new Date();
        String fileName = generate(periodical, time);
        if(fileName != null && periodical instanceof EmailPeriodicalResource)
        {
            send((EmailPeriodicalResource)periodical, fileName, time);
        }
    }
        
    /**
     * Process periodicals defined in all sites.
     * 
     * <p>This method is supposed to be called by a scheduled job, once each
     * hour.</p>
     */
    public void processPeriodicals(Date time)
        throws PeriodicalsException
    {
        Set toProcess = findPeriodicalsToProcess(time);
        Iterator i = toProcess.iterator();
        while(i.hasNext() && !Thread.interrupted())
        {
            PeriodicalResource p = (PeriodicalResource)i.next();
            String fileName = generate(p, time);
            if(fileName != null && p instanceof EmailPeriodicalResource)
            {
                send((EmailPeriodicalResource)p, fileName, time);
            }
        }
    }
    
    // implementation ///////////////////////////////////////////////////////
    
    private Set findPeriodicalsToProcess(Date time)
    {
        Set set = new HashSet();
        try
        {
            QueryResults results = resourceService.getQuery().executeQuery(
                "FIND RESOURCE FROM cms.periodicals.periodical");
            Iterator rows = results.iterator();
            while(rows.hasNext())
            {
                QueryResults.Row row = (QueryResults.Row)rows.next();
                PeriodicalResource r = (PeriodicalResource)row.get();
                if(shouldProcess(r, time))
                {
                    set.add(r);
                }
            }
        }
        catch(Exception e)
        {
            log.error("failed to lookup periodicals", e);
        }
        return set;
    }
    
    private boolean shouldProcess(PeriodicalResource r, Date time)
    {
        Calendar timeCal = new GregorianCalendar();
        timeCal.setTime(time);
        Calendar lastCal = new GregorianCalendar();
        lastCal.setTime(r.getLastPublished());
        PublicationTimeResource[] publicationTimes = r.getPublicationTimes();
        for(int i = 0; i < publicationTimes.length; i++)
        {
            PublicationTimeResource pt = publicationTimes[i];
            if(timeCal.get(Calendar.HOUR_OF_DAY) == pt.getHour())
            {
                if((pt.getDayOfMonth() == -1) && (pt.getDayOfWeek() == timeCal.get(Calendar.DAY_OF_WEEK)) ||
                   (pt.getDayOfWeek() == -1) && (pt.getDayOfMonth() == timeCal.get(Calendar.DAY_OF_MONTH)))
                {   
                    if((timeCal.get(Calendar.YEAR) == lastCal.get(Calendar.YEAR)) &&
                       (timeCal.get(Calendar.DAY_OF_YEAR) == lastCal.get(Calendar.DAY_OF_YEAR)))
                    {
                        // already published today
                        return false;
                    }
                    // it's the time, publish now
                    return true;
                }
            }
            else
            {
                if(pt.getDayOfMonth() == -1)
                {
                    // 1 week + 1 hour passed since last publish
                    if(timeCal.getTimeInMillis() - lastCal.getTimeInMillis() > (7*24+1)*60*60*1000)
                    {
                        // we've missed the time, publish now
                        return true;
                    }
                }
                else
                {
                    // we haven't published this month, but the day and hour say we should
                    if(((lastCal.get(Calendar.MONTH) < timeCal.get(Calendar.MONTH)) ||
                        (lastCal.get(Calendar.YEAR) < timeCal.get(Calendar.YEAR))) &&
                       (lastCal.get(Calendar.DAY_OF_MONTH) >= timeCal.get(Calendar.DAY_OF_MONTH)) &&
                       (lastCal.get(Calendar.HOUR_OF_DAY) >= timeCal.get(Calendar.HOUR_OF_DAY)))
                    {
                        // we've missed the time, publish now
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Generates periodical's contents using a renderer.
     * 
     * @param r the periodical.
     * @param time the generation time.
     * @return returns the name of the generated file, or null on failure.
     */
    private String generate(PeriodicalResource r, Date time)
    {
        PeriodicalRenderer renderer = getRenderer(r.getRenderer());
        if(renderer == null)
        {
            log.error("cannot generate "+r.getPath()+"because renderer "+r.getRenderer()+
                " is not installed");
            return null;
        }
        Calendar cal = new GregorianCalendar();
        cal.setTime(time);
        NumberFormat format = new DecimalFormat("00");
        FieldPosition  field = new FieldPosition(NumberFormat.Field.INTEGER);
        StringBuffer buff = new StringBuffer();
        buff.append(cal.get(Calendar.YEAR));
        format.format((long)cal.get(Calendar.MONTH)+1, buff, field);
        format.format((long)cal.get(Calendar.DAY_OF_MONTH), buff, field);
        buff.append('.');
        format.format((long)cal.get(Calendar.HOUR_OF_DAY), buff, field);
        format.format((long)cal.get(Calendar.MINUTE), buff, field);
        format.format((long)cal.get(Calendar.SECOND), buff, field);
        buff.append('.');
        buff.append(renderer.getFilenameSuffix());
        String fileName = buff.toString();
        FileResource file;
        
        try
        {
            file = (FileResource)r.getStorePlace().getChild(fileName);
        }
        catch(EntityDoesNotExistException e)
        {
            try
            {
                 file = cmsFilesService.createFile(buff.toString(), null, renderer.getMimeType(),
                    r.getEncoding(), r.getStorePlace(), rootSubject);
            }
            catch (FilesException ee)
            {
                log.error("failed to create file for periodical "+r.getPath(), ee);
                return null;
            }                
        }
        catch(AmbigousNameException e)
        {
            log.error("inconsistend data in cms files application", e);
            return null;
        }
        boolean success = renderer.render(r, time, file);
        if(success && r instanceof EmailPeriodicalResource && !((EmailPeriodicalResource)r).getFullContent())
        {
            EmailPeriodicalResource er = (EmailPeriodicalResource)r;
            PeriodicalRenderer notificationRenderer = getRenderer(er.getNotificationRenderer());
            success = notificationRenderer.render(r, time, file);
            releaseRenderer(notificationRenderer);
            fileName = fileName+"-notification";
        }
        releaseRenderer(renderer);
        if(success)
        {
            r.setLastPublished(time);
            r.update(rootSubject);
            return fileName;
        }
        else
        {
            return null;
        }
    }
    
    private void send(EmailPeriodicalResource r, String fileName, Date time)
        throws PeriodicalsException
    {
        String path = r.getStorePlace().getPath()+"/"+fileName; // for error reporting
        FileResource file;
        try
        {
            file = (FileResource)r.getStorePlace().getChild(fileName);
        }
        catch (Exception e)
        {
            log.error(path+" does not exist", e);
            return;
        }
        InputStream is = cmsFilesService.getInputStream(file);
        if(is == null)
        {
            log.error("failed to open "+path);
            return;
        }
        LabeoMessage message = mailService.newMessage();
        if(fileName.endsWith(".eml"))
        {
            Message msg;
            try
            {
                msg = new MimeMessage(mailService.getSession(), is);
            }
            catch(MessagingException e)
            {
                log.error("malformed message "+path, e);
                return;
            }
            message.setMessage(msg);
        }
        else
        {
            String contents;
            try
            {
                StringWriter sw = new StringWriter();
                InputStreamReader osr = new InputStreamReader(is, file.getEncoding());
                char[] buff = new char[4096];
                int count = 0;
                while(count >= 0)
                {
                    count = osr.read(buff, 0, buff.length);
                    if(count > 0)
                    {
                        sw.write(buff, 0, count);
                    }
                }
                sw.flush();
                contents = sw.toString(); 
            }
            catch(IOException e)
            {
                log.error("failed to read "+path, e);
                return;
            }
            String media = "PLAIN";
            if(file.getMimetype().startsWith("text/"))
            {
                media = file.getMimetype().substring(5);
                if(media.indexOf(';') > 0)
                {
                    media = media.substring(0, media.indexOf(';'));
                }
                media = media.toUpperCase();
            }
            try
            {
                message.setText(contents, media);
                message.setEncoding(file.getEncoding());
                String subject = r.getSubject();
                if(subject == null || subject.length() == 0)
                {
                    subject = r.getName();
                }
                message.getMessage().setSubject(subject);
                message.getMessage().setSentDate(time);
                message.prepare();
            }
            catch(Exception e)
            {
                log.error("failed to create message", e);
                return;
            }
        }
        try
        {
            message.getMessage().setFrom(new InternetAddress(r.getFromHeader()));
            message.getMessage().setRecipient(Message.RecipientType.TO, new InternetAddress(r.getFromHeader()));
            StringTokenizer st = new StringTokenizer(r.getAddresses());
            while(st.hasMoreTokens())
            {
                message.getMessage().addRecipient(Message.RecipientType.BCC, new InternetAddress(st.nextToken()));
            }
        }
        catch(Exception e)
        {
            log.error("failed to set message headers", e);
            return;
        }
        try
        {
            message.send(true);        
        }
        catch(Exception e)
        {
            throw new PeriodicalsException("failed to send message", e);
        }
    }

    private PeriodicalsNodeResource getApplicationRoot(SiteResource site) throws PeriodicalsException
    {
        Resource[] apps = resourceService.getStore().getResource(site, "applications");
        if (apps.length == 0)
        {
            throw new PeriodicalsException("failed to lookup applications node in site " + site.getName());
        }
        Resource[] res = resourceService.getStore().getResource(apps[0], "periodicals");
        if(res.length == 0)
        {
            try
            {
                return PeriodicalsNodeResourceImpl.createPeriodicalsNodeResource(resourceService, "periodicals", apps[0], rootSubject);
            }
            catch (ValueRequiredException e)
            {
                throw new PeriodicalsException("faild to create periodicals application root node", e);
            }
        }
        else
        {
            return (PeriodicalsNodeResource)res[0];
        }
    }
    
    // factory //////////////////////////////////////////////////////////////
    
    public Factory getFactory(Class cl, final Configuration config, 
        final ServiceBroker broker, final LoggingFacility log)
    {
        if(!PeriodicalRenderer.class.isAssignableFrom(cl))
        {
            throw new InitializationError(cl.getName()+" is not subclass of PeriodicalRenderer");
        }
        final Class[] classes = new Class[] { cl };
        return new Factory() 
        {
            public Class[] getClasses()
            {
                return classes;
            }
            
            public Object newInstance(Class cl)
            {
                if(!classes[0].equals(cl))
                {
                    throw new IllegalArgumentException("invalid class "+cl.getName());
                }
                PeriodicalRenderer renderer = (PeriodicalRenderer)ObjectUtils.
                    instantiate(cl.getName());
                renderer.init(config, broker, log);
                return renderer;
            }
        };
    }
    
    public PeriodicalRenderer getRenderer(String name)
    {
        Class cl = (Class)rendererClass.get(name);
        return (PeriodicalRenderer)poolService.getInstance(cl);
    }
    
    public void releaseRenderer(PeriodicalRenderer renderer)
    {
        if(renderer instanceof Recyclable)
        {
            ((Recyclable)renderer).recycle();
        }
    }
    
    // template variants ////////////////////////////////////////////////////
    
    // inherit doc
    public String[] getTemplateVariants(SiteResource site, String renderer)
    {
        String dir = "/templates/cms/sites/"+site.getName()+
            "/messages/periodicals/"+renderer;
        String[] items = fileService.list(dir);
        if(items == null || items.length == 0)
        {
            return new String[0];
        }
        ArrayList temp = new ArrayList();
        for (int i = 0; i < items.length; i++)
        {
            String path = dir+"/"+items[i];
            if(fileService.isFile(path) && path.endsWith(".vt"))
            {
                temp.add(items[i].substring(0, items[i].length()-3));
            }
        }
        String[] result = new String[temp.size()];
        temp.toArray(result);
        return result;
    }
    
    // inherit doc
    public boolean hasTemplateVariant(SiteResource site, String renderer, String name)
    {
        String path = getTemplateVariantPath(site, renderer, name);
        return fileService.exists(path);
    }

    // inherit doc
    public Template getTemplateVariant(SiteResource site, String renderer, String name)
        throws TemplateNotFoundException
    {
        String path = "/sites/"+site.getName()+"/messages/periodicals/"+renderer+"/"+name;
        return templatingService.getTemplate("cms", path);
    }
    
    // inherit doc
    public void createTemplateVariant(SiteResource site, String renderer, String name, String contents)
        throws ProcessingException
    {
        String path = getTemplateVariantPath(site, renderer, name);
        if(fileService.exists(path))
        {
            throw new ProcessingException("variant "+name+" of "+renderer+
                " render already exists in site "+site.getName());
        }
        
        writeTemplate(path, contents, "failed to write template contents");
        invalidateTemplate(site, renderer, name);
    }
    
    // inherit doc
    public void deleteTemplateVariant(SiteResource site, String renderer, String name)
        throws ProcessingException
    {
        String path = getTemplateVariantPath(site, renderer, name);
        if(!fileService.exists(path))
        {
            throw new ProcessingException("variant "+name+" of "+renderer+
                " render does not exist in site "+site.getName());
        }
        try
        {
            fileService.delete(path);
            invalidateTemplate(site, renderer, name);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to delete template", e);
        }
    }
    
    public List getDefaultTemplateLocales(String renderer)
        throws ProcessingException
    {
        List list = new ArrayList();
        String suffix = "/messages/periodicals/"+renderer+"/default.vt";
        List supportedLocales = webcoreService.getSupportedLocales();
        for (Iterator i = supportedLocales.iterator(); i.hasNext();)
        {
            Locale l = (Locale)i.next();
            if(fileService.exists("/templates/cms/"+
                l.toString()+"_"+getMedium(renderer)+suffix))
            {
                list.add(l);
            }
        }
        return list;
    }
    
    public String getDefaultTemplateContents(String renderer, Locale locale)
        throws ProcessingException
    {
        String path = "/templates/cms/"+locale.toString()+"_"+getMedium(renderer)+
            "/messages/periodicals/"+renderer+"/default.vt";
        try
        {
            return fileService.read(path, templateEncoding);            
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to read template contents for renderer "+
                renderer+" "+locale.toString(), e);
        }
    }

    public Template getDefaultTemplate(String renderer, Locale locale)
        throws ProcessingException
    {
        String path = locale.toString()+"_"+getMedium(renderer)+
            "/messages/periodicals/"+renderer+"/default";
        try
        {
            return templatingService.getTemplate("cms", path);
        }
        catch(TemplateNotFoundException e)
        {
            return null;
        }
    }
    
    protected String getMedium(String renderer)
    {
        PeriodicalRenderer r = getRenderer(renderer);
        String medium = r.getMedium();
        releaseRenderer(r);
        return medium;
    }
        
    // inherit doc
    public String getTemplateVariantContents(SiteResource site, String renderer, String name)
        throws ProcessingException
    {
        String path = getTemplateVariantPath(site, renderer, name);
        if(!fileService.exists(path))
        {
            throw new ProcessingException("variant "+name+" of "+renderer+
                " render does not exist in site "+site.getName());
        }
        try
        {
            return fileService.read(path, templateEncoding);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to read template contents", e);
        }
    }
    
    // inherit doc
    public void getTemplateVariantContents(SiteResource site, String renderer, String name, OutputStream out)
        throws ProcessingException
    {
        String path = getTemplateVariantPath(site, renderer, name);
        if(!fileService.exists(path))
        {
            throw new ProcessingException("variant "+name+" of "+renderer+
                " render does not exist in site "+site.getName());
        }
        try
        {
            fileService.read(path,out);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to read template contents", e);
        }
    }
    
    // inherit doc
    public long getTemplateVariantLength(SiteResource site, String renderer, String name)
        throws ProcessingException
    {
        String path = getTemplateVariantPath(site, renderer, name);
        if(!fileService.exists(path))
        {
            throw new ProcessingException("variant "+name+" of "+renderer+
                " render does not exist in site "+site.getName());
        }
        return fileService.length(path);
    }
    
    // inherit doc
    public void setTemplateVariantContents(SiteResource site, String renderer, String name, String contents)
        throws ProcessingException
    {
        String path = getTemplateVariantPath(site, renderer, name);
        if(!fileService.exists(path))
        {
            throw new ProcessingException("variant "+name+" of "+renderer+
                " render does not exist in site "+site.getName());
        }
        writeTemplate(path, contents, "failed to write template contents");
        invalidateTemplate(site, renderer, name);
    }
    
    // inherit doc
    protected String getTemplateVariantPath(SiteResource site, String renderer, String variant)
    {
        return "/templates/cms/sites/"+site.getName()+"/messages/periodicals/"+renderer+
            "/"+variant+".vt";    
    }
    
    protected void invalidateTemplate(SiteResource site, String renderer, String variant)
    {
        String name = "/sites/"+site.getName()+"/messages/periodicals/"+renderer+
            "/"+variant;
        templatingService.invalidateTemplate("cms", name);
    }
    
    // lame link tool ///////////////////////////////////////////////////////

    public LinkRenderer getLinkRenderer()
    {
        return new LinkRenderer()
        {
            public String getFileURL(FileResource file)
            {
                return PeriodicalsServiceImpl.this.getFileURL(file);
            }

			public String getCommonResourceURL(SiteResource site, String path)
			{
				return PeriodicalsServiceImpl.this.getCommonResourceURL(site, path);
			}

			public String getAbsoluteURL(SiteResource site, String path)
			{
                return PeriodicalsServiceImpl.this.getAbsoluteURL(site, path);
			}

            public String getNodeURL(NavigationNodeResource node)
            {
                return PeriodicalsServiceImpl.this.getNodeURL(node);
            }
        };
    }
        
    public String getFileURL(FileResource file)
    {
        Resource parent = file.getParent();
        while(parent != null && !(parent instanceof RootDirectoryResource))
        {
            parent = parent.getParent();
        }
        if(parent == null)
        {
            throw new IllegalStateException("cannot determine root directory");
        }
        RootDirectoryResource rootDirectory = ((RootDirectoryResource)parent);

        while(parent != null && !(parent instanceof SiteResource))
        {
            parent = parent.getParent();
        }
        if(parent == null)
        {
            throw new IllegalStateException("cannot determine site");
        }
        SiteResource site = (SiteResource)parent;
        
        if(rootDirectory.getExternal())
        {
            String path = "";
            for(parent = file; parent != null; parent = parent.getParent())
            {
                if(parent instanceof RootDirectoryResource)
                {
                    break;
                }
                else
                {
                    path = "/"+URLEncoder.encode(parent.getName())+path;
                }
            }
            
            StringBuffer sb = new StringBuffer();
            sb.append(getContextURL(site));
            sb.append("files/");
            sb.append(site.getName());
            sb.append("/");
            sb.append(rootDirectory.getName());
            sb.append(path);
            return sb.toString();
        }
        else
        {
            String path = "";
            for(parent = file; parent != null; parent = parent.getParent())
            {
                if(parent instanceof RootDirectoryResource)
                {
                    break;
                }
                else
                {
                    path = ","+parent.getName()+path;
                }
            }
            path = "/"+rootDirectory.getName()+path;

            StringBuffer sb = new StringBuffer();
            sb.append(getApplicationURL(site));
            sb.append("view/files,Download?");
            sb.append("path=").append(path).append('&');
            sb.append("file_id=").append(file.getIdString());
            return sb.toString();
        }
    }
    
    public String getAbsoluteURL(SiteResource site, String path) 
    {
        return getContextURL(site) + path;
    }
    
    public String getCommonResourceURL(SiteResource site, String path)
    {
        return getContextURL(site) + "content/default/" + path;
    }
    
    public String getNodeURL(NavigationNodeResource node)
    {
        return getApplicationURL(node.getSite())+"x/"+node.getIdString();
    }

    protected String getContextURL(SiteResource site)
    {
        StringBuffer buff = new StringBuffer();
        buff.append("http://")
            .append(getServer(site));
        if(port != 80)
        {
            buff.append(':')
                .append(port);
        }
        buff.append(context);
        return buff.toString();
    }
    
    protected String getApplicationURL(SiteResource site)
    {
        StringBuffer buff = new StringBuffer();
        buff.append("http://")
            .append(getServer(site));
        if(port != 80)
        {
            buff.append(':')
                .append(port);
        }
        buff.append(context)
            .append(servletAndApp);
        return buff.toString();
    }    

    protected String getServer(SiteResource site)
    {
        String server = null;
        try
        {
            server = siteService.getPrimaryMapping(site);
        }
        catch(Exception e)
        {
            log.error("failed to deteremine site domain address", e);
        }
        if(server == null)
        {
            server = serverName;        
        }
        return server;
    }
    
    private void writeTemplate(String path, String contents, String message)
        throws ProcessingException
    {
        try
        {
            HTMLEntityEncoder encoder = new HTMLEntityEncoder();
            if(!fileService.exists(path))
            {
                fileService.mkdirs(StringUtils.directoryPath(path));
            }
            fileService.write(path,
                encoder.encodeHTML(contents, templateEncoding), templateEncoding);
        }
        catch(IOException e)
        {
            throw new ProcessingException(message, e);
        }
    }
}
