package net.cyklotron.cms.security.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.SchemaPermissionResource;
import net.cyklotron.cms.integration.SchemaRoleResource;
import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.RoleResourceImpl;
import net.cyklotron.cms.security.SubtreeRoleResourceImpl;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.labeo.services.BaseService;
import net.labeo.services.InitializationError;
import net.labeo.services.resource.AttributeDefinition;
import net.labeo.services.resource.BackendException;
import net.labeo.services.resource.CircularDependencyException;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.PermissionAssignment;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.RoleAssignment;
import net.labeo.services.resource.RoleImplication;
import net.labeo.services.resource.SecurityException;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.UnknownAttributeException;
import net.labeo.services.resource.ValueRequiredException;

/**
 * An implementation of CMS SecurityService.
 *
 * @author <a href="mailto:rkrzewsk@ngo.pl">Rafal Krzewski</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@ngo.pl">Pawe� Potempski</a>
 * @version $Id: SecurityServiceImpl.java,v 1.1 2005-01-12 20:45:09 pablo Exp $
 */
public class SecurityServiceImpl extends BaseService implements net.cyklotron.cms.security.SecurityService
{
    // instance variables ////////////////////////////////////////////////////

    /** The resource service. */
    private ResourceService resourceService;

    /** The integration service */
    private IntegrationService integrationService;

	/** The site service */
	private SiteService siteService;

    /** The root subject. */
    private Subject rootSubject;

    private boolean allowAddUser;
    
    // initialization ////////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public void init()
    {
        resourceService = (ResourceService)getBroker().getService(ResourceService.SERVICE_NAME);
        integrationService = (IntegrationService)getBroker().getService(IntegrationService.SERVICE_NAME);
		//siteService = (SiteService)getBroker().getService(SiteService.SERVICE_NAME);
        try
        {
            rootSubject = resourceService.getSecurity().getSubject(Subject.ROOT);
        }
        catch (EntityDoesNotExistException e)
        {
            throw new InitializationError("Couldn't find root subject");
        }
        allowAddUser = config.get("allow_add_user").asBoolean(false);
    }

    // SecurityService interface /////////////////////////////////////////////

    public boolean getAllowAddUser()
    {
        return allowAddUser;
    }
    
    /**
     * Returns the security information parent node, useful for showing role
     * tree using the Table toolkit.
     *
     * @param site the site in question.
     */
    public Resource getRoleInformationRoot(SiteResource site)
    {
        Resource res[] = resourceService.getStore().getResource(site, "security");
        if (res.length != 1)
        {
            throw new IllegalStateException("failed to lookuop security node in site #" + site.getIdString());
        }
        return res[0];
    }

    /**
     * Returns the roles defined for a site.
     *
     * @param site the site in question.
     */
    public RoleResource[] getRoles(SiteResource site)
    {
        ArrayList roles = new ArrayList();
        ArrayList stack = new ArrayList();
        stack.add(getRoleInformationRoot(site));
        while (stack.size() > 0)
        {
            Resource r = (Resource)stack.remove(stack.size() - 1);
            Resource[] children = resourceService.getStore().getResource(r);
            for (int i = 0; i < children.length; i++)
            {
                roles.add(children[i]);
                stack.add(children[i]);
            }
        }
        RoleResource[] result = new RoleResource[roles.size()];
        roles.toArray(result);
        return result;
    }

    /**
     * Returns a RoleResource object describing a specific role.
     *
     * @param site the site to search.
     * @param role the role in question.
     * @return the RoleResource object, or <code>null</code> if not found.
     */
    public RoleResource getRole(SiteResource site, Role role)
    {
        ArrayList stack = new ArrayList();
        stack.add(getRoleInformationRoot(site));
        while (stack.size() > 0)
        {
            Resource r = (Resource)stack.remove(stack.size() - 1);
            if (r instanceof RoleResource)
            {
                if (((RoleResource)r).getRole().equals(role))
                {
                    return (RoleResource)r;
                }
            }
            Resource[] children = resourceService.getStore().getResource(r);
            for (int i = 0; i < children.length; i++)
            {
                stack.add(children[i]);
            }
        }
        return null;
    }

