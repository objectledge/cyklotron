package net.cyklotron.cms.modules.actions.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
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
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: UpdateFooter.java,v 1.1 2006-05-08 12:29:37 pablo Exp $
 */
public class UpdateKeyword
    extends BaseDocumentAction
{
    private final CoralSessionFactory coralSessionFactory;

    public UpdateKeyword(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FormsService formsService,
        DocumentService documentService, CoralSessionFactory coralSessionFactory)
    {
        super(logger, structureService, cmsDataFactory, styleService, formsService, documentService);
        this.coralSessionFactory = coralSessionFactory;
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long keywordId = parameters.getLong("keywordId");
            KeywordResource keyword = KeywordResourceImpl.getKeywordResource(coralSession,
                keywordId);

            String pattern = parameters.get("pattern", "");
            if(pattern.length() == 0)
            {
                route(mvcContext, templatingContext, "documents.UpdateKeyword",
                    "invalid_keyword_pattern");
                return;
            }

            String title = parameters.get("title", "");
            boolean regExp = parameters.getBoolean("reg_exp", false);
            boolean newWindow = parameters.getBoolean("new_window", false);
            String linkClass = parameters.get("link_class", "");
            String hrefExternal = parameters.get("href_external", "");
            String hrefInternal = parameters.get("href_internal", "");
            boolean external = "external".equals(parameters.get("link_type", "external")) ? true
                : false;

            keyword.setTitle(title);
            keyword.setExternal(external);
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
                    route(mvcContext, templatingContext, "documents.UpdateKeyword",
                        "invalid_target");
                    return;
                }
                keyword.setHrefInternal((NavigationNodeResource)section[0]);
            }

            try
            {
                ResourceList<CategoryResource> categories = getCategories(parameters, "categories",
                    coralSession, coralSessionFactory);
                keyword.setCategories(categories);
            }
            catch(EntityDoesNotExistException e)
            {
                route(mvcContext, templatingContext, "documents.UpdateKeyword", "invalid_category");
            }

            keyword.setNewWindow(newWindow);
            keyword.setRegexp(regExp);
            keyword.setPattern(pattern);
            keyword.setLinkClass(linkClass);
            keyword.update();
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to update keyword", e);
        }
    }
    
    private static ResourceList<CategoryResource> getCategories(Parameters parameters,
        String parameterName,CoralSession coralSession, CoralSessionFactory coralSessionFactory)
        throws EntityDoesNotExistException
    {
        CategoryResource category = null;
        ResourceList<CategoryResource> categories = new ResourceList<CategoryResource>(coralSessionFactory);
        String[] tmp = parameters.get(parameterName).split(" ");

        for(int i = 0; i < tmp.length; i++)
        {
            category = CategoryResourceImpl.getCategoryResource(coralSession, new Long(tmp[i]));
            if(category != null)
            {
                categories.add(category);
            }
        }
        return categories;
    }
    
    public boolean checkAccessRights(Context context)
                    throws ProcessingException
    {
          CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
          return coralSession.getUserSubject().hasRole(getSite(context).getAdministrator());
    }
}
