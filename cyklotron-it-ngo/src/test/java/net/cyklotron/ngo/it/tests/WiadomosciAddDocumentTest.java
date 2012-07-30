package net.cyklotron.ngo.it.tests;

import net.cyklotron.ngo.it.SeleniumTest;
import net.cyklotron.ngo.it.common.Wiadomosci;
import org.junit.Test;

public class WiadomosciAddDocumentTest
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

        // add document as unsigned without attachment
        wiadomosci.addDocument(false, false);
        // add document as unsigned with attachment
        wiadomosci.addDocument(false, true);

        wiadomosci.login("selenium", "12345");
        // add document as signed without attachment
        wiadomosci.addDocument(true, false);
        // add document as signed with attachment
        wiadomosci.addDocument(true, true);
        wiadomosci.logout();

        wiadomosci.Close();
    }
}
