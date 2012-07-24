package net.cyklotron.ngo.it.tests;

import net.cyklotron.ngo.it.SeleniumTest;
import net.cyklotron.ngo.it.common.Ogloszenia;
import org.junit.Test;

public class OgloszeniaAddDocumentTest
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
        Ogloszenia ogloszenia = new Ogloszenia(selenium);
        ogloszenia.Close();
    }
}
