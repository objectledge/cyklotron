package net.cyklotron.ngo.it.tests;

import net.cyklotron.ngo.it.SeleniumTest;
import net.cyklotron.ngo.it.common.Admin;
import net.cyklotron.ngo.it.common.EditorialTasks;
import net.cyklotron.ngo.it.common.Wiadomosci;

import org.junit.Test;

public class AcceptPublishedDocumentProposalTest
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
         * Test accept all document proposal
         */
        test1();

        /**
         * Test accept/reject document proposal
         */
        test2();

        /**
         * Test accept all document proposal
         */
        test3();

        /**
         * Test accept/reject document proposal
         */
        test4();

    }

    /**
     * Test accept all document proposal
     * 
     * @throws Exception
     */
    public void test1()
        throws Exception
    {

        // add document
        Wiadomosci wiadomosci = new Wiadomosci(selenium);
        wiadomosci.login("selenium", "12345");
        String documentName = wiadomosci.addDocument(true, false);
        wiadomosci.logout();

        // publish document
        Admin admin = new Admin(selenium);
        admin.login("root", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());
        String documentId = editorialTasks.getDocumentId(documentName);
        editorialTasks.assignToMe(documentId);
        editorialTasks.publishDocument(documentId);
        admin.logout();

        // edit document
        wiadomosci.login("selenium", "12345");
        wiadomosci.editDocument(documentName);
        wiadomosci.logout();

        admin.login("root", "12345");
        editorialTasks.acceptAllPublishedDocumentProposal(documentName);
        admin.logout();


    }

    /**
     * Test accept/reject document proposal
     * 
     * @throws Exception
     */
    public void test2()
        throws Exception
    {

        // add document
        Wiadomosci wiadomosci = new Wiadomosci(selenium);
        wiadomosci.login("selenium", "12345");
        String documentName = wiadomosci.addDocument(true, false);
        wiadomosci.logout();

        // publish document
        Admin admin = new Admin(selenium);
        admin.login("root", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());
        String documentId = editorialTasks.getDocumentId(documentName);
        editorialTasks.assignToMe(documentId);
        editorialTasks.publishDocument(documentId);
        admin.logout();

        // edit document
        wiadomosci.login("selenium", "12345");
        wiadomosci.editDocument(documentName);
        wiadomosci.logout();

        admin.login("root", "12345");
        editorialTasks.acceptSomePublishedDocumentProposal(documentName);
        admin.logout();


    }

    /**
     * Test accept all published document proposal and edit document.
     * 
     * @throws Exception
     */
    public void test3()
        throws Exception
    {

        // add document
        Wiadomosci wiadomosci = new Wiadomosci(selenium);
        wiadomosci.login("selenium", "12345");
        String documentName = wiadomosci.addDocument(true, false);
        wiadomosci.logout();

        // publish document
        Admin admin = new Admin(selenium);
        admin.login("root", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());
        String documentId = editorialTasks.getDocumentId(documentName);
        editorialTasks.assignToMe(documentId);
        editorialTasks.publishDocument(documentId);
        admin.logout();

        // edit document
        wiadomosci.login("selenium", "12345");
        wiadomosci.editDocument(documentName);
        wiadomosci.logout();

        admin.login("root", "12345");
        editorialTasks.acceptPublishedDocumentProposalEditDocument(documentName);
        admin.logout();


    }

    /**
     * Test accept all published document proposal and edit properties.
     * 
     * @throws Exception
     */
    public void test4()
        throws Exception
    {

        // add document
        Wiadomosci wiadomosci = new Wiadomosci(selenium);
        wiadomosci.login("selenium", "12345");
        String documentName = wiadomosci.addDocument(true, false);
        wiadomosci.logout();

        // publish document
        Admin admin = new Admin(selenium);
        admin.login("root", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());
        String documentId = editorialTasks.getDocumentId(documentName);
        editorialTasks.assignToMe(documentId);
        editorialTasks.publishDocument(documentId);
        admin.logout();

        // edit document
        wiadomosci.login("selenium", "12345");
        wiadomosci.editDocument(documentName);
        wiadomosci.logout();

        admin.login("root", "12345");
        editorialTasks.acceptPublishedDocumentProposalEditProperties(documentName);
        admin.logout();

    }
}
