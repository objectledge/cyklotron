package net.cyklotron.ngo.it.tests;

import net.cyklotron.ngo.it.SeleniumTest;
import net.cyklotron.ngo.it.common.Admin;
import org.junit.Test;

public class AdminLogin
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

        Admin admin = new Admin(selenium);
        admin.login("root", "12345");
        admin.logout();

    }

}
