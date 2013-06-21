package net.cyklotron.cms.rewrite;

import net.cyklotron.cms.site.SiteResource;

public class SitePath
{
    private final SiteResource site;

    private final String path;

    public SitePath(SiteResource site, String path)
    {
        this.site = site;
        this.path = path;
    }

    public SiteResource getSite()
    {
        return site;
    }

    public String getPath()
    {
        return path;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((site == null) ? 0 : site.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        SitePath other = (SitePath)obj;
        if(path == null)
        {
            if(other.path != null)
                return false;
        }
        else if(!path.equals(other.path))
            return false;
        if(site == null)
        {
            if(other.site != null)
                return false;
        }
        else if(!site.equals(other.site))
            return false;
        return true;
    }
}