    /**
     * Registers a role for a site.
     *
     * @param site the site the role belongs to.
     * @param role the role.
     * @param subtree the root of the subtree the role has rigths to (may be
     * <code>null</code>).
     * @param recursive if subtree is not null, are rights assigned to the
     *        role, on the specified node recursive or not.
     * @param deletable can this role be deleted and recreated from the UI
     * @param descriptionKey the i18n key of the role's description.
     * @param superRole the role's super role (may be <code>null</code>).
     * @param subject used to create role resources
     */
    public RoleResource registerRole(
        SiteResource site,
        Role role,
        Resource subtree,
        boolean recursive,
        boolean deletable,
        String descriptionKey,
        RoleResource superRole,
        Subject subject)
    {
        if (role == null)
        {
            throw new IllegalArgumentException("null role");
        }
        Resource root = getRoleInformationRoot(site);
        Resource parent = (superRole != null) ? (Resource)superRole : root;
        
        Resource[] resources = resourceService.getStore().getResource(parent,role.getName());
        if(resources.length > 0)
        {
            //already registered
            return (RoleResource)resources[0];
        }
        
        RoleResource roleRes = null;
        try
        {
            if (subtree == null)
            {
                roleRes = RoleResourceImpl.createRoleResource(resourceService, role.getName(), parent, role, deletable, subject);
            }
            else
            {
                roleRes =
                    SubtreeRoleResourceImpl.createSubtreeRoleResource(resourceService, role.getName(), parent, role, deletable, subtree, recursive, subject);
            }
            /*
            CrossReference refs = root.getRelations();
            refs.put(parent, roleRes);
            root.setRelations(refs);
            root.update(subject);
            */
        }
        catch (ValueRequiredException e)
        {
            // won't happen
        }
        if (descriptionKey != null)
        {
            roleRes.setDescriptionKey(descriptionKey);
            roleRes.update(subject);
        }
        return roleRes;
    }

    /**
     * Unregisters a role for a site.
     *
     * @param site the site.
     * @param role the role.
     */
    public void unregisterRole(SiteResource site, Role role, boolean ignoreDeletableFlag) throws EntityInUseException
    {
        RoleResource roleRes = getRole(site, role);
        if (!ignoreDeletableFlag && !roleRes.getDeletable())
        {
            throw new IllegalArgumentException("role " + role.getName() + " is not deletable");
        }
        try
        {
            Resource[] children = resourceService.getStore().getResource(roleRes);
            for (int i = 0; i < children.length; i++)
            {
                resourceService.getStore().setParent(children[i], roleRes.getParent());
            }
        }
        catch (CircularDependencyException e)
        {
            throw new BackendException("unexpected ARL exception", e);
        }
        resourceService.getStore().deleteResource(roleRes);
    }

    // here goes the changes...

