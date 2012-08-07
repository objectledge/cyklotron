package net.cyklotron.ngo.it.tests;

import org.junit.Test;

import net.cyklotron.ngo.it.SeleniumTest;
import net.cyklotron.ngo.it.common.Ogloszenia;

public class OgloszeniaLoginTest
    extends SeleniumTest
{

    protected String startPage()
    {

        return "http://ogloszenia.ngo.pl/";

    }

    @Test
    public void test()
        throws Exception
    {

        Ogloszenia ogloszenia = new Ogloszenia(selenium);
        ogloszenia.login("selenium", "12345");
        ogloszenia.logout();

    }

}
