package net.cyklotron.cms.documents.keywords;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMElement;
import org.dom4j.dom.DOMText;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.documents.LinkRenderer;

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
        p = "\\b" + p + "\\b";
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

    private static String simplePatternToRegexp(String p)
    {
        String[] segs = p.split("\\s+");
        StringBuilder b = new StringBuilder();
        for(String seg : segs)
        {
            String r = "\\Q" + seg + "\\E";
            r = r.replace("?", "\\E[^\\s\\u00A0]?\\Q");
            r = r.replace("*", "\\E[^\\s\\u00A0]*\\Q");
            r = r.replace("\\Q\\E", "");
            b.append(r);
            b.append("[\\s\\u00A0]+");
        }
        b.setLength(b.length() - 11); // crop trailing [\s\u00A0]+
        return b.toString();
    }

    public Node link(String content, String defaultLinkClass, LinkRenderer linkRenderer,
        CoralSession coralSession)
        throws ProcessingException
    {
        Element a = new DOMElement("A");
        if(keyword.getExternal() && keyword.isHrefExternalDefined())
        {
            a.addAttribute("href", keyword.getHrefExternal());
        }
        else if(!keyword.getExternal() && keyword.isHrefInternalDefined())
        {
            a.addAttribute("href", linkRenderer.getNodeURL(coralSession, keyword.getHrefInternal()));
        }
        else
        {
            // invalid keyword - href not defined
            return new DOMText(content);
        }
        if(keyword.getNewWindow())
        {
            a.addAttribute("target", "_blank");
        }
        if(keyword.isTitleDefined())
        {
            a.addAttribute("title", keyword.getTitle());
        }
        String linkClass = null;
        if(keyword.isLinkClassDefined())
        {
            linkClass = keyword.getLinkClass();
        }
        else if(defaultLinkClass != null)
        {
            linkClass = defaultLinkClass;
        }
        if(linkClass != null && linkClass.trim().length() > 0)
        {
            a.addAttribute("class", linkClass);
        }
        a.addText(content);
        return a;
    }
}
