package net.cyklotron.ngo.it.tests;

import java.util.HashMap;
import java.util.Map;

import net.cyklotron.ngo.it.SeleniumTest;
import net.cyklotron.ngo.it.common.Admin;
import net.cyklotron.ngo.it.common.EditorialTasks;
import net.cyklotron.ngo.it.common.Wiadomosci;

import org.junit.Test;

public class DocumentMassAssignTest
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

        Wiadomosci wiadomosci = new Wiadomosci(selenium);

        for(int i = 0; i < 5; i++)
        {
            wiadomosci.addDocument(false, false);
        }

        wiadomosci.login("selenium", "12345");
        for(int i = 0; i < 5; i++)
        {
            wiadomosci.addDocument(true, false);
        }
        wiadomosci.logout();

        for(int i = 0; i < 5; i++)
        {
            wiadomosci.addDocument(false, false);
        }
        
        Admin admin = new Admin(selenium);
        admin.login("root", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());
        editorialTasks.documentsMassAsign(wiadomosci.getDocuments(), "bogdan");
        admin.logout();
        
        wiadomosci.Close();
    }
}
