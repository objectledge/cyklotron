package net.cyklotron.cms.modules.actions.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import pl.caltha.forms.FormsService;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.documents.FooterResource;
import net.cyklotron.cms.documents.FooterResourceImpl;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: UpdateFooter.java,v 1.1 2006-05-08 12:29:37 pablo Exp $
 */
public class UpdateFooter extends BaseDocumentAction
{
    public UpdateFooter(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FormsService formsService,
        DocumentService documentService)
    {
        super(logger, structureService, cmsDataFactory, styleService, formsService, documentService);
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long footerId = parameters.getLong("footerId");
            FooterResource footer = FooterResourceImpl.getFooterResource(coralSession, footerId);
            String name = parameters.get("name","");
            if(name.length() == 0)
            {
                route(mvcContext, templatingContext, "documents.EditFooter", "invalid_footer_name");
                return;
            }
            String content = parameters.get("content", "");
            if(!footer.getName().equals(name))
            {
                SiteResource site = getSite(context);
                Resource root = documentService.getFootersRoot(coralSession, site);
                Resource[] children = coralSession.getStore().getResource(root, name);
                if(children.length > 0)
                {   
                    route(mvcContext, templatingContext, "documents.EditFooter", "footer_name_repeated");
                    return;
                }
                coralSession.getStore().setName(footer, name);
            }
            footer.setContent(content);
            footer.update();
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to add footer", e);
        }
    }
}