    /**
     * Creates a Role upon following parameters:
     *  @param superRole    super role of all roles defined in roles schema
     *  @param roleName     selected role name - one which can be found in roles schema
     *  @param subtree      subtree of resources for which role is created
     *  @param subject      the creator of the role
     */
    public Role createRole(Role superRole, String roleName, Resource subtree, Subject subject) throws CmsSecurityException
    {
        Resource schemaRoleRoot = integrationService.getSchemaRoleRoot(subtree.getResourceClass());

        // get a schema role representing created role
        SchemaRoleResource schemaRoleResource = getSchemaRoleResource(schemaRoleRoot, roleName);
        if (schemaRoleResource == null)
        {
            throw new CmsSecurityException("Couldn't find role schema for resource class: " + subtree.getResourceClass().getName());
        }

        Role role = null;

        // check if role reference should be saved in subtree attribute
        AttributeDefinition roleAttributeDef = null;
        if (schemaRoleResource.getRoleAttributeName() != null && subtree.getResourceClass().hasAttribute(schemaRoleResource.getRoleAttributeName()))
        {
            roleAttributeDef = subtree.getResourceClass().getAttribute(schemaRoleResource.getRoleAttributeName());
            role = (Role) (subtree.get(roleAttributeDef));
        }

        if (role == null)
        {
            // get site
            SiteResource site = CmsTool.getSite(subtree);
            if (site == null)
            {
                throw new CmsSecurityException("Cannot find site resource for resource with id=" + subtree.getIdString());
            }

            //find super role from schema and stick current role under it
            SchemaRoleResource schemaSuperRole = (SchemaRoleResource)schemaRoleResource.getSuperRole();
            Role newSuperRole = null;
            while (schemaSuperRole != null && newSuperRole == null)
            {
                String realSuperRoleName = roleNameFromSufix(schemaSuperRole, subtree);
                Role[] temp = resourceService.getSecurity().getRole(realSuperRoleName);
                if (temp.length > 1)
                {
                    throw new CmsSecurityException("Cannot cope with multiple super roles");
                }
                else if (temp.length == 1)
                {
                    newSuperRole = temp[0];
                }
                schemaSuperRole = (SchemaRoleResource)schemaSuperRole.getSuperRole();
            }
            if (newSuperRole != null)
            {
                superRole = newSuperRole;
            }
            else
            {
                // here check if super role in resource view exist in
                // reference...
                /*
                if(schemaRoleResource.getSubtreeRole())
                {
                    Resource parent = subtree.getParent();
                    while(parent != null)
                    {
                    parent = parent.getParent();
                    }
                }
                */
            }

            // get super role resource
            RoleResource superRoleRes = getRole(site, superRole);

            // try to grab or create the role
            String realRoleName = roleNameFromSufix(schemaRoleResource, subtree);
            Role[] temp = resourceService.getSecurity().getRole(realRoleName);
            if (temp.length == 1)
            {
                role = temp[0];
            }
            else
            {
                role = resourceService.getSecurity().createRole(realRoleName);

                // create role resource
                // TODO: Add a possiblity to override recursive and deletable flags for a created role.
                RoleResource roleRes = null;
                // create a subtree role according to role schema
                if (schemaRoleResource.getSubtreeRole())
                {
                    roleRes =
                        registerRole(
                            site,
                            role,
                            subtree,
                            schemaRoleResource.getRecursive(),
                            schemaRoleResource.getDeletable(),
                            roleName,
                            superRoleRes,
                            subject);
                }
                else
                {
                    roleRes =
                        registerRole(site, role, null, schemaRoleResource.getRecursive(), schemaRoleResource.getDeletable(), roleName, superRoleRes, subject);
                }

                // grant permissions
                grantPermissions(schemaRoleResource, roleName, subtree, subject);

                // add "role" under "superRole" and move subroles under current role
                try
                {
                    resourceService.getSecurity().addSubRole(superRole, role);

                    Resource[] resources = resourceService.getStore().getResource(schemaRoleResource);
                    for (int i = 0; i < resources.length; i++)
                    {
                        if (resources[i] instanceof SchemaRoleResource)
                        {
                            SchemaRoleResource schemaSubRole = (SchemaRoleResource)resources[i];
                            String subRoleName = roleNameFromSufix(schemaSubRole, subtree);
                            Role[] subRole = resourceService.getSecurity().getRole(subRoleName);
                            for (int j = 0; j < subRole.length; j++)
                            {
                                Role currentSubRole = subRole[j];
                                if (currentSubRole != null)
                                {
                                    resourceService.getSecurity().deleteSubRole(superRole, currentSubRole);
                                    resourceService.getSecurity().addSubRole(role, currentSubRole);
                                    resourceService.getStore().setParent(getRole(site, currentSubRole), roleRes);
                                }
                            }
                        }
                    }
                }
                catch (CircularDependencyException e)
                {
                    throw new CmsSecurityException("Error when moving subroles", e);
                }
            }

            // save role reference in subtree attribute if needed
            if (roleAttributeDef != null)
            {
                try
                {
                    subtree.set(roleAttributeDef, role);
                    subtree.update(subject);
                }
                catch (Exception e)
                {
                    throw new CmsSecurityException("ARL error when assigning role to resource", e);
                }
            }
        }

        return role;
    }

