package net.cyklotron.cms.modules.actions.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;
import net.cyklotron.cms.util.IndexTitleComparator;

public class SwapPhoto
    extends BaseStructureAction
{
    private final RelatedService relatedService;

    private final IntegrationService integrationService;

    public SwapPhoto(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, RelatedService relatedService,
        IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        this.relatedService = relatedService;
        this.integrationService = integrationService;
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            long photoId = parameters.getLong("photo_id", -1);
            FileResource selected = null;
            if(photoId != -1)
            {
                selected = FileResourceImpl.getFileResource(coralSession, photoId);
            }
            NavigationNodeResource node = cmsData.getNode();
            ResourceList<Resource> sequence = null;
            if(node instanceof DocumentNodeResource)
            {
                sequence = ((DocumentNodeResource)node).getRelatedResourcesSequence();
            }
            I18nContext i18nContext = context.getAttribute(I18nContext.class);
            Comparator<Resource> comp = new IndexTitleComparator<Resource>(context,
                integrationService, i18nContext.getLocale());
            List<Resource> related = new ArrayList<Resource>(Arrays.asList(relatedService.getRelatedTo(coralSession, node, sequence, comp)));
            FileResource current = node.getThumbnail();
            if(selected != null)
            {
                // selected -> thumbnail
                // thnumbail -> related
                node.setThumbnail(selected);
                related.remove(selected);
                if(current != null)
                {
                    related.add(current);
                }
                if(sequence != null)
                {
                    sequence.remove(selected);
                    if(current != null)
                    {
                        sequence.add(current);
                    }
                }
            }
            else
            {
                // thumbnail -> related
                // none -> thumbnail
                node.setThumbnail(null);
                related.add(current);
                if(sequence != null)
                {
                    sequence.add(current);
                }
            }
            relatedService.setRelatedTo(coralSession, node, related.toArray(new Resource[related.size()]));
            node.update();
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("internal error", e);
        }
    }

    @Override
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        return checkModifyPermission(context);
    }
}
