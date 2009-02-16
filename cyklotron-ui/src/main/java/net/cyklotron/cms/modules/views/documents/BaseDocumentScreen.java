package net.cyklotron.cms.modules.views.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.structure.BaseStructureScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/** Base class for document editing screens.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseDocumentScreen.java,v 1.3 2005-01-26 06:43:39 pablo Exp $
 */
public abstract class BaseDocumentScreen 
    extends BaseStructureScreen 
{
    /** Document service. */
    protected  DocumentService documentService;

    protected IntegrationService integrationService;

    public BaseDocumentScreen(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService,
        DocumentService documentService, IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService);
        this.documentService = documentService;
        this.integrationService = integrationService;
        
    }

    public DocumentNodeResource getDocument()
        throws ProcessingException
    {
        NavigationNodeResource node = getNode();
        if(node instanceof DocumentNodeResource)
        {
            return (DocumentNodeResource)node;
        }
        else
        {
            throw new ProcessingException("Current node is not a document node");
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkModifyPermission();
    }
}

