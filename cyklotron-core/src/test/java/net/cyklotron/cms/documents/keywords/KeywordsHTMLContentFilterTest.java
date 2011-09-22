package net.cyklotron.cms.documents.keywords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.html.HTMLContentFilter;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.documents.LinkRenderer;

public class KeywordsHTMLContentFilterTest
    extends MockObjectTestCase
{
    private List<String> excludedElements = new ArrayList<String>();

    private List<String> excludedClasses = new ArrayList<String>();

    private LinkRenderer linkRenderer;

    private CoralSession coralSession;

    private KeywordResource keywordResource;

    public void setUp()
        throws Exception
    {
        super.setUp();
        excludedElements.add("PRE");
        excludedClasses.add("no-keywords");
        linkRenderer = mock(LinkRenderer.class);
        coralSession = mock(CoralSession.class);
        keywordResource = mock(KeywordResource.class);
        checking(new Expectations()
            {
                {
                    allowing(keywordResource).getRegexp();
                    will(returnValue(true));

                    allowing(keywordResource).getPattern();
                    will(returnValue("bbb"));

                    allowing(keywordResource).getExternal();
                    will(returnValue(true));

                    allowing(keywordResource).isHrefExternalDefined();
                    will(returnValue(true));

                    allowing(keywordResource).getHrefExternal();
                    will(returnValue("http://objectledge.org/"));

                    allowing(keywordResource).isTitleDefined();
                    will(returnValue(false));

                    allowing(keywordResource).getNewWindow();
                    will(returnValue(false));
                }
            });
    }

    private HTMLContentFilter newFilter(KeywordResource... keywords)
    {
        return new KeywordsHTMLConententFilter(Arrays.asList(keywords), excludedElements,
            excludedClasses, linkRenderer, coralSession);
    }

    private Document dom(String text)
        throws DocumentException
    {
        return DocumentHelper.parseText("<HTML><BODY>\n" + text + "\n</BODY></HTML>");
    }

    public void testSimple()
        throws ProcessingException, DocumentException
    {
        HTMLContentFilter filter = newFilter(keywordResource);
        Document result = filter.filter(dom("aaa bbb aaa"));
        Node a = result.selectSingleNode("//A");
        assertNotNull(a);
        assertEquals("bbb", a.getText());
        assertEquals("http://objectledge.org/", a.selectObject("string(./@href)"));
    }

    public void testExcludeElement()
        throws ProcessingException, DocumentException
    {
        HTMLContentFilter filter = newFilter(keywordResource);
        Document result = filter.filter(dom("<P>aaa bbb</P><PRE>aaa bbb</PRE>"));
        @SuppressWarnings("unchecked")
        List<Node> a = result.selectNodes("//A");
        assertNotNull(a);
        assertEquals(1, a.size());
    }

    public void testExcludeClass()
        throws ProcessingException, DocumentException
    {
        HTMLContentFilter filter = newFilter(keywordResource);
        Document result = filter.filter(dom("<P class='fancy'>aaa bbb</P><P class='decorated no-keywords'>aaa bbb</P>"));
        @SuppressWarnings("unchecked")
        List<Node> a = result.selectNodes("//A");
        assertNotNull(a);
        assertEquals(1, a.size());
    }
}
