package net.cyklotron.cms.documents.keywords;

import org.jmock.integration.junit3.MockObjectTestCase;

public class MatchTest
    extends MockObjectTestCase
{
    public void testOverlaps()
    {
        Match m02 = new Match(0, 2, null);
        Match m24 = new Match(2, 4, null);
        Match m04 = new Match(0, 4, null);
        Match m13 = new Match(1, 3, null);
        Match m57 = new Match(5, 7, null);
        
        assertFalse(m02.overlaps(m24));        
        assertFalse(m24.overlaps(m02));
        assertTrue(m02.overlaps(m04));
        assertTrue(m04.overlaps(m02));
        assertTrue(m13.overlaps(m02));
        assertTrue(m13.overlaps(m24));
        assertFalse(m02.overlaps(m57));
        assertFalse(m04.overlaps(m57));
    }
    
    public void testCompare()
    {
        Match m02 = new Match(0, 2, null);
        Match m24 = new Match(2, 4, null);
        Match m04 = new Match(0, 4, null);
        
        assertTrue(m02.compareTo(m24) < 0);
        assertTrue(m24.compareTo(m02) > 0);
        
        assertTrue(m04.compareTo(m02) < 0);
        assertTrue(m02.compareTo(m04) > 0);
        
        assertTrue(m04.compareTo(m24) < 0);
        assertTrue(m24.compareTo(m04) > 0);
        
        assertTrue(m02.compareTo(m02) == 0);
    }
}
