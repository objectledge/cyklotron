// 
// Copyright (c) 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
// 
// * Redistributions of source code must retain the above copyright notice,  
//       this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//       this list of conditions and the following disclaimer in the documentation  
//       and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//       nor the names of its contributors may be used to endorse or promote products  
//       derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
// 
 
package net.cyklotron.cms;


/**
 * Provide all of the constant definition
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: CmsConstants.java,v 1.2 2005-01-19 08:24:50 pablo Exp $
 */
public interface CmsConstants
{
    /** state - component configuration/administer */
    public static final String FROM_COMPONENT = "cms_from_component";

    /** component instance */
    public static final String COMPONENT_INSTANCE = "cms_component_instance";

    /** component node */
    public static final String COMPONENT_NODE = "cms_component_node";

    // constants
    public static String CMS_DATA_KEY = "cms_data";

    public static String BROWSE_MODES_KEY = "cms_browse_modes";

    public static String BROWSE_MODE_ADMINISTER = "administer";
    
    public static String BROWSE_MODE_BROWSE = "browse";
    
    public static String BROWSE_MODE_EDIT = "edit"; 

    public static String BROWSE_MODE_EMERGENCY = "emergency"; 

    public static String BROWSE_MODE_IMPORT = "import"; 

    public static String BROWSE_MODE_EXPORT = "export"; 

    public static String CMS_DATE_KEY = "cms_date";

    //TODO maybe we should move it to TableConstants
    /** Defines list type of view. */
    public final static int VIEW_AS_LIST = 0;

    /** Defines tree or forest type of view. */
    public final static int VIEW_AS_TREE = 1;

    /** Defines ascending sorting direction. */
    public final static int SORT_ASC = 0;

    /** Defines descending sorting direction. */
    public final static int SORT_DESC = 1;

}
