package net.cyklotron.ngo.it.common;

import net.cyklotron.ngo.it.Page;

import com.thoughtworks.selenium.Selenium;

import junit.framework.Assert;

public class Ogloszenia
    extends Page
{

    private String login;

    private String password;

    public Ogloszenia(Selenium selenium)
    {
        super(selenium);
    }

    /**
     * login
     * 
     * @return
     * @throws Exception
     */
    public void login()
        throws Exception
    {
        selenium.open("/dodaj");
        Assert.assertTrue(selenium.isTextPresent("Dodaj ogłoszenie - wybór kategorii"));
        selenium.click("link=wolontariat");
        selenium.waitForPageToLoad("30000");
        Assert.assertTrue(selenium.isTextPresent("Dodaj ogłoszenie"));
        Assert.assertTrue(selenium.isTextPresent("Nie jesteś zalogowany/a"));
        selenium.type("id=login", this.login);
        selenium.type("id=password", this.password);
        selenium.click("id=submitbutton");
        selenium.waitForPageToLoad("30000");
        Assert.assertTrue(selenium.isTextPresent("Dodaj ogłoszenie"));
        Assert.assertEquals("Jesteś zalogowany/a jako: " + this.login,
            selenium.getText("css=strong"));
    }

    /**
     * login
     * 
     * @param login
     * @param password
     * @throws Exception
     */
    public void login(String login, String password)
        throws Exception
    {
        setLogin(login);
        setPassword(password);
    }

    /**
     * logout
     * 
     * @throws Exception
     */
    public void logout()
        throws Exception
    {
        selenium.open("/dodaj");
        selenium.click("link=wolontariat");
        selenium.waitForPageToLoad("30000");
        Assert.assertTrue(selenium.isTextPresent("Dodaj ogłoszenie"));
        Assert.assertEquals("Jesteś zalogowany/a jako: " + this.login,
            selenium.getText("css=strong"));
        selenium.click("css=input.dodaj.do_prawej");
        selenium.waitForPageToLoad("30000");
        Assert.assertTrue(selenium.isTextPresent("Dodaj ogłoszenie"));
        Assert.assertEquals("Nie jesteś zalogowany/a", selenium.getText("css=strong"));
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
