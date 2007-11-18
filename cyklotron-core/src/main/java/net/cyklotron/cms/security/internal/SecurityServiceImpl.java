package net.cyklotron.cms.security.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.AmbigousEntityNameException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CircularDependencyException;
import org.objectledge.coral.schema.UnknownAttributeException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.PermissionAssignment;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.RoleAssignment;
import org.objectledge.coral.security.RoleImplication;
import org.objectledge.coral.security.SecurityException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.SchemaPermissionResource;
import net.cyklotron.cms.integration.SchemaRoleResource;
import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.RoleResourceImpl;
import net.cyklotron.cms.security.SubtreeRoleResource;
import net.cyklotron.cms.security.SubtreeRoleResourceImpl;
import net.cyklotron.cms.site.SiteResource;


/**
 * An implementation of CMS SecurityService.
 *
 * @author <a href="mailto:rkrzewsk@ngo.pl">Rafal Krzewski</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@ngo.pl">Pawe� Potempski</a>
 * @version $Id: SecurityServiceImpl.java,v 1.11 2007-11-18 21:23:17 rafal Exp $
 */
public class SecurityServiceImpl
    implements net.cyklotron.cms.security.SecurityService
{
    // instance variables ////////////////////////////////////////////////////

    private Logger logger;
    
    /** The integration service */
    private IntegrationService integrationService;

    private boolean allowAddUser;
    
    // initialization ////////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public SecurityServiceImpl(Logger logger, Configuration config, IntegrationService integrationService)
    {
        this.logger = logger;
        this.integrationService = integrationService;
        allowAddUser = config.getChild("allow_add_user").getValueAsBoolean(false);
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
    public Resource getRoleInformationRoot(CoralSession coralSession, SiteResource site)
    {
        Resource parentNode = site;
        if(parentNode == null)
        {
            try
            {
                parentNode = coralSession.getStore().getUniqueResourceByPath("/cms");
            }
            catch(AmbigousEntityNameException e)
            {
                throw new IllegalStateException("ambigous cms main node");
            }
            catch(EntityDoesNotExistException e)
            {
                throw new IllegalStateException("failed to lookuop cms main node");
            }
        }
        Resource res[] = coralSession.getStore().getResource(parentNode, "security");
        if (res.length != 1)
        {
            throw new IllegalStateException("failed to lookuop security node" +
                    " under '"+parentNode.getPath()+ "' resource");
        }
        return res[0];
    }

    /**
     * Returns the roles defined for a site.
     *
     * @param site the site in question.
     */
    public RoleResource[] getRoles(CoralSession coralSession,SiteResource site)
    {
        ArrayList roles = new ArrayList();
        ArrayList stack = new ArrayList();
        stack.add(getRoleInformationRoot(coralSession, site));
        while (stack.size() > 0)
        {
            Resource r = (Resource)stack.remove(stack.size() - 1);
            Resource[] children = coralSession.getStore().getResource(r);
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
    public RoleResource getRole(CoralSession coralSession,SiteResource site, Role role)
    {
        ArrayList stack = new ArrayList();
        stack.add(getRoleInformationRoot(coralSession, site));
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
            Resource[] children = coralSession.getStore().getResource(r);
            for (int i = 0; i < children.length; i++)
            {
                stack.add(children[i]);
            }
        }
        return null;
    }

    /**
     * Registers a role for a site.
     * @param site the site the role belongs to.
     * @param role the role.
     * @param subtree the root of the subtree the role has rigths to (may be
     * <code>null</code>).
     * @param recursive if subtree is not null, are rights assigned to the
     *        role, on the specified node recursive or not.
     * @param deletable can this role be deleted and recreated from the UI
     * @param descriptionKey the i18n key of the role's description.
     * @param superRole the role's super role (may be <code>null</code>).
     */
    public RoleResource registerRole(
        CoralSession coralSession,
        SiteResource site,
        Role role,
        Resource subtree,
        boolean recursive,
        boolean deletable,
        String descriptionKey,
        RoleResource superRole)
    {
        if (role == null)
        {
            throw new IllegalArgumentException("null role");
        }
        Resource root = getRoleInformationRoot(coralSession, site);
        Resource parent = (superRole != null) ? (Resource)superRole : root;
        
        Resource[] resources = coralSession.getStore().getResource(parent,role.getName());
        if(resources.length > 0)
        {
            //already registered
            return (RoleResource)resources[0];
        }
        
        RoleResource roleRes = null;
        try
        {
            try
            {
                if(subtree == null)
                {
                    roleRes = RoleResourceImpl.createRoleResource(coralSession, role.getName(),
                        parent, role, deletable);
                }
                else
                {
                    roleRes = SubtreeRoleResourceImpl.createSubtreeRoleResource(coralSession, role
                        .getName(), parent, role, deletable, subtree, recursive);
                }
            }
            catch(InvalidResourceNameException e)
            {
                throw new RuntimeException("role " + role.toString()
                    + "has illegal characters in it's name", e);
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
            roleRes.update();
        }
        return roleRes;
    }

    /**
     * Unregisters a role for a site.
     *
     * @param site the site.
     * @param role the role.
     */
    public void unregisterRole(CoralSession coralSession, SiteResource site, Role role, boolean ignoreDeletableFlag) throws EntityInUseException
    {
        RoleResource roleRes = getRole(coralSession, site, role);
        if (!ignoreDeletableFlag && !roleRes.getDeletable())
        {
            throw new IllegalArgumentException("role " + role.getName() + " is not deletable");
        }
        try
        {
            Resource[] children = coralSession.getStore().getResource(roleRes);
            for (int i = 0; i < children.length; i++)
            {
                coralSession.getStore().setParent(children[i], roleRes.getParent());
            }
        }
        catch (CircularDependencyException e)
        {
            throw new BackendException("unexpected ARL exception", e);
        }
        coralSession.getStore().deleteResource(roleRes);
    }

    // here goes the changes...

    /**
     * Creates a Role upon following parameters:
     * @param superRole    super role of all roles defined in roles schema
     * @param roleName     selected role name - one which can be found in roles schema
     * @param subtree      subtree of resources for which role is created
     */
    public Role createRole(CoralSession coralSession, Role superRole, String roleName, Resource subtree) throws CmsSecurityException
    {
        Resource schemaRoleRoot = integrationService.getSchemaRoleRoot(coralSession, subtree.getResourceClass());

        // get a schema role representing created role
        SchemaRoleResource schemaRoleResource = getSchemaRoleResource(coralSession, schemaRoleRoot, roleName);
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
            
            //find super role from schema and stick current role under it
            SchemaRoleResource schemaSuperRole = (SchemaRoleResource)schemaRoleResource.getSuperRole();
            Role newSuperRole = null;
            while (schemaSuperRole != null && newSuperRole == null)
            {
                String realSuperRoleName = roleNameFromSufix(schemaSuperRole, subtree);
                Role[] temp = coralSession.getSecurity().getRole(realSuperRoleName);
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

            // get super role resource -> null if has no super role
            RoleResource superRoleRes = getRole(coralSession, site, superRole);

            // try to grab or create the role
            String realRoleName = roleNameFromSufix(schemaRoleResource, subtree);
            Role[] temp = coralSession.getSecurity().getRole(realRoleName);
            if (temp.length == 1)
            {
                role = temp[0];
            }
            else
            {
                role = coralSession.getSecurity().createRole(realRoleName);

                // create role resource
                RoleResource roleRes = null;
                // create a subtree role according to role schema
                if (schemaRoleResource.getSubtreeRole())
                {
                    roleRes =
                        registerRole(
                            coralSession,
                            site,
                            role,
                            subtree,
                            schemaRoleResource.getRecursive(),
                            schemaRoleResource.getDeletable(),
                            roleName,
                            superRoleRes);
                }
                else
                {
                    roleRes =
                        registerRole(coralSession, site, role, null, schemaRoleResource.getRecursive(), schemaRoleResource.getDeletable(), roleName, superRoleRes);
                }

                // grant permissions
                grantPermissions(coralSession, schemaRoleResource, roleName, subtree);

                // add "role" under "superRole" and move subroles under current role
                try
                {
                    coralSession.getSecurity().addSubRole(superRole, role);

                    Resource[] resources = coralSession.getStore().getResource(schemaRoleResource);
                    for (int i = 0; i < resources.length; i++)
                    {
                        if (resources[i] instanceof SchemaRoleResource)
                        {
                            SchemaRoleResource schemaSubRole = (SchemaRoleResource)resources[i];
                            String subRoleName = roleNameFromSufix(schemaSubRole, subtree);
                            Role[] subRole = coralSession.getSecurity().getRole(subRoleName);
                            for (int j = 0; j < subRole.length; j++)
                            {
                                Role currentSubRole = subRole[j];
                                if (currentSubRole != null)
                                {
                                    coralSession.getSecurity().deleteSubRole(superRole, currentSubRole);
                                    coralSession.getSecurity().addSubRole(role, currentSubRole);
                                    coralSession.getStore().setParent(getRole(coralSession, site, currentSubRole), roleRes);
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
                    subtree.update();
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
     * @param roleName selected role name - one which can be found in roles schema
     * @param subtree the resource to which a role is assigned.
     */
    public void deleteRole(CoralSession coralSession, String roleName, Resource subtree, boolean ignoreDeletableFlag) throws CmsSecurityException
    {
        Resource schemaRoleRoot = integrationService.getSchemaRoleRoot(coralSession, subtree.getResourceClass());
        // get a schema role representing created role
        SchemaRoleResource schemaRoleResource = getSchemaRoleResource(coralSession, schemaRoleRoot, roleName);
        if (schemaRoleResource == null)
        {
            throw new CmsSecurityException("Couldn't find role '" + roleName + "' in schema for resource class: " + subtree.getResourceClass().getName());
        }

        // get the role
        Role role = null;
        Role[] temp = coralSession.getSecurity().getRole(roleNameFromSufix(schemaRoleResource, subtree));
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
                subtree.update();
            }
            catch (Exception e)
            {
                throw new CmsSecurityException("ARL error when removing role from resource", e);
            }
        }

        // get site
        
        SiteResource site = CmsTool.getSite(subtree);

        // delete the role from RoleResource tree and Role graph
        try
        {
            unregisterRole(coralSession, site, role, ignoreDeletableFlag);
            RoleAssignment[] assignments = role.getRoleAssignments();
            for (int i = 0; i < assignments.length; i++)
            {
                coralSession.getSecurity().revoke(role, assignments[i].getSubject());
            }
            Role superRole = null;
            ArrayList subRoles = new ArrayList();
            RoleImplication[] implications = role.getImplications();
            for (int i = 0; i < implications.length; i++)
            {
                if (implications[i].getSubRole().equals(role))
                {
                    superRole = implications[i].getSuperRole();
                    coralSession.getSecurity().deleteSubRole(superRole, role);
                }
                else
                {
                    subRoles.add(implications[i].getSubRole());
                    coralSession.getSecurity().deleteSubRole(role, implications[i].getSubRole());
                }
            }
            for (int i = 0; i < subRoles.size(); i++)
            {
                Role subRole = (Role)subRoles.get(i);
                coralSession.getSecurity().addSubRole(superRole, subRole);
            }
            PermissionAssignment[] permissions = role.getPermissionAssignments();
            for (int i = 0; i < permissions.length; i++)
            {
                coralSession.getSecurity().revoke(permissions[i].getResource(), role, permissions[i].getPermission());
            }
            coralSession.getSecurity().deleteRole(role);
        }
        catch (Exception e)
        {
            throw new CmsSecurityException("failed to delete role", e);
        }
    }

    /**
     * Removes any roles created using the schema from the resource, or a
     * resource tree.
     * @param resource the resource, or tree root.
     * @param recursive <code>true<?code> to cleanup whole tree.
     */
    public void cleanupRoles(CoralSession coralSession, Resource resource, boolean recursive) throws CmsSecurityException
    {
		if (recursive)
        {
			logger.debug("Probuje oczyscic zasob ktory jest rekursywny: "+resource.getPath());
            List stack = new ArrayList();
            stack.add(resource);
            while (!stack.isEmpty())
            {
				resource = (Resource)stack.remove(stack.size() - 1);
				logger.debug("Zdejmuje ze stosu zasob "+resource.getPath());
                cleanupRoles(coralSession, resource);
                Resource[] children = coralSession.getStore().getResource(resource);
                for (int i = 0; i < children.length; i++)
                {
					logger.debug("Wkladam na stos "+children[i].getPath());
                    stack.add(children[i]);
                }
            }
        }
        else
        {
            cleanupRoles(coralSession, resource);
        }
    }

    private void cleanupRoles(CoralSession coralSession, Resource resource) throws CmsSecurityException
    {
		logger.debug("Probuje oczyscic zasob: "+resource.getPath());
        try
        {
			
			// revoke all granted on resource
            PermissionAssignment[] pa = resource.getPermissionAssignments();
            for (int i = 0; i < pa.length; i++)
            {
                coralSession.getSecurity().revoke(resource, pa[i].getRole(), pa[i].getPermission());
            }
			logger.debug("Zdjalem wszystkie prawa z zasobu");
            Resource schemaRoleRoot = integrationService.getSchemaRoleRoot(coralSession, resource.getResourceClass());
			logger.debug("Pobralem scheme dla zasobu");
            //prepare stack
            Stack stack = new Stack();
            prepareStack(coralSession, stack, schemaRoleRoot);
			logger.debug("Przygotowalem stos definicji rol");
            
            
            Resource securityRoot = getRoleInformationRoot(coralSession, getSite(resource));

            //put roles by name    
            Map map = new HashMap();
            prepareRolesMap(coralSession, map, securityRoot);
			logger.debug("Przygotowalem mape rol");
            while (!stack.isEmpty())
            {
                SchemaRoleResource roleDef = (SchemaRoleResource)stack.pop();
                String roleName = roleNameFromSufix(roleDef, resource);
                if (map.containsKey(roleName))
                {
					logger.debug("No to mam role: " + roleName);
                    RoleResource roleResource = (RoleResource)map.get(roleName);
					logger.debug("No to mam role2");
                    // unbind role from resource
                    try
                    {
						AttributeDefinition roleAttributeDef = resource.getResourceClass().getAttribute(roleDef.getRoleAttributeName());
						logger.debug("Pobralem def atrybutu");
                        resource.unset(roleAttributeDef);
						logger.debug("Kasuje atrybut");
                        resource.update();
						logger.debug("Aktualizuje baze");
                    }
                    catch (UnknownAttributeException e)
                    {
						logger.debug("EXCEPTION"+e);    // ignore it.
                    }
                    catch (ValueRequiredException e)
                    {
                        // ignore it.
						logger.debug("EXCEPTION2"+e);
                    }
					Role role = coralSession.getSecurity().getUniqueRole(roleName);
					RoleAssignment[] ra = role.getRoleAssignments();
					for(int i = 0; i < ra.length; i++)
					{
						coralSession.getSecurity().revoke(ra[i].getRole(), ra[i].getSubject());
					}
					logger.debug("Usunalem granty");
					RoleImplication[] ri = role.getImplications();
					for(int i = 0; i < ri.length; i++)
					{
						coralSession.getSecurity().deleteSubRole(ri[i].getSuperRole(), ri[i].getSubRole());
					}
					logger.debug("Usunalem zaleznosci rol");
					logger.debug("A teraz pragne usunac zasob nr. "+roleResource.getIdString()+" : "+roleResource.getPath());
					coralSession.getStore().deleteResource(roleResource);
					logger.debug("No i udalo sie");
					logger.debug("I jeszcze sprobujemy usunac kikut roli");
                    try
                    {
                        coralSession.getSecurity().deleteRole(role);
                        logger.debug("No i ponownie udalo sie");
                    }
                    catch(Exception e)
                    {
                        logger.debug("No i udalo sie usunąć roli");
                    }
               }
                else
                {
                    logger.debug("Nie mam roli: " + roleName);
                }
				logger.debug("Pa pa resource\n");
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
		logger.debug("Skonczylem czyscic zasob: "+resource.getPath());
    }

    private void prepareStack(CoralSession coralSession, Stack stack, Resource resource)
    {
        Resource[] resources = coralSession.getStore().getResource(resource);
        for (int i = 0; i < resources.length; i++)
        {
            if (resources[i] instanceof SchemaRoleResource)
            {
                stack.push(resources[i]);
                prepareStack(coralSession, stack, resources[i]);
            }
        }
    }

    private void prepareRolesMap(CoralSession coralSession,Map map, Resource resource)
    {
        Resource[] resources = coralSession.getStore().getResource(resource);
        for (int i = 0; i < resources.length; i++)
        {
            if (resources[i] instanceof RoleResource)
            {
                map.put(resources[i].getName(), resources[i]);
                prepareRolesMap(coralSession, map, resources[i]);
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
    public Role getRole(CoralSession coralSession,String roleName, Resource resource) throws CmsSecurityException
    {
        Resource schemaRoot = integrationService.getSchemaRoleRoot(coralSession, resource.getResourceClass());
        SchemaRoleResource schema = getSchemaRoleResource(coralSession, schemaRoot, roleName);
        if (schema == null)
        {
            throw new CmsSecurityException(
                "Schema resource for role '" + roleName + "' does not exists in schema defined for class '" + resource.getResourceClass().getName() + "'");
        }
        String fullRoleName = roleNameFromSufix(schema, resource);
        Role role = coralSession.getSecurity().getUniqueRole(fullRoleName);
        return role;
    }

    /**
     * Grants permissions to a given role on a given subtree according to a given role schema.
     *
     * @param schemaRoleResource roles schema - a tree of roles with assigned permissions
     * @param roleName selected role name - one which allows finding an original role name
     *                                         in roles schema
     * @param subtree the resource to which a role is assigned, and from which gets its name suffix.
     */
    public void grantPermissions(CoralSession coralSession,SchemaRoleResource schemaRoleResource, String roleName, Resource subtree) throws CmsSecurityException
    {
        // get the role
        Role role = null;
        String realRoleName = roleNameFromSufix(schemaRoleResource, subtree);
        Role[] temp = coralSession.getSecurity().getRole(realRoleName);
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
            grantPermisions(coralSession,schemaRoleResource, role, subtree, true);
        }
        catch (SecurityException e)
        {
            throw new CmsSecurityException("Can't grant permissions", e);
        }
    }

    private void grantPermisions(CoralSession coralSession,SchemaRoleResource schemaRoleResource, Role role, Resource subtree, boolean recursive)
        throws SecurityException
    {
        Resource[] resources = coralSession.getStore().getResource(schemaRoleResource);
        for (int i = 0; i < resources.length; i++)
        {
            if (resources[i] instanceof SchemaPermissionResource)
            {
                SchemaPermissionResource schemaPermissionResource = (SchemaPermissionResource)resources[i];
                Permission permission = coralSession.getSecurity().getUniquePermission(schemaPermissionResource.getName());
                if (!role.hasPermission(subtree, permission))
                {
                    coralSession.getSecurity().grant(subtree, role, permission, schemaPermissionResource.getRecursive());
                }
            }
            if (recursive && resources[i] instanceof SchemaRoleResource)
            {
                grantPermisions(coralSession,(SchemaRoleResource)resources[i], role, subtree, true);
            }
        }
    }

    // useful private methods
    private SchemaRoleResource getSchemaRoleResource(CoralSession coralSession,Resource schemaRolesRoot, String roleName)
    {
        Resource[] resources = coralSession.getStore().getResource(schemaRolesRoot);
        for (int i = 0; i < resources.length; i++)
        {
            if (resources[i] instanceof SchemaRoleResource)
            {
                SchemaRoleResource result = getSchemaRoleResource(coralSession,(SchemaRoleResource)resources[i], roleName);
                if (result != null)
                {
                    return result;
                }
            }
        }
        return null;
    }

    private SchemaRoleResource getSchemaRoleResource(CoralSession coralSession, SchemaRoleResource schemaRole, String roleName)
    {
        // Deep first search
        if (schemaRole.getName().equals(roleName))
        {
            return schemaRole;
        }
        Resource[] resources = coralSession.getStore().getResource(schemaRole);
        for (int i = 0; i < resources.length; i++)
        {
            if (resources[i] instanceof SchemaRoleResource)
            {
                SchemaRoleResource result = getSchemaRoleResource(coralSession,(SchemaRoleResource)resources[i], roleName);
                if (result != null)
                {
                    return result;
                }
            }
        }
        return null;
    }
    
    
    public void cleanupSite(CoralSession coralSession, SiteResource site)
        throws Exception 
    {
        site.setAdministrator(null);
        site.setLayoutAdministrator(null);
        site.setTeamMember(null);
        Role siteRole = site.getSiteRole();
        site.setSiteRole(null);
        site.update();
        if(siteRole != null)
        {
            clearRole(coralSession, siteRole.getName());
            deleteRole(coralSession, siteRole.getName());
        }
        Resource[] res = coralSession.getStore().getResource(site, "security");
        if(res.length == 0)
        {
            return;
        }
        
        RoleResource[] roles = getRoles(coralSession, site);
        for(int i = roles.length-1; i >= 0; i--)
        {
            RoleResource roleDef = roles[i];
            Role role = roleDef.getRole();
            if(roleDef instanceof SubtreeRoleResource)
            {
                deleteRole(coralSession, roleDef.getDescriptionKey(), ((SubtreeRoleResource)roleDef).getSubtreeRoot(), true);
            }
            else
            {
                coralSession.getStore().deleteResource(roleDef);
                clearRole(coralSession, role.getName());
                deleteRole(coralSession, role.getName());
            }
        }
    }
    
    private void deletePermission(CoralSession coralSession, String name) throws Exception
    {
        Permission[] p = coralSession.getSecurity().getPermission(name);
        for (int i = 0; i < p.length; i++)
        {
            coralSession.getSecurity().deletePermission(p[i]);
        }
    }
    
    private void clearRole(CoralSession coralSession, String name)
        throws Exception
    {
        Role[] p = coralSession.getSecurity().getRole(name);
        for (int i = 0; i < p.length; i++)
        {
            PermissionAssignment[] pa = p[i].getPermissionAssignments();
            for(int j = 0; j < pa.length; j++)
            {
                coralSession.getSecurity().revoke(pa[j].getResource(), pa[j].getRole(), pa[j].getPermission());
            }
        }
    }
    
    private void deleteRole(CoralSession coralSession, String name)
        throws Exception
    {
        try
        {
            Role[] p = coralSession.getSecurity().getRole(name);
            for (int i = 0; i < p.length; i++)
            {
                RoleAssignment[] ra = p[i].getRoleAssignments();
                for(int j = 0; j < ra.length; j++)
                {
                    coralSession.getSecurity().revoke(ra[j].getRole(), ra[j].getSubject());
                }
                RoleImplication[] ri = p[i].getImplications();
                for(int j = 0; j < ri.length; j++)
                {
                    coralSession.getSecurity().deleteSubRole(ri[j].getSuperRole(), ri[j].getSubRole());
                }
                coralSession.getSecurity().deleteRole(p[i]);
            }
        }
        catch(Exception e)
        {
            throw new Exception("failed to delete role: '"+name+"'", e);
        }
    }

}
