package net.cyklotron.ngo.it.common;

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
    
    public String getDocumentId(String name) {
        
        selenium.open("/view/site.EditSite?site_id=8439");
        selenium.click("link=(OBIEG)");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);

        // Go to Edit Preferences  
        selenium.click("//a[starts-with(@title,'"+ name +"')]");
        Assert.assertTrue(selenium.isTextPresent("Edycja właściwości dokumentu"));
        String docId = selenium.getValue("//input[@name='docId']/@value");
        selenium.click("link=Anuluj");
        return docId;
    }
    
    public void assignToMe(String id)
        throws Exception
    {
        selenium.open("/view/site.EditSite?site_id=8439");
        selenium.click("link=(OBIEG)");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);

        // Assign 
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

    public void publishDocument(String id)
        throws Exception
    {
        selenium.open("/view/site.EditSite?site_id=8439");
        selenium.click("link=(OBIEG)");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        selenium.click("//a[contains(@href, '/view/structure.EditNode?node_id=" + id + "&site_id=8439')]");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertEquals("Selenium test - wiadomość testowa", selenium.getValue("name=title"));
        Assert.assertTrue(selenium.isTextPresent("Dokument przydzielony"));
        selenium.click("link=Przyjmij dokument");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium.isTextPresent("Stan dokumentu zmieniono poprawnie"));
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
        selenium.selectWindow("null");
        selenium.click("xpath=(//a[contains(text(),'Edytuj')])[2]");
        selenium.waitForPopUp("categorization", DEFAULT_PAGE_LOAD_TIME);
        selenium.selectWindow("name=categorization");
        selenium.click("id=category-267525");
        selenium.click("id=category-673805");
        selenium
            .click("//div[@id='main-block']/table[2]/tbody/tr/td/form/table/tbody/tr[11]/td[2]/a/img");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        selenium
            .click("//div[@id='main-block']/table[2]/tbody/tr/td/form/table/tbody/tr[14]/td[2]/a/img");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        selenium.click("id=category-564755");
        selenium.click("id=category-564755");
        selenium
            .click("//div[@id='main-block']/table[2]/tbody/tr/td/form/table/tbody/tr[30]/td[2]/a/img");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        selenium.click("id=category-378093");
        selenium
            .click("//div[@id='main-block']/table[2]/tbody/tr/td/form/table/tbody/tr[41]/td[2]/a/img");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        selenium.click("id=category-26553");
        selenium.click("id=category-769271");
        selenium
            .click("//div[@id='main-block']/table[2]/tbody/tr/td/form/table/tbody/tr[39]/td[2]/a/img");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        selenium.click("id=category-75825");
        selenium.click("id=category-26587");
        selenium.selectWindow("name=categorization");
        selenium.click("link=Zapisz");
        selenium.selectWindow("null");
        selenium.click("link=Podgląd strony");
        selenium.goBack();
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        selenium.click("link=Przeslij do akceptacji");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert
            .assertTrue(selenium
                .isTextPresent("1 do wiadomosci ngo.pl, 2 na główną serwisu wiadomosci, Aktywne społeczności CAL, Białoruś, dyskryminacja, dzieci, Warszawa - na główną"));
        selenium.click("link=Wymuś publikację");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
    }

}
