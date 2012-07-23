package net.cyklotron.ngo.it.common;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertEquals("Nie jesteś zalogowany/a.", selenium.getText("css=strong"));

    }

    public String addDocument(Boolean logged, Boolean attachment)
        throws Exception
    {
        String documentName = "";
        if(logged)
        {
            selenium.open("/dodaj");
            selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
            Assert.assertTrue(selenium.isTextPresent("Dodaj wiadomość / informację o szkoleniu"));
            selenium.select("name=type", "label=wiadomość / zaproszenie");
            selenium.click("css=input.dodaj");
            selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
            Assert.assertTrue(selenium.isTextPresent("Dodaj wiadomość / informację o szkoleniu"));
            Assert.assertTrue(selenium.isElementPresent("//input[@name='name']"));
            documentName = selenium.getValue("//input[@name='name']");
            selenium.type("name=title", "Selenium test - wiadomość testowa");
            selenium
                .type(
                    "name=abstract",
                    "Selenium test - skrót wiadomości testowej, Selenium test - skrót wiadomości testowej, Selenium test - skrót wiadomości testowej, Selenium test - skrót wiadomości testowej, Selenium test - skrót wiadomości testowej, Selenium test - skrót wiadomości testowej, Selenium test - skrót wiadomości testowej.");
            selenium.click("css=html");
            selenium.click("css=button.calendar");
            selenium.click("//tr[4]/td[4]");
            selenium.click("xpath=(//button[@type='button'])[2]");
            selenium.click("//tr[4]/td[4]");
            selenium.type("name=event_place", "Warszawa");
            selenium.type("id=organization_1_name", "Stwarzyszenie Klon/ Jawo");
            selenium.type("name=source_name", "Selenium test");
            selenium.type("name=proposer_credentials", "Selenium");
            selenium.click("name=content_rights_confirmation");
            selenium.click("css=input.dodaj");
            selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
            Assert.assertTrue(selenium
                .isTextPresent("Wiadomość przesłano do moderacji. Jesteś zalogowany/a jako: "
                    + this.login));
        }
        else
        {

        }
        return documentName;
    }

    public void verifyPublishedDocument(String id)
        throws Exception
    {
        selenium.open("/wiadomosci/" + id + ".html");
        Assert.assertTrue(selenium.isTextPresent("Selenium test - wiadomość testowa"));
        selenium.close();
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
