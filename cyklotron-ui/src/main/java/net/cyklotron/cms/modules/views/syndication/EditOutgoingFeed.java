package net.cyklotron.cms.modules.views.syndication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.syndication.OutgoingFeedResource;
import net.cyklotron.cms.syndication.OutgoingFeedResourceData;
import net.cyklotron.cms.syndication.OutgoingFeedUtil;
import net.cyklotron.cms.syndication.SyndicationService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18n;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

/**
 * Editing an outgoing feed..
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EditOutgoingFeed.java,v 1.1 2005-06-16 11:14:14 zwierzem Exp $
 */
public class EditOutgoingFeed extends BaseSyndicationScreen
{
    protected CategoryQueryService categoryQueryService;
    private I18n i18n;
    
    public EditOutgoingFeed(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SyndicationService syndicationService, CategoryQueryService categoryQueryService,
        I18n i18n)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
            syndicationService);
        this.categoryQueryService = categoryQueryService;
        this.i18n = i18n;
    }
    
    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession) throws org.objectledge.pipeline.ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        
        OutgoingFeedResource feed = null;
        if(parameters.isDefined(OutgoingFeedUtil.FEED_ID_PARAM))
        {
            feed = getOutgoingFeed(coralSession, parameters);
            templatingContext.put("feed", feed);
        }
        if(parameters.getBoolean("fromList", false))
        {
            OutgoingFeedResourceData.removeData(httpContext, feed);
        }
        // WARN: feed may be null -> creation of a new feed
        OutgoingFeedResourceData feedData = OutgoingFeedResourceData.getData(httpContext, feed);
        templatingContext.put("feed_data", feedData);
        
        List templates;
        try
        {
            templates = syndicationService.getOutgoingFeedsManager().getGenerationTemplates();
        }
        catch(IOException e)
        {
            throw new ProcessingException(e);
        }
        templatingContext.put("templates", templates);
        
        try
        {
            Resource parent = categoryQueryService.getCategoryQueryRoot(coralSession, cmsData.getSite());
            Resource[] categoryQueries = coralSession.getStore().getResource(parent);
            Arrays.sort(categoryQueries, new NameComparator(i18nContext.getLocale()));
            templatingContext.put("categoryQueries", categoryQueries);
        }
        catch(CategoryQueryException e)
        {
            throw new ProcessingException("cannot create category query list", e);
        }
        
        Locale[] locales = i18n.getSupportedLocales();
        ArrayList languages = new ArrayList(locales.length);
        for (Locale locale : locales)
        {
            languages.add(locale.getLanguage());
        }
        Collections.sort(languages);
        templatingContext.put("languages", languages);
    }

    public boolean checkAccessRights(Context context)
    throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.syndication.outfeed.modify");
    }
}
