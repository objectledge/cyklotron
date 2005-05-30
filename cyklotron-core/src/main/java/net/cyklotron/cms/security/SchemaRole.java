package net.cyklotron.cms.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 */
public class SchemaRole
{
    // schema attributes //////////////////////////////////////////////////
    
    /** Base name of the role. */
    private String name;
    /** Name of an attribute used to build roles real name, for instance <code>id</code>. */
    private String suffixAtributeName;
    /** Name of an attribute used to store a role reference. */
    private String roleAtributeName;
    /** Determines if a role should be showed in GUI as a role defined for a subtree of resources. */
    private boolean subtreeRole;
    /** Determines if a role can be deleted from GUI. */
    private boolean deletable;
    
    /** Parent role in role schema. */
    private SchemaRole parent;
    /** Subroles for this role in role schema. */
    private HashMap subRoles = new HashMap();
    /** Permissions defined for this role. */
    private ArrayList permissions = new ArrayList();

    // calculated attributes /////////////////////////////////////////////
    private List localPermissions;
    private List recursivePermissions;
    private Boolean recursive;
    
    // schema construction /////////////////////////////////////////////////////////////////////////
    public SchemaRole(String name, String suffixAtributeName, String roleAtributeName,
                        boolean subtreeRole, boolean deletable,
                        SchemaRole parent)
    {
        this.name = name;
        this.suffixAtributeName = suffixAtributeName;
        this.roleAtributeName = roleAtributeName;
        this.subtreeRole = subtreeRole;
        this.deletable = deletable;
        this.parent = parent;
        if(parent != null)
        {
            parent.addSubRole(this);
        }
    }
    
    public void addPermission(SchemaPermission perm)
    {
        permissions.add(perm);
    }

    private void addSubRole(SchemaRole child)
    {
        subRoles.put(child.name, child);
    }
    
    // access methods //////////////////////////////////////////////////////////////////////////////
    public SchemaRole getSchemaRole(String roleName)
    {
        if(name.equals(roleName))
        {
            return this;
        }
        else if(subRoles.containsKey(roleName))
        {
            return (SchemaRole)(subRoles.get(roleName));
        }
        else
        {
            for(Iterator i=subRoles.values().iterator(); i.hasNext();)
            {
                SchemaRole role = ((SchemaRole)(i.next())).getSchemaRole(roleName);
                if(role != null)
                {
                    return role;
                }
            }
        }
        return null;
    }
    
    /** Getter for property name.
     * @return Value of property name.
     *
     */
    public String getName()
    {
        return name;
    }
    
    /** Getter for property suffixAtributeName.
     * @return Value of property suffixAtributeName.
     *
     */
    public String getSuffixAtributeName()
    {
        return suffixAtributeName;
    }

    /** Getter for property roleAtributeName.
     * @return Value of property roleAtributeName.
     *
     */
    public java.lang.String getRoleAtributeName()
    {
        return roleAtributeName;
    }
    
    /** Getter for property subtreeRole.
     * @return Value of property subtreeRole.
     *
     */
    public boolean isSubtreeRole()
    {
        return subtreeRole;
    }

    /** Getter for property deletable.
     * @return Value of property deletable.
     *
     */
    public boolean isDeletable()
    {
        return deletable;
    }
    
    /** Getter for property parent.
     * @return Value of property parent.
     *
     */
    public SchemaRole getParent()
    {
        return parent;
    }
    
    /** Getter for property subRoles.
     * @return Value of property subRoles.
     *
     */
    public List getSubRoles()
    {
        return new ArrayList(subRoles.values());
    }
    
    /** Determines if a role is recursive (this property is being used for hinting users about role
     *  function). If all the permissions implicated by this role are recursive than this role is
     *  recursive. If all the permissions implicated by this role are NOT recursive than this role
     *  is not recursive. If there are recursive and non recursive permissions for this role it
     *  means that the role schema is wrong and an exception is being thrown.
     *
     * @return Value of property permissions.
     *
     */
    public boolean isRecursive()
        throws CmsSecurityException
    {
        if(recursive == null)
        {
            List perms = getPermissions(true);

            Iterator i = perms.iterator();
            boolean recur = ((SchemaPermission)(i.next())).isRecursive();
            while(i.hasNext())
            {
                boolean nextRecursive = ((SchemaPermission)(i.next())).isRecursive();
                if(recur != nextRecursive)
                {
                    throw new CmsSecurityException(
                        "Wrong schema - Role cannot have recursive and non recursive permissions");
                }
            }
            
            recursive = new Boolean(recur);
        }
        return recursive.booleanValue();
    }
    
    /** Returns all the permissions defined for this role. If <code>recursive</code> flag is
     *  <code>true</code>, permissions are gathered from the whole role schema subtree defined by
     *  this role.
     *  Permissions with the same name can be found many times in the subtree, but as a rule
     *  permissions which are recursive override non recursive ones.
     *
     * @return Value of property permissions.
     *
     */
    public List getPermissions(boolean recursive)
    {
        if((recursive && recursivePermissions == null) ||
           (!recursive && localPermissions == null))
        {
            HashMap perms = new HashMap();
            getPermissionsRecursive(perms, recursive);
            ArrayList perms2 = new ArrayList(perms.values());
            List collectedPerms = Collections.unmodifiableList(perms2);
            
            if(recursive)
            {
                recursivePermissions = collectedPerms;
            }
            else
            {
                localPermissions = collectedPerms;
            }
        }

        if(recursive)
        {
            return recursivePermissions;
        }
        else
        {
            return localPermissions;
        }
    }
    
    private void getPermissionsRecursive(HashMap perms, boolean recursive)
    {
        for(int i=0; i<permissions.size(); i++)
        {
            SchemaPermission perm = (SchemaPermission)(permissions.get(i));
            if(perms.containsKey(perm.getName()))
            {
                SchemaPermission mapPerm = (SchemaPermission)(perms.get(perm.getName()));
                if(perm.isRecursive() && !mapPerm.isRecursive())
                {
                    perms.put(perm.getName(), perm);
                }
            }
            else
            {
                perms.put(perm.getName(), perm);
            }
        }

        if(recursive)
        {
            for(Iterator i=subRoles.values().iterator(); i.hasNext();)
            {
                ((SchemaRole)(i.next())).getPermissionsRecursive(perms, recursive);
            }
        }
    }  
}
