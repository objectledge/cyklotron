package net.cyklotron.cms.documents.keywords;

import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.documents.LinkRenderer;
import net.cyklotron.cms.structure.NavigationNodeResource;

public class KeywordTest
    extends MockObjectTestCase
{   
    public void testSimpleOne()
    {
        final KeywordResource keywordResource = mock(KeywordResource.class);
        checking(new Expectations()
            {
                {
                    allowing(keywordResource).getRegexp();
                    will(returnValue(false));

                    one(keywordResource).getPattern();
                    will(returnValue("aaa? bbb"));
                }
            });

        Keyword keyword;
        keyword = new Keyword(keywordResource);
        assertSame(keywordResource, keyword.getKeyword());

        assertTrue(keyword.matcher("aaa bbb").find());
        assertTrue(keyword.matcher("aaax bbb").find());
        assertFalse(keyword.matcher("aaayy bbb").find());
    }
    
    public void testSimpleMany()
    {
        final KeywordResource keywordResource = mock(KeywordResource.class);
        checking(new Expectations()
            {
                {
                    allowing(keywordResource).getRegexp();
                    will(returnValue(false));

                    one(keywordResource).getPattern();
                    will(returnValue("aaa* bbb"));
                }
            });

        Keyword keyword;
        keyword = new Keyword(keywordResource);
        assertSame(keywordResource, keyword.getKeyword());

        assertTrue(keyword.matcher("aaa bbb").find());
        assertTrue(keyword.matcher("aaax bbb").find());
        assertTrue(keyword.matcher("aaayy bbb").find());
    }
    
    public void testSimpleQote()
    {
        final KeywordResource keywordResource = mock(KeywordResource.class);
        checking(new Expectations()
            {
                {
                    allowing(keywordResource).getRegexp();
                    will(returnValue(false));

                    one(keywordResource).getPattern();
                    will(returnValue("aaa. bbb"));
                }
            });

        Keyword keyword;
        keyword = new Keyword(keywordResource);
        assertSame(keywordResource, keyword.getKeyword());

        assertTrue(keyword.matcher("aaa. bbb").find());
        assertFalse(keyword.matcher("aaa, bbb").find());
    }
    
    public void testSimpleMidOne()
    {
        final KeywordResource keywordResource = mock(KeywordResource.class);
        checking(new Expectations()
            {
                {
                    allowing(keywordResource).getRegexp();
                    will(returnValue(false));

                    one(keywordResource).getPattern();
                    will(returnValue("a?a"));
                }
            });

        Keyword keyword;
        keyword = new Keyword(keywordResource);
        assertSame(keywordResource, keyword.getKeyword());

        assertTrue(keyword.matcher("aa").find());
        assertTrue(keyword.matcher("aba").find());
        assertFalse(keyword.matcher("a a").find());        
        assertFalse(keyword.matcher("a\u00A0a").find());
    }
    
    public void testSimpleTwoMany()
    {
        final KeywordResource keywordResource = mock(KeywordResource.class);
        checking(new Expectations()
            {
                {
                    allowing(keywordResource).getRegexp();
                    will(returnValue(false));

                    one(keywordResource).getPattern();
                    will(returnValue("*aa*"));
                }
            });

        Keyword keyword;
        keyword = new Keyword(keywordResource);
        assertSame(keywordResource, keyword.getKeyword());

        assertTrue(keyword.matcher("aa").find());
        assertTrue(keyword.matcher(" qqaa").find());
        assertFalse(keyword.matcher("aqq").find());
        assertTrue(keyword.matcher(" qqaaa").find());
    }

    public void testLinkExternal()
        throws ProcessingException
    {
        final KeywordResource keywordResource = mock(KeywordResource.class);
        final LinkRenderer linkRenderer = mock(LinkRenderer.class);
        final CoralSession coralSession = mock(CoralSession.class);
        checking(new Expectations()
            {
                {
                    one(keywordResource).getExternal();
                    will(returnValue(true));

                    one(keywordResource).isHrefExternalDefined();
                    will(returnValue(true));

                    one(keywordResource).getHrefExternal();
                    will(returnValue("http://objectledge.org/"));

                    one(keywordResource).getNewWindow();
                    will(returnValue(false));

                    one(keywordResource).isTitleDefined();
                    will(returnValue(false));
                    
                    one(keywordResource).isLinkClassDefined();
                    will(returnValue(false));

                    ignoring(same(keywordResource)).method("getRegexp");
                    ignoring(same(keywordResource)).method("getPattern");
                    ignoring(linkRenderer);
                }
            });

        Keyword keyword;
        Node link;

        keyword = new Keyword(keywordResource);
        link = keyword.link("foo", "keyword", linkRenderer, coralSession);

        assertTrue(link instanceof Element);
        assertEquals("A", link.getName());
        assertEquals("foo", link.selectObject("string(./text())"));
        assertNull(link.selectSingleNode("./@title"));
        assertNull(link.selectSingleNode("./@target"));
        assertEquals("http://objectledge.org/", link.selectObject("string(./@href)"));
        assertEquals("keyword", link.selectObject("string(./@class)"));
    }

    public void testLinkInternal()
        throws ProcessingException
    {
        final KeywordResource keywordResource = mock(KeywordResource.class);
        final NavigationNodeResource hrefInternal = mock(NavigationNodeResource.class);
        final LinkRenderer linkRenderer = mock(LinkRenderer.class);
        final CoralSession coralSession = mock(CoralSession.class);
        checking(new Expectations()
            {
                {
                    exactly(2).of(keywordResource).getExternal();
                    will(returnValue(false));

                    one(keywordResource).isHrefInternalDefined();
                    will(returnValue(true));

                    one(keywordResource).getHrefInternal();
                    will(returnValue(hrefInternal));

                    one(keywordResource).getNewWindow();
                    will(returnValue(true));

                    one(keywordResource).isTitleDefined();
                    will(returnValue(true));

                    one(keywordResource).getTitle();
                    will(returnValue("title"));

                    one(linkRenderer).getNodeURL(coralSession, hrefInternal);
                    will(returnValue("http://cyklotron.org/x/100"));
                    
                    one(keywordResource).isLinkClassDefined();
                    will(returnValue(true));
                    
                    one(keywordResource).getLinkClass();
                    will(returnValue("special-keyword"));

                    ignoring(same(keywordResource)).method("getRegexp");
                    ignoring(same(keywordResource)).method("getPattern");
                }
            });

        Keyword keyword;
        Node link;

        keyword = new Keyword(keywordResource);
        link = keyword.link("foo", "keyword", linkRenderer, coralSession);

        assertTrue(link instanceof Element);
        assertEquals("A", link.getName());
        assertEquals("foo", link.selectObject("string(./text())"));
        assertEquals("title", link.selectObject("string(./@title)"));
        assertNotNull(link.selectSingleNode("./@target"));
        assertEquals("http://cyklotron.org/x/100", link.selectObject("string(./@href)"));
        assertEquals("special-keyword", link.selectObject("string(./@class)"));
    }

    public void testInvalid()
        throws ProcessingException
    {
        final KeywordResource keywordResource = mock(KeywordResource.class);
        final LinkRenderer linkRenderer = mock(LinkRenderer.class);
        final CoralSession coralSession = mock(CoralSession.class);
        final Sequence sequence = sequence("sequence");
        checking(new Expectations()
        {
            {
                // external
                exactly(2).of(keywordResource).getExternal();
                inSequence(sequence);
                will(returnValue(true));
                
                one(keywordResource).isHrefExternalDefined();
                will(returnValue(false));
                
                // internal
                exactly(2).of(keywordResource).getExternal();
                inSequence(sequence);
                will(returnValue(false));
                
                one(keywordResource).isHrefInternalDefined();
                will(returnValue(false));
                
                ignoring(same(keywordResource)).method("getRegexp");
                ignoring(same(keywordResource)).method("getPattern");
            }
        });
        
        Keyword keyword;
        Node link;

        // external
        keyword = new Keyword(keywordResource);
        link = keyword.link("foo", null, linkRenderer, coralSession);        
        assertTrue(link instanceof Text);
        assertEquals("foo", link.getText());
        
        // internal
        keyword = new Keyword(keywordResource);
        link = keyword.link("foo", null, linkRenderer, coralSession);        
        assertTrue(link instanceof Text);
        assertEquals("foo", link.getText());
    }
}
