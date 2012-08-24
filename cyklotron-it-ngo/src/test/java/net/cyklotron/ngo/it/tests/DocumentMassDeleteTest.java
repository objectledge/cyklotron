package net.cyklotron.ngo.it.tests;

import java.util.HashMap;
import java.util.Map;

import net.cyklotron.ngo.it.SeleniumTest;
import net.cyklotron.ngo.it.common.Admin;
import net.cyklotron.ngo.it.common.EditorialTasks;
import net.cyklotron.ngo.it.common.Wiadomosci;

import org.junit.Test;

public class DocumentMassDeleteTest
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
        admin.login("selenium_administrator_serwisu", "12345");
        EditorialTasks editorialTasks = new EditorialTasks(admin.getPage());
        editorialTasks.documentsMassDelete(wiadomosci.getDocuments());
        admin.logout();

        wiadomosci.Close();
    }
}