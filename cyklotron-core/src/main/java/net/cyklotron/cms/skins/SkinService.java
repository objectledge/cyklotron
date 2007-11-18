package net.cyklotron.cms.skins;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;

import net.cyklotron.cms.site.SiteResource;

/**
 * Provides skinning funcitonality.
 */
public interface SkinService
{
    // constatns /////////////////////////////////////////////////////////////
    
    public static final String SERVICE_NAME = "cms_skins";

    public static final String LOGGING_FACILITY = "cms_skins";

    public static final String PREVIEW_KEY_PREFIX = "net.cyklotron.cms.skins.";

    // skins /////////////////////////////////////////////////////////////////

    /**
     * Returns the currently selected skin for a site.
     */
    public String getCurrentSkin(CoralSession coralSession, SiteResource site)
        throws SkinException;

    /**
     * Selects a skin for a site.
     */
    public void setCurrentSkin(CoralSession coralSession, SiteResource site, String skin)
        throws SkinException;

    /**
     * Checks if the site has a skin with the given name.
     */
    public boolean hasSkin(CoralSession coralSession, SiteResource site, String skin)
        throws SkinException;

    /**
     * Returns a skin descriptor object.
     */
    public SkinResource getSkin(CoralSession coralSession, SiteResource site, String skin)
        throws SkinException;

    /**
     * Returns skins available for a given site.
     */
    public SkinResource[] getSkins(CoralSession coralSession, SiteResource site)
        throws SkinException;

    /**
     * Creates a new skin
     * 
     * @param site the site to create skin for.
     * @param name new skin's name.
     * @param source the skin to copy (possibly from another site), or null.
     * @return newly created skin.
     * @throws SkinException if site by the requested name exists, or the operation
     * otherwise fails.
     */
    public SkinResource createSkin(CoralSession coralSession, SiteResource site, String name, 
        SkinResource source)
        throws SkinException;

    /**
     * Renames a skin.
     * 
     * <p>If the skin is currently enabled for the site, the setting is updated
     * accordingly.</p>
     * 
     * @param skin the skin to rename.
     * @param name the new name.
     * @throws SkinException if the skin has a sibling skin with a given name
     * or the opeartion otheriwse fails.
     */
    public void renameSkin(CoralSession coralSession, SkinResource skin, String name)
        throws SkinException;
        
    /**
     * Deletes a skin.
     * 
     * @param skin the skin to delete.
     * @throws SkinException if the skin is currently enabled, or the operation
     * otherwise fails.
     */    
    public void deleteSkin(CoralSession coralSession, SkinResource skin)
        throws SkinException;    

    // layouts ///////////////////////////////////////////////////////////////

    /**
     * Returns layouts defined by the skin.
     */
    public LayoutResource[] getLayouts(CoralSession coralSession, SiteResource site, String skin)
        throws SkinException;

    /**
     * Checks if the skin defines a layout wiht the given name.
     */
    public boolean hasLayoutTemplate(CoralSession coralSession, SiteResource site, String skin, String name)
        throws SkinException;

    /**
     * Returns a layout template provided by the skin.
     */
    public Template getLayoutTemplate(CoralSession coralSession, SiteResource site, String skin, String name)
        throws TemplateNotFoundException, SkinException;                                      

    // components ////////////////////////////////////////////////////////////
    
    /**
     * Returns visual variants available for a component.
     */
    public ComponentVariantResource[] getComponentVariants(CoralSession coralSession, SiteResource site, String skin,
                                                           String app, String component)
        throws SkinException;

    /**
     * Returns visual variant of a component.
     */
    public ComponentVariantResource getComponentVariant(CoralSession coralSession, SiteResource site, String skin,
                                                         String app, String component, String variant)
        throws SkinException;


    /**
     * Checks if the skin defines component variant with the given name.
     */
    public boolean hasComponentVariant(CoralSession coralSession, SiteResource site, String skin,
                                       String app, String component, String variant)
        throws SkinException;
        
