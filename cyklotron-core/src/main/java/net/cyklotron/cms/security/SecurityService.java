package net.cyklotron.cms.security;

import net.labeo.services.Service;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;

import net.cyklotron.cms.integration.SchemaRoleResource;
import net.cyklotron.cms.site.SiteResource;


/**
 * Manages administrative roles associated with a site.
 */
public interface SecurityService
    extends Service
{
    public static final String SERVICE_NAME = "cms_security";

    public static final String LOGGING_FACILITY = "cms_security";

    /**
     * Check if anonymous users are allowed to crate accounts for themselves.
     * 
     * @return <code>true</code> if anonymous users are allowed to crate accounts for themselves.
     */
    public boolean getAllowAddUser();
    
    /**
     * Returns the security information parent node, useful for showing role
     * tree using the Table toolkit.
     *
     * @param site the site in question.
     */
    public Resource getRoleInformationRoot(SiteResource site);

    /**
     * Returns the roles defined for a site.
     *
     * @param site the site in question.
     */
    public RoleResource[] getRoles(SiteResource site);

    /**
     * Returns a RoleResource object describing a specific role.
     *
     * @param site the site to search.
     * @param role the role in question.
     * @return the RoleResource object, or <code>null</code> if not found.
     */
    public RoleResource getRole(SiteResource site, Role role);


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
    public RoleResource registerRole(SiteResource site, Role role,
                                     Resource subtree, boolean recursive,
                                     boolean deletable, String descriptionKey,
                                     RoleResource superRole, Subject subject);

    /**
     * Unregisters a role for a site.
     *
     * @param site the site.
     * @param role the role.
     */
    public void unregisterRole(SiteResource site, Role role, boolean ignoreDeletableFlag)
        throws EntityInUseException;


    /**
     * Creates a Role upon following parameters:
     *  @param superRole    super role of all roles defined in roles schema
     *  @param roleName     selected role name - one which can be found in roles schema
     *  @param subtree      subtree of resources for which role is created
     *  @param subject      the creator of the role
     */
    public Role createRole(Role superRole, String roleName,
                           Resource subtree, Subject subject)
        throws CmsSecurityException;
    
    /**
     * Deletes a role assigned to a resource subtree, role is revoked with root subject rights.
     *
     * @param roleName selected role name - one which can be found in roles schema
     * @param subtree the resource to which a role is assigned.
     * @param subject subject resposible for role deletion (used to modify resources with role ref)
     */
    public void deleteRole(String roleName, Resource subtree, Subject subject, boolean ignoreDeletableFlag)
        throws CmsSecurityException;
    
    /**
     * Removes any roles created using the schema from the resource, or a
     * resource tree.
     * 
     * @param resource the resource, or tree root.
     * @param recursive <code>true<?code> to cleanup whole tree.
     * @param subject the subject that performs the opertation.
     */
    public void cleanupRoles(Resource resource, boolean recursive, Subject subject)
        throws CmsSecurityException;

    /**
     * Get the role name for specified schemar role and resource.
     * 
     * @param schemaRole the schema role.
     * @param resource the resource.
     * @return role name.
     */
    public String roleNameFromSufix(SchemaRoleResource schemaRole, Resource resource);
    	

    /**
     * Get the role for specified resource and base role name.
     * 
     * @param roleName the base name of the role.
     * @param resource the resource.
     * @return the role.
     */
    public Role getRole(String roleName, Resource resource)
		throws CmsSecurityException;
}
