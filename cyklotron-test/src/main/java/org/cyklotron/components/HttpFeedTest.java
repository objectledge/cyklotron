package org.cyklotron.components;

import org.objectledge.web.test.LedgeWebTestCase;

public class HttpFeedTest extends LedgeWebTestCase 
{

    public void testScenario1() throws Exception 
    {
        beginAt("/"); 
        assertActualView("BROWSING:/home_page");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("httpFeeds");
        assertActualView("BROWSING:/home_page/components/httpFeeds");

        clickLink("showHTTPFeed");
        assertActualView("BROWSING:/home_page/components/httpFeeds/showHTTPFeed");
        assertTextPresent("Strona w przebudowie :: Website under construction");

        // ^^^ insert new recordings here (do not remove) ^^^
    }
}
