package net.cyklotron.cms.ngodatabase.organizations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.schema.AttributeHandler;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.util.ProtectedValidityFilter;

public class OrganizationUtils
{
    protected OrganizationUtils()
    {
        // no instantiation
    }

    public static Date offsetDate(Date date, int offsetDays)
    {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -offsetDays);
        return cal.getTime();
    }

    public static SiteResource[] getSites(Configuration config, SiteService siteService,
        CoralSession coralSession)
        throws SiteException, ConfigurationException
    {
        Configuration[] siteConfigElm = config.getChildren("site");
        SiteResource[] sites = new SiteResource[siteConfigElm.length];
        for(int i = 0; i < siteConfigElm.length; i++)
        {
            sites[i] = siteService.getSite(coralSession, siteConfigElm[i].getValue());
        }
        return sites;
    }

    public static List<DocumentNodeResource> queryDocuments(SiteResource[] sites, Date endDate,
        long organizationId, CoralSession coralSession)
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
            query.append(getDateLiteral(endDate, coralSession));
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
    private static String getDateLiteral(Date date, CoralSession coralSession)
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
