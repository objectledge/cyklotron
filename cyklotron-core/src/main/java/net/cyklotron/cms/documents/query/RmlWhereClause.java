package net.cyklotron.cms.documents.query;

import java.util.Iterator;
import java.util.Set;

/**
 * @author lukasz@caltha.pl
 */
public class RmlWhereClause
{
    public static enum ClauseOperator
    {
        EQUAL
        {
            public String getClause(String name, String value)
            {
                return name + "=" + value;
            }
        },
        EQUAL_STRING
        {
            public String getClause(String name, String value)
            {
                return name + "='" + value + "'";
            }
        },

        GREATER
        {
            public String getClause(String name, String value)
            {
                return name + ">" + value;
            }
        },

        LOWER
        {

            public String getClause(String name, String value)
            {
                return name + "<" + value;
            }
        },

        LIKE
        {
            public String getClause(String name, String value)
            {
                return name + " LIKE '%" + value + "%'";
            }
        };

        public abstract String getClause(String name, String value);
    }

    private String attrName;

    private String attrValue;

    private ClauseOperator whereClauseOperator;

    public RmlWhereClause(String attrName, String attrValue)
    {
        this(attrName, attrValue, RmlWhereClause.ClauseOperator.EQUAL);
    }

    public RmlWhereClause(String attrName, String attrValue, ClauseOperator whereClauseOperator)
    {
        this.attrName = attrName;
        this.attrValue = attrValue;
        this.whereClauseOperator = whereClauseOperator;
    }

    /**
     * get RML where clause;
     * 
     * @return RML WhereClause String;
     */
    public String getWhereClause()
    {
        return whereClauseOperator.getClause(attrName, attrValue);
    }

    /**
     * @param rmlWhereClauseSet
     * @return RML WhereClause set String
     */
    public static String getWhereClause(Set<RmlWhereClause> rmlWhereClauseSet)
    {
        String whereClauseSet = "";
        if(rmlWhereClauseSet.size() > 0)
        {
            whereClauseSet = " WHERE ";
            Iterator<RmlWhereClause> i = rmlWhereClauseSet.iterator();
            while(i.hasNext())
            {
                RmlWhereClause whereClause = i.next();
                whereClauseSet += whereClause.getWhereClause();
                if(i.hasNext())
                {
                    whereClauseSet += " AND ";
                }
            }
        }
        return whereClauseSet;
    }

}
