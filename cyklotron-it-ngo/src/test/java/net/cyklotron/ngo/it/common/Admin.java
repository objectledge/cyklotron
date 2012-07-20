package net.cyklotron.ngo.it.common;

import net.cyklotron.ngo.it.Page;

import com.thoughtworks.selenium.Selenium;
import junit.framework.Assert;

public class Admin
    extends Page
{
    private String login;

    private String password;

    public Admin(Selenium selenium)
    {
        super(selenium);
    }

    public Selenium login()
        throws Exception
    {
        selenium.open("/admin");
        selenium.waitForPageToLoad("30000");
        Assert.assertTrue(selenium.isElementPresent("link=strona logowania"));
        Assert.assertTrue(selenium.isTextPresent("Gość"));
        Assert.assertEquals("Zaloguj", selenium.getValue("id=submitbutton"));
        selenium.type("id=login", this.login);
        selenium.type("id=password", this.password);
        selenium.click("id=submitbutton");
        selenium.waitForPageToLoad("30000");
        Assert.assertEquals("wyloguj się", selenium.getText("link=wyloguj się"));
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
        selenium.open("/admin");
        selenium.waitForPageToLoad("30000");
        Assert.assertEquals("wyloguj się", selenium.getText("link=wyloguj się"));
        selenium.click("link=wyloguj się");
        selenium.waitForPageToLoad("30000");
        Assert.assertTrue(selenium.isElementPresent("link=strona logowania"));
        Assert.assertTrue(selenium.isTextPresent("Gość"));
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
