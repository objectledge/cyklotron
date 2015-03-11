package net.cyklotron.cms.modules.actions.documents;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResourceImpl;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.actions.category.BaseCategorizationAction;
import net.cyklotron.cms.structure.StructureService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.relation.MalformedRelationQueryException;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.relation.RelationModification;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CategorizeDocuments
    extends BaseCategorizationAction
{

    CategoryQueryService categoryQueryService;

    public CategorizeDocuments(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryService categoryService,
        IntegrationService integrationService, CategoryQueryService categoryQueryService)
    {
        super(logger, structureService, cmsDataFactory, categoryService, integrationService);
        this.categoryQueryService = categoryQueryService;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        int actions_done = 0;
        String command_xml = parameters.get("command_xml", "");
        Document document = null;
        if(command_xml != "")
        {
            DocumentBuilder parser;
            try
            {
                parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                ByteArrayInputStream stream = new ByteArrayInputStream(command_xml.getBytes());
                document = parser.parse(stream);
                NodeList actions = document.getElementsByTagName("action");
                List<Resource> documents = new ArrayList<Resource>();
                templatingContext.put("actions_total", actions.getLength());
                for(int i = 0; i < actions.getLength(); i++)
                {
                    Relation refs = categoryService.getResourcesRelation(coralSession);
                    RelationModification diff = new RelationModification();
                    Element action = (Element)actions.item(i);

                    String[] from_ids = null;
                    String from_query = null;
                    long from_query_id = 0;
                    NodeList fromCategories = ((Element)action.getElementsByTagName("from").item(0))
                        .getElementsByTagName("category");
                    NodeList fromQuery = ((Element)action.getElementsByTagName("from").item(0))
                        .getElementsByTagName("query");
                    NodeList fromQueryId = ((Element)action.getElementsByTagName("from").item(0))
                                    .getElementsByTagName("query_id");
                    if(fromCategories.getLength() > 0)
                    {
                        from_ids = new String[fromCategories.getLength()];
                        for(int f = 0; f < fromCategories.getLength(); f++)
                        {
                            Element element = (Element)fromCategories.item(f);
                            from_ids[f] = element.getTextContent();
                        }
                    }
                    else if(fromQuery.getLength() > 0)
                    {
                        from_query = ((Element)fromQuery.item(0)).getTextContent();
                    }
                    else if(fromQueryId.getLength() > 0)
                    {
                        from_query_id = Long.parseLong(((Element)fromQueryId.item(0)).getTextContent());
                    }
                    if(from_ids != null && from_ids.length > 0)
                    {
                        documents = getDocumentsByCategoryIds(from_ids, coralSession);
                    }
                    else if(from_query != null)
                    {
                        documents = getDocumentsByCategoryQuery(from_query, coralSession);
                    }
                    else if(from_query_id > 0) {
                        CategoryQueryResource categoryQueryResource = CategoryQueryResourceImpl
                                        .getCategoryQueryResource(coralSession, from_query_id);
                        documents = getDocumentsByCategoryQuery(categoryQueryResource.getQuery(), coralSession);
                    }
                    
                    NodeList add = action.getElementsByTagName("empty");
                    if(action.getElementsByTagName("add").getLength() > 0)
                    {
                        add = ((Element)action.getElementsByTagName("add").item(0))
                            .getElementsByTagName("category");
                    }
                    NodeList remove = action.getElementsByTagName("empty");
                    if(action.getElementsByTagName("remove").getLength() > 0)
                    {
                        remove = ((Element)action.getElementsByTagName("remove").item(0))
                            .getElementsByTagName("category");
                    }
                    for(Resource doc : documents)
                    {
                        if(doc instanceof DocumentNodeResource)
                        {
                            for(int t = 0; t < add.getLength(); t++)
                            {
                                Element element = (Element)add.item(t);
                                long catId = Long.parseLong(element.getTextContent());
                                Resource category = coralSession.getStore().getResource(catId);
                                if(category instanceof CategoryResource)
                                {
                                    diff.add((CategoryResource)category, doc);
                                }
                            }
                            for(int t = 0; t < remove.getLength(); t++)
                            {
                                Element element = (Element)remove.item(t);
                                long catId = Long.parseLong(element.getTextContent());
                                Resource category = coralSession.getStore().getResource(catId);
                                if(category instanceof CategoryResource)
                                {
                                    diff.remove((CategoryResource)category, doc);
                                }
                            }
                            coralSession.getRelationManager().updateRelation(refs, diff);
                        }
                    }
                    actions_done++;
                }
            }
            catch(ParserConfigurationException | SAXException | IOException
                            | MalformedRelationQueryException | EntityDoesNotExistException e)
            {
                templatingContext.put("result", "error");
                templatingContext.put("trace", e.getMessage());
                templatingContext.put("actions_done", actions_done);
                logger.error(new StackTrace(e).toString(), e);
                return;
            }
        }
        templatingContext.put("actions_done", actions_done);
        templatingContext.put("uploaded", command_xml);
        templatingContext.put("result", "success");
    }

    private List<Resource> getDocumentsByCategoryIds(String[] categoryIds, CoralSession coralSession)
        throws MalformedRelationQueryException, EntityDoesNotExistException
    {
        String catRelName = categoryService.getResourcesRelation(coralSession).getName();
        StringBuilder query = new StringBuilder();
        for(String categoryId : categoryIds)
        {
            query.append(query.length() == 0 ? "" : "*");
            query.append("MAP('").append(catRelName).append("')");
            query.append("{RES(").append(categoryId).append(")}");
        }
        query.append(";");
        return Arrays.asList(coralSession.getRelationQuery().query(query.toString(),
            categoryQueryService.getCategoryResolver()));
    }

    private List<Resource> getDocumentsByCategoryQuery(String query, CoralSession coralSession)
        throws MalformedRelationQueryException, EntityDoesNotExistException
    {
        return Arrays.asList(coralSession.getRelationQuery().query(query.toString(),
            categoryQueryService.getCategoryResolver()));
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }
}
