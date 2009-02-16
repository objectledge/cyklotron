package net.cyklotron.cms.security;

/**
 */
public class SchemaPermission
{
    private String name;
    private boolean recursive;

    public SchemaPermission(String name, boolean recursive)
    {
        this.name = name;
        this.recursive = recursive;
    }

    /** Getter for property name.
     * @return Value of property name.
     *
     */
    public String getName()
    {
        return name;
    }

    /** Getter for property recursive.
     * @return Value of property recursive.
     *
     */
    public boolean isRecursive()
    {
        return recursive;
    }
}
