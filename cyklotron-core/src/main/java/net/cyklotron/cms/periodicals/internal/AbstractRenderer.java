/*
 * Created on Oct 24, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.periodicals.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.documents.DiscardImagesHTMLContentFilter;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.periodicals.PeriodicalRenderer;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.structure.table.PriorityAndValidityStartComparator;
import net.cyklotron.cms.util.SiteFilter;
import net.labeo.services.ServiceBroker;
import net.labeo.services.i18n.DateFormatTool;
import net.labeo.services.logging.Logger;
import net.labeo.services.pool.PoolService;
import net.labeo.services.pool.Recyclable;
import net.labeo.services.pool.RecyclableObject;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.table.NameComparator;
import net.labeo.services.templating.Context;
import net.labeo.services.templating.MergingException;
import net.labeo.services.templating.Template;
import net.labeo.services.templating.TemplateNotFoundException;
import net.labeo.services.templating.TemplatingService;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.StringTool;

/**
 * An implementation of PeriodicalRenderer that uses the Templating service to render
 * the content.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: AbstractRenderer.java,v 1.2 2005-01-18 17:38:15 pablo Exp $ 
 */
public abstract class AbstractRenderer
    extends RecyclableObject
    implements PeriodicalRenderer
{
    // instance variables ///////////////////////////////////////////////////
    
    /** the service broker. */
    protected ServiceBroker broker;
    
    /** the logging facility. */
    protected Logger log;
    
    /** configuration. */
    protected Configuration config;
    
    /** templating service. */
    protected TemplatingService templatingService;

    /** category query service. */
    protected CategoryQueryService categoryQueryService;
    
    /** periodicals service. */
    protected PeriodicalsService periodicalsService;

    /** file service. */
    protected FilesService cmsFilesService;
    
    /** pool service. */
    protected PoolService poolService;
    
    /** 'everyone' system role. */
    protected Role anonymous;
    
    /** document view permission. */
    protected Permission viewPermission;
    
    // initialization ///////////////////////////////////////////////////////
    
    public void init(Configuration config, ServiceBroker broker, Logger log)
    {
        this.config = config;
        this.broker = broker;
        this.log = log;
        this.templatingService = 
            (TemplatingService)broker.getService(TemplatingService.SERVICE_NAME);
        this.categoryQueryService = 
            (CategoryQueryService)broker.getService(CategoryQueryService.SERVICE_NAME);
        periodicalsService = (PeriodicalsService)broker.
            getService(PeriodicalsService.SERVICE_NAME);
        cmsFilesService = (FilesService)broker.
            getService(FilesService.SERVICE_NAME);
        poolService = (PoolService)broker.
            getService(PoolService.SERVICE_NAME);
        CoralSession resourceService = (CoralSession)broker.
            getService(CoralSession.SERVICE_NAME);
        anonymous = resourceService.getSecurity().getUniqueRole("cms.anonymous");
        viewPermission = resourceService.getSecurity().getUniquePermission("cms.structure.view");
    }

    public boolean render(PeriodicalResource periodical, Date time, FileResource file)
    {
        String result = renderTemplate(periodical, time, file);
        if(result != null)
        {
            try
            {
                byte[] image = postProcess(result,file.getEncoding());
                OutputStream os = cmsFilesService.getOutputStream(file);
                os.write(image);
                os.flush();
                return true;
            }
            catch(IOException e)
            {
                log.error("failed to write out data", e);
            }
        }
        return false;
    }

    protected String getTemplateName(PeriodicalResource r)
    {
        return r.getTemplate();
    }
    
    protected String getRendererName(PeriodicalResource r)
    {
        return r.getRenderer();
    }

    protected String renderTemplate(PeriodicalResource periodical, Date time, FileResource file)
    {
        Context context = setupContext(periodical, time, file);
        String templateName = getTemplateName(periodical);
        String rendererName = getRendererName(periodical);
        try
        {
            Template template;
            if(templateName == null || templateName.equals(""))
            {
                template = periodicalsService.getDefaultTemplate(rendererName, 
                    StringUtils.getLocale(periodical.getLocale()));
                if(template == null)
                {
                    log.error("failed to render "+periodical.getPath()+" default template for "+
                        rendererName+" renderer not available in locale "+
                        periodical.getLocale());
                    return null;
                }
                log.warning("using default template for "+periodical.getPath()+
                    " because no template variant was selected");
            }
            else
            {
                if(periodicalsService.hasTemplateVariant(
                    periodical.getSite(), rendererName, templateName))
                {
                    template = periodicalsService.getTemplateVariant(
                        periodical.getSite(), rendererName, templateName);
                }
                else
                {
                    log.error("failed to render "+periodical.getPath()+" template variant "+
                        templateName+" not defined for "+rendererName+
                        " in site "+periodical.getSite().getName());
                    return null;
                }
            }
            return template.merge(context);
        }
        catch (ProcessingException e)
        {
            log.error("failed to load template", e);
        }
        catch (TemplateNotFoundException e)
        {
            log.error("failed to load template", e);
        }
        catch (MergingException e)
        {
            log.error("failed to render template", e);
        }
        finally
        {
            releaseContext(context);
        }
        return null;
    }
    
    protected byte[] postProcess(String result, String encoding)
        throws IOException
    {
        return result.getBytes(encoding);
    }

    /**
     * Prepares a templating context.
     * 
     * @param periodical the periodical resource.
     * @param time publication time.
     * @return the context.
     */
    protected Context setupContext(PeriodicalResource periodical, Date time, FileResource file)
    {
        Context context = templatingService.createContext();
        DateFormatTool dateFormat = new DateFormatTool();
        dateFormat.init(broker, config);
        Locale locale = StringUtils.getLocale(periodical.getLocale());
        dateFormat.prepare(locale);
        context.put("format_date", dateFormat);
        context.put("renderer", this);        
        context.put("periodical", periodical);
        context.put("time", time);
        context.put("file", file);
        context.put("link", periodicalsService.getLinkRenderer());
        context.put("html_content_filter", new DiscardImagesHTMLContentFilter());
        context.put("string", poolService.getInstance(StringTool.class));
        CategoryQueryPoolResource cqp = periodical.getCategoryQuerySet();
        List queries = cqp.getQueries();
        Collections.sort(queries, new NameComparator(locale));
        context.put("queryList", queries);
        Map results = new HashMap();
        context.put("queryResults", results);
        Iterator i = queries.iterator();
        while(i.hasNext())
        {
            CategoryQueryResource cq = (CategoryQueryResource)i.next();
            String[] siteNames = cq.getAcceptedSiteNames();
            SiteFilter siteFilter = null;
            try
            {
                if(siteNames != null && siteNames.length > 0)
                {
                    siteFilter = new SiteFilter(siteNames);
                }
                Resource[] docs = categoryQueryService.forwardQuery(cq.getQuery());
                ArrayList temp = new ArrayList();
                for (int j = 0; j < docs.length; j++)
                {
                    DocumentNodeResource doc = (DocumentNodeResource)docs[j];
                    if(periodical.getLastPublished() == null || 
                        (doc.getValidityStart() == null && doc.getCreationTime().compareTo(periodical.getLastPublished()) > 0) ||
                        (doc.getValidityStart() != null && doc.getValidityStart().compareTo(periodical.getLastPublished()) > 0))
                    {
                        if(doc.getValidityStart() == null || doc.getValidityStart().compareTo(time) < 0)
                        {
                            if (doc.getState() == null
                                || doc.getState().getName().equals("published"))
                            {
                                if(anonymous.hasPermission(doc, viewPermission))
                                {
                                    if(siteFilter != null)
                                    {
                                        if(siteFilter.accept(doc))
                                        {
                                            temp.add(doc);
                                        }
                                    }
                                    else
                                    {
                                        temp.add(doc);
                                    }
                                }
                            }
                        }
                    }
                }
                Collections.sort(temp, new PriorityAndValidityStartComparator());
                results.put(cq, temp);
            }
            catch(Exception e)
            {
                log.error("failed to execute query for periodical "+periodical.getPath()+
                    " query pool "+cqp+" query "+cq.getPath(), e);
            }
        }
        return context;
    }
    
    /**
     * Release context after use.
     * 
     * @param context the context.
     */
    protected void releaseContext(Context context)
    {
        if(context instanceof Recyclable)
        {
            ((Recyclable)context).recycle();    
        } 
    }

    // inherited doc
    public abstract String getFilenameSuffix();

    // inherited doc
    public abstract String getMimeType();

    // inherited doc
    public abstract String getMedium();
    
    // inherited doc
    /*
    public String getCharacterEncoding()
    {
        return "UTF-8";
    }
    */
}