    /**
     * Deletes a role assigned to a resource subtree, role is revoked with root subject rights.
     *
     * @param roleName selected role name - one which can be found in roles schema
     * @param subtree the resource to which a role is assigned.
     * @param subject subject resposible for role deletion (used to modify resources with role ref)
     */
    public void deleteRole(String roleName, Resource subtree, Subject subject, boolean ignoreDeletableFlag) throws CmsSecurityException
    {
        Resource schemaRoleRoot = integrationService.getSchemaRoleRoot(subtree.getResourceClass());
        // get a schema role representing created role
        SchemaRoleResource schemaRoleResource = getSchemaRoleResource(schemaRoleRoot, roleName);
        if (schemaRoleResource == null)
        {
            throw new CmsSecurityException("Couldn't find role '" + roleName + "' in schema for resource class: " + subtree.getResourceClass().getName());
        }

        // get the role
        Role role = null;
        Role[] temp = resourceService.getSecurity().getRole(roleNameFromSufix(schemaRoleResource, subtree));
        if (temp.length > 1)
        {
            throw new CmsSecurityException("Cannot delete roles, many roles with the same name");
        }
        else if (temp.length == 1)
        {
            role = temp[0];
        }
        else // == 0 - guard from null pointer exceptions when there is no role to delete
            {
            return;
        }

        // remove resource -> role reference if one exists
        if (schemaRoleResource.getRoleAttributeName() != null && subtree.getResourceClass().hasAttribute(schemaRoleResource.getRoleAttributeName()))
        {
            try
            {
                AttributeDefinition roleAttributeDef = subtree.getResourceClass().getAttribute(schemaRoleResource.getRoleAttributeName());
                subtree.unset(roleAttributeDef);
                subtree.update(subject);
            }
            catch (Exception e)
            {
                throw new CmsSecurityException("ARL error when removing role from resource", e);
            }
        }

        // get site
        SiteResource site = CmsTool.getSite(subtree);
        if (site == null)
        {
            throw new CmsSecurityException("Cannot find site resource for resource with id=" + subtree.getIdString());
        }

        // delete the role from RoleResource tree and Role graph
        try
        {
            unregisterRole(site, role, ignoreDeletableFlag);
            RoleAssignment[] assignments = role.getRoleAssignments();
            for (int i = 0; i < assignments.length; i++)
            {
                resourceService.getSecurity().revoke(role, assignments[i].getSubject(), rootSubject);
            }
            Role superRole = null;
            ArrayList subRoles = new ArrayList();
            RoleImplication[] implications = role.getImplications();
            for (int i = 0; i < implications.length; i++)
            {
                if (implications[i].getSubRole().equals(role))
                {
                    superRole = implications[i].getSuperRole();
                    resourceService.getSecurity().deleteSubRole(superRole, role);
                }
                else
                {
                    subRoles.add(implications[i].getSubRole());
                    resourceService.getSecurity().deleteSubRole(role, implications[i].getSubRole());
                }
            }
            for (int i = 0; i < subRoles.size(); i++)
            {
                Role subRole = (Role)subRoles.get(i);
                resourceService.getSecurity().addSubRole(superRole, subRole);
            }
            PermissionAssignment[] permissions = role.getPermissionAssignments();
            for (int i = 0; i < permissions.length; i++)
            {
                resourceService.getSecurity().revoke(permissions[i].getResource(), role, permissions[i].getPermission(), rootSubject);
            }
            resourceService.getSecurity().deleteRole(role);
        }
        catch (Exception e)
        {
            throw new CmsSecurityException("failed to delete role", e);
        }
    }

    /**
     * Removes any roles created using the schema from the resource, or a
     * resource tree.
     * 
     * @param resource the resource, or tree root.
     * @param recursive <code>true<?code> to cleanup whole tree.
     * @param subject the subject that performs the opertation.
     */
    public void cleanupRoles(Resource resource, boolean recursive, Subject subject) throws CmsSecurityException
    {
		if (recursive)
        {
			System.out.println("Probuje oczyscic zasob ktory jest rekursywny: "+resource.getPath());
            List stack = new ArrayList();
            stack.add(resource);
            while (!stack.isEmpty())
            {
				resource = (Resource)stack.remove(stack.size() - 1);
				System.out.println("Zdejmuje ze stosu zasob "+resource.getPath());
                cleanupRoles(resource, subject);
                Resource[] children = resourceService.getStore().getResource(resource);
                for (int i = 0; i < children.length; i++)
                {
					System.out.println("Wkladam na stos "+children[i].getPath());
                    stack.add(children[i]);
                }
            }
        }
        else
        {
            cleanupRoles(resource, subject);
        }
    }