    /**
     * Creates a component variant. 
     * 
     * @param site the site.
     * @param skin the skin.
     * @param app the application.
     * @param component the component.
     * @param variant new varaint name.
     * @param subject the subject that performs the operation.
     * @throws SkinException if the operation fails.
     * @throws InvalidResourceNameException if the app, component or variant arguments contain
     * invalid characters.
     */
    public ComponentVariantResource createComponentVariant(CoralSession coralSession, SiteResource site, 
        String skin, String app, String component, String variant, Subject subject)
        throws SkinException, InvalidResourceNameException;

    /**
     * Deletes a component variant;
     * 
     * @param site the site.
     * @param skin the skin
     * @param app the application name.
     * @param component the component name.
     * @param variant the variant name.
     */
    public void deleteComponentVariant(CoralSession coralSession, SiteResource site, String skin, 
        String app, String component, String variant)
        throws SkinException;
      
    /**
     * Return a component variant template provided by the skin.
     */
    public Template getComponentTemplate(CoralSession coralSession, SiteResource site, String skin,
                                         String app, String component, 
                                         String variant, String state)
        throws TemplateNotFoundException, SkinException;  

    /**
     * Checks if a component template provided by the skin.
     */
    public boolean hasComponentTemplate(CoralSession coralSession, SiteResource site, String skin,
                                         String app, String component, 
                                         String variant, String state)
        throws SkinException;  

    // screens ///////////////////////////////////////////////////////////////

    /**
     * Returns visual variants available for a screen.
     */
    public ScreenVariantResource[] getScreenVariants(CoralSession coralSession, SiteResource site, String skin,
                                                     String app, String screen)
        throws SkinException;

    /**
     * Checks if the skin defines screen variant with the given name.
     */
    public boolean hasScreenVariant(CoralSession coralSession, SiteResource site, String skin,
                                    String app, String screen, String variant)
        throws SkinException;

    /**
     * Returns a list of Locale objects indicating locales in which the 
     * application provides screen templates.
     * 
     * @param app application 
     * @param screen screen name 
     * @param state state 
     * @return list of Locale objects
     * @throws SkinException
     */
    public List getScreenTemplateLocales(String app, String screen, String state)
        throws SkinException;    

    /**
     * Returns the screen template file size. 
     *  
     * @param site the site.
     * @param skin the skin.
     * @param app the application where screen belongs.
     * @param screen the screen.
     * @param variant the screen variant.
     * @param state the screen state
     * @return the template contents.
     * @throws SkinException if the operation fails.
     */
    public long getScreenTemplateLength(SiteResource site, String skin, 
        String app,  String screen, String variant, String state)
        throws SkinException;

    /**
     * Writes the contents of the screen variant templates into an ouptut
     * stream. 
     *  
     * @param site the site.
     * @param skin the skin.
     * @param app the application where screen belongs.
     * @param screen the screen.
     * @param variant the screen variant.
     * @param state the screen state
     * @throws SkinException if the operation fails.
     */
    public void getScreenTemplateContents(SiteResource site, String skin, 
        String app,  String screen, String variant, String state,
        OutputStream out)
        throws SkinException;

    /**
     * Returns the contents of screen template as provided by an application.
     *  
     * @param app application 
     * @param screen screen name 
     * @param state state 
     * @param locale requested locale
     * @return contents of the template.
     * @throws SkinException if the operation fails.
     */     
    public String getScreenTemplateContents(String app, String screen, String state, Locale locale)
        throws SkinException;

    /**
     * Returns visual variant of a screen.
     */
    public ScreenVariantResource getScreenVariant(CoralSession coralSession, SiteResource site, String skin,
                                                         String app, String screen, String variant)
        throws SkinException;

    /**
     * Return a screen variant template provided by the skin.
     */
    public Template getScreenTemplate(CoralSession coralSession, SiteResource site, String skin,
                                      String app, String screen, 
                                      String variant, String state)
        throws TemplateNotFoundException, SkinException;

    /**
     * Checks if a screen template provided by the skin.
     */
    public boolean hasScreenTemplate(CoralSession coralSession, SiteResource site, String skin,
                                     String app, String component, 
                                     String variant, String state)
        throws SkinException;  

