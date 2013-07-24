package net.cyklotron.cms.rewrite;

public class RewriteEntry
{
    private String provider;

    private String site;

    private String path;

    private String target;

    private String description;

    public RewriteEntry(String provider, String site, String path, String target, String description)
    {
        this.provider = provider;
        this.site = site;
        this.path = path;
        this.target = target;
        this.description = description;
    }

    public String getProvider()
    {
        return provider;
    }

    public String getSite()
    {
        return site;
    }

    public String getPath()
    {
        return path;
    }

    public String getTarget()
    {
        return target;
    }

    public String getDescription()
    {
        return description;
    }
}