    private void cleanupRoles(Resource resource, Subject subject) throws CmsSecurityException
    {
		System.out.println("Probuje oczyscic zasob: "+resource.getPath());
        try
        {
			
			// revoke all granted on resource
            PermissionAssignment[] pa = resource.getPermissionAssignments();
            for (int i = 0; i < pa.length; i++)
            {
                resourceService.getSecurity().revoke(resource, pa[i].getRole(), pa[i].getPermission(), subject);
            }
			System.out.println("Zdjalem wszystkie prawa z zasobu");
            Resource schemaRoleRoot = integrationService.getSchemaRoleRoot(resource.getResourceClass());
			System.out.println("Pobralem scheme dla zasobu");
            //prepare stack
            Stack stack = new Stack();
            prepareStack(stack, schemaRoleRoot);
			System.out.println("Przygotowalem stos definicji rol");
            
            
            Resource securityRoot = getRoleInformationRoot(getSite(resource));

            //put roles by name    
            Map map = new HashMap();
            prepareRolesMap(map, securityRoot);
			System.out.println("Przygotowalem mape rol");
            while (!stack.isEmpty())
            {
                SchemaRoleResource roleDef = (SchemaRoleResource)stack.pop();
                String roleName = roleNameFromSufix(roleDef, resource);
                if (map.containsKey(roleName))
                {
					System.out.println("No to mam role: " + roleName);
                    RoleResource roleResource = (RoleResource)map.get(roleName);
					System.out.println("No to mam role2");
                    // unbind role from resource
                    try
                    {
						AttributeDefinition roleAttributeDef = resource.getResourceClass().getAttribute(roleDef.getRoleAttributeName());
						System.out.println("Pobralem def atrybutu");
                        resource.unset(roleAttributeDef);
						System.out.println("Kasuje atrybut");
                        resource.update(subject);
						System.out.println("Aktualizuje baze");
                    }
                    catch (UnknownAttributeException e)
                    {
						System.out.println("EXCEPTION"+e);    // ignore it.
                    }
                    catch (ValueRequiredException e)
                    {
                        // ignore it.
						System.out.println("EXCEPTION2"+e);
                    }
					Role role = resourceService.getSecurity().getUniqueRole(roleName);
					RoleAssignment[] ra = role.getRoleAssignments();
					for(int i = 0; i < ra.length; i++)
					{
						resourceService.getSecurity().revoke(ra[i].getRole(), ra[i].getSubject(), subject);
					}
					System.out.println("Usunalem granty");
					RoleImplication[] ri = role.getImplications();
					for(int i = 0; i < ri.length; i++)
					{
						resourceService.getSecurity().deleteSubRole(ri[i].getSuperRole(), ri[i].getSubRole());
					}
					System.out.println("Usunalem zaleznosci rol");
					System.out.println("A teraz pragne usunac zasob nr. "+roleResource.getIdString()+" : "+roleResource.getPath());
					resourceService.getStore().deleteResource(roleResource);
					System.out.println("No i udalo sie");
					
					//resourceService.getSecurity().deleteRole(role);
               }
                else
                {
                    System.out.println("Nie mam roli: " + roleName);
                }
				System.out.println("Pa pa resource\n");
            }

            /*
            AttributeDefinition attrs[] = resource.getResourceClass().getAllAttributes();
            for (int i = 0; i < attrs.length; i++)
            {
                AttributeDefinition attr = attrs[i];
                if(Role.class.isAssignableFrom(attr.getAttributeClass().getJavaClass()))
                {
                    Role role = (Role)resource.get(attr);
                    if(role != null)
                    {
                        String roleName = role.getName();
                        roleName = roleName.substring(0, roleName.lastIndexOf('.'));
                        SchemaRoleResource schemaRoleResource = getSchemaRoleResource(schemaRoleRoot, roleName);
                        if(schemaRoleResource != null)
                        {
                            deleteRole(roleName, resource, subject, true);
                        }
                    }
                }
            }
            */
        }
        catch (Exception e)
        {
            throw new CmsSecurityException("Exception occured: ", e);
        }
		System.out.println("Skonczylem czyscic zasob: "+resource.getPath());
    }

    private void prepareStack(Stack stack, Resource resource)
    {
        Resource[] resources = resourceService.getStore().getResource(resource);
        for (int i = 0; i < resources.length; i++)
        {
            if (resources[i] instanceof SchemaRoleResource)
            {
                stack.push(resources[i]);
                prepareStack(stack, resources[i]);
            }
        }
    }

    private void prepareRolesMap(Map map, Resource resource)
    {
        Resource[] resources = resourceService.getStore().getResource(resource);
        for (int i = 0; i < resources.length; i++)
        {
            if (resources[i] instanceof RoleResource)
            {
                map.put(resources[i].getName(), resources[i]);
                prepareRolesMap(map, resources[i]);
            }
        }
    }

    private SiteResource getSite(Resource resource) throws CmsSecurityException
    {
        Resource temp = resource;
        while (temp != null && !(temp instanceof SiteResource))
        {
            temp = temp.getParent();
        }
        if (temp == null)
        {
            throw new CmsSecurityException("Resource does not belong to any site");
        }
        return (SiteResource)temp;
    }

