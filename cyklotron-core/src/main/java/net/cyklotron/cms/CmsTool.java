package net.cyklotron.cms;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationException;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.relation.MalformedRelationQueryException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;

import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.documents.DocumentAliasResource;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.util.IndexTitleComparator;

/**
 * A context tool used for cms application.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CmsTool.java,v 1.18 2007-11-18 21:23:37 rafal Exp $
 */
public class CmsTool
{
    /** logging service */
    private Logger log;

    /** preferences service */
    private PreferencesService preferencesService;

    /** authentication service */
    private UserManager userManager;

    /** integration service */
    private IntegrationService integrationService;

    /** user data */
    private UserData userData;
    
    /** context */
    private Context context;
    
    private CmsDataFactory cmsDataFactory;
    
    private final SecurityService securityService;
    
    private final RelatedService relatedService;

    private final CategoryService categoryService;

    private final CategoryQueryService categoryQueryService;

    // initialization ////////////////////////////////////////////////////////
    
    public CmsTool(Context context, Logger logger, PreferencesService preferencesService,
        UserManager userManager, IntegrationService integrationService,
        RelatedService relatedService, CategoryService categoryService,
        CategoryQueryService categoryQueryService, SecurityService securityService,
        CmsDataFactory cmsDataFactory)
    {
        this.context = context;
        this.log = logger;
        this.preferencesService = preferencesService;
        this.userManager = userManager;
        this.integrationService = integrationService;
        this.categoryService = categoryService;
        this.categoryQueryService = categoryQueryService;
        this.securityService = securityService;
        this.relatedService = relatedService;
        this.cmsDataFactory = cmsDataFactory;
    }

    // public interface ///////////////////////////////////////////////////////

    // subjects ///////////////////////////////////////////////////////////////

    /**
     * Return the user data.
     *
     * @return the subject.
     */
    public UserData getUserData()
    {
        if(userData == null)
        {
            userData = new UserData(context, log, preferencesService,
                userManager, null);
        }
        return userData;
    }

    /**
     * Return the user data.
     *
     * @return the subject.
     */
    public UserData getUserData(Subject subject)
    {
        return new UserData(context, log, preferencesService,
            userManager, subject);
    }    
    
    /**
     * Return current logged subject.
     *
     * @return the subject.
     */
    public Subject getSubject()
    {
        return getUserData().getSubject();
    }
    
    /**
     * Return the subject with the given name.
     */
    public Subject getSubject(String dn)
        throws Exception
    {
        return getCoralSession().getSecurity().getSubject(dn);
    }
    
    /**
     * Returns the login of the subject with given dn.
     */
    public String getSubjectLogin(String dn)
        throws Exception
    {
        return userManager.getLogin(dn);
    }
    
    /**
     * Returns the dn of the subject with given login.
     */
    public String getSubjectName(String login)
    {
    	try
    	{
            return userManager.getUserByLogin(login).getName();
    	}
    	catch(AuthenticationException e)
    	{
    		return null;
    	}
    }

    /**
     * Return the anonymous subject.
     *
     */
    public Subject getAnonymousSubject()
        throws Exception
    {
        return getCoralSession().getSecurity().getSubject(
            Subject.ANONYMOUS);
    }
    

    // role & permission checking ////////////////////////////////////////////

    /**
     * checks whether subject has a role.
     *
     * @param subject the subject.
     * @param role the role.
     */
    public boolean hasRole(Subject subject, String role)
        throws Exception
    {
        if(subject == null)
        {
            log.error("Emtpy null has no rights");
            return false;
        }
        try
        {
            Role roleEntity = getCoralSession().getSecurity().getUniqueRole(role);
            return subject.hasRole(roleEntity);
        }
        catch(Exception e)
        {
            log.error("CmsTool",e);
            throw e;
        }
    }
    
    /**
     * checks right to the resource.
     *
     * @param subject the subject.
     * @param resource the resource.
     * @param permission the permission.
     */
    public boolean hasPermission(Subject subject, Resource resource, String permission)
        throws Exception
    {
        try
        {
            Permission permissionEntity = getCoralSession().getSecurity().
                getUniquePermission(permission);
            return subject.hasPermission(resource, permissionEntity);
        }
        catch(Exception e)
        {
            log.error("CmsTool",e);
            throw e;
        }
    }
    
