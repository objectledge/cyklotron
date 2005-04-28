package org.cyklotron.components;

// generated by MaxQ [ com.bitmechanic.maxq.generator.TemplateCodeGenerator ]

import org.objectledge.web.test.LedgeWebTestCase;

public class ClickThroughComponentsTest extends LedgeWebTestCase {

    public void testScenario1() throws Exception {
        beginAt("/"); 
        assertActualView("BROWSING:/home_page");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("banners");
        assertActualView("BROWSING:/home_page/components/banners");

        clickLink("showBanner");
        assertActualView("BROWSING:/home_page/components/banners/showBanner");

        clickLink("banners");
        assertActualView("BROWSING:/home_page/components/banners");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("categories");
        assertActualView("BROWSING:/home_page/components/categories");

        clickLink("categoryQueryList");
        assertActualView("BROWSING:/home_page/components/categories/categoryQueryList");

        clickLink("categories");
        assertActualView("BROWSING:/home_page/components/categories");

        clickLink("documentResourceList");
        assertActualView("BROWSING:/home_page/components/categories/documentResourceList");

        clickLink("categories");
        assertActualView("BROWSING:/home_page/components/categories");

        clickLink("holdingResourceList");
        assertActualView("BROWSING:/home_page/components/categories/holdingResourceList");

        clickLink("categories");
        assertActualView("BROWSING:/home_page/components/categories");

        clickLink("relatedResourceList");
        assertActualView("BROWSING:/home_page/components/categories/relatedResourceList");

        clickLink("categories");
        assertActualView("BROWSING:/home_page/components/categories");

        clickLink("resourceList");
        assertActualView("BROWSING:/home_page/components/categories/resourceList");

        clickLink("categories");
        assertActualView("BROWSING:/home_page/components/categories");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("otherComponents");
        assertActualView("BROWSING:/home_page/components/otherComponents");

        clickLink("emptyComponent");
        assertActualView("BROWSING:/home_page/components/otherComponents/emptyComponent");

        clickLink("otherComponents");
        assertActualView("BROWSING:/home_page/components/otherComponents");

        clickLink("applicationScreen");
        assertActualView("BROWSING:/home_page/components/otherComponents/applicationScreen");

        clickLink("otherComponents");
        assertActualView("BROWSING:/home_page/components/otherComponents");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("documents");
        assertActualView("BROWSING:/home_page/components/documents");

        clickLink("calendarEvents");
        assertActualView("BROWSING:/home_page/components/documents/calendarEvents");

        clickLink("documents");
        assertActualView("BROWSING:/home_page/components/documents");

        clickLink("documentView");
        assertActualView("BROWSING:/home_page/components/documents/documentView");

        clickLink("documents");
        assertActualView("BROWSING:/home_page/components/documents");

        clickLink("printDocument");
        assertActualView("BROWSING:/home_page/components/documents/printDocument");

        clickLink("documents");
        assertActualView("BROWSING:/home_page/components/documents");

        clickLink("documentProposal");
        assertActualView("BROWSING:/home_page/components/documents/documentProposal");

        clickLink("documents");
        assertActualView("BROWSING:/home_page/components/documents");

        clickLink("recommendDocument");
        assertActualView("BROWSING:/home_page/components/documents/recommendDocument");

        clickLink("documents");
        assertActualView("BROWSING:/home_page/components/documents");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("fileRepository");
        assertActualView("BROWSING:/home_page/components/fileRepository");

        clickLink("repositoryView");
        assertActualView("BROWSING:/home_page/components/fileRepository/repositoryView");

        clickLink("fileRepository");
        assertActualView("BROWSING:/home_page/components/fileRepository");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("forum");
        assertActualView("BROWSING:/home_page/components/forum");

        clickLink("forumComponent");
        assertActualView("BROWSING:/home_page/components/forum/forumComponent");

        clickLink("forum");
        assertActualView("BROWSING:/home_page/components/forum");

        clickLink("recentlyAddedMessages");
        assertActualView("BROWSING:/home_page/components/forum/recentlyAddedMessages");

        clickLink("forum");
        assertActualView("BROWSING:/home_page/components/forum");

        clickLink("comments");
        assertActualView("BROWSING:/home_page/components/forum/comments");

        clickLink("forum");
        assertActualView("BROWSING:/home_page/components/forum");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("httpFeeds");
        assertActualView("BROWSING:/home_page/components/httpFeeds");

        clickLink("showHTTPFeed");
        assertActualView("BROWSING:/home_page/components/httpFeeds/showHTTPFeed");

        clickLink("httpFeeds");
        assertActualView("BROWSING:/home_page/components/httpFeeds");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("links");
        assertActualView("BROWSING:/home_page/components/links");

        clickLink("linkCollection");
        assertActualView("BROWSING:/home_page/components/links/linkCollection");

        clickLink("links");
        assertActualView("BROWSING:/home_page/components/links");

        clickLink("recommendLink");
        assertActualView("BROWSING:/home_page/components/links/recommendLink");

        clickLink("links");
        assertActualView("BROWSING:/home_page/components/links");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("periodicals");
        assertActualView("BROWSING:/home_page/components/periodicals");

        clickLink("bulletins");
        assertActualView("BROWSING:/home_page/components/periodicals/bulletins");

        clickLink("periodicals");
        assertActualView("BROWSING:/home_page/components/periodicals");

        clickLink("periodicalsComponent");
        assertActualView("BROWSING:/home_page/components/periodicals/periodicalsComponent");

        clickLink("periodicals");
        assertActualView("BROWSING:/home_page/components/periodicals");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("polls");
        assertActualView("BROWSING:/home_page/components/polls");

        clickLink("displayPoll");
        assertActualView("BROWSING:/home_page/components/polls/displayPoll");

        clickLink("polls");
        assertActualView("BROWSING:/home_page/components/polls");

        clickLink("showPollPool");
        assertActualView("BROWSING:/home_page/components/polls/showPollPool");

        clickLink("polls");
        assertActualView("BROWSING:/home_page/components/polls");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("resourceRelations");
        assertActualView("BROWSING:/home_page/components/resourceRelations");

        clickLink("relatedResourcesList");
        assertActualView("BROWSING:/home_page/components/resourceRelations/relatedResourcesList");

        clickLink("resourceRelations");
        assertActualView("BROWSING:/home_page/components/resourceRelations");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("searchEngine");
        assertActualView("BROWSING:/home_page/components/searchEngine");

        clickLink("searchInSite");
        assertActualView("BROWSING:/home_page/components/searchEngine/searchInSite");

        clickLink("searchEngine");
        assertActualView("BROWSING:/home_page/components/searchEngine");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("structure");
        assertActualView("BROWSING:/home_page/components/structure");

        clickLink("breadCrumbNavigation");
        assertActualView("BROWSING:/home_page/components/structure/breadCrumbNavigation");

        clickLink("structure");
        assertActualView("BROWSING:/home_page/components/structure");

        clickLink("dynamicNavigation");
        assertActualView("BROWSING:/home_page/components/structure/dynamicNavigation");

        clickLink("structure");
        assertActualView("BROWSING:/home_page/components/structure");

        clickLink("listNavigation");
        assertActualView("BROWSING:/home_page/components/structure/listNavigation");

        clickLink("structure");
        assertActualView("BROWSING:/home_page/components/structure");

        clickLink("siteMap");
        assertActualView("BROWSING:/home_page/components/structure/siteMap");

        clickLink("structure");
        assertActualView("BROWSING:/home_page/components/structure");

        clickLink("treeNavigation");
        assertActualView("BROWSING:/home_page/components/structure/treeNavigation");

        clickLink("structure");
        assertActualView("BROWSING:/home_page/components/structure");

        clickLink("components");
        assertActualView("BROWSING:/home_page/components");

        clickLink("home_page");
        assertActualView("BROWSING:/home_page");

        // ^^^ insert new recordings here (do not remove) ^^^
    }
}
