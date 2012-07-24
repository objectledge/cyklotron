package net.cyklotron.ngo.it.common;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.selenium.Selenium;
import net.cyklotron.ngo.it.Page;
import org.junit.Assert;

public class Wiadomosci
    extends Page
{
    private String login;

    private String password;

    // keep added documents names
    private List<String> documents = new ArrayList();

    public Wiadomosci(Selenium selenium)
    {
        super(selenium);
    }

    /**
     * login
     * 
     * @throws Exception
     */
    public void login()
        throws Exception
    {
        selenium.open("/dodaj");
        Assert.assertEquals("Nie jesteś zalogowany/a.", selenium.getText("css=strong"));
        selenium.type("id=login", this.login);
        selenium.type("id=password", this.password);
        selenium.click("id=submitbutton");
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
        Assert.assertEquals("Jesteś zalogowany/a jako: " + this.login,
            selenium.getText("css=strong"));
        selenium.click("css=input.dodaj.do_prawej");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertEquals("Nie jesteś zalogowany/a.", selenium.getText("css=strong"));

    }

    /**
     * Test add document
     * 
     * @param logged if true add document as signed user, else add document as anonimous.
     * @param attachment if true attache test file to added document
     * @return document name
     * @throws Exception
     */
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
            selenium.type("name=title", "Selenium@" + documentName);
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
        documents.add(documentName);
        return documentName;
    }

    /**
     * Verify is document published
     * 
     * @param id document id
     * @param name document name
     * @throws Exception
     */
    public void verifyPublishedDocument(String id, String name)
        throws Exception
    {
        selenium.open("/wiadomosci/" + id + ".html", "preview_document_" + id);
        Assert.assertTrue(selenium.isTextPresent("Selenium@" + name));
        selenium.close();
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

    public void setPassword(String password)
    {
        this.password = password;
    }

    public List<String> getDocuments()
    {
        return documents;
    }

}
