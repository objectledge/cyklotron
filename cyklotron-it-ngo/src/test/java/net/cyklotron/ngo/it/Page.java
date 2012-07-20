package net.cyklotron.ngo.it;

import com.thoughtworks.selenium.Selenium;

public abstract class Page {
	
    protected Selenium selenium;
    
    public Page(Selenium selenium)  { 
        
        this.selenium = selenium;
    
    }

}
