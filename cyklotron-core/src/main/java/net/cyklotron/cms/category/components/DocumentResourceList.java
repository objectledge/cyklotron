package net.cyklotron.cms.category.components;

import java.util.Calendar;
import java.util.Date;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;

import bak.pcj.set.LongSet;

/**
 * This class contains logic of component which displays lists of documents assigned
 * to queried categories.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentResourceList.java,v 1.8 2005-05-17 06:19:58 zwierzem Exp $
 */
public class DocumentResourceList
extends ResourceList
{
    public DocumentResourceList(Context context, IntegrationService integrationService,
        CmsDataFactory cmsDataFactory,  CategoryQueryService categoryQueryService,
        SiteService siteService, StructureService structureService)
	{
        super(context, integrationService, cmsDataFactory, categoryQueryService, siteService,
                        structureService);
	}

    public BaseResourceListConfiguration createConfig()
        throws ProcessingException
    {
        return new DocumentResourceListConfiguration();
    }

    private static String[] resourceClasses = { DocumentNodeResource.CLASS_NAME };

    protected String[] getResourceClasses(org.objectledge.coral.session.CoralSession coralSession, BaseResourceListConfiguration config)
	    throws ProcessingException
    {
        int offset = ((DocumentResourceListConfiguration) config).getPublicationTimeOffset();
        if(offset == -1)
        {
            return resourceClasses;
        }
        return null;
    }
    
    public LongSet getIdSet(CoralSession coralSession, BaseResourceListConfiguration config)
        throws ProcessingException
    {
        LongSet idSet = super.getIdSet(coralSession, config);
        CmsData cmsData = cmsDataFactory.getCmsData(context);

        int offset = ((DocumentResourceListConfiguration) config).getPublicationTimeOffset();
        if(offset != -1)
        {
            Date now = cmsData.getDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.DAY_OF_MONTH, -offset);            
            try
            {
                final LongSet validDocumentsSet = structureService.getDocumentsValidAtOrAfter(
                    calendar.getTime(), coralSession);
                if(idSet != null)
                {
                    idSet.retainAll(validDocumentsSet);
                    return idSet;
                }
                else
                {
                    return validDocumentsSet;
                }
            }
            catch(StructureException e)
            {
               throw new ProcessingException(e);
            }
        }
        return idSet;
    }    
}
