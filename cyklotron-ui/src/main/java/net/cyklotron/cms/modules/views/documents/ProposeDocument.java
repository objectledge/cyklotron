package net.cyklotron.cms.modules.views.documents;

import java.util.Arrays;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
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
    public ProposeDocument(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        StructureService structureService, StyleService styleService, SkinService skinService,
        MVCFinder mvcFinder, TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, structureService, styleService,
                        skinService, mvcFinder, tableStateManager);
        
    }

    public String getState()
    {
        // this method is called multiple times during rendering, so it makes sense to cache the evaluated state
        String state = (String) context.getAttribute(getClass().getName()+".state");
        if(state == null)
        {
            Parameters parameters = RequestParameters.getRequestParameters(context);
            AuthenticationContext authContext = context.getAttribute(AuthenticationContext.class);
            state = parameters.get("state",null);
            if(state == null)
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
            context.setAttribute(getClass().getName()+".state", state);
        }
        return state;
    }
    
    @Override
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        String state = getState();
        if("EditDocument".equals(state))
        {
            return true;
        }
        if("MyDocuments".equals(state))
        {
            return true;
        }
        return false;
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
        if("MyDocuments".equals(state))
        {
            // no particular permission needed to see the stuff you sumbitted, provided you are
            // authenticated
            return true;
        }
        try
        {
            Parameters requestParameters = context.getAttribute(RequestParameters.class);
            CoralSession coralSession = context.getAttribute(CoralSession.class);
            Subject userSubject = coralSession.getUserSubject();
            if("AddDocument".equals(state))
            {
                long id = requestParameters.getLong("parent_node_id", -1);
                Resource node;
                if(id != -1)
                {
                    node = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, id);
                }
                else
                {
                    node = cmsDataFactory.getCmsData(context).getHomePage();
                }
                Permission submitPermission = coralSession.getSecurity().getUniquePermission(
                    "cms.structure.submit");
                return userSubject.hasPermission(node, submitPermission);
            }
            if("EditDocument".equals(state))
            {
                long id = requestParameters.getLong("node_id", -1);
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
                return false;
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException(e);
        }
        throw new ProcessingException("invalid state " + state);
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
     * Submitted documents list for an authenticated user.
     */
    public void prepareMyDocuments(Context context) throws ProcessingException
    {
        try
        {
            CoralSession coralSession = context.getAttribute(CoralSession.class);
            String query = "FIND RESOURCE FROM documents.document_node WHERE created_by = "
                + coralSession.getUserSubject().getIdString();
            List<Resource> myDocuments = coralSession.getQuery().executeQuery(query).getList(1);
            TemplatingContext templatingContext = context.getAttribute(TemplatingContext.class);
            templatingContext.put("myDocuments", myDocuments);
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
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        SiteResource site = getSite();
        try
        {
            templatingContext.put("styles", Arrays.asList(styleService.getStyles(coralSession, site)));
            long parent_node_id = parameters.getLong("parent_node_id", -1);
            if(parent_node_id == -1)
            {
                templatingContext.put("parent_node",getHomePage());
            }
            else
            {
                templatingContext.put("parent_node",NavigationNodeResourceImpl.getNavigationNodeResource(coralSession,parent_node_id));
            }
            // refill parameters in case we are coming back failed validation
            templatingContext.put("calendar_tree", parameters.getBoolean("calendar_tree", false));
            templatingContext.put("inherit_categories", parameters.getBoolean("inherit_categories",false));
            templatingContext.put("name", parameters.get("name",""));
            templatingContext.put("title", parameters.get("title",""));
            templatingContext.put("abstract", parameters.get("abstract",""));
            templatingContext.put("content", parameters.get("content",""));
            templatingContext.put("event_place", parameters.get("event_place",""));
            templatingContext.put("organized_by", parameters.get("organized_by",""));
            templatingContext.put("organized_address", parameters.get("organized_address",""));
            templatingContext.put("organized_phone", parameters.get("organized_phone",""));
            templatingContext.put("organized_fax", parameters.get("organized_fax",""));
            templatingContext.put("organized_email", parameters.get("organized_email",""));
            templatingContext.put("organized_www", parameters.get("organized_www",""));
            templatingContext.put("source_name", parameters.get("source_name",""));
            templatingContext.put("source_url", parameters.get("source_url",""));
            templatingContext.put("proposer_credentials", parameters.get("proposer_credentials",""));
            templatingContext.put("proposer_email", parameters.get("proposer_email",""));
            templatingContext.put("description", parameters.get("description",""));
            transferDateParam(parameters, templatingContext, "validity_start");
            transferDateParam(parameters, templatingContext, "validity_end");
            transferDateParam(parameters, templatingContext, "event_start");
            transferDateParam(parameters, templatingContext, "event_end");
            templatingContext.put("category_ids", parameters.getLongs("category_id"));

            prepareCategories(context, true);

            Parameters screenConfig = getScreenConfig();
            boolean attachmentsEnabled = screenConfig.getBoolean("attachments_enabled", false);
            if(attachmentsEnabled)
            {
                templatingContext.put("attachments_enabled", attachmentsEnabled);
                templatingContext.put("attachments_max_count", screenConfig.getInt(
                    "attachments_max_count", -1));
                templatingContext.put("attachments_max_size", screenConfig.getInt(
                    "attachments_max_size", -1));
                templatingContext.put("attachments_allowed_formats", screenConfig.get(
                    "attachments_allowed_formats", "jpg gif doc rtf pdf xls"));
                for (int i = 0; i < screenConfig.getInt("attachments_max_count", 0); i++)
                {
                    String descriptionKey = "attachment_description_" + (i + 1);
                    String descriptions = parameters.get(descriptionKey, "");
                    templatingContext.put(descriptionKey, descriptions);
                }
            }
            
        }
        catch(Exception e)
        {
            screenError(getNode(), context, "Screen Error "+e);
        }
    }
    
    /**
     * Edit a previously submitted document.
     * 
     * @param context
     */
    public void prepareEditDocument(Context context)
    {
        
    }
    
    public void prepareResult(Context context)
    throws ProcessingException
    {
        // does nothing
    }

    private void transferDateParam(Parameters parameters, TemplatingContext templatingContext, String param)
    {
        if(parameters.get(param, "").length() > 0)
        {
            templatingContext.put(param, Long.parseLong(parameters.get(param,"")));
        }
        else
        {
            templatingContext.remove(param);
        }
    }
}
