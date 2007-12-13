/*
 * Created on Oct 14, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.category.query;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.relation.query.parser.ASTIntersectionExpression;
import org.objectledge.coral.relation.query.parser.ASTInvertedRelationExpression;
import org.objectledge.coral.relation.query.parser.ASTRelationMapExpression;
import org.objectledge.coral.relation.query.parser.ASTRelationName;
import org.objectledge.coral.relation.query.parser.ASTResourceIdentifierId;
import org.objectledge.coral.relation.query.parser.ASTResourceIdentifierPath;
import org.objectledge.coral.relation.query.parser.ASTStart;
import org.objectledge.coral.relation.query.parser.ASTSumExpression;
import org.objectledge.coral.relation.query.parser.ASTTransitiveRelationMapExpression;
import org.objectledge.coral.relation.query.parser.RelationQueryParser;
import org.objectledge.coral.relation.query.parser.RelationQueryParserVisitor;
import org.objectledge.coral.relation.query.parser.SimpleNode;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

/**
 * Displays a list of defined category queries.
 */
public class CategoryQueryList
    extends BaseCMSScreen
{
    private final CategoryQueryService categoryQueryService;

    public CategoryQueryList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryQueryService categoryQueryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.categoryQueryService = categoryQueryService;
    }

    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        SiteResource site = getSite();
        try
        {
            Resource queryRoot = categoryQueryService.getCategoryQueryRoot(coralSession, site);
            TableState state = tableStateManager.getState(context,
                "cms:category,query,CategoryQueryList:" + site.getIdString());
            if(state.isNew())
            {
                state.setRootId(queryRoot.getIdString());
                state.setTreeView(false);
                state.setShowRoot(false);
                state.setSortColumnName("name");
            }
            TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
            TableTool table = new TableTool(state, null, model);
            templatingContext.put("table", table);
            boolean verbose = parameters.getBoolean("verbose", false);
            templatingContext.put("verbose", verbose);
            templatingContext.put("queryParser", new QueryParserTool(coralSession));
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.category.query.modify");
    }

    public class QueryParserTool
    {
        private final CoralSession coralSession;

        public QueryParserTool(CoralSession coralSession)
        {
            this.coralSession = coralSession;
        }

        public String parse(CategoryQueryResource queryResource)
            throws Exception
        {
            RelationQueryParserVisitor visitor = new RelationQueryParserVisitor()
                {
                    public Object visit(SimpleNode node, Object data)
                    {
                        if(node.jjtGetNumChildren() > 1)
                        {
                            StringBuilder buff = new StringBuilder();
                            for (int i = 0; i < node.jjtGetNumChildren(); i++)
                            {
                                buff.append((String)node.jjtGetChild(i).jjtAccept(this, data));
                            }
                            return buff.toString();
                        }
                        else
                        {
                            return (String)node.jjtGetChild(0).jjtAccept(this, data);
                        }
                    }

                    public Object visit(ASTStart node, Object data)
                    {
                        return visit((SimpleNode)node, data);
                    }

                    private Object visitComposite(SimpleNode node, Object data, char op)
                    {
                        StringBuilder buff = new StringBuilder();
                        for (int i = 0; i < node.jjtGetNumChildren(); i++)
                        {
                            buff.append((String)node.jjtGetChild(i).jjtAccept(this, data));
                            if(i < node.jjtGetNumChildren() - 1)
                            {
                                buff.append(' ').append(op).append(' ');
                            }
                        }
                        if(node.jjtGetNumChildren() > 1
                            && !(node.jjtGetParent() instanceof ASTStart))
                        {
                            buff.insert(0, '(');
                            buff.append(')');
                        }
                        return buff.toString();
                    }

                    public Object visit(ASTSumExpression node, Object data)
                    {
                        return visitComposite(node, data, '|');
                    }

                    public Object visit(ASTIntersectionExpression node, Object data)
                    {
                        return visitComposite(node, data, '&');
                    }

                    public Object visit(ASTRelationMapExpression node, Object data)
                    {
                        return visit((SimpleNode)node, data);
                    }

                    public Object visit(ASTTransitiveRelationMapExpression node, Object data)
                    {
                        return visit((SimpleNode)node, data);
                    }

                    public Object visit(ASTInvertedRelationExpression node, Object data)
                    {
                        return "";
                    }

                    public Object visit(ASTRelationName node, Object data)
                    {
                        return "";
                    }

                    public Object visit(ASTResourceIdentifierId node, Object data)
                    {
                        try
                        {
                            return coralSession.getStore().getResource(
                                Long.parseLong(node.getIdentifier())).getName();
                        }
                        catch(Exception e)
                        {
                            throw new RuntimeException("Invalid category resource id in query", e);
                        }
                    }

                    public Object visit(ASTResourceIdentifierPath node, Object data)
                    {
                        try
                        {
                            return coralSession.getStore().getUniqueResourceByPath(
                                node.getIdentifier()).getName();
                        }
                        catch(Exception e)
                        {
                            throw new RuntimeException("Invalid category resource path in query", e);
                        }
                    }
                };
            try
            {
                String query = queryResource.getQuery();
                if(query == null || query.equals(""))
                {
                    return "";
                }
                else
                {
                    SimpleNode queryAST = RelationQueryParser.executeParse(query);
                    return (String)visitor.visit(queryAST, null);
                }
            }
            catch(Exception e)
            {
                throw new ProcessingException("failed to parse query #"
                    + queryResource.getIdString(), e);
            }
        }
    }
}
