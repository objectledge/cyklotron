package net.cyklotron.cms.documents.keywords;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Keyword
{
    private final Pattern pattern;

    private final KeywordResource keyword;

    public Keyword(KeywordResource keyword)
    {
        this.keyword = keyword;
        String p = keyword.getPattern();
        if(!keyword.getRegexp())
        {
            p = simplePatternToRegexp(p);
        }
        this.pattern = Pattern.compile(p, Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
    }

    public KeywordResource getKeyword()
    {
        return keyword;
    }

    public Matcher matcher(String input)
    {
        return pattern.matcher(input);
    }

    private static String simplePatternToRegexp(String s)
    {
        s = s.replace(".","\\.");
        s = s.replace("?","[^ \\t\\r\\n\\u00A0]?");
        s = s.replaceAll("*", "[^ \\t\\r\\n\\u00A0]*?");
        return s;                                
    }
}
