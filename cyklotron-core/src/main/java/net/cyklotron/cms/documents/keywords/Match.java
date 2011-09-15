package net.cyklotron.cms.documents.keywords;

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
}
