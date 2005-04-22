//Copyright (c) 2003, 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
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
package net.cyklotron.cms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.objectledge.parameters.AmbiguousParameterException;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.ScopedParameters;

/**
* An implementation of parameters decorator class to hide multiple values of parameter.
*
* @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
* @version $Id: SingleValueParameters.java,v 1.1 2005-04-22 03:48:23 pablo Exp $
*/
public class SingleValueParameters implements Parameters
{
  /** the base parameters */ 
  private Parameters parameters;
  
  /**
   * Create the container returning single value. 
   * 
   * @param parameters the container to decorate.
   * @param prefix the scope prefix.
   */
  public SingleValueParameters(Parameters parameters)
  {
      this.parameters = parameters;
  }
  
  /**
   * {@inheritDoc}
   */
  public void add(Parameters parameters, boolean overwrite)
  {
      String[] names = parameters.getParameterNames();
      for (int i = 0; i < names.length; i++)
      {
          String[] values = parameters.getStrings(names[i]);
          if (values != null)
          {
              if (overwrite)
              {
                  set(names[i], values);
              }
              else
              {
                  add(names[i], values);
              }
          }
      }
  }
  
  /**
   * {@inheritDoc}
   */
  public void add(String name, boolean value)
  {
      parameters.add(name, value);
  }
  
  /**
   * {@inheritDoc}
   */
  public void add(String name, boolean[] values)
  {
      parameters.add(name, values);
  }

  /**
   * {@inheritDoc}
   */
  public void add(String name, Date value)
  {
      parameters.add(name, value);
  }

  /**
   * {@inheritDoc}
   */
  public void add(String name, Date[] values)
  {
      parameters.add(name, values);
  }
  
  /**
   * {@inheritDoc}
   */
  public void add(String name, float value)
  {
      parameters.add(name, value);
  }
  
  /**
   * {@inheritDoc}
   */
  public void add(String name, float[] values)
  {
      parameters.add(name, values);
  }
  
  /**
   * {@inheritDoc}
   */
  public void add(String name, int value)
  {
      parameters.add(name, value);
  }
  
  /**
   * {@inheritDoc}
   */
  public void add(String name, int[] values)
  {
      parameters.add(name, values);
  }
  
  /**
   * {@inheritDoc}
   */
  public void add(String name, long value)
  {
      parameters.add(name, value);
  }
  
  /**
   * {@inheritDoc}
   */
  public void add(String name, long[] values)
  {
      parameters.add(name, values);
  }
  
  /**
   * {@inheritDoc}
   */
  public void add(String name, String value)
  {
      parameters.add(name, value);
  }
  
  /**
   * {@inheritDoc}
   */
  public void add(String name, String[] values)
  {
      parameters.add(name, values);
  }
  
  /**
   * {@inheritDoc}
   */
  public String get(String name, String defaultValue)
  {
      try
      {
          return parameters.get(name, defaultValue);
      }
      catch(AmbiguousParameterException e)
      {
          String[] values = parameters.getStrings(name);
          ArrayList<String> list = new ArrayList<String>();
          for(String v:values)
          {
              list.add(v);
          }
          Collections.sort(list);
          return list.get(0);
      }
  }
  
