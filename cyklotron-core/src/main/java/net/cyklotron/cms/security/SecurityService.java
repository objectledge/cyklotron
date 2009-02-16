package net.cyklotron.cms.security;

import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.integration.SchemaRoleResource;
import net.cyklotron.cms.site.SiteResource;


/**
 * Manages administrative roles associated with a site.
 */
public interface SecurityService
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
     * Return the Subject entry for the named user, or create it if necessary.
     * 
     * @param coralSession the coral session.
     * @param dn user's Distinguished Name.
     * @return Subject object.
     */
    public Subject getSubject(CoralSession coralSession, String dn);
    
    /**
     * Returns the security information parent node, useful for showing role
     * tree using the Table toolkit.
     *
     * @param site the site in question.
     */
    public Resource getRoleInformationRoot(CoralSession coralSession, SiteResource site);

    /**
     * Returns the roles defined for a site.
     *
     * @param site the site in question.
     */
    public RoleResource[] getRoles(CoralSession coralSession,SiteResource site);

    /**
     * Returns a RoleResource object describing a specific role.
     *
     * @param site the site to search.
     * @param role the role in question.
     * @return the RoleResource object, or <code>null</code> if not found.
     */
    public RoleResource getRole(CoralSession coralSession,SiteResource site, Role role);


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
    public RoleResource registerRole(CoralSession coralSession,SiteResource site, Role role,
                                     Resource subtree, boolean recursive,
                                     boolean deletable, String descriptionKey,
                                     RoleResource superRole);

    /**
     * Unregisters a role for a site.
     *
     * @param site the site.
     * @param role the role.
     */
    public void unregisterRole(CoralSession coralSession,SiteResource site, Role role, boolean ignoreDeletableFlag)
        throws EntityInUseException;


    /**
     * Creates a Role upon following parameters:
     * @param superRole    super role of all roles defined in roles schema
     * @param roleName     selected role name - one which can be found in roles schema
     * @param subtree      subtree of resources for which role is created
     */
    public Role createRole(CoralSession coralSession,Role superRole, String roleName,
                           Resource subtree)
        throws CmsSecurityException;
    
    /**
     * Deletes a role assigned to a resource subtree, role is revoked with root subject rights.
     * @param roleName selected role name - one which can be found in roles schema
     * @param subtree the resource to which a role is assigned.
     */
    public void deleteRole(CoralSession coralSession,String roleName, Resource subtree, boolean ignoreDeletableFlag)
        throws CmsSecurityException;
    
    /**
     * Removes any roles created using the schema from the resource, or a
     * resource tree.
     * @param resource the resource, or tree root.
     * @param recursive <code>true<?code> to cleanup whole tree.
     */
    public void cleanupRoles(CoralSession coralSession,Resource resource, boolean recursive)
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
    public Role getRole(CoralSession coralSession,String roleName, Resource resource)
		throws CmsSecurityException;

    /**
     * Clean up all security information for a site being deleted.
     * 
     * @param coralSession Coral session
     * @param site site to clean up
     * @throws Exception if there is a problem cleaning up security information.
     */
    public void cleanupSite(CoralSession coralSession, SiteResource site)
        throws Exception;
    
    /**
     * Returns the list of groups defined for a given site.
     * 
     * @param coralSession Coral session
     * @param site the site to query group information for.
     * @return groups defined in the site.
     */
    public RoleResource[] getGroups(CoralSession coralSession, SiteResource site);
    
    /**
     * Checks if the given role resource describes a group, including special team_member group. 
     * 
     * @param roleResource role resource.
     * @return if the role resource describes a group, including special team_member group. 
     */
    public boolean isGroupResource(RoleResource roleResource);
    
    /**
     * Checks if the given string is a valid group name.
     * <p>Allowed characters are letters (either case), numbers and underscore.</p>
     *  
     * @param groupName the name to be chekced.
     * @return true if the name is valid.
     */
    public boolean isValidGroupName(String groupName);
    
    /**
     * Checks if the given group name is already in use.
     * 
     * @param coralSession
     * @param site
     * @param groupName
     * @return
     */
    public boolean isGroupNameInUse(CoralSession coralSession, SiteResource site, String groupName);

    /**
     * Creates a new group. 
     * 
     * @param coralSession Coral session
     * @param site the site where the group is to be created
     * @param groupName name of the new group
     * @return RoleResource object describing the new group
     * @throws CmsSecurityException when there is a problem creating the site, for example the name of the group is invalid or alredy in use.
     */
    public RoleResource createGroup(CoralSession coralSession, SiteResource site, String groupName)
        throws CmsSecurityException;
    
    /**
     * Deletes a group.
     * 
     * @param coralSession Coral session
     * @param group the group to be deleted
     * @throws CmsSecurityException if there is a problem deleting the group.
     */
    public void deleteGroup(CoralSession coralSession, RoleResource group)
        throws CmsSecurityException;

    /**
     * Recover short group name from a RoleResource representing a group.
     * 
     * @param roleResource RoleResource
     * @return group name
     * @throws CmsSecurityException if given RoleResouce does not represent a group.
     */
    public String getShortGroupName(RoleResource roleResource)
        throws CmsSecurityException;
}
