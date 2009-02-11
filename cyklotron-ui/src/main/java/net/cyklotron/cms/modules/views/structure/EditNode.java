package net.cyklotron.cms.modules.views.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;
import net.cyklotron.cms.util.IndexTitleComparator;

/**
 *
 */
public class EditNode
    extends BaseStructureScreen
{
    
    private final IntegrationService integrationService;

    public EditNode(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService,
        IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService);
        this.integrationService = integrationService;
        
    }
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        SiteResource site = getSite();

        try
        {
            templatingContext.put("styles", Arrays.asList(styleService.getStyles(coralSession, site)));
        }
        catch (Exception e)
        {
            logger.error("Exception :",e);
            throw new ProcessingException("failed to lookup available styles", e);
        }
        List<Integer> priorities = new ArrayList<Integer>();
        NavigationNodeResource node = getNode();
        Subject subject = coralSession.getUserSubject();
        int min = structureService.getMinPriority(coralSession, node, subject);
        int max = structureService.getMaxPriority(coralSession, node, subject);
        int allowed = structureService.getAllowedPriority(coralSession, node, subject, node
            .getPriority(structureService.getDefaultPriority()));
        for(int i = min; i <= max; i++)
        {
        	priorities.add(new Integer(i));
        }
        templatingContext.put("priorities", priorities);
        templatingContext.put("selectedPriority", allowed);
        ResourceList<Resource> sequence = null;
        if(node instanceof DocumentNodeResource)
        {
            sequence = ((DocumentNodeResource)node).getRelatedResourcesSequence();
        }
        Resource[] relatedTo = relatedService.getRelatedTo(coralSession, node, sequence,
            new IndexTitleComparator<Resource>(context, integrationService, i18nContext.getLocale()));
        templatingContext.put("related_to", Arrays.asList(relatedTo));
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        return getCmsData().getNode().canModify(coralSession, coralSession.getUserSubject());
    }
}
