package net.cyklotron.cms.structure.vote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

/**
 * Sorts documents according to a specific votes based criterion.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public abstract class SortHandler<T extends Comparable<T>>
{
    private SortedMap<T, LongSet> sortedEqivalenceClasses = new TreeMap<T, LongSet>();
    
    private int resultCount = 0;
    
    /**
     * Returns sorting criterion measure.
     * 
     * @param positive number of positive votes.
     * @param negative number of negative votes.
     * @return sorting criterion measure or {@code null} if the resource should be skipped.
     */
    protected abstract T getValue(int positive, int negative);
    
    /**
     * Process a document instance
     * 
     * @param id document id.
     * @param positive positive votes.
     * @param negative negative votes.
     */
    public void process(long id, int positive, int negative)
    {
        T value = getValue(positive, negative);
        if(value != null)
        {
            LongSet set = sortedEqivalenceClasses.get(value);
            if(set == null) {
                set = new LongOpenHashSet();
                sortedEqivalenceClasses.put(value, set);
            }
            set.add(id); 
            resultCount++;
        }
    }

    /**
     * Returns results as an ordered list of equivalence class buckets.
     * 
     * @return sort results.
     */
    public List<LongSet> results()
    {
        Iterator<Map.Entry<T, LongSet>> i = sortedEqivalenceClasses.entrySet().iterator();
        List<LongSet> results = new ArrayList<LongSet>(sortedEqivalenceClasses.size());
        while(i.hasNext())
        {
            results.add(i.next().getValue());
        }
        Collections.reverse(results); // switch to descending order
        return results;
    }
}
