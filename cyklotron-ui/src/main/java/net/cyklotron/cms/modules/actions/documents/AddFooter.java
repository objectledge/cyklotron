package net.cyklotron.cms.modules.actions.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.forms.FormsService;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;


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
 * @version $Id: AddFooter.java,v 1.1 2006-05-08 12:29:37 pablo Exp $
 */
public class AddFooter extends BaseDocumentAction
{
    public AddFooter(Logger logger, StructureService structureService,
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
            String name = parameters.get("name","");
            if(name.length() == 0)
            {
                route(mvcContext, templatingContext, "documents.AddFooter", "invalid_footer_name");
                return;
            }
            String content = parameters.get("content", "");
            boolean enabled = parameters.getBoolean("enabled", false);
            SiteResource site = getSite(context);
            Resource root = documentService.getFootersRoot(coralSession, site);
            Resource[] children = coralSession.getStore().getResource(root, name);
            if(children.length > 0)
            {
                route(mvcContext, templatingContext, "documents.AddFooter", "footer_name_repeated");
                return;
            }
            FooterResource footer = FooterResourceImpl.createFooterResource(coralSession, name, root);
            footer.setContent(content);
            footer.setEnabled(enabled);
            footer.setSequence(coralSession.getStore().getResource(root).length);
            footer.update();
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to add footer", e);
        }
    }
}
