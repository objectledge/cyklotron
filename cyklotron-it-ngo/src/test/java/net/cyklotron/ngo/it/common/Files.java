package net.cyklotron.ngo.it.common;

import junit.framework.Assert;

import net.cyklotron.ngo.it.Page;

import com.thoughtworks.selenium.Selenium;

public class Files
    extends Page
{

    public Files(Selenium selenium)
    {
        super(selenium);
    }

    /**
     * Init files configuration
     * 
     * @param name
     */
    public void initConfiguration()
    {
        selenium.open("/view/site.EditSite?site_id=8439");
        Assert.assertTrue(selenium.isElementPresent("link=PLIKI"));
        selenium.click("link=PLIKI");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert
            .assertTrue(selenium
                .isElementPresent("//a[contains(@href, '/view/files.EditConfiguration?site_id=8439')]"));
        selenium.click("//a[contains(@href, '/view/files.EditConfiguration?site_id=8439')]");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium.isTextPresent("Konfiguracja aplikacji - Pliki"));
        selenium.click("link=Wybierz");
        selenium.waitForPopUp("Directory", DEFAULT_PAGE_LOAD_TIME);
        selenium.selectWindow("name=Directory");
        Assert.assertTrue(selenium.isElementPresent("link=public"));
        selenium.click("link=public");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        selenium.click("//SPAN[@class='table-pagesize-chooser']/INPUT[contains(@id, 'size0')]");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        String filespublic = "//a[contains(@href, ##javascript:select('/public/filespublic','428790')##)]"
            .replaceAll("##", "\"");
        Assert.assertTrue(selenium.isElementPresent(filespublic));
        selenium.click(filespublic);
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        selenium.selectWindow("null");
        Assert.assertEquals("/public/filespublic", selenium.getValue("name=expanded_directory"));
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        selenium.selectWindow("null");
        selenium.click("xpath=(//a[contains(text(),'Wybierz')])[2]");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        selenium.selectWindow("name=ChooseCategory");
        Assert.assertTrue(selenium.isElementPresent("link=Rozwiń wszystko"));
        selenium.click("link=Rozwiń wszystko");
        selenium.click("id=category-670506");
        selenium.click("id=category-670507");
        Assert.assertTrue(selenium.isElementPresent("link=Zapisz"));
        selenium.click("link=Zapisz");
        selenium.selectWindow("null");
        Assert.assertEquals("logo, pionowe", selenium.getValue("name=front_categories"));
        Assert.assertTrue(selenium.isElementPresent("link=Zapisz"));
        selenium.click("link=Zapisz");
    }

    /**
     * Clear files configuration
     */
    public void clearConfiguration()
    {
        selenium.open("/view/site.EditSite?site_id=8439");
        Assert.assertTrue(selenium.isElementPresent("link=PLIKI"));
        selenium.click("link=PLIKI");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert
            .assertTrue(selenium
                .isElementPresent("//a[contains(@href, '/view/files.EditConfiguration?site_id=8439')]"));
        selenium.click("//a[contains(@href, '/view/files.EditConfiguration?site_id=8439')]");
        selenium.waitForPageToLoad(DEFAULT_PAGE_LOAD_TIME);
        Assert.assertTrue(selenium.isElementPresent("xpath=(//a[contains(text(),'Wyczyść')])[1]"));
        selenium.click("xpath=(//a[contains(text(),'Wyczyść')])[1]");
        Assert.assertTrue(selenium.isElementPresent("xpath=(//a[contains(text(),'Wyczyść')])[2]"));
        selenium.click("xpath=(//a[contains(text(),'Wyczyść')])[2]");
        Assert.assertTrue(selenium.isElementPresent("link=Zapisz"));
        selenium.click("link=Zapisz");
    }
}
