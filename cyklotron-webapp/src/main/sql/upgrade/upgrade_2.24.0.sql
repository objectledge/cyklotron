-- 
-- Copyright (c) 2012, Caltha - Krzewski, Mach, Potempski Sp.J. 
-- All rights reserved. 
-- 
-- Redistribution and use in source and binary forms, with or without modification,  
-- are permitted provided that the following conditions are met: 
-- 
-- * Redistributions of source code must retain the above copyright notice,  
--   this list of conditions and the following disclaimer. 
-- * Redistributions in binary form must reproduce the above copyright notice,  
--   this list of conditions and the following disclaimer in the documentation  
--   and/or other materials provided with the distribution. 
-- * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
--   nor the names of its contributors may be used to endorse or promote products  
--   derived from this software without specific prior written permission. 
-- 
-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
-- AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
-- WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
-- IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
-- INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
-- BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
-- OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
-- WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
-- ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
-- POSSIBILITY OF SUCH DAMAGE. 
-- 

--
-- Coral 1.0.16
--

update coral_resource_class 
set db_table_name = 'coral_node' 
where resource_class_id = 1;

delete from coral_attribute_definition 
where resource_class_id = 2;

delete from coral_resource_class 
where resource_class_id = 2;

update coral_resource_class 
set db_table_name = replace(name, '.', '_') 
where db_table_name is null and name ~ '^cms';

update coral_resource_class 
set db_table_name = 'cms_' || replace(name, '.', '_') 
where db_table_name is null and not name ~ '^cms';

