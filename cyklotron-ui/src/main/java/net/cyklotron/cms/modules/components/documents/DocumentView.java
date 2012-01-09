package net.cyklotron.cms.modules.components.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * DocumentView component displays document contents.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentView.java,v 1.3 2005-03-08 10:54:53 pablo Exp $
 */
public class DocumentView
    extends SkinableCMSComponent
{
    public DocumentView(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        NavigationNodeResource node = getCmsData().getContentNode();
        if(node != null)
        {
            if(node instanceof DocumentNodeResource)
            {
                templatingContext.put("document_tool", ((DocumentNodeResource)node).getDocumentTool(context));
            }
            else
            {
                componentError(context, "Cannot display a non document node");
            }
        }
        else
        {
            componentError(context, "No navigation node selected");            
        }
    }
}