    // management ////////////////////////////////////////////////////////////

    /**
     * Lists the directories in the skins's static content located in the
     * directory at the given path.
     */
    public String[] getContentDirectoryNames(SiteResource site, String skin, 
                                             String path)
        throws SkinException;
    
    /**
     * Lists the files in the skins's static content located in the
     * directory at the given path.
     */
    public String[] getContentFileNames(SiteResource site, String skin, 
                                        String path)
        throws SkinException;

    /**
     * Checks it a content item (file or directory) exists.
     * 
     * @param site the site.
     * @param skin the skin.
     * @param path item path.
     * @return troe if item exists.
     * @throws SkinException
     */
    public boolean contentItemExists(SiteResource site, String skin, String path)
        throws SkinException;
    
    // Skin editing /////////////////////////////////////////////////////////

	/**
	 * Create a new layout template in the skin.
	 * 
	 * @param site the site.
	 * @param skin the skin.
	 * @param layout the layout.
	 * @param contents the contents of the layout template.
	 * @throws SkinException if the operation fails.
	 */
    public void createLayoutTemplate(CoralSession coralSession, SiteResource site, String skin, String layout, 
    	String contents, Subject subject)
    	throws SkinException;
	
	/**
	 * Removes a layout template from the skin.
	 * 
	 * @param site the site.
	 * @param skin the skin.
	 * @param layout the layout.
	 * @throws SkinException if the operation fails.
	 */
    public void deleteLayoutTemplate(CoralSession coralSession, SiteResource site, String skin, String layout)
    	throws SkinException;
    	
    /**
     * Returns the contents of a layout template
     *  
	 * @param site the site.
	 * @param skin the skin.
	 * @param layout the layout.
     * @return the contents of the template.
	 * @throws SkinException if the operation fails.
     */
    public String getLayoutTemplateContents(SiteResource site, String skin, 
    	String layout)
    	throws SkinException;

    /**
     * Writes the contents af a layout template into a stream.
     *  
     * @param site the site.
     * @param skin the skin.
     * @param layout the layout.
     * @param out the stream to write contents to.
     * @throws SkinException if the operation fails.
     */
    public void getLayoutTemplateContents(SiteResource site, String skin, 
        String layout, OutputStream out)
        throws SkinException;

    /**
     * Return layout template file size.
     * 
     * @param site the site.
     * @param skin the skin.
     * @param path the content file path.
     */
    public long getLayoutTemplateLength(SiteResource site, String skin, String path)
        throws SkinException;
    
    /**
     * Changes the contents of a layout template.
     *  
	 * @param site the site.
	 * @param skin the skin.
	 * @param layout the layout.
	 * @param contents the contents of the layout template.
	 * @throws SkinException if the operation fails.
     */
    public void setLayoutTemplateContents(SiteResource site, String skin, 
    	String layout, String contents)
    	throws SkinException;        

    /**
     * Return the filename applicable for a component/screen template 
     * @param variant
     * @param state
     */
    public String getTemplateFilename(String item, String state, String variant);

	/**
	 * Create a component variant template.
	 * 
	 * @param site the site.
	 * @param skin the skin.
	 * @param app the application where component belongs.
	 * @param component the component.
	 * @param variant the component variant.
     * @param state the component state
	 * @param contents the template contents.
	 * @throws SkinException if the operation fails.
	 */
    public void createComponentTemplate(CoralSession coralSession, SiteResource site, String skin, 
    	String app, String component, String variant, String state,
        String contents)
    	throws SkinException;
    	
    /**
     * Deletes a component variant template from the skin.
     * 
	 * @param site the site.
	 * @param skin the skin.
	 * @param app the application where component belongs.
	 * @param component the component.
	 * @param variant the component variant.
     * @param state the component state
	 * @throws SkinException if the operation fails.
     */
    public void deleteComponentTemplate(CoralSession coralSession, SiteResource site, String skin, 
    	String app,	String component, String variant, String state)
    	throws SkinException;
    
