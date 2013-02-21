package net.cyklotron.cms.category;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class CategoryDtoContext
    implements ContextResolver<ObjectMapper>
{
    private final ObjectMapper objectMapper;

    public CategoryDtoContext()
    {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ObjectMapper getContext(Class<?> type)
    {
        return type.equals(CategoryDto.class) ? objectMapper : null;
    }

}
