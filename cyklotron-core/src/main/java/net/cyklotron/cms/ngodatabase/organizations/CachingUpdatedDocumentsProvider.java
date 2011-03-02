package net.cyklotron.cms.ngodatabase.organizations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.event.ResourceChangeListener;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.database.Database;
import org.objectledge.database.DatabaseUtils;

import bak.pcj.LongIterator;
import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongOpenHashMap;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentNodeResourceImpl;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

public class CachingUpdatedDocumentsProvider
    extends UpdatedDocumentsProvider
{
    private final Database database;

    private final CoralSessionFactory coralSessionFactory;
    
    private final LongKeyMap documentToOrganization = new LongKeyOpenHashMap();

    private final LongKeyMap organizationToDocument = new LongKeyOpenHashMap();

    private final LongKeyLongMap documentToSite = new LongKeyLongOpenHashMap();

    private final LongKeyMap siteToDocument = new LongKeyOpenHashMap();

    private final SortedMap<Long, LongSet> updateTimeToDocument = new TreeMap<Long, LongSet>();

    private final LongKeyLongMap documentToUpdateTime = new LongKeyLongOpenHashMap();
    
    private final Calendar calendar = new GregorianCalendar();

    private DocumentUpdateListener listener = null;

    private boolean cacheLoaded;

    public CachingUpdatedDocumentsProvider(Database database,
        CoralSessionFactory coralSessionFactory, SiteService siteService)
    {
        super(siteService);
        this.database = database;
        this.coralSessionFactory = coralSessionFactory;
    }
    
    @Override
    public List<DocumentNodeResource> queryDocuments(SiteResource[] sites, Date endDate,
        long organizationId, CoralSession coralSession)
    {
        LongSet documents = queryDocuments(sites, endDate, organizationId);
        List<DocumentNodeResource> result = new ArrayList<DocumentNodeResource>(documents.size());
        try
        {
            LongIterator i = documents.iterator();
            while(i.hasNext())
            {
                result
                    .add(DocumentNodeResourceImpl.getDocumentNodeResource(coralSession, i.next()));
            }
            return result;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new RuntimeException("internal error");
        }
    }

    private LongSet queryDocuments(SiteResource[] sites, Date date, long org)
    {
        synchronized(organizationToDocument)
        {
            if(!cacheLoaded)
            {
                preloadCache();
            }
            LongSet result = new LongOpenHashSet();
            
            if(org > 0L)
            {
                result.addAll((LongSet)organizationToDocument.get(org));
            }
            else
            {
                result.addAll(documentToOrganization.keySet());
            }
            
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long dateKey = calendar.getTimeInMillis();

            LongSet temp = new LongOpenHashSet();
            for(SortedMap.Entry<Long, LongSet> entry : updateTimeToDocument.tailMap(dateKey)
                .entrySet())
            {
                temp.addAll(entry.getValue());
            }
            result.retainAll(temp);

            temp.clear();
            for(SiteResource site : sites)
            {
                temp.addAll((LongSet)siteToDocument.get(site.getId()));
            }
            result.retainAll(temp);
            
            return result;
        }
    }

    private void preloadCache()
    {
        synchronized(organizationToDocument)
        {
            if(!cacheLoaded)
            {
                Connection conn = null;
                Statement stmt = null;
                ResultSet rset = null;
                CoralSession coralSession = null;
                try
                {
                    coralSession = coralSessionFactory.getAnonymousSession();

                    @SuppressWarnings("unchecked")
                    ResourceClass<DocumentNodeResource> documentNodeClass = coralSession
                        .getSchema().getResourceClass(DocumentNodeResource.CLASS_NAME);
                    @SuppressWarnings("unchecked")
                    AttributeDefinition<String> organizationIdsAttr = (AttributeDefinition<String>)documentNodeClass
                        .getAttribute("organizationIds");
                    @SuppressWarnings("unchecked")
                    AttributeDefinition<Resource> siteAttr = (AttributeDefinition<Resource>)documentNodeClass
                        .getAttribute("site");
                    @SuppressWarnings("unchecked")
                    AttributeDefinition<Date> customModificationTimeAttr = (AttributeDefinition<Date>)documentNodeClass
                        .getAttribute("customModificationTime");

                    conn = database.getConnection();
                    stmt = conn.createStatement();
                    rset = stmt
                        .executeQuery("SELECT gr.resource_id, s.data, d.data, r.data "
                            + "FROM coral_generic_resource gs, coral_attribute_string s, "
                            + "coral_generic_resource gd, coral_attribute_date d "
                            + "coral_generic_resource gr, coral_attribute_resource r, "
                            + "WHERE gs.attribute_definition_id = " + organizationIdsAttr.getId() + " "
                            + "AND gd.attribute_definition_id = " + customModificationTimeAttr.getId() + " "
                            + "AND gr.attribute_definition_id " + siteAttr.getId() + " "
                            + "AND s.data_key = gs.data_key AND d.data_Key = gd.data_key AND r.data_key = gr.data_key "
                            + "AND gd.resource_id = gs.resource_id AND gr.resource_id = gd.resource_id");

                    while(rset.next())
                    {
                        updateCache(rset.getLong(1), rset.getString(2), rset.getDate(3),
                            rset.getLong(4));
                    }

                    listener = new DocumentUpdateListener();
                    coralSession.getEvent().addResourceChangeListener(listener, documentNodeClass);
                    cacheLoaded = true;
                }
                catch(Exception e)
                {
                    throw new RuntimeException("internal error", e);
                }
                finally
                {
                    DatabaseUtils.close(conn, stmt, rset);
                    if(coralSession != null)
                    {
                        coralSession.close();
                    }
                }
            }
        }
    }

    private void updateCache(long doc, String organizationIds, Date updateTime, long site)
    {
        synchronized(organizationToDocument)
        {
            LongSet docs = null;
            
            LongSet oldOrgs = (LongSet)documentToOrganization.get(doc);
            LongSet orgs = new LongOpenHashSet();
            for(String id : organizationIds.split(","))
            {
                orgs.add(Long.parseLong(id));
            }
            boolean orgsEqual = oldOrgs != null ? orgs.equals(oldOrgs) : orgs.isEmpty();
            if(oldOrgs != null && !orgsEqual)
            {
                // remove old organizationToDocument mappings
                LongIterator i = oldOrgs.iterator();
                while(i.hasNext())
                {
                    long orgId = i.next();
                    docs = (LongSet)organizationToDocument.get(orgId);
                    if(docs != null)
                    {
                        docs.remove(doc);
                        if(docs.isEmpty())
                        {
                            organizationToDocument.remove(orgId);
                        }
                    }
                }                
            }
            if(oldOrgs == null || !orgsEqual)
            {
                // add new organizationToDocument mappings
                LongIterator i = orgs.iterator();
                while(i.hasNext())
                {
                    long orgId = i.next();
                    docs = (LongSet)organizationToDocument.get(orgId);
                    if(docs == null)
                    {
                        docs = new LongOpenHashSet();
                        organizationToDocument.put(orgId, docs);
                    }
                    docs.add(doc);
                }
                // add or replace documentToOrganization mapping
                documentToOrganization.put(doc, orgs);                
            }
            
            long updateTimeKey = 0L;
            if(updateTime != null)
            {
                calendar.setTime(updateTime);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                updateTimeKey = calendar.getTimeInMillis();
            }
            long oldUpdateTimeKey = documentToUpdateTime.get(doc);            
            if(oldUpdateTimeKey != 0L && oldUpdateTimeKey != updateTimeKey)
            {
                // remove old updateTimeToDocument mapping
                docs = updateTimeToDocument.get(oldUpdateTimeKey);
                if(docs != null)
                {
                    docs.remove(doc);
                    if(docs.isEmpty())
                    {
                        updateTimeToDocument.remove(oldUpdateTimeKey);
                    }
                }                    
            }
            if((oldUpdateTimeKey == 0L || oldUpdateTimeKey != updateTimeKey) && updateTimeKey != 0L)
            {
                // add new updateTimeToDocument mapping
                docs = updateTimeToDocument.get(updateTimeKey);
                if(docs == null)
                {
                    docs = new LongOpenHashSet();
                    updateTimeToDocument.put(updateTimeKey, docs);
                }                
                docs.add(doc);
                
                // add or replace documentToUpdateTime mapping
                documentToUpdateTime.put(doc, updateTimeKey);
            }
            
            long oldSite = documentToSite.get(doc);
            if(oldSite != 0 && oldSite != site)
            {
                // remove old siteToDocument mapping
                docs = (LongSet)siteToDocument.get(oldSite);
                if(docs != null)
                {
                    docs.remove(doc);
                    if(docs.isEmpty())
                    {
                        siteToDocument.remove(oldSite);
                    }
                }
            }
            if(oldSite == 0L || oldSite != site)
            {
                // add new siteToDocument mapping
                docs = (LongSet)siteToDocument.get(site);
                if(docs == null)
                {
                    docs = new LongOpenHashSet();
                    siteToDocument.put(site, docs);
                }
                docs.add(doc);
                
                // add or replace documentToSite mapping
                documentToSite.put(doc, site);            
            }
        }
    }

    private class DocumentUpdateListener
        implements ResourceChangeListener
    {
        @Override
        public void resourceChanged(Resource resource, Subject subject)
        {
            if(resource instanceof DocumentNodeResource)
            {
                DocumentNodeResource doc = (DocumentNodeResource)resource;
                updateCache(resource.getId(), doc.getOrganizationIds(),
                    doc.getCustomModificationTime(), doc.getSite().getId());
            }
        }
    }
}
