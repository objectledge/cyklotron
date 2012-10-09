package net.cyklotron.cms.ngodatabase.organizations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.schema.AttributeHandler;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.util.ProtectedValidityFilter;

public class CoralQueryUpdatedDocumentsProvider
    extends UpdatedDocumentsProvider
{
    public CoralQueryUpdatedDocumentsProvider(SiteService siteService)
    {
        super(siteService);
    }

    public List<DocumentNodeResource> queryDocuments(SiteResource[] sites, Date startDate,
        Date endDate, long organizationId, CoralSession coralSession)
    {
        try
        {
            StringBuilder query = new StringBuilder();
            query.append("FIND RESOURCE FROM documents.document_node ");
            query.append("WHERE (");
            for(int i = 0; i < sites.length; i++)
            {
                query.append("site = ").append(sites[i].getId());
                if(i < sites.length - 1)
                {
                    query.append(" OR ");
                }
            }
            query.append(") ");
            if(organizationId != -1L)
            {
                query.append("AND organizationIds LIKE '%," + organizationId + ",%' ");
            }
            else
            {
                query.append("AND organizationIds != '' ");
            }
            query.append("AND customModificationTime > ");
            query.append(getDateLiteral(startDate, coralSession));
            if(endDate != null)
            {
                query.append("AND customModificationTime < ");
                query.append(getDateLiteral(endDate, coralSession));
            }
            QueryResults results = coralSession.getQuery().executeQuery(query.toString());
            List<DocumentNodeResource> documents = new ArrayList<DocumentNodeResource>();
    
            // trim down the results to publicly visible documents
            Subject anonymousSubject = coralSession.getSecurity().getSubject(Subject.ANONYMOUS);
            ProtectedValidityFilter filter = new ProtectedValidityFilter(coralSession,
                anonymousSubject, new Date());
            for(QueryResults.Row row : results)
            {
                if(filter.accept(row.get()))
                {
                    documents.add((DocumentNodeResource)row.get());
                }
            }
            return documents;
        }
        catch(MalformedQueryException e)
        {
            throw new RuntimeException("internal error", e);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new RuntimeException("internal error", e);
        }
    }

    @SuppressWarnings("unchecked")
    private String getDateLiteral(Date date, CoralSession coralSession)
    {
        try
        {
            AttributeHandler<Date> handler = (AttributeHandler<Date>)coralSession.getSchema()
                .getAttributeClass("date").getHandler();
            return handler.toExternalString(date);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new RuntimeException("internal error", e);
        }
    }
   
}
