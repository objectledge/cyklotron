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

    /**
     * login to admin panel
     * 
     * @return login
     * @throws Exception
     */
    public void login()
        throws Exception
    {
        selenium.open("/admin");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium.isElementPresent("link=strona logowania"));
        Assert.assertTrue(selenium.isTextPresent("Gość"));
        Assert.assertEquals("Zaloguj", selenium.getValue("id=submitbutton"));
        selenium.type("id=login", this.login);
        selenium.type("id=password", this.password);
        selenium.click("id=submitbutton");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertEquals("wyloguj się", selenium.getText("link=wyloguj się"));
    }

    /**
     * login to admin panel
     * 
     * @param login
     * @param password
     * @return
     * @throws Exception
     */
    public void login(String login, String password)
        throws Exception
    {
        setLogin(login);
        setPassword(password);
        login();
    }

    /**
     * logout
     * 
     * @throws Exception
     */
    public void logout()
        throws Exception
    {
        selenium.open("/admin");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertEquals("wyloguj się", selenium.getText("link=wyloguj się"));
        selenium.click("link=wyloguj się");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium.isElementPresent("link=strona logowania"));
        Assert.assertTrue(selenium.isTextPresent("Gość"));
    }

    /**
     * get login
     * 
     * @return login
     */
    public String getLogin()
    {
        return login;
    }

    /**
     * set login
     * 
     * @param login
     */
    public void setLogin(String login)
    {
        this.login = login;
    }

    /**
     * get password
     * 
     * @return password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * set password
     * 
     * @param password
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

}
