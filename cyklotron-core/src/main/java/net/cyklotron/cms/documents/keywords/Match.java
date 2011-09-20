package net.cyklotron.cms.documents.keywords;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

class Match
    implements Comparable<Match>
{
    private final int start;
    
    private final int end;

    private final Keyword keyword;
    
    public Match(int start, int end, Keyword keyword)
    {
        this.start = start;
        this.end = end;
        this.keyword = keyword;
    }

    public int getStart()
    {
        return start;
    }

    public int getEnd()
    {
        return end;
    }
    
    public Keyword getKeyword()
    {
        return keyword;
    }

    public boolean overlaps(Match o)
    {
        if(o.start <= start)
        {
            return o.end >= start;
        }
        else
        {
            return end >= o.start;
        }
    }

    @Override
    public int compareTo(Match o)
    {
        if(o.start != start)
        {
            // earlier match wins
            return start - o.start;
        }
        else
        {
            // longer match wins
            return o.end - end;
        }
    }
    
    public static List<Match> findMatches(List<Keyword> keywords, String text)
    {
        List<Match> matches = new ArrayList<Match>();
        for(Keyword keyword : keywords)
        {
            Matcher m = keyword.matcher(text);
            while(m.find())
            {
                matches.add(new Match(m.start(), m.end(), keyword));
            }
        }
        Collections.sort(matches);
        List<Match> result = new ArrayList<Match>(matches.size());
        Match last = null;
        for(Match match : matches)
        {
            if(last == null || !match.overlaps(last))
            {
                result.add(match);
                last = match;
            }
        }
        return result;
    }
}
