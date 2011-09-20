package net.cyklotron.cms.documents.keywords;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
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

    public void testFindMatchesSimple()
    {
        final KeywordResource keywordResource = mock(KeywordResource.class);
        checking(new Expectations()
            {
                {
                    one(keywordResource).getPattern();
                    will(returnValue("aaa"));
                    one(keywordResource).getRegexp();
                    will(returnValue(true));
                }
            });
        List<Keyword> keywords = Collections.singletonList(new Keyword(keywordResource));
        List<Match> matches = Match.findMatches(keywords, "aaa bbb aaa bbb aaa");
        assertNotNull(matches);
        assertEquals(3, matches.size());
        assertEquals(0, matches.get(0).getStart());
        assertEquals(3, matches.get(0).getEnd());
        assertEquals(8, matches.get(1).getStart());
        assertEquals(11, matches.get(1).getEnd());
        assertEquals(16, matches.get(2).getStart());
        assertEquals(19, matches.get(2).getEnd());
    }

    public void testFindMatchesOverlappingSameStart()
    {
        final KeywordResource keywordResource1 = mock(KeywordResource.class, "1");
        final KeywordResource keywordResource2 = mock(KeywordResource.class, "2");
        checking(new Expectations()
            {
                {
                    one(keywordResource1).getPattern();
                    will(returnValue("aa bb"));
                    one(keywordResource1).getRegexp();
                    will(returnValue(true));

                    one(keywordResource2).getPattern();
                    will(returnValue("aa"));
                    one(keywordResource2).getRegexp();
                    will(returnValue(true));
                }
            });
        Keyword keyword1 = new Keyword(keywordResource1);
        Keyword keyword2 = new Keyword(keywordResource2);
        List<Keyword> keywords = Arrays.asList(keyword1, keyword2);        
        List<Match> matches = Match.findMatches(keywords, "aa bb cc");
        assertNotNull(matches);
        assertEquals(1, matches.size());
        assertSame(keyword1, matches.get(0).getKeyword());
    }
    
    public void testFindMatchesOverlappingDifferentStart()
    {
        final KeywordResource keywordResource1 = mock(KeywordResource.class, "1");
        final KeywordResource keywordResource2 = mock(KeywordResource.class, "2");
        checking(new Expectations()
            {
                {
                    one(keywordResource1).getPattern();
                    will(returnValue("aa bb"));
                    one(keywordResource1).getRegexp();
                    will(returnValue(true));

                    one(keywordResource2).getPattern();
                    will(returnValue("bb cc"));
                    one(keywordResource2).getRegexp();
                    will(returnValue(true));
                }
            });
        Keyword keyword1 = new Keyword(keywordResource1);
        Keyword keyword2 = new Keyword(keywordResource2);
        List<Keyword> keywords = Arrays.asList(keyword1, keyword2);        
        List<Match> matches = Match.findMatches(keywords, "aa bb cc");
        assertNotNull(matches);
        assertEquals(1, matches.size());
        assertSame(keyword1, matches.get(0).getKeyword());
    }
}
