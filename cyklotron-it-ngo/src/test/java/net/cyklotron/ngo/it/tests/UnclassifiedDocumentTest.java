package net.cyklotron.ngo.it.tests;

import net.cyklotron.ngo.it.SeleniumTest;
import net.cyklotron.ngo.it.common.Admin;
import net.cyklotron.ngo.it.common.EditorialTasks;
import net.cyklotron.ngo.it.common.Wiadomosci;

import org.junit.Test;

public class UnclassifiedDocumentTest
    extends SeleniumTest
{

    protected String startPage()
    {

        return "http://wiadomosci.ngo.pl/";

    }

    @Test
    public void test()
        throws Exception
    {

        /**
         * Test accept unclassified document
         */
        test1();

        /**
         * Test dismiss unclassified document
         */
        test2();

        /**
         * Test accept unclassified document with edit
         */
        test3();

    }

    /**
     * Test accept unclassified document
     * 
     * @throws Exception
     */
    private void test1()
        throws Exception
    {

        // add document
        Wiadomosci wiadomosci = new Wiadomosci(selenium);
        wiadomosci.login("selenium", "12345");
        wiadomosci.addDocument(true, false);
        wiadomosci.logout();

        // publish
        Admin admin = new Admin(selenium);
        admin.login("selenium_administrator_serwisu", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());
        String documentId = editorialTasks.getDocumentId(wiadomosci.getDocuments().get(0));
        editorialTasks.assignToMe(documentId);
        editorialTasks.publishDocumentAsUnclassified(wiadomosci.getDocuments().get(0));
        admin.logout();

        admin.login("selenium_administrator_serwisu", "12345");
        editorialTasks.acceptUnclassifiedDocument(wiadomosci.getDocuments().get(0));
        admin.logout();
    }

    /**
     * Test dismiss unclassified document
     * 
     * @throws Exception
     */
    private void test2()
        throws Exception
    {

        // add document
        Wiadomosci wiadomosci = new Wiadomosci(selenium);
        wiadomosci.login("selenium", "12345");
        wiadomosci.addDocument(true, false);
        wiadomosci.logout();

        // publish
        Admin admin = new Admin(selenium);
        admin.login("selenium_administrator_serwisu", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());
        String documentId = editorialTasks.getDocumentId(wiadomosci.getDocuments().get(0));
        editorialTasks.assignToMe(documentId);
        editorialTasks.publishDocumentAsUnclassified(wiadomosci.getDocuments().get(0));
        admin.logout();

        admin.login("selenium_administrator_serwisu", "12345");
        editorialTasks.dismissUnclassifiedDocument(wiadomosci.getDocuments().get(0));
        admin.logout();
    }

    /**
     * Test accept unclassified document with edit
     * 
     * @throws Exception
     */
    private void test3()
        throws Exception
    {

        // add document
        Wiadomosci wiadomosci = new Wiadomosci(selenium);
        wiadomosci.login("selenium", "12345");
        wiadomosci.addDocument(true, false);
        wiadomosci.logout();

        // publish
        Admin admin = new Admin(selenium);
        admin.login("selenium_administrator_serwisu", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());
        String documentId = editorialTasks.getDocumentId(wiadomosci.getDocuments().get(0));
        editorialTasks.assignToMe(documentId);
        editorialTasks.publishDocumentAsUnclassified(wiadomosci.getDocuments().get(0));
        admin.logout();

        admin.login("selenium_administrator_serwisu", "12345");
        editorialTasks.acceptUnclasifiedDocumentEditDocument(wiadomosci.getDocuments().get(0));
        admin.logout();
    }

}
