package net.cyklotron.cms.ngodatabase.organizations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SortedMap;
import java.util.TreeMap;

import org.objectledge.coral.event.ResourceChangeListener;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.database.Database;
import org.objectledge.database.DatabaseUtils;

import net.cyklotron.cms.documents.DocumentNodeResource;

import bak.pcj.LongIterator;
import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongOpenHashMap;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

public class DocumentCache
{
    private final Database database;

    private final LongKeyMap documentToOrganization = new LongKeyOpenHashMap();

    private final LongKeyMap organizationToDocument = new LongKeyOpenHashMap();

    private final LongKeyLongMap documentToSite = new LongKeyLongOpenHashMap();

    private final LongKeyMap siteToDocument = new LongKeyOpenHashMap();

    private final SortedMap<Long, LongSet> updateTimeToDocument = new TreeMap<Long, LongSet>();

    private final LongKeyLongMap documentToUpdateTime = new LongKeyLongOpenHashMap();
    
    private final Calendar calendar = new GregorianCalendar();

    private DocumentUpdateListener listener = null;

    private boolean cacheLoaded;

    public DocumentCache(Database database)
    {
        this.database = database;
    }

    private void preloadCache(CoralSession coralSession)
    {
        synchronized(organizationToDocument)
        {
            if(!cacheLoaded)
            {
                Connection conn = null;
                Statement stmt = null;
                ResultSet rset = null;
                try
                {
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
            // remove old organizationToDocument mappings
            if(oldOrgs != null)
            {
                LongIterator i = oldOrgs.iterator();
                while(i.hasNext())
                {
                    long orgId = i.next();
                    docs = (LongSet)organizationToDocument.get(orgId);
                    if(docs != null)
                    {
                        docs.remove(doc);
                    }
                }
            }
            // add new organizationToDocument mappings
            LongSet orgs = new LongOpenHashSet();
            for(String id : organizationIds.split(","))
            {
                orgs.add(Long.parseLong(id));
            }
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

            if(updateTime != null)
            {
                calendar.setTime(updateTime);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long dateKey = calendar.getTimeInMillis();
                long oldUpdateTime = documentToUpdateTime.get(doc);
                if(oldUpdateTime != dateKey)
                {
                    // remove old updateTimeToDocument mapping
                    docs = updateTimeToDocument.get(oldUpdateTime);
                    if(docs != null)
                    {
                        docs.remove(doc);
                    }                    
                }
                // add new updateTimeToDocument mapping
                docs = updateTimeToDocument.get(dateKey);
                if(docs == null)
                {
                    docs = new LongOpenHashSet();
                    updateTimeToDocument.put(dateKey, docs);
                }                
                docs.add(doc);
                
                // add or replace documentToUpdateTime mapping
                documentToUpdateTime.put(doc, dateKey);
            }
            
            long oldSite = documentToSite.get(doc);
            if(oldSite != site)
            {
                // remove old siteToDocument mapping
                docs = (LongSet)siteToDocument.get(oldSite);
                if(docs != null)
                {
                    docs.remove(doc);
                }
            }
            
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

    public class DocumentUpdateListener
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
