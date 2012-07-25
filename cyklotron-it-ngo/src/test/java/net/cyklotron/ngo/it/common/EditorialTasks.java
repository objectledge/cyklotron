package net.cyklotron.ngo.it.common;

import java.util.List;

import net.cyklotron.ngo.it.Page;

import com.thoughtworks.selenium.Selenium;
import junit.framework.Assert;

public class EditorialTasks
    extends Page
{

    public EditorialTasks(Selenium selenium)
    {
        super(selenium);
    }

    /**
     * Test finds document at EditorialTasks view open Document Node view and stores document Id
     * 
     * @param name Document name
     * @return document id
     */
    public String getDocumentId(String name)
    {
        String documentId = "";
        selenium.open("/view/site.EditSite?site_id=8439");
        selenium.click("link=(OBIEG)");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);

        // open PopUp and go to Edit Preferences
        Assert.assertTrue(selenium.isElementPresent("//td/span/span/b[contains(text(),'Selenium@"
            + name + "')]"));
        selenium.mouseDown("//td/span/span/b[contains(text(),'Selenium@" + name + "')]");
        Assert.assertTrue(selenium.isElementPresent("//a[contains(@title, '" + name + "')]"));
        selenium.click("//a[contains(@title, '" + name + "')]");
        Assert.assertTrue(selenium.isTextPresent("Edycja właściwości dokumentu"));
        Assert.assertTrue(selenium.isElementPresent("//input[@name='docid']"));
        documentId = selenium.getValue("//input[@name='docid']");

        Assert.assertTrue(selenium.isElementPresent("link=Anuluj i przejdz do obiegu"));
        selenium.click("link=Anuluj i przejdz do obiegu");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        return documentId;
    }

    /**
     * Test document assign to me
     * 
     * @param id document id
     * @throws Exception
     */
    public void assignToMe(String id)
        throws Exception
    {
        selenium.open("/view/site.EditSite?site_id=8439");
        selenium.click("link=(OBIEG)");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);

        // Assign document
        Assert.assertTrue(selenium.isElementPresent("//td[@id='N" + id + "']/input"));
        selenium.click("//td[@id='N" + id + "']/input");
        Assert.assertTrue(selenium.isElementPresent("link=Przydziel mi zaznaczone"));
        selenium.click("link=Przydziel mi zaznaczone");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium.isElementPresent("link=Przydziel"));
        selenium.click("link=Przydziel");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertEquals("Stan dokumentu zmieniono poprawnie", selenium.getText("css=b"));
    }

    /**
     * Test publish document
     * 
     * @param id document id
     * @throws Exception
     */
    public void publishDocument(String id)
        throws Exception
    {
        selenium.open("/view/site.EditSite?site_id=8439");
        selenium.click("link=(OBIEG)");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);

        // open PopUp and go to edit node
        Assert.assertTrue(selenium.isElementPresent("//td[@id='N" + id + "']/span/span/b"));
        selenium.mouseDown("//td[@id='N" + id + "']/span/span/b");
        selenium.click("//a[contains(@href, '/view/structure.EditNode?node_id=" + id
            + "&site_id=8439')]");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);

        // change document state
        Assert.assertTrue(selenium.isTextPresent("Dokument przydzielony"));
        Assert.assertTrue(selenium.isElementPresent("link=Przyjmij dokument"));
        selenium.click("link=Przyjmij dokument");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium.isTextPresent("Stan dokumentu zmieniono poprawnie"));

        // search and relate "logo" images to document
        Assert.assertTrue(selenium.isElementPresent("link=Edytuj"));
        selenium.click("link=Edytuj");
        selenium.waitForPopUp("related", DEFAULT_PAGE_LOAD_TIME);
        selenium.selectWindow("name=related");
        selenium.type("name=search", "logo");
        selenium.selectWindow("name=related");
        selenium.click("link=szukaj");
        selenium.waitForPageToLoad("60000");
        Assert.assertEquals("Dodaj poniżej",
            selenium.getText("xpath=(//a[contains(text(),'Dodaj poniżej')])[2]"));
        selenium.click("id=resource-447227");
        selenium.click("id=resource-436241");
        selenium
            .click("css=form[name=\"form1\"] > div.action-buttons.clearfix > div.modification > a");
        selenium.close();

        // categorize document
        selenium.selectWindow("null");
        Assert.assertTrue(selenium.isElementPresent("xpath=(//a[contains(text(),'Edytuj')])[2]"));
        selenium.click("xpath=(//a[contains(text(),'Edytuj')])[2]");
        selenium.waitForPopUp("categorization", DEFAULT_PAGE_LOAD_TIME);
        selenium.selectWindow("name=categorization");
        selenium.click("id=category-267525");
        selenium.click("id=category-673805");
        Assert
            .assertTrue(selenium
                .isElementPresent("//div[@id='main-block']/table[2]/tbody/tr/td/form/table/tbody/tr[11]/td[2]/a/img"));
        selenium
            .click("//div[@id='main-block']/table[2]/tbody/tr/td/form/table/tbody/tr[11]/td[2]/a/img");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert
            .assertTrue(selenium
                .isElementPresent("//div[@id='main-block']/table[2]/tbody/tr/td/form/table/tbody/tr[14]/td[2]/a/img"));
        selenium
            .click("//div[@id='main-block']/table[2]/tbody/tr/td/form/table/tbody/tr[14]/td[2]/a/img");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        selenium.click("id=category-564755");
        selenium.click("id=category-564755");
        Assert
            .assertTrue(selenium
                .isElementPresent("//div[@id='main-block']/table[2]/tbody/tr/td/form/table/tbody/tr[30]/td[2]/a/img"));
        selenium
            .click("//div[@id='main-block']/table[2]/tbody/tr/td/form/table/tbody/tr[30]/td[2]/a/img");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        selenium.click("id=category-378093");
        Assert
            .assertTrue(selenium
                .isElementPresent("//div[@id='main-block']/table[2]/tbody/tr/td/form/table/tbody/tr[41]/td[2]/a/img"));
        selenium
            .click("//div[@id='main-block']/table[2]/tbody/tr/td/form/table/tbody/tr[41]/td[2]/a/img");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        selenium.click("id=category-26553");
        selenium.click("id=category-769271");
        Assert
            .assertTrue(selenium
                .isElementPresent("//div[@id='main-block']/table[2]/tbody/tr/td/form/table/tbody/tr[39]/td[2]/a/img"));
        selenium
            .click("//div[@id='main-block']/table[2]/tbody/tr/td/form/table/tbody/tr[39]/td[2]/a/img");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        selenium.click("id=category-75825");
        selenium.click("id=category-26587");
        selenium.selectWindow("name=categorization");
        Assert.assertTrue(selenium.isElementPresent("link=Zapisz"));
        selenium.click("link=Zapisz");
        selenium.selectWindow("null");

        // change document state
        Assert.assertTrue(selenium.isElementPresent("link=Przeslij do akceptacji"));
        selenium.click("link=Przeslij do akceptacji");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium.isTextPresent("Stan dokumentu zmieniono poprawnie"));

        // verify document categories
        Assert
            .assertTrue(selenium
                .isTextPresent("1 do wiadomosci ngo.pl, 2 na główną serwisu wiadomosci, Aktywne społeczności CAL, Białoruś, dyskryminacja, dzieci, Warszawa - na główną  "));

        // publish document
        Assert.assertTrue(selenium.isElementPresent("link=Wymuś publikację"));
        selenium.click("link=Wymuś publikację");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
    }

    /**
     * Documents mass assign to assignee.
     * 
     * @param ids list of documents ids
     * @param assignee login of assignee
     * @throws Exception
     */
    public void documentsMassAsign(List<String> names, String assignee)
        throws Exception
    {

        selenium.open("/view/site.EditSite?site_id=8439");
        selenium.click("link=(OBIEG)");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);

        for(String name : names)
        {
            Assert.assertTrue(selenium
                .isElementPresent("//td/span/span/b[contains(text(),'Selenium@" + name
                    + "')]/../../../input"));
            selenium.click("//td/span/span/b[contains(text(),'Selenium@" + name
                + "')]/../../../input");
        }

        Assert.assertTrue(selenium.isElementPresent("name=subject_name"));
        selenium.type("name=subject_name", assignee);
        Assert.assertTrue(selenium.isElementPresent("link=Przydziel zaznaczone do redaktora"));
        selenium.click("link=Przydziel zaznaczone do redaktora");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium.isElementPresent("link=Przydziel"));
        selenium.click("link=Przydziel");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
    }

}