    /**
     * Get the role name for specified schemar role and resource.
     *
     * @param schemaRole the schema role.
     * @param resource the resource.
     * @return role name.
     */
    public String roleNameFromSufix(SchemaRoleResource schemaRole, Resource resource)
    {
        String roleName = schemaRole.getName();
        if (schemaRole.getSuffixAttributeName() != null && schemaRole.getSuffixAttributeName().length() > 0)
        {
            AttributeDefinition roleSuffixAttributeDef = resource.getResourceClass().getAttribute(schemaRole.getSuffixAttributeName());
            roleName = roleName + '.' + resource.get(roleSuffixAttributeDef);
        }
        return roleName;
    }

    /**
     * Get the role for specified resource and base role name.
     *
     * @param roleName the base name of the role.
     * @param resource the resource.
     * @return the role.
     */
    public Role getRole(String roleName, Resource resource) throws CmsSecurityException
    {
        Resource schemaRoot = integrationService.getSchemaRoleRoot(resource.getResourceClass());
        SchemaRoleResource schema = getSchemaRoleResource(schemaRoot, roleName);
        if (schema == null)
        {
            throw new CmsSecurityException(
                "Schema resource for role '" + roleName + "' does not exists in schema defined for class '" + resource.getResourceClass().getName() + "'");
        }
        String fullRoleName = roleNameFromSufix(schema, resource);
        Role role = resourceService.getSecurity().getUniqueRole(fullRoleName);
        return role;
    }

    /**
     * Grants permissions to a given role on a given subtree according to a given role schema.
     *
     * @param schemaRoles roles schema - a tree of roles with assigned permissions
     * @param roleName selected role name - one which allows finding an original role name
     *                                         in roles schema
     * @param subtree the resource to which a role is assigned, and from which gets its name suffix.
     * @param grantor subject responsible for permission granting
     */
    public void grantPermissions(SchemaRoleResource schemaRoleResource, String roleName, Resource subtree, Subject grantor) throws CmsSecurityException
    {
        // get the role
        Role role = null;
        String realRoleName = roleNameFromSufix(schemaRoleResource, subtree);
        Role[] temp = resourceService.getSecurity().getRole(realRoleName);
        if (temp.length == 1)
        {
            role = temp[0];
        }
        else
        {
            throw new CmsSecurityException("Cannot grant permission (many roles or no role with the name '" + realRoleName + "')");
        }

        try
        {
            grantPermisions(schemaRoleResource, role, subtree, grantor, true);
        }
        catch (SecurityException e)
        {
            throw new CmsSecurityException("Can't grant permissions", e);
        }
    }

    private void grantPermisions(SchemaRoleResource schemaRoleResource, Role role, Resource subtree, Subject grantor, boolean recursive)
        throws SecurityException
    {
        Resource[] resources = resourceService.getStore().getResource(schemaRoleResource);
        for (int i = 0; i < resources.length; i++)
        {
            if (resources[i] instanceof SchemaPermissionResource)
            {
                SchemaPermissionResource schemaPermissionResource = (SchemaPermissionResource)resources[i];
                Permission permission = resourceService.getSecurity().getUniquePermission(schemaPermissionResource.getName());
                if (!role.hasPermission(subtree, permission))
                {
                    resourceService.getSecurity().grant(subtree, role, permission, schemaPermissionResource.getRecursive(), rootSubject);
                }
            }
            if (recursive && resources[i] instanceof SchemaRoleResource)
            {
                grantPermisions((SchemaRoleResource)resources[i], role, subtree, rootSubject, true);
            }
        }
    }

    // useful private methods
    private SchemaRoleResource getSchemaRoleResource(Resource schemaRolesRoot, String roleName)
    {
        Resource[] resources = resourceService.getStore().getResource(schemaRolesRoot);
        for (int i = 0; i < resources.length; i++)
        {
            if (resources[i] instanceof SchemaRoleResource)
            {
                SchemaRoleResource result = getSchemaRoleResource((SchemaRoleResource)resources[i], roleName);
                if (result != null)
                {
                    return result;
                }
            }
        }
        return null;
    }

    private SchemaRoleResource getSchemaRoleResource(SchemaRoleResource schemaRole, String roleName)
    {
        // Deep first search
        if (schemaRole.getName().equals(roleName))
        {
            return schemaRole;
        }
        Resource[] resources = resourceService.getStore().getResource(schemaRole);
        for (int i = 0; i < resources.length; i++)
        {
            if (resources[i] instanceof SchemaRoleResource)
            {
                SchemaRoleResource result = getSchemaRoleResource((SchemaRoleResource)resources[i], roleName);
                if (result != null)
                {
                    return result;
                }
            }
        }
        return null;
    }
}
