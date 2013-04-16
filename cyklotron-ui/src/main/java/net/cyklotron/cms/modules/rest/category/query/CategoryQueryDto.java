package net.cyklotron.cms.modules.rest.category.query;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import net.cyklotron.cms.category.query.CategoryQueryResource;

@XmlRootElement
public class CategoryQueryDto
{

    private String id;

    private String name;

    private String query;

    private String longQuery;

    public static Collection<CategoryQueryDto> toDtos(
        Collection<CategoryQueryResource> categoryQueries)
    {
        Collection<CategoryQueryDto> categoryQueriesDto = new ArrayList<>(categoryQueries.size());
        for(CategoryQueryResource categoryQueryResource : categoryQueries)
        {
            categoryQueriesDto.add(toDto(categoryQueryResource));
        }
        return categoryQueriesDto;
    }

    private static CategoryQueryDto toDto(CategoryQueryResource categoryQueryResource)
    {
        CategoryQueryDto categoryQueryDto = new CategoryQueryDto();
        categoryQueryDto.id = categoryQueryResource.getIdString();
        categoryQueryDto.name = categoryQueryResource.getName();
        categoryQueryDto.query = categoryQueryResource.getQuery();
        categoryQueryDto.longQuery = categoryQueryResource.getLongQuery();
        return categoryQueryDto;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public String getLongQuery()
    {
        return longQuery;
    }

    public void setLongQuery(String longQuery)
    {
        this.longQuery = longQuery;
    }

}
