package net.cyklotron.ngo.it.common;

import java.io.File;
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

        selenium.open("/dodaj");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium.isTextPresent("Dodaj wiadomość / informację o szkoleniu"));

        if(logged)
        {
            Assert.assertTrue(selenium.isTextPresent("Jesteś zalogowany/a jako: " + this.login
                + " "));
            Assert.assertTrue(selenium.isElementPresent("name=type"));
            selenium.select("name=type", "label=wiadomość / zaproszenie");
            Assert.assertTrue(selenium.isElementPresent("css=input.dodaj"));
            selenium.click("css=input.dodaj");
            selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);

            Assert.assertTrue(selenium.isTextPresent("Dodaj wiadomość / informację o szkoleniu"));
            Assert.assertTrue(selenium.isElementPresent("//input[@name='name']"));
            documentName = selenium.getValue("//input[@name='name']");
            Assert.assertTrue(selenium.isElementPresent("name=title"));
            selenium.type("name=title", "Selenium@" + documentName);
            Assert.assertTrue(selenium.isElementPresent("name=abstract"));
            selenium.type("name=abstract", "Selenium@wiadomosci as signed user.");
            Assert.assertTrue(selenium.isElementPresent("css=html"));
            selenium.click("css=html");
            Assert.assertTrue(selenium.isElementPresent("css=button.calendar"));
            selenium.click("css=button.calendar");
            Assert.assertTrue(selenium.isElementPresent("//tr[4]/td[4]"));
            selenium.click("//tr[4]/td[4]");
            Assert.assertTrue(selenium.isElementPresent("xpath=(//button[@type='button'])[2]"));
            selenium.click("xpath=(//button[@type='button'])[2]");
            Assert.assertTrue(selenium.isElementPresent("//tr[4]/td[4]"));
            selenium.click("//tr[4]/td[4]");
            Assert.assertTrue(selenium.isElementPresent("name=event_place"));
            selenium.type("name=event_place", "Warszawa");
            Assert.assertTrue(selenium.isElementPresent("id=organization_1_name"));
            selenium.type("id=organization_1_name", "Stwarzyszenie Klon/ Jawor");
            Assert.assertTrue(selenium.isElementPresent("name=source_name"));
            selenium.type("name=source_name", "Selenium test");
            Assert.assertTrue(selenium.isElementPresent("name=proposer_credentials"));
            selenium.type("name=proposer_credentials", "Selenium");
            if(attachment)
            {
                Assert.assertTrue(selenium.isElementPresent("name=attachment_1"));
                selenium.type("name=attachment_1", this.getAttachmentUri("selenium.jpg"));
                selenium.type("name=attachment_description_1", "Selenium - zdjęcie testowe");
            }
            Assert.assertTrue(selenium.isElementPresent("name=content_rights_confirmation"));
            selenium.click("name=content_rights_confirmation");
            if(attachment)
            {
                Assert.assertTrue(selenium.isElementPresent("name=file_rights_confirmation"));
                selenium.click("name=file_rights_confirmation");
            }
            Assert.assertTrue(selenium.isElementPresent("css=input.dodaj"));
            selenium.click("css=input.dodaj");
            selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);

            Assert.assertTrue(selenium
                .isTextPresent("Wiadomość przesłano do moderacji. Jesteś zalogowany/a jako: "
                    + this.login));
        }
        else
        {
            Assert.assertTrue(selenium.isTextPresent("Nie jesteś zalogowany/a."));
            Assert.assertTrue(selenium.isElementPresent("link=Dodaj wiadomość bez logowania"));
            selenium.click("link=Dodaj wiadomość bez logowania");
            selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);

            Assert.assertTrue(selenium.isTextPresent("Nie jesteś zalogowany/a."));
            Assert.assertTrue(selenium.isTextPresent("Dodaj wiadomość / informację o szkoleniu"));

            Assert.assertTrue(selenium.isElementPresent("//input[@name='name']"));
            documentName = selenium.getValue("//input[@name='name']");
            Assert.assertTrue(selenium.isElementPresent("name=title"));
            selenium.type("name=title", "Selenium@" + documentName);
            Assert.assertTrue(selenium.isElementPresent("name=abstract"));
            selenium.type("name=abstract", "Selenium@wiadomosci as signed user.");
            Assert.assertTrue(selenium.isElementPresent("css=html"));
            selenium.click("css=html");
            Assert.assertTrue(selenium.isElementPresent("css=button.calendar"));
            selenium.click("css=button.calendar");
            Assert.assertTrue(selenium.isElementPresent("//tr[4]/td[4]"));
            selenium.click("//tr[4]/td[4]");
            Assert.assertTrue(selenium.isElementPresent("xpath=(//button[@type='button'])[2]"));
            selenium.click("xpath=(//button[@type='button'])[2]");
            Assert.assertTrue(selenium.isElementPresent("//tr[4]/td[4]"));
            selenium.click("//tr[4]/td[4]");
            Assert.assertTrue(selenium.isElementPresent("name=event_place"));
            selenium.type("name=event_place", "Warszawa");
            Assert.assertTrue(selenium.isElementPresent("id=organization_1_name"));
            selenium.type("id=organization_1_name", "Stwarzyszenie Klon/ Jawor");
            Assert.assertTrue(selenium.isElementPresent("name=source_name"));
            selenium.type("name=source_name", "Selenium test");
            Assert.assertTrue(selenium.isElementPresent("name=proposer_credentials"));
            selenium.type("name=proposer_credentials", "Selenium");
            if(attachment)
            {
                Assert.assertTrue(selenium.isElementPresent("name=attachment_1"));
                selenium.type("name=attachment_1", this.getAttachmentUri("selenium.jpg"));
                Assert.assertTrue(selenium.isElementPresent("name=attachment_description_1"));
                selenium.type("name=attachment_description_1", "Selenium - zdjęcie testowe");
            }
            Assert.assertTrue(selenium.isElementPresent("name=content_rights_confirmation"));
            selenium.click("name=content_rights_confirmation");
            if(attachment)
            {
                Assert.assertTrue(selenium.isElementPresent("name=file_rights_confirmation"));
                selenium.click("name=file_rights_confirmation");
            }
            Assert.assertTrue(selenium.isElementPresent("css=input.dodaj"));
            selenium.click("css=input.dodaj");
            selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
            Assert.assertTrue(selenium.isTextPresent("Wiadomość przesłano do moderacji."));
        }
        documents.add(documentName);
        return documentName;
    }

    /**
     * Test edit document
     * 
     * @param name document name
     * @throws Exception
     */
    public void editDocument(String name)
        throws Exception
    {
        selenium.open("/dodaj");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium.isTextPresent("Jesteś zalogowany/a jako: " + this.login + " "));
        Assert.assertTrue(selenium.isElementPresent("//td/a[contains(text(),'Selenium@" + name
            + "')]/../../td/div/a[contains(text(),'edytuj')]"));
        selenium.click("//td/a[contains(text(),'Selenium@" + name
            + "')]/../../td/div/a[contains(text(),'edytuj')]");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium.isTextPresent("Dodaj wiadomość / informację o szkoleniu"));
        Assert.assertTrue(selenium.isElementPresent("name=abstract"));
        selenium.type("name=abstract", "Selenium@wiadomosci as signed user. Proposal.");
        Assert.assertTrue(selenium.isElementPresent("name=proposer_credentials"));
        selenium.type("name=proposer_credentials", "Selenium. Proposal.");
        Assert.assertTrue(selenium.isElementPresent("css=input.dodaj"));
        selenium.click("css=input.dodaj");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium
            .isTextPresent("Propozycja zmian została zapisana. Jesteś zalogowany/a jako: "
                + this.login));
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
        selenium.openWindow("/wiadomosci/" + id + ".html", "preview_document_" + id);
        Assert.assertTrue(selenium.isTextPresent("Selenium@" + name));
        selenium.close();
    }

    /**
     * Get test attachment file path
     * 
     * @param name
     * @return file absolute path
     * @throws Exception
     */
    public String getAttachmentUri(String name)
        throws Exception
    {
        File attachment = new File((new File("")).getAbsolutePath() + "/src/test/resources/" + name);
        return attachment.isFile() ? attachment.toURI().toURL().toString() : "";
    }

    /**
     * Get login
     * 
     * @return login
     */
    public String getLogin()
    {
        return login;
    }

    /**
     * Set login
     * 
     * @param login
     */
    public void setLogin(String login)
    {
        this.login = login;
    }

    /**
     * Get password
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

    /**
     * Return added document names.
     * 
     * @return list of document names
     */
    public List<String> getDocuments()
    {
        return documents;
    }

}
