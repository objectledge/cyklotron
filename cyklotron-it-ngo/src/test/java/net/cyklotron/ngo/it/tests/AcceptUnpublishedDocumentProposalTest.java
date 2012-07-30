package net.cyklotron.ngo.it.tests;

import net.cyklotron.ngo.it.SeleniumTest;
import net.cyklotron.ngo.it.common.Admin;
import net.cyklotron.ngo.it.common.EditorialTasks;
import net.cyklotron.ngo.it.common.Wiadomosci;

import org.junit.Test;

public class AcceptUnpublishedDocumentProposalTest
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
        wiadomosci.addDocument(true, false);
        wiadomosci.logout();

        // edit document
        wiadomosci.login("selenium", "12345");
        wiadomosci.editDocument(wiadomosci.getDocuments().get(0));
        wiadomosci.logout();

        // accept unpublished document proposal
        Admin admin = new Admin(selenium);
        admin.login("selenium_administrator_serwisu", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());
        editorialTasks.acceptUnpublishedDocumentProposal(wiadomosci.getDocuments().get(0));
        admin.logout();

        wiadomosci.Close();
    }
}