    /**
     * Returns subjects that belong to any resource sharing workgroup in the current site as the
     * current subject.
     * 
     * @return set of subjects.
     */
    public Set<Subject> getSharingWorkgroupPeers()
    {
        try
        {
            SiteResource site = cmsDataFactory.getCmsData(context).getSite();
            return securityService.getSharingWorkgroupPeers(getCoralSession(), site, getSubject());
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a RoleResource corresponding to a given group within current site.
     * 
     * @param groupName name of the group. Note that special group '@team_member' can be used.
     * @return a RoleResource or <code>null</code> if the given group is not found.
     */
    public RoleResource getGroup(String groupName)
    {
        try
        {
            SiteResource site = cmsDataFactory.getCmsData(context).getSite();
            RoleResource[] groups = securityService.getGroups(getCoralSession(), site);
            for(RoleResource group : groups)
            {
                if(securityService.getShortGroupName(group).equals(groupName))
                {
                    return group;
                }
            }
            return null;
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a given subject is member of a given group within the current site.
     * 
     * @param subject the subject to check.
     * @param groupName name of the group. Note that special group '@team_member' can be used.
     * @return <code>true</code> if the subject is member of the group. If group with a given name
     *         does not exist in the current site, <code>false</code> is returned.
     */
    public boolean isGroupMember(Subject subject, String groupName)
    {
        RoleResource group = getGroup(groupName);
        if(group != null)
        {
            return subject.hasRole(group.getRole());
        }
        return false;
    }

    // application data access ///////////////////////////////////////////////

    /**
     * Returns the aplication data root.
     *
     * @return the application resource. 
     */
    public Resource getApplication(String application)
        throws Exception
    {
        try
        {
            SiteResource site = cmsDataFactory.getCmsData(context).getSite();
            return getApplication(site,application);
        }
        catch(Exception e)
        {
            log.error("CmsTool exception ",e);
            throw e;
        }
    }

    /**
     * Returns the application data root.
     *
     * @param site the site resource.
     * @return the application resource. 
     */
    public Resource getApplication(SiteResource site, String application)
        throws Exception
    {
        Resource[] match = getCoralSession().getStore().
            getResourceByPath(site.getPath()+"/applications/"+application);
        if(match.length != 1)
        {
            throw new SiteException("application "+application+" not found");
        }
        return match[0];
    }

	public Resource getHomePage()
		throws Exception
	{
		return cmsDataFactory.getCmsData(context).getHomePage();
	}
	
    // preferences ///////////////////////////////////////////////////////////
    
    public NavigationNodeResource getNodePreferenceOrigin(NavigationNodeResource node, 
                                                          String preference)
    {
        return preferencesService.getNodePreferenceOrigin(node, preference);
    }
    
    public Parameters getCombinedNodePreferences(NavigationNodeResource node) 
    {
        return preferencesService.getCombinedNodePreferences(getCoralSession(), node);
    }

    // resource lookup (?) ///////////////////////////////////////////////////

    public NavigationNodeResource getNavigationNodeResource(long id)
        throws EntityDoesNotExistException
    {
        return NavigationNodeResourceImpl.
            getNavigationNodeResource(getCoralSession(),id);
    }
    
    public NavigationNodeResource getNavigationNodeResource(String id)
        throws EntityDoesNotExistException
    {
        return NavigationNodeResourceImpl.
            getNavigationNodeResource(getCoralSession(),(new Long(id)).longValue());
    }

    // integration ///////////////////////////////////////////////////////////

    public ResourceClassResource getClassDefinition(long id)
        throws EntityDoesNotExistException
    {
        return getClassDefinition(getCoralSession().getSchema().getResourceClass(id));
    }
    
    public ResourceClassResource getClassDefinition(Resource res)
    {
        return integrationService.getResourceClass(getCoralSession(),res.getResourceClass());
    }
    
    public ResourceClassResource getClassDefinition(ResourceClass rc)
    {
        return integrationService.getResourceClass(getCoralSession(),rc);
    }

    public static SiteResource getSite(Resource res)
    {
        Resource save = res;
        while(res != null && !(res instanceof SiteResource))
        {
            res = res.getParent();
        }
        return (SiteResource)res;
    }
    
    public String getSitePath(Resource res)
    throws Exception
    {
        ResourceClassResource rcr = getClassDefinition(res);
        String[] targetPaths = rcr.getAggregationTargetPathsList();
        
        String sitePath = getSite(res).getPath();
        String resPath = res.getPath();
        
        for(int i=0; i<targetPaths.length; i++)
        {
            String targetPath = targetPaths[i];
            if(targetPath.charAt(0) != '/')
            {
                String startPath = sitePath+'/'+targetPath;
                if(resPath.startsWith(startPath))
                {
                    return resPath.substring(startPath.length());
                }
            }
        }
        return null;
    }
    
    public String getSiteNamePath(Resource res)
    throws Exception
    {   
        String siteName = getSite(res).getName();
        String resPath = "";

        while(res instanceof NavigationNodeResource)
        {   if(res.getParent() instanceof NavigationNodeResource)
            {
                resPath = "/" + ((DocumentNodeResource)res).getTitle() + resPath;
            }
            res = res.getParent();
        }
        if(resPath.length() == 0)
        {
            resPath = "/";
        }
        resPath = siteName + resPath;
        return resPath;
    }
    
    /**
     * Returns unified node title followed by rules: all special chars except space are removed.
     * space chars are converted to _. all polish chars are converted to UTF-8
     */
    public String getDocumentUnifiedTitle(DocumentNodeResource res)
    {
        String unifiedTitle = res.getTitle().replaceAll(" ", "_").replaceAll("ą", "a").replaceAll(
            "Ą", "A").replaceAll("ę", "e").replaceAll("Ę", "E").replaceAll("ć", "c").replaceAll(
            "Ć", "C").replaceAll("ś", "s").replaceAll("Ś", "S").replaceAll("ń", "n").replaceAll(
            "Ń", "N").replaceAll("ó", "o").replaceAll("Ó", "O").replaceAll("ł", "l").replaceAll(
            "Ł", "L").replaceAll("[źż]", "z").replaceAll("[ŹŻ]", "Z").replaceAll("[^a-zA-Z0-9_]", "");
        return unifiedTitle;
    }

    // attribute access (introspection) //////////////////////////////////////

    public Object resourceAttribute(Resource resource, String attributeName)
    {
        if(attributeName == null)
        {
            return null;
        }
        AttributeDefinition attribute = resource.getResourceClass().
            getAttribute(attributeName);
        return resource.get(attribute);
    }

    public Object resourceAttribute(Resource resource, String attributeName, Object defaultValue)
    {
        AttributeDefinition attribute = resource.getResourceClass().
            getAttribute(attributeName);
        if(resource.isDefined(attribute))
        {
            return resource.get(attribute);
        }
        else
        {
            return defaultValue;
        }
    }
    
    // current navigation node access //////////////////////////////////////////////////////////////

    // current site adminstrative access ///////////////////////////////////////////////////////////
    
    /**
     * Checks if the current user has administrative privileges on the current site.
     */
    public boolean checkAdministrator()
        throws ProcessingException
    {
        SiteResource site = cmsDataFactory.getCmsData(context).getSite();
        if(site != null)
        {
            if(getSubject().hasRole(site.getAdministrator()))
            {
                return true;
            }
        }
        CoralSession coralSession = getCoralSession();
        Role cmsAdministrator = coralSession.getSecurity().
            getUniqueRole("cms.administrator");
        return getSubject().hasRole(cmsAdministrator);
    }

    public boolean checkCmsAdministrator()
        throws ProcessingException
    {
        CoralSession coralSession = getCoralSession();
        Role cmsAdministrator = coralSession.getSecurity().
            getUniqueRole("cms.administrator");
        return getSubject().hasRole(cmsAdministrator);
    }
    
    /**
     * Checks if a resource is an instance of a given class
     *
     * @param resource the resource.
     * @param className the name of resource class. 
     */
    public boolean isInstance(Resource resource, String className)
        throws Exception
    {
        ResourceClass rc = getCoralSession().getSchema().getResourceClass(className);
        if(resource.getResourceClass().equals(rc))
        {
            return true;
        }
        ResourceClass[] ancestors = resource.getResourceClass().getParentClasses();
        for (int i = 0; i < ancestors.length; i++)
        {
            if(ancestors[i].equals(rc))
            {
                return true;
            }            
        }
        return false;
    }
    
    public Resource getResource(long id)
    	throws Exception
    {
		return getCoralSession().getStore().getResource(id);    	
    }
    
    
    public CoralSession getCoralSession()
    {   
        return (CoralSession)context.getAttribute(CoralSession.class);
    }
    
    public Object getTemplatingContext()
    {
        return context.getAttribute(TemplatingContext.class); 
    }
    
    public Context getContext()
    {
        return context; 
    }
    
    public List<Resource> getRelatedResources(NavigationNodeResource resource)
    {
        ResourceList<Resource> sequence = null;
        if(resource instanceof DocumentNodeResource)
        {
            sequence = ((DocumentNodeResource)resource).getRelatedResourcesSequence();
        }
        NavigationNodeResource relatedSource;
        if(resource instanceof DocumentAliasResource)
        {
            relatedSource = ((DocumentAliasResource)resource).getOriginalDocument();
        }
        else
        {
            relatedSource = resource;
        }
        I18nContext i18nContext = context.getAttribute(I18nContext.class);
        Resource[] relatedTo = relatedService.getRelatedTo(getCoralSession(), relatedSource, sequence, 
            new IndexTitleComparator(context, integrationService,i18nContext.getLocale()));
        return Arrays.asList(relatedTo);
    }
    
    public List<Resource> categoryQuery(String query)
        throws MalformedRelationQueryException, EntityDoesNotExistException
    {
        CoralSession coralSession = getCoralSession();
        return Arrays.asList(coralSession.getRelationQuery().query(query.toString(),
            categoryQueryService.getCategoryResolver()));
    }

    public List<Resource> categoryQueryById(String[] categoryIds)
        throws MalformedRelationQueryException, EntityDoesNotExistException
    {
        CoralSession coralSession = getCoralSession();
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

    public boolean isAppEnabled(String appName) 
        throws ProcessingException
    {
        return cmsDataFactory.getCmsData(context).isApplicationEnabled(appName);
    }
}

