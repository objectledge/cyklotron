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

        // add document
        Wiadomosci wiadomosci = new Wiadomosci(selenium);
        wiadomosci.login("selenium", "12345");
        String documentName = wiadomosci.addDocument(true, false);
        wiadomosci.logout();

        // edit document
        wiadomosci.login("selenium", "12345");
        wiadomosci.editDocument(documentName);
        wiadomosci.logout();

        // publish document
        Admin admin = new Admin(selenium);
        admin.login("root", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());

        String documentId = editorialTasks.getDocumentId(documentName);
        editorialTasks.assignToMe(documentId);
        editorialTasks.publishDocument(documentId);
        admin.logout();

        wiadomosci.verifyPublishedDocument(documentId, documentName);

        admin.login("root", "12345");
        editorialTasks.acceptAllPublishedDocumentProposal(documentName);
        admin.logout();

        admin.Close();
    }
}
