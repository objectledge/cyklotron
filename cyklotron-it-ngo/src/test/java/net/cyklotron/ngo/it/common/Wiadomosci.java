package net.cyklotron.ngo.it.common;

import com.thoughtworks.selenium.Selenium;

import net.cyklotron.ngo.it.Page;

import org.junit.Assert;

public class Wiadomosci
    extends Page
{
    private String login;

    private String password;

    public Wiadomosci(Selenium selenium)
    {
        super(selenium);
    }

    public Selenium login()
        throws Exception
    {
        selenium.open("/dodaj");
        Assert.assertEquals("Nie jesteś zalogowany/a.", selenium.getText("css=strong"));
        selenium.type("id=login", this.login);
        selenium.type("id=password", this.password);
        selenium.click("id=submitbutton");
        Assert.assertEquals("Jesteś zalogowany/a jako: " + this.login,
            selenium.getText("css=strong"));
        return selenium;
    }

    public Selenium login(String login, String password)
        throws Exception
    {
        setLogin(login);
        setPassword(password);
        return login();
    }

    public void logout()
        throws Exception
    {
        selenium.open("/dodaj");
        Assert.assertEquals("Jesteś zalogowany/a jako: " + this.login,
            selenium.getText("css=strong"));
        selenium.click("css=input.dodaj.do_prawej");
        selenium.waitForPageToLoad("30000");
        Assert.assertEquals("Nie jesteś zalogowany/a.", selenium.getText("css=strong"));

    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

}
