package org.cyklotron.components;

import org.objectledge.web.test.LedgeWebTestCase;

public class ShowBannerTest extends LedgeWebTestCase {

    public void testScenario1() throws Exception {
        beginAt("/"); 
        assertActualView("BROWSING:/home_page");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("banners");
        assertActualView("BROWSING:/home_page/components/banners");

        clickLink("showBanner");
        assertActualView("BROWSING:/home_page/components/banners/showBanner");
        assertLinkPresentWithImage("caltha-logo.gif");
        // ^^^ insert new recordings here (do not remove) ^^^
    }
}
