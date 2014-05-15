// 
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
//   
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
//   
//* Redistributions of source code must retain the above copyright notice,  
//this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
//this list of conditions and the following disclaimer in the documentation  
//and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//nor the names of its contributors may be used to endorse or promote products  
//derived from this software without specific prior written permission. 
// 
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
//POSSIBILITY OF SUCH DAMAGE. 
//

package net.cyklotron.cms.modules.views.documents;

import java.util.List;
import java.util.Locale;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.workflow.WorkflowException;

import org.objectledge.coral.entity.AmbigousEntityNameException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.relation.MalformedRelationQueryException;
import org.objectledge.parameters.Parameters;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;

import bak.pcj.set.LongSet;

/**
 * The interface to indicate MyDocuments component implementation.
 */
public interface MyDocuments
{
    /**
     * @param excluded
     * @return
     * @throws EntityDoesNotExistException
     * @throws AmbigousEntityNameException
     * @throws WorkflowException
     */
    public List<TableFilter<? super DocumentNodeResource>> excludeStatesFilter(String... excluded)
        throws EntityDoesNotExistException, AmbigousEntityNameException, WorkflowException;

    /**
     * @param cmsData
     * @param locale
     * @param whereClause
     * @return
     * @throws MalformedQueryException
     * @throws TableException
     */
    public TableModel<DocumentNodeResource> siteBasedModel(CmsData cmsData, Locale locale,
        String whereClause)
        throws MalformedQueryException, TableException;

    /**
     * @param includeQuery
     * @param excludeQuery
     * @param cmsData
     * @param locale
     * @param whereClause
     * @return
     * @throws SiteException
     * @throws MalformedQueryException
     * @throws MalformedRelationQueryException
     * @throws EntityDoesNotExistException
     * @throws TableException
     */
    public TableModel<DocumentNodeResource> queryBasedModel(CategoryQueryResource includeQuery,
        CategoryQueryResource excludeQuery, CmsData cmsData, Locale locale, String whereClause)
        throws SiteException, MalformedQueryException, MalformedRelationQueryException,
        EntityDoesNotExistException, TableException;

    /**
     * @param queryPool
     * @param whereClause
     * @return
     * @throws SiteException
     * @throws MalformedQueryException
     * @throws MalformedRelationQueryException
     * @throws EntityDoesNotExistException
     * @throws TableException
     */
    public LongSet queryPoolBasedSet(CategoryQueryPoolResource queryPool, String whereClause)
        throws SiteException, MalformedQueryException, MalformedRelationQueryException,
        EntityDoesNotExistException, TableException;

    /**
     * @param name
     * @param config
     * @return
     */
    public CategoryQueryResource getQueryResource(String name, Parameters config);
}
