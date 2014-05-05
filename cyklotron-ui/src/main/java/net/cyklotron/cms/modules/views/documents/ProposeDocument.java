package net.cyklotron.cms.modules.views.documents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.html.HTMLService;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentNodeResourceImpl;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.organizations.Organization;
import net.cyklotron.cms.organizations.OrganizationRegistryService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.internal.OrganizationData;
import net.cyklotron.cms.structure.internal.ProposedDocumentData;
import net.cyklotron.cms.style.StyleService;

/**
 * Stateful screen for propose document application.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawe� Potempski</a>
 * @version $Id: ProposeDocument.java,v 1.12 2008-11-04 17:14:48 rafal Exp $
 */
public class ProposeDocument
    extends BaseSkinableDocumentScreen
{
    private final CategoryService categoryService;

    private final RelatedService relatedService;

    private final HTMLService htmlService;

    private final OrganizationRegistryService organizationsRegistry;

    private final List<String> REQUIRES_AUTHENTICATED_USER = Arrays.asList("MyDocuments",
        "EditDocument", "RemovalRequest", "RedactorsNote");

    private final DocumentService documentService;

    private final MyDocumentsImpl myDocumentsImpl;

    public ProposeDocument(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        StructureService structureService, StyleService styleService, SkinService skinService,
        MVCFinder mvcFinder, TableStateManager tableStateManager, CategoryService categoryService,
        RelatedService relatedService, HTMLService htmlService,
        OrganizationRegistryService ngoDatabaseService, DocumentService documentService,
        MyDocumentsImpl myDocumentsImpl)
    {
        super(context, logger, preferencesService, cmsDataFactory, structureService, styleService,
                        skinService, mvcFinder, tableStateManager);
        this.categoryService = categoryService;
        this.relatedService = relatedService;
        this.htmlService = htmlService;
        this.organizationsRegistry = ngoDatabaseService;
        this.documentService = documentService;
        this.myDocumentsImpl = myDocumentsImpl;
    }

    @Override
    public String getState()
        throws ProcessingException
    {
        // this method is called multiple times during rendering, so it makes sense to cache the
        // evaluated state
        String state = (String)context.getAttribute(getClass().getName() + ".state");
        if(state == null)
        {
            Parameters parameters = RequestParameters.getRequestParameters(context);
            AuthenticationContext authContext = context.getAttribute(AuthenticationContext.class);
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            Parameters screenConfig = cmsData.getEmbeddedScreenConfig();
            long mainCategoryRoot = screenConfig.getLong("category_id_2", -1);
            long[] selectedCategories = parameters.getLongs("selected_categories");
            state = parameters.get("state", null);
            if(state == null)
            {
                boolean editingEnabled = screenConfig.getBoolean("editing_enabled", false);
                if(editingEnabled)
                {
                    if(authContext.isUserAuthenticated())
                    {
                        state = "MyDocuments";
                    }
                    else
                    {
                        state = "Anonymous";
                    }
                }
                else
                {
                    state = "AddDocument";
                }
            }
            if("AddDocument".equals(state) && mainCategoryRoot != -1
                && selectedCategories.length == 0)
            {
                state = "DocumentCategory";
            }
            context.setAttribute(getClass().getName() + ".state", state);
        }
        return state;
    }

    @Override
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return REQUIRES_AUTHENTICATED_USER.contains(getState());
    }

    @Override
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        TemplatingContext templatingContext = context.getAttribute(TemplatingContext.class);
        String result = (String)templatingContext.get("result");
        if("exception".equals(result))
        {
            return true;
        }
        String state = getState();
        if("Anonymous".equals(state))
        {
            return true;
        }
        try
        {
            Parameters requestParameters = context.getAttribute(RequestParameters.class);
            CoralSession coralSession = context.getAttribute(CoralSession.class);
            Subject userSubject = coralSession.getUserSubject();
            if("AddDocument".equals(state) || "MyDocuments".equals(state)
                || "DocumentCategory".equals(state))
            {
                Permission submitPermission = coralSession.getSecurity().getUniquePermission(
                    "cms.structure.submit");
                CmsData cmsData = cmsDataFactory.getCmsData(context);
                Parameters screenConfig = cmsData.getEmbeddedScreenConfig();
                long parentId = screenConfig.getLong("parent_id", -1L);
                Resource parent = parentId != -1L ? NavigationNodeResourceImpl
                    .getNavigationNodeResource(coralSession, parentId) : cmsData.getNode();
                return userSubject.hasPermission(parent, submitPermission);
            }
            if("EditDocument".equals(state) || "RemovalRequest".equals(state)
                || "RedactorsNote".equals(state))
            {
                long id = requestParameters.getLong("doc_id", -1);
                Resource node = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession,
                    id);
                Permission modifyPermission = coralSession.getSecurity().getUniquePermission(
                    "cms.structure.modify");
                Permission modifyOwnPermission = coralSession.getSecurity().getUniquePermission(
                    "cms.structure.modify_own");
                if(userSubject.hasPermission(node, modifyPermission))
                {
                    return true;
                }
                if(node.getOwner().equals(userSubject)
                    && userSubject.hasPermission(node, modifyOwnPermission))
                {
                    return true;
                }
                if(node.getCreatedBy().equals(userSubject)
                    && userSubject.hasPermission(node, modifyOwnPermission))
                {
                    return true;
                }
            }
            return false;
        }
        catch(Exception e)
        {
            throw new ProcessingException(e);
        }
    }

    /**
     * Welcome view for the anonymous user.
     * <p>
     * Allows the user to log in, or proceed to submit a document anonymously.
     * </p>
     */
    public void prepareAnonymous(Context context)
    {

    }

    /**
     * Set main category for new document.
     */
    public void prepareDocumentCategory(Context context)
        throws ProcessingException
    {
        try
        {
            prepareCategories(context, true);
        }
        catch(Exception e)
        {
            throw new ProcessingException("internal errror", e);
        }
    }

    /**
     * Submitted documents list for an authenticated user.
     */
    public void prepareMyDocuments(Context context)
        throws ProcessingException
    {
        try
        {
            I18nContext i18nContext = context.getAttribute(I18nContext.class);
            TemplatingContext templatingContext = context.getAttribute(TemplatingContext.class);
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            Parameters parameters = RequestParameters.getRequestParameters(context);
            Parameters screenConfig = cmsData.getEmbeddedScreenConfig();
            CoralSession coralSession = context.getAttribute(CoralSession.class);
            CategoryQueryResource includeQuery = myDocumentsImpl.getQueryResource("include",
                screenConfig);
            CategoryQueryResource excludeQuery = myDocumentsImpl.getQueryResource("exclude",
                screenConfig);
            String whereClause = "created_by = " + coralSession.getUserSubject().getIdString();

            TableModel<DocumentNodeResource> model;
            if(includeQuery == null)
            {
                model = myDocumentsImpl.siteBasedModel(cmsData, i18nContext.get(), whereClause);
            }
            else
            {
                model = myDocumentsImpl.queryBasedModel(includeQuery, excludeQuery, cmsData,
                    i18nContext.get(), whereClause);
            }

            List<TableFilter<? super DocumentNodeResource>> filters;
            String[] selectedStates = parameters.getStrings("selected_states");
            if(selectedStates.length > 0) { 
                filters = myDocumentsImpl.statesFilter(selectedStates);
            } else {
                filters = myDocumentsImpl.excludeStatesFilter("expired");
            }

            TableState state = tableStateManager.getState(context, this.getClass().getName()
                + ":MyDocuments");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setSortColumnName("creation.time");
                state.setAscSort(false);
                state.setPageSize(20);
            }
            templatingContext.put("table", new TableTool<DocumentNodeResource>(state, filters,
                model));
            templatingContext.put("selectedStates", Arrays.asList(selectedStates));
            templatingContext.put("filterStates",parameters.getBoolean("filter_states",false));
            templatingContext.put("documentState", new DocumentStateTool(coralSession, logger));
        }
        catch(Exception e)
        {
            throw new ProcessingException("internal errror", e);
        }
    }

    /**
     * Propse a new document, either anonymously or as an authenitcated user.
     * 
     * @param context
     * @throws ProcessingException
     */
    public void prepareAddDocument(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        try
        {
            // refill parameters in case we are coming back failed validation
            Parameters screenConfig = getScreenConfig();
            ProposedDocumentData data = new ProposedDocumentData(screenConfig,
                documentService.getPreferredImageSizes(), logger);
            data.fromParameters(parameters, coralSession);
            if(!parameters.getBoolean("form_loaded", false))
            {
                long organizationId = parameters.getLong("organizationId", -1L);
                if(organizationId > 0)
                {
                    Organization organization = this.organizationsRegistry
                        .getOrganization(organizationId);
                    if(organization != null)
                    {
                        List<OrganizationData> organizationDataList = new ArrayList<OrganizationData>();
                        OrganizationData organizationData = new OrganizationData();
                        organizationData.fromOrganization(organization);
                        organizationDataList.add(organizationData);
                        data.setOrganizations(organizationDataList);
                    }
                }
            }
            data.toTemplatingContext(templatingContext);
            prepareCategories(context, true);
            // resolve parent node in case template needs it for security check
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            long parentId = screenConfig.getLong("parent_id", -1L);
            Resource parentNode = parentId != -1L ? NavigationNodeResourceImpl
                .getNavigationNodeResource(coralSession, parentId) : cmsData.getNode();
            templatingContext.put("parent_node", parentNode);
            templatingContext.put("add_captcha", screenConfig.getBoolean("add_captcha", false));
        }
        catch(Exception e)
        {
            screenError(getNode(), context, "Screen Error ", e);
        }
    }

    /**
     * Edit a previously submitted document.
     * 
     * @param context
     * @throws ProcessingException
     */
    public void prepareEditDocument(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        try
        {
            long docId = parameters.getLong("doc_id");
            DocumentNodeResource node = DocumentNodeResourceImpl.getDocumentNodeResource(
                coralSession, docId);
            templatingContext.put("doc", node);
            ProposedDocumentData data = new ProposedDocumentData(getScreenConfig(),
                documentService.getPreferredImageSizes(), logger);
            if(parameters.getBoolean("form_loaded", false))
            {
                data.fromParameters(parameters, coralSession);
            }
            else
            {
                if(node.isProposedContentDefined())
                {
                    data.fromProposal(node, coralSession);
                }
                else
                {
                    data.fromNode(node, categoryService, relatedService, coralSession);
                    data.cleanupContent(htmlService);
                }
            }
            data.toTemplatingContext(templatingContext);
            prepareCategories(context, true);
        }
        catch(Exception e)
        {
            screenError(getNode(), context, "Internal Error", e);
        }
    }

    /**
     * Send removal request previously submitted document.
     * 
     * @param context
     * @throws ProcessingException
     */
    public void prepareRemovalRequest(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        try
        {
            long docId = parameters.getLong("doc_id");
            DocumentNodeResource node = DocumentNodeResourceImpl.getDocumentNodeResource(
                coralSession, docId);
            templatingContext.put("doc", node);
            ProposedDocumentData data = new ProposedDocumentData(getScreenConfig(),
                documentService.getPreferredImageSizes(), logger);
            if(parameters.getBoolean("form_loaded", false))
            {
                data.fromParameters(parameters, coralSession);
            }
            else
            {
                if(node.isProposedContentDefined())
                {
                    data.fromProposal(node, coralSession);
                }
                else
                {
                    data.fromNode(node, categoryService, relatedService, coralSession);
                    data.cleanupContent(htmlService);
                }
            }
            data.toTemplatingContext(templatingContext);
            prepareCategories(context, true);
        }
        catch(Exception e)
        {
            screenError(getNode(), context, "Internal Error", e);
        }
    }

    /**
     * Receive redactor's note.
     * 
     * @param context
     * @throws ProcessingException
     */
    public void prepareRedactorsNote(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);

        try
        {
            long docId = parameters.getLong("doc_id");
            DocumentNodeResource node = DocumentNodeResourceImpl.getDocumentNodeResource(
                coralSession, docId);
            templatingContext.put("doc", node);
            prepareCategories(context, true);
        }
        catch(Exception e)
        {
            screenError(getNode(), context, "Internal Error", e);
        }
    }
}
