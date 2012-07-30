package net.cyklotron.ngo.it.tests;

import java.util.HashMap;
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
        // sores added document name,id
        Map<String, String> documents = new HashMap();

        // login and add document
        Wiadomosci wiadomosci = new Wiadomosci(selenium);
        wiadomosci.login("selenium", "12345");
        wiadomosci.addDocument(true, true);
        wiadomosci.logout();

        // publish document
        Admin admin = new Admin(selenium);
        admin.login("selenium_administrator_serwisu", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());
        for(String documentName : wiadomosci.getDocuments())
        {
            documents.put(documentName, editorialTasks.getDocumentId(documentName));
            editorialTasks.assignToMe(documents.get(documentName));
            editorialTasks.publishDocument(documents.get(documentName));
        }
        admin.logout();

        // verify published document
        for(String key : documents.keySet())
        {
            wiadomosci.verifyPublishedDocument(documents.get(key), key);
        }
        wiadomosci.Close();
    }
}
