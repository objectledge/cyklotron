package net.cyklotron.cms.modules.views.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.StructureUtil;
import net.cyklotron.cms.style.StyleService;

/**
 * Print Document screen displays document for printing.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PrintDocument.java,v 1.3 2005-01-26 06:43:39 pablo Exp $
 */
public class PrintDocument
    extends BaseSkinableScreen
{


    public PrintDocument(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        StructureService structureService, StyleService styleService, SkinService skinService,
        MVCFinder mvcFinder, TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, structureService, styleService,
                        skinService, mvcFinder, tableStateManager);
        // TODO Auto-generated constructor stub
    }
    public void prepareDefault(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        CmsData cmsData = getCmsData();
        long printDocId = parameters.getLong("print_doc_id", -1L);
        if(printDocId == -1L)
        {
            screenError(cmsData.getNode(), context, "no 'print_doc_id' parameter defined");
            return;
        }

        try
        {
            NavigationNodeResource printDoc = StructureUtil.getNode(coralSession, printDocId);
            if(printDoc instanceof DocumentNodeResource)
            {
                templatingContext.put("document_tool", ((DocumentNodeResource)printDoc).getDocumentTool(context));
            }
            else
            {
                screenError(cmsData.getNode(), context, "cannot display a non document node");
            }
        }
        catch(ProcessingException e)
        {
            screenError(cmsData.getNode(), context, "cannot find document resource for printing");
        }
    }
    
    /**
     * Because this screen shows document from other part of the site depending on a URL parameter
     * it needs a special security check in order to avoid secure document data interception by
     * using hand written URLs.
     * This method check for visibility of a printing node itself and for visiblity of a document
     * viewed in print mode.
     */
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        if(super.checkAccessRights(context))
        {
            long printDocId = parameters.getLong("print_doc_id", -1L);
            if( printDocId != -1L )
            {
                CmsData cmsData = cmsDataFactory.getCmsData(context);
                NavigationNodeResource printDoc = StructureUtil.getNode(coralSession, printDocId);
                return printDoc.canView(context, cmsData, cmsData.getUserData().getSubject());
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }
}

