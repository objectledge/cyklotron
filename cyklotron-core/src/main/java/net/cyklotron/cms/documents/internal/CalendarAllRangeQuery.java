package net.cyklotron.cms.documents.internal;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.jcontainer.dna.Logger;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.search.SearchUtil;

/**
 * A calendar 'all' range query with heuristic rewriting which minimizes a number of term queries
 * in rewritten version of this query. Also maximal number of boolean clauses is pumped up to avoid
 * TooManyClauses exception.
 *
 * @author    <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version   $Id: CalendarAllRangeQuery.java,v 1.5 2005-02-21 16:29:16 zwierzem Exp $
 */
public class CalendarAllRangeQuery extends Query
{
    private Logger log;
    private Date startDate;
    private Date endDate;
    
    private Term lowerEndDate;
    private Term upperStartDate;
    
    /** 
     * Constructs calendar range query. 
     * 
     * @param log the logger
     * @param startDate
     * @param endDate
     */
    public CalendarAllRangeQuery(Logger log, Date startDate, Date endDate)
    {
        this.log = log;
        this.startDate = startDate;
        this.endDate = endDate;

        // get terms
        lowerEndDate = new Term("event_end", SearchUtil.dateToString(startDate));
        upperStartDate = new Term("event_start", SearchUtil.dateToString(endDate));
        
    }
    
    public Query rewrite(IndexReader indexReader) throws IOException
    {
        // total number of calendar documents in index
        // it is equal or more than number of date terms per field
        int numCalendarDocs = indexReader.maxDoc() - 
        indexReader.docFreq(new Term("title_calendar", DocumentNodeResource.EMPTY_TITLE));

        // remember max clause count
        //int maxClauseCount = BooleanQuery.getMaxClauseCount();
        
        // calculate which range is more efficient for query execution
        // calculate number of terms in expanded RangeQueries
        int termsAfterCut1 = termsInUpperRange(indexReader, lowerEndDate, numCalendarDocs);
        int termsAfterCut2 = termsInUpperRange(indexReader, upperStartDate, numCalendarDocs);

        int termsAfterCut = termsAfterCut1 > termsAfterCut2 ? termsAfterCut1 : termsAfterCut2; 
        
        // create boolean query with range queries
        BooleanQuery rewritten = new BooleanQuery();   // and
        // only both upper or both lower range boundaries are open
        // reverse the query if number of terms before cut date is smaller
        // than half of the total number of terms
        if(termsAfterCut < (int)(0.5 * numCalendarDocs))
        {
            setMaxClauseCount(termsAfterCut);

            RangeQuery endDateAfterRangeStart = new RangeQuery(lowerEndDate, null, true);
            RangeQuery startDateNotAfterRangeEnd = new RangeQuery(upperStartDate, null, false);
            rewritten.add(new BooleanClause(endDateAfterRangeStart, true, false));
            rewritten.add(new BooleanClause(startDateNotAfterRangeEnd, true, true));   // negated
        }
        else
        {
            setMaxClauseCount(numCalendarDocs - termsAfterCut);

            RangeQuery endDateNotBeforeRangeStart = new RangeQuery(null, lowerEndDate, false);
            RangeQuery startDateBeforeRangeEnd = new RangeQuery(null, upperStartDate, true);
            rewritten.add(new BooleanClause(endDateNotBeforeRangeStart, true, true));   // negated
            rewritten.add(new BooleanClause(startDateBeforeRangeEnd, true, false));
        }
        
        // rewrite boolean query
        BooleanQuery rewritten2 = (BooleanQuery) rewritten.rewrite(indexReader);
        if(log.isDebugEnabled())
        {
            log.debug("CalendarAllRangeQuery: real number of clauses="+rewritten2.getClauses().length);
        }
        
        // bring back old value
        //BooleanQuery.setMaxClauseCount(maxClauseCount);

        return rewritten2;
    }
    
    private int termsInUpperRange(IndexReader indexReader, Term lowerTerm, int numCalendarDocs)
        throws IOException
    {
        // compute date boundaries to estimate number of terms expanded by range query rewriting
        long lowDate = 0L;
        long cutDate = 0L;
        try
        {
            // get lowest non null date in index
            TermEnum te = indexReader.terms(new Term(lowerTerm.field(), ""));
            if(te.next())
            {
                String lowestDateText = te.term().text();
                if(lowestDateText.equals("0"))
                {
                    if(te.next())
                    {
                        lowestDateText = te.term().text();
                    }
                }

                if(!lowestDateText.equals("0"))
                {
                    lowDate = SearchUtil.dateFromString(lowestDateText).getTime();
                }
            }
            // no need to rewrite - seems like the number of terms is small
            if(lowDate == 0L)
            {
                return 1;
            }
            
            // get date from which date terms are collected (defined by query term)
            cutDate = SearchUtil.dateFromString(lowerTerm.text()).getTime();
            if(cutDate < lowDate)
            {
                cutDate = lowDate;
            }
        }
        catch(ParseException e)
        {
            throw new RuntimeException("Could not rewrite calendar range query", e);
        }

        // get current date - this estimates highest date in index, because
        // most of the documents are historical ones
        long highDate = (new Date()).getTime();
        if(highDate < cutDate)
        {
            highDate = cutDate + 24L*60L*60L*1000L;
        }

        // calc number of terms before cut date
        int termsAfterCut = (int)Math.floor(
                    (double)((long)numCalendarDocs * (highDate - cutDate))
                    /
                    (double)(highDate - lowDate));
        
        return termsAfterCut;
    }
    
    private final void setMaxClauseCount(int termsCount)
    {
        termsCount = (int) Math.ceil( termsCount * 1.5e0 );
        if(termsCount > BooleanQuery.getMaxClauseCount())
        {
            log.debug("CalendarAllRangeQuery: calculated number of clauses="+termsCount);
            BooleanQuery.setMaxClauseCount(termsCount);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.lucene.search.Query#toString(java.lang.String)
     */
    public String toString(String arg0)
    {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append("(");
        
        buffer.append("+(");
        buffer.append(lowerEndDate.field());
        buffer.append(":[");
        buffer.append(lowerEndDate.text());
        buffer.append(" TO null]) ");

        buffer.append("-(");
        buffer.append(upperStartDate.field());
        buffer.append(":{");
        buffer.append(upperStartDate.text());
        buffer.append(" TO null})");
        
        buffer.append(")");
        if (getBoost() != 1.0f)
        {
            buffer.append("^");
            buffer.append(Float.toString(getBoost()));
        }
        return buffer.toString();
    }
}
