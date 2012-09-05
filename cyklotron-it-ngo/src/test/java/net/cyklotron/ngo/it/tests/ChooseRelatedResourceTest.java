package net.cyklotron.ngo.it.tests;

import org.junit.Test;

import net.cyklotron.ngo.it.SeleniumTest;
import net.cyklotron.ngo.it.common.Admin;
import net.cyklotron.ngo.it.common.EditorialTasks;
import net.cyklotron.ngo.it.common.Files;
import net.cyklotron.ngo.it.common.Wiadomosci;

public class ChooseRelatedResourceTest
    extends SeleniumTest
{

    protected String startPage()
    {

        return "http://wiadomosci.ngo.pl/";

    }

    /**
     * Expand directory configuration test
     * 
     * @throws Exception
     */
    @Test
    public void test1()
        throws Exception
    {

        // edit document configuration
        Admin admin = new Admin(selenium);
        admin.login("selenium_administrator_serwisu", "12345");
        Files files = new Files(admin.getPage());
        files.initConfiguration();
        admin.logout();

        admin.login("selenium_administrator_serwisu", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());
        editorialTasks.testExpandedDirectory();
        admin.logout();

        admin.login("selenium_administrator_serwisu", "12345");
        files.clearConfiguration();
        admin.logout();

    }

    /**
     * Front categories test
     * 
     * @throws Exception
     */
    @Test
    public void test2()
        throws Exception
    {

        // edit document configuration
        Admin admin = new Admin(selenium);
        admin.login("selenium_administrator_serwisu", "12345");
        Files files = new Files(admin.getPage());
        files.initConfiguration();
        admin.logout();

        // login and add document
        Wiadomosci wiadomosci = new Wiadomosci(selenium);
        wiadomosci.login("selenium", "12345");
        wiadomosci.addDocument(true, true);
        wiadomosci.logout();

        // select front category test
        admin.login("selenium_administrator_serwisu", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());
        String docId = editorialTasks.getDocumentId(wiadomosci.getDocuments().get(0));
        editorialTasks.assignToMe(docId);
        editorialTasks.testSelectFrontCategories(docId);
        admin.logout();

        // clear files configuration
        admin.login("selenium_administrator_serwisu", "12345");
        files.clearConfiguration();
        admin.logout();
    }
    
    
    /**
     * thumbnail on choose related resource view test
     * 
     * @throws Exception
     */
    @Test
    public void test3()
        throws Exception
    {

        // edit document configuration
        Admin admin = new Admin(selenium);
        admin.login("selenium_administrator_serwisu", "12345");
        Files files = new Files(admin.getPage());
        files.initConfiguration();
        admin.logout();

        // login and add document
        Wiadomosci wiadomosci = new Wiadomosci(selenium);
        wiadomosci.login("selenium", "12345");
        wiadomosci.addDocument(true, true);
        wiadomosci.logout();

        // select front category test
        admin.login("selenium_administrator_serwisu", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());
        String docId = editorialTasks.getDocumentId(wiadomosci.getDocuments().get(0));
        editorialTasks.assignToMe(docId);
        editorialTasks.testThumbnailOnRelatedResource(docId);
        admin.logout();

        // clear files configuration
        admin.login("selenium_administrator_serwisu", "12345");
        files.clearConfiguration();
        admin.logout();
    }
}
