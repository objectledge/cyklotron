package net.cyklotron.cms.accesslimits;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
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

    private void load()
    {
        try(Connection conn = database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt
                .executeQuery("SELECT address, hits, last_hit, matches, last_match, last_matching_rule_id "
                    + "FROM ledge_accesslimits_hits "))
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

    public void save()
    {
        try
        {
            database.beginTransaction();
            try(Connection conn = database.getConnection();
                Statement stmt1 = conn.createStatement();
                PreparedStatement stmt2 = conn
                    .prepareStatement("INSERT INTO ledge_accesslimits_hits (address, hits, last_hit, matches, last_match, last_matching_rule_id) "
                        + "VALUES (?, ?, ?, ?, ?, ?)"))
            {
                stmt1.execute("DELETE FROM ledge_accesslimits_hits");
                Iterator<Map.Entry<String, HitTable.Hit>> i = hitTable.getAllHits();
                while(i.hasNext())
                {
                    Map.Entry<String, HitTable.Hit> e = i.next();
                    stmt2.setString(1, e.getKey());
                    stmt2.setInt(2, e.getValue().getHits());
                    stmt2.setTimestamp(3, new Timestamp(e.getValue().getLastHit().getTime()));
                    stmt2.setInt(4, e.getValue().getMatches());
                    stmt2.setTimestamp(5, new Timestamp(e.getValue().getLastMatch().getTime()));
                    stmt2.setLong(6, e.getValue().getLastMatchingRuleId());
                    stmt2.addBatch();
                }
                stmt2.executeBatch();
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

        protected Iterator<Map.Entry<String, Hit>> getAllHits()
        {
            return table.entrySet().iterator();
        }
    }
}
