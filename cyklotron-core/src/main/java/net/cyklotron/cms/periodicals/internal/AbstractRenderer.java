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

import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.DateFormatTool;
import org.objectledge.i18n.DateFormatter;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.mvc.tools.StringTool;

import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.documents.DiscardImagesHTMLContentFilter;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.periodicals.PeriodicalRenderer;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PeriodicalsTemplatingService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.table.PriorityAndValidityStartComparator;
import net.cyklotron.cms.util.SiteFilter;

/**
 * An implementation of PeriodicalRenderer that uses the Templating service to render
 * the content.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: AbstractRenderer.java,v 1.7 2006-05-04 11:54:08 rafal Exp $ 
 */
public abstract class AbstractRenderer
    implements PeriodicalRenderer
{
    // instance variables ///////////////////////////////////////////////////
   
    /** the logging facility. */
    protected Logger log;
    
    /** templating service. */
    protected Templating templating;

    /** category query service. */
    protected CategoryQueryService categoryQueryService;
    
    /** periodicals service. */
    protected PeriodicalsService periodicalsService;

    /** periodicals templating service. */
    protected PeriodicalsTemplatingService periodicalsTemplatingService;
    
    /** file service. */
    protected FilesService cmsFilesService;
    
    /** date formater */
    protected DateFormatter dateFormatter;
    
    protected IntegrationService integrationService;
    
    protected SiteService siteService;
    
    /** 'everyone' system role. */
    //protected Role anonymous;
    
    /** document view permission. */
    //protected Permission viewPermission;
    
    // initialization ///////////////////////////////////////////////////////
    
    public AbstractRenderer(Logger log, Templating templating,
        CategoryQueryService categoryQueryService, PeriodicalsService periodicalsService,
        PeriodicalsTemplatingService periodicalsTemplatingService,
        FilesService cmsFilesService, DateFormatter dateFormatter,
        IntegrationService integrationService, SiteService siteService)
    {
        this.log = log;
        this.templating = templating;
        this.categoryQueryService = categoryQueryService;
        this.periodicalsService = periodicalsService;
        this.periodicalsTemplatingService = periodicalsTemplatingService;
        this.cmsFilesService = cmsFilesService;
        this.dateFormatter = dateFormatter;
        this.siteService = siteService;
    }

    public boolean render(CoralSession coralSession, PeriodicalResource periodical, Date time, FileResource file)
    {
        String result = renderTemplate(coralSession, periodical, time, file);
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

    protected String renderTemplate(CoralSession coralSession, PeriodicalResource periodical, Date time, FileResource file)
    {
        TemplatingContext tContext = setupContext(coralSession, periodical, time, file);
        String templateName = getTemplateName(periodical);
        String rendererName = getRendererName(periodical);
        PeriodicalRenderer renderer = periodicalsService.getRenderer(rendererName);
        try
        {
            Template template;
            if(templateName == null || templateName.equals(""))
            {
                template = periodicalsTemplatingService.getDefaultTemplate(renderer, 
                    StringUtils.getLocale(periodical.getLocale()));
                if(template == null)
                {
                    log.error("failed to render "+periodical.getPath()+" default template for "+
                        rendererName+" renderer not available in locale "+
                        periodical.getLocale());
                    return null;
                }
                log.warn("using default template for "+periodical.getPath()+
                    " because no template variant was selected");
            }
            else
            {
                if(periodicalsTemplatingService.hasTemplateVariant(
                    periodical.getSite(), renderer.getName(), templateName))
                {
                    template = periodicalsTemplatingService.getTemplateVariant(
                        periodical.getSite(), renderer.getName(), templateName);
                }
                else
                {
                    log.error("failed to render "+periodical.getPath()+" template variant "+
                        templateName+" not defined for "+rendererName+
                        " in site "+periodical.getSite().getName());
                    return null;
                }
            }
            return template.merge(tContext);
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
        catch (PeriodicalsException e)
        {
            log.error("failed to render ", e);
        }
        finally
        {
            periodicalsService.releaseRenderer(renderer);
            releaseContext(tContext);
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
     * @param coralSession the coral session
     * @param periodical the periodical resource.
     * @param time publication time.
     * 
     * @return the context.
     */
    protected TemplatingContext setupContext(CoralSession coralSession, PeriodicalResource periodical, Date time, FileResource file)
    {
        TemplatingContext context = templating.createContext();
        Locale locale = new Locale(periodical.getLocale());
        DateFormatTool dateFormat = new DateFormatTool(dateFormatter, locale, dateFormatter.getDateFormat(locale));
        context.put("format_date", dateFormat);
        context.put("renderer", this);        
        context.put("periodical", periodical);
        context.put("time", time);
        context.put("file", file);
        context.put("link", periodicalsService.getLinkRenderer());
        context.put("html_content_filter", new DiscardImagesHTMLContentFilter());
        context.put("string", new StringTool());
        CategoryQueryPoolResource cqp = periodical.getCategoryQuerySet();
        List queries = cqp.getQueries();
        Collections.sort(queries, new NameComparator(locale));
        context.put("queryList", queries);
        Map results = new HashMap();
        context.put("queryResults", results);
        Iterator i = queries.iterator();
        Role anonymous = coralSession.getSecurity().getUniqueRole("cms.anonymous");
        Permission viewPermission = coralSession.getSecurity().getUniquePermission("cms.structure.view");
        while(i.hasNext())
        {
            CategoryQueryResource cq = (CategoryQueryResource)i.next();
            String[] siteNames = cq.getAcceptedSiteNames();
            SiteFilter siteFilter = null;
            try
            {
                if(siteNames != null && siteNames.length > 0)
                {
                    siteFilter = new SiteFilter(coralSession, siteNames, siteService);
                }
                Resource[] docs = categoryQueryService.forwardQuery(coralSession, cq.getQuery());
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
    protected void releaseContext(TemplatingContext context)
    {
        // context pooling not implemented
    }

    // inherited doc
    public abstract String getName(); 
    
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
