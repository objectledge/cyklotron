package net.cyklotron.cms.docimport;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Implementation of ImportTargetConfiguration based on {@link ImportResource}
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class PersistentImportTargetConfiguration
    implements ImportTargetConfiguration
{
    private static final String DEFAULT_CALENDAR_STRUCTURE_TYPE = "N";

    private final ImportResource res;

    public PersistentImportTargetConfiguration(ImportResource res)
    {
        this.res = res;
    }

    @Override
    public String getContentOwnerLogin()
    {
        return res.getOwnerLogin();
    }

    @Override
    public NavigationNodeResource getTargetLocation()
    {
        return (NavigationNodeResource)res.getTargetLocation();
    }

    @Override
    public String getCalendarStructureType()
    {
        return res.isCalendarStructureTypeDefined() ? res.getCalendarStructureType()
            : DEFAULT_CALENDAR_STRUCTURE_TYPE;
    }

    @Override
    public Set<CategoryResource> getCategories()
    {
        if(res.isCategoriesDefined())
        {
            final List<CategoryResource> categories = res.getCategories();
            return new HashSet<CategoryResource>(categories);
        }
        else
        {
            return Collections.emptySet();
        }
    }
    
    @Override
    public String getFooter()
    {
        return res.getFooter();
    }

    @Override
    public DirectoryResource getAttachmentsLocation()
    {
        return (DirectoryResource)res.getAttachmentsLocation();
    }
}
