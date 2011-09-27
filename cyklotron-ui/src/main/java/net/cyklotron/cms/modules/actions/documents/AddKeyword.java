package net.cyklotron.cms.modules.actions.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.forms.FormsService;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;


import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.documents.keywords.KeywordResource;
import net.cyklotron.cms.documents.keywords.KeywordResourceImpl;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: AddFooter.java,v 1.1 2006-05-08 12:29:37 pablo Exp $
 */
public class AddKeyword extends BaseDocumentAction
{
    public AddKeyword(Logger logger, StructureService structureService,
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
                route(mvcContext, templatingContext, "documents.AddKeyword", "invalid_keyword_name");
                return;
            }            
            String pattern = parameters.get("pattern","");
            if(pattern.length() == 0)
            {
                route(mvcContext, templatingContext, "documents.AddKeyword", "invalid_keyword_pattern");
                return;
            }
            
            String title = parameters.get("title", "");
            boolean regExp = parameters.getBoolean("reg_exp", false);
            boolean newWindow = parameters.getBoolean("new_window", false);
            String linkClass = parameters.get("link_class", "");
            String hrefExternal = parameters.get("href_external", "");
            String hrefInternal = parameters.get("href_internal", "");
            boolean external = "external".equals(parameters.get("link_type", "external")) ? true : false;

            SiteResource site = getSite(context);
            Resource root = documentService.getKeywordsRoot(coralSession, site);
            KeywordResource keyword = KeywordResourceImpl.createKeywordResource(coralSession, name,
                root, external, newWindow, pattern, regExp);

            keyword.setTitle(title);
            if(external)
            {
                if(!(hrefExternal.startsWith("http://") || hrefExternal.startsWith("https://")))
                {
                    hrefExternal = "http://" + hrefExternal;
                }
                keyword.setHrefExternal(hrefExternal);
            }
            else 
            {
                String relativePath = parameters.get("relative_path", "");
                Resource[] section = coralSession.getStore().getResourceByPath(
                    relativePath + hrefInternal);
                if(section.length != 1 || !(section[0] instanceof NavigationNodeResource))
                {
                    route(mvcContext, templatingContext, "documents.AddKeyword", "invalid_target");
                    return;
                }
                keyword.setHrefInternal((NavigationNodeResource)section[0]);
            }
            
            long category_id = parameters.getLong("category_id", -1L);
            CategoryResource category = null;
            try
            {
                if(category_id != -1L)
                {
                    category = CategoryResourceImpl.getCategoryResource(coralSession, category_id);
                }
                keyword.setCategory(category);
            }
            catch(EntityDoesNotExistException e)
            {
                route(mvcContext, templatingContext, "documents.UpdateKeyword", "invalid_category");
            }
            
            keyword.setLinkClass(linkClass);
            keyword.update();
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to add keyword", e);
        }
    }
}
