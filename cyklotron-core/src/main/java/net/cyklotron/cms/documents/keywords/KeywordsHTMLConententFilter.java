package net.cyklotron.cms.documents.keywords;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.objectledge.html.HTMLContentFilter;

public class KeywordsHTMLConententFilter
    implements HTMLContentFilter
{
    private final List<Keyword> keywords;

    private final List<String> excludedElements;

    private final List<String> excludedClasses;

    public KeywordsHTMLConententFilter(List<KeywordResource> keywordsResources,
        List<String> excludedElements, List<String> excludedClasses)
    {
        this.excludedElements = excludedElements;
        this.excludedClasses = excludedClasses;

        keywords = new ArrayList<Keyword>();
        for(KeywordResource keywordResource : keywordsResources)
        {
            keywords.add(new Keyword(keywordResource));
        }
    }

    private List<Match> findMatches(String s)
    {
        List<Match> matches = new ArrayList<Match>();
        for(Keyword keyword : keywords)
        {
            Matcher m = keyword.matcher(s);
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

    @Override
    public Document filter(Document dom)
    {
        @SuppressWarnings("unchecked")
        List<Node> textNodes = (List<Node>)dom.selectNodes("//text()");
        for(Node textNode : textNodes)
        {
            if(textNode.getParent() instanceof Element)
            {
                Element parent = (Element)textNode.getParent();
                if(excludedElements.contains(parent.getName()))
                {
                    continue;
                }
                Attribute classAttr = (Attribute)parent.selectSingleNode("//@class");
                if(classAttr != null)
                {
                    String[] cssClasses = classAttr.getValue().split("[ ,]*");
                    for(String cssClass : cssClasses)
                    {
                        if(excludedClasses.contains(cssClass))
                        {
                            continue;
                        }
                    }
                }
                List<Match> matches = findMatches(textNode.getText());
            }
        }
        return dom;
    }
}
