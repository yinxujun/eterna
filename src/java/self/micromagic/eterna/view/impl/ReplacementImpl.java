
package self.micromagic.eterna.view.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.view.BaseManager;
import self.micromagic.eterna.view.Component;
import self.micromagic.eterna.view.Replacement;
import self.micromagic.eterna.view.ReplacementGenerator;
import self.micromagic.eterna.view.ViewAdapter;
import self.micromagic.eterna.view.ViewAdapterGenerator;
import self.micromagic.util.container.MultiIterator;
import self.micromagic.util.container.UnmodifiableIterator;

public class ReplacementImpl extends ComponentImpl
      implements Replacement, ReplacementGenerator
{
   protected boolean ignoreGlobalSetted = false;
   protected String baseComponentName;
   protected Component baseComponent;
   protected Map directMatchMap;
   protected boolean linkTypical = true;
   protected boolean hasSpecialEvent = false;

   private ViewAdapterGenerator.ModifiableViewRes viewRes = null;
   private List replacedList = null;

   public void initialize(EternaFactory factory, Component parent)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(factory, parent);
      boolean topReplace = true;
      if (this.baseComponentName != null)
      {
         if (this.baseComponentName.startsWith("view:"))
         {
            ViewAdapter view = factory.createViewAdapter(this.baseComponentName.substring(5));
            this.baseComponent = new ViewWrapComponent(view);
         }
         else
         {
            String bmName;
            if (this.baseComponentName.startsWith("typical:"))
            {
               bmName = this.baseComponentName.substring(8);
            }
            else
            {
               bmName = this.baseComponentName;
            }
            this.baseComponent = this.unWrapReplacement(factory.getTypicalComponent(bmName));
            if (this.baseComponent == null)
            {
               throw new ConfigurationException("The Typical Component [" + this.baseComponentName + "] not found.");
            }
         }
         if (this.baseComponent != null)
         {
            // ����Ҫ�ȶ�baseComponent��ʼ��, ��Ϊ������Ҫ��ʼ���õ�baseComponent
            // ���Ҫ��Component�ĳ�ʼ������Ҫ���ѳ�ʼ�����, �ж��Ƿ���Ҫִ�г�ʼ��
            this.baseComponent.initialize(factory, null);
            if (this.directMatchMap != null)
            {
               Iterator subComponentItr = this.componentList.iterator();
               while (subComponentItr.hasNext())
               {
                  Component sub = (Component) subComponentItr.next();
                  Object tmpObj = this.directMatchMap.get(sub.getName());
                  if (tmpObj != null)
                  {
                     if (tmpObj == INIT_COMPONENT)
                     {
                        this.directMatchMap.put(sub.getName(), sub);
                        subComponentItr.remove();
                     }
                     else
                     {
                        throw new ConfigurationException("The direct match component \""
                              + sub.getName() + "\" appeared more than once.");
                     }
                  }
               }
            }
         }
      }
      else
      {
         if (parent == null || !(parent instanceof Replacement))
         {
            throw new ConfigurationException("Top replacement must set base component.");
         }
         topReplace = false;
         this.directMatchMap = ((Replacement) parent).getDirectMatchMap();
      }
      if (topReplace)
      {
         this.initReplace(factory, null);
      }
   }

   public void initReplace(EternaFactory factory, Replacement parent)
         throws ConfigurationException
   {
      if (parent == null)
      {
         this.initBase(factory, this.baseComponent);
      }
      else
      {
         // parent��Ϊnull, ��ʾ����һ��ռλ�ڵ�, ��Ҫ�����ռλ�ڵ��滻����Ӧλ��
         parent.replaceComponent(factory, this);
      }

      if (!this.linkTypical)
      {
         // �滻��Ҫ�滻��Component
         Iterator subComponentItr = this.componentList.iterator();
         while (subComponentItr.hasNext())
         {
            Component sub = (Component) subComponentItr.next();
            if (sub instanceof Replacement)
            {
               Replacement myReplace = (Replacement) sub;
               if (myReplace.getBaseComponent() == null)
               {
                  myReplace.initReplace(factory, this);
               }
               else
               {
                  this.replaceComponent(factory, sub);
               }
            }
            else
            {
               this.replaceComponent(factory, sub);
            }
         }

         // �������Ľڵ��滻��replacement
         this.dealAutoWrap(factory);
      }
   }

   /**
    * ��ʣ��ڵ��Զ����
    */
   private void dealAutoWrap(EternaFactory factory)
         throws ConfigurationException
   {
      boolean checkLinkTypical = false;
      if (this.replacedList == null)
      {
         // ���replacedListΪnull, ˵��Ҫ���linkTypical
         checkLinkTypical = true;
         Iterator itr = this.getBaseComponent().getSubComponents();
         this.replacedList = new LinkedList();
         while (itr.hasNext())
         {
            this.replacedList.add(itr.next());
         }
      }

      // �������Ľڵ��滻��
      ListIterator itr = this.replacedList.listIterator();
      while (itr.hasNext())
      {
         Component sub = (Component) itr.next();
         String name = sub.getName();
         if (sub.getParent() != this)
         {
            if (this.directMatchMap != null)
            {
               // ����ֱ���滻�ڵ����滻
               Object tmpObj = this.directMatchMap.get(name);
               if (tmpObj != null && tmpObj != INIT_COMPONENT)
               {
                  boolean replaced = false;
                  if (tmpObj instanceof Replacement)
                  {
                     Replacement myReplace = (Replacement) tmpObj;
                     if (myReplace.getBaseComponent() == null)
                     {
                        myReplace.initBase(factory, sub);
                        itr.set(myReplace);
                        replaced = true;
                     }
                  }
                  if (!replaced)
                  {
                     ReplacementImpl ri = null;
                     ri = new ReplacementImpl();
                     ri.setName(name);
                     ri.initialize(factory, this);
                     ri.baseComponent = this.unWrapReplacement((Component) tmpObj);
                     itr.set(ri);
                  }
                  // �нڵ��滻, ����Ҫ���linkTypical, ���Խ�����Ϊfalse
                  if (checkLinkTypical)
                  {
                     this.linkTypical = false;
                  }
                  continue;
               }
            }
            sub = this.unWrapReplacement(sub);
            ReplacementImpl ri = new ReplacementImpl();
            ri.setName(name);
            ri.initialize(factory, this);
            ri.baseComponent = sub;
            if (this.directMatchMap != null)
            {
               // ���directMatchMap��Ϊnull, ����Ҫ�ݹ��滻
               ri.dealAutoWrap(factory);
            }
            itr.set(ri);
         }
      }

      if (checkLinkTypical)
      {
         if (this.linkTypical)
         {
            // linkTypicalΪtrue, ˵���޽ڵ��滻, ���replacedList
            this.replacedList = null;
         }
         else
         {
            // linkTypicalΪfalse, ˵���нڵ��滻, ��Ҫ����BaseComponent������
            if (this.getParent() instanceof ReplacementImpl)
            {
               // �нڵ��滻, �Ҹ��ڵ�ΪReplacementImpl, �򽫸��ڵ��linkTypical��Ϊfalse
               ReplacementImpl pri = (ReplacementImpl) this.getParent();
               pri.linkTypical = false;
            }
            Component base = this.getBaseComponent();
            this.componentParam = base.getComponentParam();
            this.ignoreGlobalParam = base.isIgnoreGlobalParam();
            this.beforeInit = base.getBeforeInit();
            this.initScript = base.getInitScript();
         }
      }
   }

   /**
    * �⿪autoWrap(�Զ�������)�ؼ�, ����typical-replacement�������Ѿ������
    */
   private Component unWrapReplacement(Component com)
   {
      while (com instanceof ReplacementImpl)
      {
         ReplacementImpl tmp = (ReplacementImpl) com;
         if (!tmp.linkTypical || tmp.hasSpecialEvent || tmp.getBaseComponent() == null)
         {
            break;
         }
         com = tmp.getBaseComponent();
      }
      return com;
   }

   /**
    * ���ԭʼ�Ŀؼ����������eventList������Ҳ���event��ӽ�ȥ
    */
   private Component getPrimaryComponent(Component com, List eventList)
   {
      while (com instanceof ReplacementImpl)
      {
         ReplacementImpl tmp = (ReplacementImpl) com;
         if (!tmp.linkTypical || tmp.getBaseComponent() == null)
         {
            break;
         }
         if (eventList != null && tmp.hasSpecialEvent)
         {
            ListIterator litr = tmp.eventList.listIterator(tmp.eventList.size());
            while (litr.hasPrevious())
            {
               eventList.add(0, litr.previous());
            }
         }
         com = tmp.getBaseComponent();
      }
      return com;
   }

   public void replaceComponent(EternaFactory factory, Component newReplace)
         throws ConfigurationException
   {
      ListIterator itr = this.replacedList.listIterator();
      while (itr.hasNext())
      {
         Component com = (Component) itr.next();
         if (com.getParent() != this && com.getName().equals(newReplace.getName()))
         {
            // ���parent��Ϊ���ؼ�, ���ʾ����Ҫ�滻��
            if (newReplace instanceof Replacement)
            {
               Replacement myReplace = (Replacement) newReplace;
               if (myReplace.getBaseComponent() == null)
               {
                  myReplace.initBase(factory, com);
               }
               itr.set(newReplace);
            }
            else
            {
               itr.set(newReplace);
            }
            break;
         }
      }
   }

   public void initBase(EternaFactory factory, Component base)
         throws ConfigurationException
   {
      if (this.baseComponent == null)
      {
         this.baseComponent = this.unWrapReplacement(base);
      }

      this.hasSpecialEvent = this.eventList.size() > 0;
      if (this.componentParam == null && this.beforeInit == null && this.initScript == null
            && this.componentList.size() == 0 && this.directMatchMap == null
            && !this.ignoreGlobalSetted)
      {
         this.ignoreGlobalParam = base.isIgnoreGlobalParam();
         return;
      }
      this.linkTypical = false;
      base = this.getPrimaryComponent(this.baseComponent, null);

      if (this.replacedList == null)
      {
         Iterator itr = base.getSubComponents();
         this.replacedList = new LinkedList();
         while (itr.hasNext())
         {
            this.replacedList.add(itr.next());
         }
      }

      if (this.componentParam == null)
      {
         this.componentParam = base.getComponentParam();
      }
      if (!this.ignoreGlobalSetted)
      {
         this.ignoreGlobalParam = base.isIgnoreGlobalParam();
      }

      String parentScript = base.getBeforeInit();
      this.beforeInit = ViewTool.addParentScript(this.beforeInit, parentScript);

      parentScript = base.getInitScript();
      this.initScript = ViewTool.addParentScript(this.initScript, parentScript);
   }

   public Map getDirectMatchMap()
   {
      return this.directMatchMap;
   }

   public void printBody(Writer out, AppData data, ViewAdapter view)
         throws IOException, ConfigurationException
   {
      super.printBody(out, data, view);
   }

   public void printSpecialBody(Writer out, AppData data, ViewAdapter view)
         throws IOException, ConfigurationException
   {
      if (this.linkTypical)
      {
         String idName = ViewTool.createTypicalComponentName(data, this.baseComponent);
         out.write(",typicalComponent:");
         out.write("\"");
         out.write(this.stringCoder.toJsonString(idName));
         out.write("\"");
      }
      else if (!this.linkTypical && this.baseComponent != null)
      {
         this.getPrimaryComponent(this.baseComponent, null).printSpecialBody(out, data, view);
      }
   }

   public void setBaseComponentName(String name)
         throws ConfigurationException
   {
      this.baseComponentName = name;
      if (name != null)
      {
         int index = name.indexOf(';');
         if (index == -1)
         {
            return;
         }
         this.baseComponentName = name.substring(0, index);
         String temp = name.substring(index + 1);
         List tmpList = new LinkedList();
         StringTokenizer token = new StringTokenizer(temp, ",");
         while (token.hasMoreTokens())
         {
            String tmpStr = token.nextToken().trim();
            if (tmpStr.length() == 0)
            {
               continue;
            }
            tmpList.add(tmpStr);
         }
         if (tmpList.size() > 0)
         {
            this.directMatchMap = new HashMap();
            Iterator itr = tmpList.iterator();
            while (itr.hasNext())
            {
               Object tmpName = itr.next();
               if (this.directMatchMap.put(tmpName, INIT_COMPONENT) != null)
               {
                  throw new ConfigurationException("The name \"" + tmpName
                        + "\" appeared more than once:[" + name + "].");
               }
            }
         }
      }
   }

   public Component getBaseComponent()
   {
      return this.baseComponent;
   }

   public void setIgnoreGlobalParam(boolean ignore)
         throws ConfigurationException
   {
      this.ignoreGlobalSetted = true;
      super.setIgnoreGlobalParam(ignore);
   }

   public String getType()
         throws ConfigurationException
   {
      if (!this.linkTypical && this.baseComponent != null)
      {
         return this.getPrimaryComponent(this.baseComponent, null).getType();
      }
      return "replacement";
   }

   public void setType(String type) {}

   public Iterator getSubComponents()
   {
      if (this.replacedList == null)
      {
         return UnmodifiableIterator.EMPTY_ITERATOR;
      }
      return new UnmodifiableIterator(this.replacedList.iterator());
   }

   public Iterator getEvents()
         throws ConfigurationException
   {
      if (this.baseComponent == null || this.linkTypical)
      {
         return super.getEvents();
      }
      List tmp = new LinkedList();
      Component tmpCom = this.getPrimaryComponent(this.baseComponent, tmp);
      Iterator tmpItr = new MultiIterator(tmpCom.getEvents(), tmp.iterator());
      return new MultiIterator(tmpItr, super.getEvents());
   }

   protected ViewAdapterGenerator.ModifiableViewRes getModifiableViewRes()
         throws ConfigurationException
   {
      if (this.viewRes == null)
      {
         this.viewRes = super.getModifiableViewRes();
         if (this.baseComponent != null)
         {
            this.viewRes.addAll(this.baseComponent.getViewRes());
         }
      }
      return this.viewRes;
   }

}
