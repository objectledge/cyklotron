package net.cyklotron.cms.accesslimits;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.database.Database;
import org.objectledge.web.ratelimit.impl.HitTable;
import org.picocontainer.Startable;

/**
 * WIP in-memory hit table.
 */
public class HitTableManagerImpl
    implements HitTableManager, Startable
{
    private static final int BATCH_SIZE = 1000;

    private final DBHitTable hitTable;

    private Database database;

    private Logger log;

    public HitTableManagerImpl(Database database, Logger logger)
    {
        this.database = database;
        this.log = logger;
        hitTable = new DBHitTable();
    }

    @Override
    public HitTable getHitTable()
    {
        return hitTable;
    }

    @Override
    public void start()
    {
        load();
    }

    @Override
    public void stop()
    {
        save();
    }

    /**
     * Load counter values from rows with NULL date into memory.
     */
    private void load()
    {
        try(Connection conn = database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt
                .executeQuery("SELECT address, hits, last_hit, matches, last_match, last_matching_rule_id "
                    + "FROM ledge_accesslimits_hits WHERE day IS NULL"))
        {
            while(rset.next())
            {
                hitTable.addHit(rset.getString(1), rset.getInt(2), rset.getTimestamp(3),
                    rset.getInt(4), rset.getTimestamp(5), rset.getLong(6));
            }
        }
        catch(SQLException e)
        {
            log.error("failed to load hits table", e);
        }
    }

    /**
     * Store counter values in DB, into rows with NULL date. In-memory counters are not modified.
     */
    public void save()
    {
        try
        {
            database.beginTransaction();
            try(Connection conn = database.getConnection())
            {
                try(Statement stmt = conn.createStatement())
                {
                    stmt.execute("DELETE FROM ledge_accesslimits_hits WHERE day is NULL");
                }
                try(PreparedStatement stmt = conn
                    .prepareStatement("INSERT INTO ledge_accesslimits_hits (day, address, hits, last_hit, matches, last_match, last_matching_rule_id) "
                        + "VALUES (NULL, ?, ?, ?, ?, ?, ?)"))
                {
                    Iterator<Map.Entry<String, HitTable.Hit>> i = hitTable.getAllHits();
                    int cnt = 0;
                    while(i.hasNext())
                    {
                        Map.Entry<String, HitTable.Hit> e = i.next();
                        stmt.setString(1, e.getKey());
                        stmt.setInt(2, e.getValue().getHits());
                        stmt.setTimestamp(3, new Timestamp(e.getValue().getLastHit().getTime()));
                        stmt.setInt(4, e.getValue().getMatches());
                        stmt.setTimestamp(5, new Timestamp(e.getValue().getLastMatch().getTime()));
                        stmt.setLong(6, e.getValue().getLastMatchingRuleId());
                        stmt.addBatch();
                    }
                    if(cnt++ > BATCH_SIZE || !i.hasNext())
                    {
                        stmt.executeBatch();
                        cnt = 0;
                    }
                }
            }
            catch(SQLException e)
            {
                database.rollbackTransaction(true);
                throw e;
            }
            database.commitTransaction(true);
        }
        catch(SQLException e)
        {
            log.error("failed to save hits table", e);
        }
    }

    /**
     * Store counter values into DB, into rows with today's date. In-memory counters are cleared,
     * but care is taken to preserve the values in case of rollback caused by DB error.
     */
    public void archive()
    {
        try
        {
            database.beginTransaction();
            final long sysTime = System.currentTimeMillis();
            java.sql.Date day = new java.sql.Date(sysTime - (sysTime % (24 * 3600 * 1000)));
            try(Connection conn = database.getConnection())
            {
                try(PreparedStatement stmt = conn
                    .prepareStatement("DELETE FROM ledge_accesslimits_hits WHERE day = ?"))
                {
                    stmt.setDate(1, day);
                    stmt.execute();
                }
                Map<String, Integer> backup = new HashMap<>();
                try(PreparedStatement stmt = conn
                    .prepareStatement("INSERT INTO ledge_accesslimits_hits (day, address, hits, last_hit, matches, last_match, last_matching_rule_id) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)"))
                {
                    Iterator<Map.Entry<String, HitTable.Hit>> i = hitTable.getAllHits();
                    int cnt = 0;
                    while(i.hasNext())
                    {
                        Map.Entry<String, HitTable.Hit> e = i.next();
                        final String address = e.getKey();
                        final int hits = e.getValue().getAndClearHits();
                        backup.put(address, hits);
                        stmt.setDate(1, day);
                        stmt.setString(2, address);
                        stmt.setInt(3, hits);
                        stmt.setTimestamp(4, new Timestamp(e.getValue().getLastHit().getTime()));
                        stmt.setInt(5, e.getValue().getMatches());
                        stmt.setTimestamp(6, new Timestamp(e.getValue().getLastMatch().getTime()));
                        stmt.setLong(7, e.getValue().getLastMatchingRuleId());
                        stmt.addBatch();
                    }
                    if(cnt++ > BATCH_SIZE || !i.hasNext())
                    {
                        stmt.executeBatch();
                        cnt = 0;
                    }
                }
                catch(SQLException e)
                {
                    for(Map.Entry<String, Integer> entry : backup.entrySet())
                    {
                        hitTable.restoreHits(entry.getKey(), entry.getValue());
                    }
                    throw e;
                }
            }
            catch(SQLException e)
            {
                database.rollbackTransaction(true);
                throw e;
            }
            database.commitTransaction(true);
        }
        catch(SQLException e)
        {
            log.error("failed to archive hits table", e);
        }
    }

    public void clear(int threshold)
    {
        hitTable.clear(threshold);
    }

    private static class DBHitTable
        extends HitTable
    {
        protected void clear(int threshold)
        {
            Iterator<Hit> i = table.values().iterator();
            while(i.hasNext())
            {
                Hit h = i.next();
                if(h.getHits() < threshold)
                {
                    i.remove();
                }
            }
        }

        protected void addHit(String address, int hits, Date lastHit, int matches, Date lastMatch,
            long lastMatchingRuleId)
        {
            table.put(address, new HitTable.Hit(hits, lastHit.getTime(), matches,
                lastMatchingRuleId, lastMatch.getTime()));
        }

        protected void restoreHits(String address, int hits)
        {
            Hit hit = table.get(address);
            if(hit != null)
            {
                hit.addHits(hits);
            }
        }

        protected Iterator<Map.Entry<String, Hit>> getAllHits()
        {
            return table.entrySet().iterator();
        }
    }
}