  /**
   * {@inheritDoc}
   */
  public String get(String name)
  {
      try
      {
          return parameters.get(name);
      }
      catch(AmbiguousParameterException e)
      {
          String[] values = parameters.getStrings(name);
          ArrayList<String> list = new ArrayList<String>();
          for(String v:values)
          {
              list.add(v);
          }
          Collections.sort(list);
          return list.get(0);
      }
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean getBoolean(String name, boolean defaultValue)
  {
      try
      {
          return parameters.getBoolean(name, defaultValue);
      }
      catch(AmbiguousParameterException e)
      {
          boolean[] values = parameters.getBooleans(name);
          ArrayList<Boolean> list = new ArrayList<Boolean>();
          for(boolean v:values)
          {
              list.add(new Boolean(v));
          }
          Collections.sort(list);
          return list.get(0).booleanValue();
      }
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean getBoolean(String name)
  {
      try
      {
          return parameters.getBoolean(name);
      }
      catch(AmbiguousParameterException e)
      {
          boolean[] values = parameters.getBooleans(name);
          ArrayList<Boolean> list = new ArrayList<Boolean>();
          for(boolean v:values)
          {
              list.add(new Boolean(v));
          }
          Collections.sort(list);
          return list.get(0).booleanValue();
      }
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean[] getBooleans(String name)
  {
      return parameters.getBooleans(name);
  }
  
  /**
   * {@inheritDoc}
   */
  public Parameters getChild(String prefix)
  {
      return new ScopedParameters(this, prefix);
  }
  
  /**
   * {@inheritDoc}
   */
  public Date getDate(String name)
  {
      try
      {
          return parameters.getDate(name);
      }
      catch(AmbiguousParameterException e)
      {
          Date[] values = parameters.getDates(name);
          ArrayList<Date> list = new ArrayList<Date>();
          for(Date v:values)
          {
              list.add(v);
          }
          Collections.sort(list);
          return list.get(0);
      }
  }

  /**
   * {@inheritDoc}
   */
  public Date getDate(String name, Date defaultValue)
  {
      try
      {
          return parameters.getDate(name, defaultValue);
      }
      catch(AmbiguousParameterException e)
      {
          Date[] values = parameters.getDates(name);
          ArrayList<Date> list = new ArrayList<Date>();
          for(Date v:values)
          {
              list.add(v);
          }
          Collections.sort(list);
          return list.get(0);
      }
  }

  /**
   * {@inheritDoc}
   */
  public Date[] getDates(String name)
  {
      return parameters.getDates(name);
  }
  
  /**
   * {@inheritDoc}
   */
  public float getFloat(String name, float defaultValue)
  {
      try
      {
          return parameters.getFloat(name, defaultValue);
      }
      catch(AmbiguousParameterException e)
      {
          float[] values = parameters.getFloats(name);
          ArrayList<Float> list = new ArrayList<Float>();
          for(float v:values)
          {
              list.add(new Float(v));
          }
          Collections.sort(list);
          return list.get(0).floatValue();
      }
  }
  
  /**
   * {@inheritDoc}
   */
  public float getFloat(String name) throws NumberFormatException
  {
      try
      {
          return parameters.getFloat(name);
      }
      catch(AmbiguousParameterException e)
      {
          float[] values = parameters.getFloats(name);
          ArrayList<Float> list = new ArrayList<Float>();
          for(float v:values)
          {
              list.add(new Float(v));
          }
          Collections.sort(list);
          return list.get(0).floatValue();
      }
  }
  
  /**
   * {@inheritDoc}
   */
  public float[] getFloats(String name) throws NumberFormatException
  {
      return parameters.getFloats(name);
  }
  
  /**
   * {@inheritDoc}
   */
  public int getInt(String name, int defaultValue)
  {
      try
      {
          return parameters.getInt(name, defaultValue);
      }
      catch(AmbiguousParameterException e)
      {
          int[] values = parameters.getInts(name);
          ArrayList<Integer> list = new ArrayList<Integer>();
          for(int v:values)
          {
              list.add(new Integer(v));
          }
          Collections.sort(list);
          return list.get(0).intValue();
      }  }
  
  /**
   * {@inheritDoc}
   */
  public int getInt(String name) throws NumberFormatException
  {
      try
      {
          return parameters.getInt(name);
      }
      catch(AmbiguousParameterException e)
      {
          int[] values = parameters.getInts(name);
          ArrayList<Integer> list = new ArrayList<Integer>();
          for(int v:values)
          {
              list.add(new Integer(v));
          }
          Collections.sort(list);
          return list.get(0).intValue();
      }
  }
  
  /**
   * {@inheritDoc}
   */
  public int[] getInts(String name) throws NumberFormatException
  {
      return parameters.getInts(name);
  }
  
  /**
   * {@inheritDoc}
   */
  public long getLong(String name, long defaultValue)
  {
      try
      {
          return parameters.getLong(name, defaultValue);
      }
      catch(AmbiguousParameterException e)
      {
          long[] values = parameters.getLongs(name);
          ArrayList<Long> list = new ArrayList<Long>();
          for(long v:values)
          {
              list.add(new Long(v));
          }
          Collections.sort(list);
          return list.get(0).longValue();
      }
  }
  
  /**
   * {@inheritDoc}
   */
  public long getLong(String name) throws NumberFormatException
  {
      try
      {
          return parameters.getLong(name);
      }
      catch(AmbiguousParameterException e)
      {
          long[] values = parameters.getLongs(name);
          ArrayList<Long> list = new ArrayList<Long>();
          for(long v:values)
          {
              list.add(new Long(v));
          }
          Collections.sort(list);
          return list.get(0).longValue();
      }
  }
  
  /**
   * {@inheritDoc}
   */
  public long[] getLongs(String name) throws NumberFormatException
  {
      return parameters.getLongs(name);
  }
  
  /**
   * {@inheritDoc}
   */
  public String[] getParameterNames()
  {
      return parameters.getParameterNames();
  }
  
  /**
   * {@inheritDoc}
   */
  public String[] getStrings(String name)
  {
      return parameters.getStrings(name);
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean isDefined(String name)
  {
      return parameters.isDefined(name);
  }
  
  /**
   * {@inheritDoc}
   */
  public void remove()
  {
      parameters.remove();
  }
  
  /**
   * {@inheritDoc}
   */
  public void remove(Set<String> keys)
  {
      parameters.remove(keys);
  }
  
  /**
   * {@inheritDoc}
   */
  public void remove(String name, Date value)
  {
      parameters.remove(name, value);
  }

  /**
   * {@inheritDoc}
   */
  public void remove(String name, float value)
  {
      parameters.remove(name, value);
  }
  
  /**
   * {@inheritDoc}
   */
  public void remove(String name, int value)
  {
      parameters.remove(name, value);
  }
  
  /**
   * {@inheritDoc}
   */
  public void remove(String name, long value)
  {
      parameters.remove(name, value);
  }
  
  /**
   * {@inheritDoc}
   */
  public void remove(String name, String value)
  {
      parameters.remove(name, value);
  }
  
  /**
   * {@inheritDoc}
   */
  public void remove(String name)
  {
      parameters.remove(name);
  }
  
  /**
   * {@inheritDoc}
   */
  public void removeExcept(Set keys)
  {
      parameters.removeExcept(keys);
  }
  
  /**
   * {@inheritDoc}
   */
  public void set(String name, boolean value)
  {
      parameters.set(name, value);
  }
  
  /**
   * {@inheritDoc}
   */
  public void set(String name, boolean[] values)
  {
      parameters.set(name, values);
  }

  /**
   * {@inheritDoc}
   */
  public void set(String name, Date value)
  {
      parameters.set(name, value);
  }

  /**
   * {@inheritDoc}
   */
  public void set(String name, Date[] values)
  {
      parameters.set(name, values);
  }

  /**
   * {@inheritDoc}
   */
  public void set(String name, float value)
  {
      parameters.set(name, value);
  }
  
  /**
   * {@inheritDoc}
   */
  public void set(String name, float[] values)
  {
      parameters.set(name, values);
  }
  
  /**
   * {@inheritDoc}
   */
  public void set(String name, int value)
  {
      parameters.set(name, value);
  }
  
  /**
   * {@inheritDoc}
   */
  public void set(String name, int[] values)
  {
      parameters.set(name, values);
  }
  
  /**
   * {@inheritDoc}
   */
  public void set(String name, long value)
  {
      parameters.set(name, value);
  }
  
  /**
   * {@inheritDoc}
   */
  public void set(String name, long[] values)
  {
      parameters.set(name, values);
  }
  
  /**
   * {@inheritDoc}
   */
  public void set(String name, String value)
  {
      parameters.set(name, value);
  }
  
  /**
   * {@inheritDoc}
   */
  public void set(String name, String[] values)
  {
      parameters.set(name, values);
  }
  
  /**
   * {@inheritDoc}
   */
  public String toString()
  {
      return parameters.toString();
  }
}
