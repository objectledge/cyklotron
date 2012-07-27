package net.cyklotron.ngo.it;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.thoughtworks.selenium.Selenium;


public abstract class SeleniumTest
{
 
    protected static String DEFAULT_TEST_SPEED = "1000";
    
    protected abstract String startPage();
    
    protected Selenium selenium;

    @Before
    public void before()
        throws Exception
    {
        WebDriver driver = new FirefoxDriver();
        selenium = new WebDriverBackedSelenium(driver, startPage());
        selenium.setSpeed(DEFAULT_TEST_SPEED);
    }
    
    @After
    public void after()
        throws Exception
    {
        selenium.stop();
    }
}
