package net.cyklotron.cms.documents.keywords;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMText;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.html.HTMLContentFilter;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.documents.LinkRenderer;

public class KeywordsHTMLConententFilter
    implements HTMLContentFilter
{
    private final List<Keyword> keywords;

    private final List<String> excludedElements;

    private final List<String> excludedClasses;

    private final LinkRenderer linkRenderer;

    private final CoralSession coralSession;

    private final String defaultLinkClass;

    public KeywordsHTMLConententFilter(List<KeywordResource> keywordsResources,
        List<String> excludedElements, List<String> excludedClasses, String defaultLinkClass,
        LinkRenderer linkRenderer, CoralSession coralSession)
    {
        this.excludedElements = excludedElements;
        this.excludedClasses = excludedClasses;
        this.defaultLinkClass = defaultLinkClass;
        this.linkRenderer = linkRenderer;
        this.coralSession = coralSession;

        keywords = new ArrayList<Keyword>();
        for(KeywordResource keywordResource : keywordsResources)
        {
            keywords.add(new Keyword(keywordResource));
        }
    }

    @Override
    public Document filter(Document dom)
        throws ProcessingException
    {
        @SuppressWarnings("unchecked")
        List<Node> textNodes = (List<Node>)dom.selectNodes("//text()");
        textNodeLoop: for(Node textNode : textNodes)
        {
            Element parent = (Element)textNode.getParent();
            if(excludedElements.contains(parent.getName()))
            {
                continue textNodeLoop;
            }
            Attribute classAttr = (Attribute)parent.selectSingleNode("./@class");
            if(classAttr != null)
            {
                String[] cssClasses = classAttr.getValue().split(" +");
                for(String cssClass : cssClasses)
                {
                    if(excludedClasses.contains(cssClass))
                    {
                        continue textNodeLoop;
                    }
                }
            }
            String text = textNode.getText();
            List<Match> matches = Match.findMatches(keywords, text);
            if(matches.size() > 0)
            {
                List<Node> replacement = new ArrayList<Node>(matches.size() * 2 + 1);
                int p = 0;
                for(Match m : matches)
                {
                    if(m.getStart() - p > 0)
                    {
                        replacement.add(text(text, p, m.getStart()));
                    }
                    replacement.add(link(text, m));
                    p = m.getEnd();
                }
                if(text.length() - p > 0)
                {
                    replacement.add(text(text, p, text.length()));
                }
                @SuppressWarnings("unchecked")
                List<Node> parentContent = parent.content();
                p = parentContent.indexOf(textNode);
                parentContent.remove(p);
                for(Node r : replacement)
                {
                    parentContent.add(p++, r);
                }
            }
        }
        return dom;
    }

    private Node text(String text, int start, int end)
    {
        return new DOMText(text.substring(start, end));
    }

    private Node link(String text, Match m)
        throws ProcessingException
    {
        return m.getKeyword().link(text.substring(m.getStart(), m.getEnd()), defaultLinkClass,
            linkRenderer, coralSession);
    }
}
