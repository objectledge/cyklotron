package net.cyklotron.cms.category.query;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class CategoryQueryDtoContext
    implements ContextResolver<ObjectMapper>
{
    private ObjectMapper objectMapper;

    public CategoryQueryDtoContext()
        throws Exception
    {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ObjectMapper getContext(Class<?> type)
    {
        return type.equals(CategoryQueryResource.class) ? objectMapper : null;
    }
}