    /**
     * Retrieves the contents of the component variant templates. 
     *  
	 * @param site the site.
	 * @param skin the skin.
	 * @param app the application where component belongs.
	 * @param component the component.
	 * @param variant the component variant.
     * @param state the component state
     * @return the template contents.
	 * @throws SkinException if the operation fails.
     */
    public String getComponentTemplateContents(SiteResource site, String skin, 
    	String app,  String component, String variant, String state)
    	throws SkinException;

    /**
     * Writes the contents of the component variant templates into an ouptut
     * stream. 
     *  
     * @param site the site.
     * @param skin the skin.
     * @param app the application where component belongs.
     * @param component the component.
     * @param variant the component variant.
     * @param state the component state
     * @throws SkinException if the operation fails.
     */
    public void getComponentTemplateContents(SiteResource site, String skin, 
        String app,  String component, String variant, String state,
        OutputStream out)
        throws SkinException;

    /**
     * Returns a list of Locale objects indicating locales in which the 
     * application provides component templates.
     * 
     * @param app application 
     * @param component component name 
     * @param state state 
     * @return list of Locale objects
     * @throws SkinException
     */
    public List getComponentTemplateLocales(String app, String component, String state)
        throws SkinException;
    
    /**
     * Returns the contents of component template as provided by an application.
     *  
     * @param app application 
     * @param component component name 
     * @param state state 
     * @param locale requested locale
     * @return contents of the template.
     * @throws SkinException if the operation fails.
     */   	
    public String getComponentTemplateContents(String app, String component, String state, Locale locale)
        throws SkinException;
 
    /**
     * Returns the component template file size. 
     *  
     * @param site the site.
     * @param skin the skin.
     * @param app the application where component belongs.
     * @param component the component.
     * @param variant the component variant.
     * @param state the component state
     * @return the template contents.
     * @throws SkinException if the operation fails.
     */
    public long getComponentTemplateLength(SiteResource site, String skin, 
        String app,  String component, String variant, String state)
        throws SkinException;

    /**
     * Modifies the contents of the component variant template.
     * 
	 * @param site the site.
	 * @param skin the skin.
	 * @param app the application where component belongs.
	 * @param component the component.
	 * @param variant the component variant.
     * @param state the component state
     * @param contents the conents of the template.
     * @throws SkinException
     */
    public void setComponentTemplateContents(CoralSession coralSession, SiteResource site, String skin, 
    	String app, String component, String variant, String state,
        String contents)
    	throws SkinException;

    /**
     * Creates a screen variant. 
     * 
     * @param site the site.
     * @param skin the skin.
     * @param app the application.
     * @param screen the screen.
     * @param variant new varaint name.
     * @throws SkinException if the operation fails.
     * @throws InvalidResourceNameException if the app, component or variant arguments contain
     * invalid characters.
     */
    public ScreenVariantResource createScreenVariant(CoralSession coralSession, SiteResource site, 
        String skin, String app, String screen, String variant)
        throws SkinException, InvalidResourceNameException;

	/**
	 * Create a screen variant template.
	 * 
	 * @param site the site.
	 * @param skin the skin.
	 * @param app the application where screen belongs.
	 * @param screen the screen.
	 * @param variant the screen variant.
     * @param state the component state
	 * @param contents the template contents.
	 * @throws SkinException if the operation fails.
	 */
	public void createScreenTemplate(CoralSession coralSession, SiteResource site, String skin, 
		String app, String screen, String variant, String state, 
        String contents)
		throws SkinException;

    /**
     * Deletes a screen variant;
     * 
     * @param site the site.
     * @param skin the skin
     * @param app the application name.
     * @param screen the screen name.
     * @param variant the variant name.
     */
    public void deleteScreenVariant(CoralSession coralSession, SiteResource site, String skin, 
        String app, String screen, String variant)
        throws SkinException;
    	
