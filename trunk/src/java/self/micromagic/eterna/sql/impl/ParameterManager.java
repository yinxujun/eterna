
package self.micromagic.eterna.sql.impl;

import java.util.ArrayList;
import java.util.Iterator;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.sql.SQLParameter;
import self.micromagic.util.StringTool;
import self.micromagic.util.StringAppender;

public class ParameterManager
{
   public static final int NORMAL_PARAMETER = 0;
   public static final int DYNAMIC_PARAMETER = 1;

   private SQLParameter param = null;
   private int type;
   private String groupName;
   private boolean parameterSetted = false;

   private boolean checked = false;
   private String[] templates = StringTool.EMPTY_STRING_ARRAY;

   private int index;

   public ParameterManager(int type)
   {
      this.type = type;
   }

   public void setGroupName(String groupName)
   {
      this.groupName = groupName;
   }

   public ParameterManager copy(boolean clear)
   {
      ParameterManager other = new ParameterManager(this.type);
      other.checked = this.checked;
      other.param = this.param;
      other.index = this.index;
      other.parameterSetted = clear ? false : this.parameterSetted;
      other.templates = this.templates;
      return other;
   }

   /**
    * Ԥ�鶯̬����
    */
   public void preCheck()
         throws ConfigurationException
   {
      if (this.type == NORMAL_PARAMETER)
      {
         return;
      }
      String template;
      ArrayList partList = new ArrayList();
      ArrayList paramList = new ArrayList();
      ArrayList subSQLList = new ArrayList();
      ArrayList subList = new ArrayList();
      for (int i = 0; i < this.templates.length; i++)
      {
         template = this.templates[i];
         SQLManager.parse(template, true, partList, paramList, subSQLList, subList);
      }
      if (paramList.size() != 1)
      {
         throw new ConfigurationException("Error dynamic parameter template, postion ["
               + (this.index + 1) + "], group name [" + this.groupName
               + "], param count [" + paramList.size() + "].");
      }
   }

   public void check(EternaFactory factory)
         throws ConfigurationException
   {
      if (this.checked)
      {
         return;
      }

      this.checked = true;
      // ����û�����õĲ������ж�, δ���õĿ���ͨ����������
      /*if (this.param == null)
      {
         throw new ConfigurationException("The parameter not bind at position:"
               + (this.index + 1) + ".");
      }*/

      if (this.type == NORMAL_PARAMETER)
      {
         return;
      }

      String template;
      ArrayList partList = new ArrayList();
      ArrayList paramList = new ArrayList();
      ArrayList subSQLList = new ArrayList();
      ArrayList subList = new ArrayList();
      for (int i = 0; i < this.templates.length; i++)
      {
         template = this.templates[i];
         SQLManager.parse(template, true, partList, paramList, subSQLList, subList);

         // ���ݽ����Ľ���޸�template
         StringAppender temp = StringTool.createStringAppender();
         Iterator itr = partList.iterator();
         while (itr.hasNext())
         {
            SQLManager.PartSQL ps = (SQLManager.PartSQL) itr.next();
            ps.initialize(factory);
            temp.append(ps.getSQL());
         }
         if (!template.equals(temp.toString()))
         {
            // �����ͬ�򲻽��д���, �����ͬ˵���г���������, Ҫ����intern����
            this.templates[i] = StringTool.intern(temp.toString(), true);
         }
         partList.clear();
      }
      if (paramList.size() != 1)
      {
         throw new ConfigurationException("Error parameter template, param name ["
               + this.param.getName() + "].");
      }
   }

   public SQLParameter getParam()
   {
      return this.param;
   }

   public void setParam(SQLParameter param)
         throws ConfigurationException
   {
      if (this.param != null)
      {
         throw new ConfigurationException("You can't bind two name in same position:"
               + (this.index + 1) + ".");
      }
      this.param = param;
   }

   public void clearParam()
   {
      this.param = null;
   }

   public int getType()
   {
      return this.type;
   }

   public void addParameterTemplate(String template)
         throws ConfigurationException
   {
      if (this.type == NORMAL_PARAMETER)
      {
         throw new ConfigurationException("You can't set template in normal parameter, name "
               + this.param.getName() + ".");
      }
      if (template == null)
      {
         throw new NullPointerException();
      }

      int oldCount = this.templates.length;
      String[] temp = new String[oldCount + 1];
      System.arraycopy(this.templates, 0, temp, 0, oldCount);
      temp[oldCount] = template;
      this.templates = temp;
   }

   public int getParameterTemplateCount()
         throws ConfigurationException
   {
      if (type == NORMAL_PARAMETER)
      {
         throw new ConfigurationException("You can't get template in normal parameter, name "
               + this.param.getName() + ".");
      }
      return this.templates.length;
   }

   public String getParameterTemplate(int index)
         throws ConfigurationException
   {
      if (type == NORMAL_PARAMETER)
      {
         throw new ConfigurationException("You can't get template in normal parameter, name "
               + this.param.getName() + ".");
      }
      return this.templates[index];
   }

   public boolean isParameterSetted()
   {
      return this.parameterSetted;
   }

   public void setParameterSetted(boolean parameterSetted)
   {
      this.parameterSetted = parameterSetted;
   }

   public int getIndex()
   {
      return this.index;
   }

   void setIndex(int index)
   {
      this.index = index;
   }

}
