package net.cyklotron.ngo.it.tests;

import java.util.Map;

import net.cyklotron.ngo.it.SeleniumTest;
import net.cyklotron.ngo.it.common.Admin;
import net.cyklotron.ngo.it.common.EditorialTasks;
import net.cyklotron.ngo.it.common.Wiadomosci;
import org.junit.Test;

public class PublishDocumentTest
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
        String documentId, documentName;

        // login and add document
        Wiadomosci wiadomosci = new Wiadomosci(selenium);
        wiadomosci.login("selenium", "12345");
        documentName = wiadomosci.addDocument(true, false);
        wiadomosci.logout();
        
        // publish document
        Admin admin = new Admin(selenium);
        admin.login("root", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());
        //documentId = editorialTasks.getDocumentId(documentName);
        //editorialTasks.assignToMe(documentName);
        //editorialTasks.publishDocument(documentName);
        admin.logout();

        // verify published document
        //wiadomosci.verifyPublishedDocument(documentId);
        wiadomosci.Close();
    }
}
