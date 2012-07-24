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
        Assert.assertTrue(selenium.isElementPresent("link=wolontariat"));
        selenium.click("link=wolontariat");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium.isTextPresent("Dodaj ogłoszenie"));
        Assert.assertTrue(selenium.isTextPresent("Nie jesteś zalogowany/a"));
        selenium.type("id=login", this.login);
        selenium.type("id=password", this.password);
        Assert.assertTrue(selenium.isElementPresent("id=submitbutton"));
        selenium.click("id=submitbutton");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
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
        selenium.open("/dodaj");
        selenium.click("link=wolontariat");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium.isTextPresent("Dodaj ogłoszenie"));
        Assert.assertEquals("Jesteś zalogowany/a jako: " + this.login,
            selenium.getText("css=strong"));
        selenium.click("css=input.dodaj.do_prawej");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium.isTextPresent("Dodaj ogłoszenie"));
        Assert.assertEquals("Nie jesteś zalogowany/a", selenium.getText("css=strong"));
    }

    /**
     * Test add document
     * 
     * @param logged if true add document as signed user, else add document as anonimous.
     * @return document name
     * @throws Exception
     */
    public String addDocument(Boolean logged)
    {
        String documentName = "";
        
        if(logged)
        {
            Assert.assertTrue(selenium.isTextPresent("Jesteś zalogowany/a jako: " + this.login));
            Assert.assertTrue(selenium.isTextPresent("Dodaj ogłoszenie"));
            selenium.select("name=type", "label=ogłoszenie drobne");
            selenium.click("css=input.dodaj");
            selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
            selenium.click("link=wolontariat");
            selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
            Assert
                .assertTrue(selenium
                    .isTextPresent("Dodaj ogłoszenie - organizacja szuka wolontariusza/wolontariuszki"));
            Assert.assertTrue(selenium.isElementPresent("//input[@name='name']"));
            documentName = selenium.getValue("//input[@name='name']");
            selenium.type("name=title", "Selenium@" + documentName);
            selenium.click("id=event_end_date");
            selenium.click("css=div.group.s_date > div.fieldset > div.middle > p");
            selenium.type("name=event_place", "Warszawa");
            selenium.click("xpath=(//input[@name='selected_categories'])[9]");
            selenium.type("id=organization_1_name", "Organizacja");
            selenium.type("name=proposer_credentials", "test");
            selenium.click("css=input.dodaj");
            selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        } else {
            
            
        }
        return documentName;
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