	/**
	 * Deletes a screen variant template from the skin.
	 * 
	 * @param site the site.
	 * @param skin the skin.
	 * @param app the application where screen belongs.
	 * @param screen the screen.
	 * @param variant the screen variant.
     * @param state the component state
	 * @throws SkinException if the operation fails.
	 */
	public void deleteScreenTemplate(CoralSession coralSession, SiteResource site, String skin, 
		String app,	String screen, String variant, String state)
		throws SkinException;
    
	/**
	 * Retrieves the contents of the screen variant templates. 
	 *  
	 * @param site the site.
	 * @param skin the skin.
	 * @param app the application where screen belongs.
	 * @param screen the screen.
	 * @param variant the screen variant.
     * @param state the component state
	 * @return the template contents.
	 * @throws SkinException if the operation fails.
	 */
	public String getScreenTemplateContents(SiteResource site, String skin, 
		String app,  String screen, String variant, String state)
		throws SkinException;
    	
	/**
	 * Modifies the contents of the screen variant template.
	 * 
	 * @param site the site.
	 * @param skin the skin.
	 * @param app the application where screen belongs.
	 * @param screen the screen.
	 * @param variant the screen variant.
     * @param state the component state
	 * @param contents the conents of the template.
	 * @throws SkinException
	 */
	public void setScreenTemplateContents(CoralSession coralSession, SiteResource site, String skin, 
		String app, String screen, String variant, String state, 
        String contents)
		throws SkinException;
    
    /**
     * Return static content file MIME type.
     * 
     * @param site the site.
     * @param skin the skin.
     * @param path the content file path.
     */
    public String getContentFileType(SiteResource site, String skin, String path)
        throws SkinException;

    /**
     * Return static content file size.
     * 
     * @param site the site.
     * @param skin the skin.
     * @param path the content file path.
     */
    public long getContentFileLength(SiteResource site, String skin, String path)
        throws SkinException;
    
    /**
     * Create a static content file.
     * 
     * @param site the site.
     * @param skin the skin.
     * @param path the content file path.
     * @param data the stream to load data from.
     */
    public void createContentFile(SiteResource site, String skin, String path, 
        InputStream data)
        throws SkinException;

    /**
     * Writes static content file contents into an OutputStream.
     * 
     * @param site the site.
     * @param skin she skin.
     * @param path content file path.
     * @param out the stream to write data to.
     */
    public void getContentFileContents(SiteResource site, String skin, 
        String path, OutputStream out)
        throws SkinException;
        
    /**
     * Returns the contents of a content file as a string in the specified
     * encoding.
     * 
     * @param site the site.
     * @param skin she skin.
     * @param path content file path.
     * @param encoding requested character encoding.
     */
    public String getContentFileContents(SiteResource site, String skin, 
        String path, String encoding)
        throws SkinException;
        
    /**
     * Reads static file contents from an InputStream.
     *  
     * @param site the site.
     * @param skin she skin.
     * @param path content file path.
     * @param in the stream to load data from.
     */
    public void setContentFileContents(SiteResource site, String skin, 
        String path, InputStream in)
        throws SkinException;

    /**
     * Reads static file contents from an InputStream.
     *  
     * @param site the site.
     * @param skin she skin.
     * @param path content file path.
     * @param contents the new contents.      
     * @param encoding requested character encoding.
     */
    public void setContentFileContents(SiteResource site, String skin, 
        String path, String contents, String encoding)
        throws SkinException;
    
    /**
     * Deletes a static content file.
     * 
     * @param site the site.
     * @param skin she skin.
     * @param path content file path.
     */    
    public void deleteContentFile(SiteResource site, String skin, String path)
        throws SkinException;

    /**
     * Create a static content directory.
     * 
     * @param site the site.
     * @param skin she skin.
     * @param path content directory path.
     */
    public void createContentDirectory(SiteResource site, String skin, 
        String path)
        throws SkinException;
        
    /**
     * Delete a static content directory.
     * 
     * @param site the site.
     * @param skin she skin.
     * @param path content directory path.
     */
    public void deleteContentDirectory(SiteResource site, String skin, 
        String path)
        throws SkinException;
}
