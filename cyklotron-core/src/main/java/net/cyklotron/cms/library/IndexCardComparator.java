package net.cyklotron.cms.library;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class IndexCardComparator implements Comparator<IndexCard>
{
    private final IndexCard.Property property;
    
    /** The Collator to use for comparisons. */
    private final Collator collator;
    
    public IndexCardComparator(IndexCard.Property property, Locale locale)
    {
        this.property = property;
        this.collator = Collator.getInstance(locale);
    }
    
    @Override
    public int compare(IndexCard o1, IndexCard o2)
    {
        String p1 = o1.getProperty(property);
        String p2 = o2.getProperty(property);
        return collator.compare(p1, p2);
    }    
}
