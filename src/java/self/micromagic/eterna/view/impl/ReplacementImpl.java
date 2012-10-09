
package self.micromagic.eterna.view.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.view.Component;
import self.micromagic.eterna.view.Replacement;
import self.micromagic.eterna.view.ReplacementGenerator;
import self.micromagic.eterna.view.ViewAdapter;
import self.micromagic.eterna.view.ViewAdapterGenerator;
import self.micromagic.util.container.MultiIterator;
import self.micromagic.util.container.UnmodifiableIterator;
import self.micromagic.util.container.PreFetchIterator;
import self.micromagic.util.StringRef;
import self.micromagic.util.IntegerRef;
import self.micromagic.util.StringTool;

/**
 * @author micromagic@sina.com
 */
public class ReplacementImpl extends ComponentImpl
      implements Replacement, ReplacementGenerator
{
   protected boolean ignoreGlobalSetted = false;
   protected String baseComponentName;
   protected Component baseComponent;

	/**
	 * ��ֱ��ƥ��ؼ���ӳ���.
	 */
   protected Map directMatchMap;

	/**
	 * �Ƿ�ֱ������baseComponent.
	 */
   protected boolean linkTypical = true;

	/**
	 * �Ƿ�����Ҫֱ������baseComponent.
	 */
   protected boolean checkLinkTypical;

	/**
	 * �Ƿ�����������¼�.
	 */
   protected boolean hasSpecialEvent;

	/**
	 * �Ƿ��Ǹ��⸲��Replacement.
	 */
	protected boolean wrap;

	/**
	 * �����ؼ��Ƿ�ΪReplacement.
	 */
	protected boolean baseReplacement;

   private ViewAdapterGenerator.ModifiableViewRes viewRes;
   private List replacedList;

   public void initialize(EternaFactory factory, Component parent)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(factory, parent);
      boolean topReplace = true;
		String tmpComName = this.baseComponentName;
      if (tmpComName != null)
      {
			this.baseComponent = this.findBaseComponent(tmpComName, factory);
			if (this.directMatchMap != null)
			{
				Iterator subComponentItr = this.componentList.iterator();
				while (subComponentItr.hasNext())
				{
					Component sub = (Component) subComponentItr.next();
					ReplacementInfo rInfo = (ReplacementInfo) this.directMatchMap.get(sub.getName());
					if (rInfo != null)
					{
						if (rInfo.base == null)
						{
							rInfo.base = sub;
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
         this.initReplace(factory, this.baseComponent, null);
      }
   }

   public void initReplace(EternaFactory factory, Component base, Replacement parent)
         throws ConfigurationException
   {
      if (base != null)
      {
         this.initBase(factory, base);
      }
      else
      {
         // parent��Ϊnull, ��ʾ����һ��ռλ�ڵ�, ��Ҫ�����ռλ�ڵ��滻����Ӧλ��
         parent.replaceComponent(factory, this);
      }
		if (this.baseReplacement)
		{
			// ��baseΪһ��Replacementʱ
			if (this.linkTypical)
			{
            // �����ֱ������baseComponent, ���replacedList
				this.replacedList = null;
			}
			return;
		}

      if (!this.linkTypical || this.checkLinkTypical)
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
                  myReplace.initReplace(factory, null, this);
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

   public void initBase(EternaFactory factory, Component base)
         throws ConfigurationException
   {
      if (this.baseComponent == null)
      {
			if (this.isReplacement(base))
			{
				// baseΪһ��Replacementʱ, ��Ҫ��ʶ����
            this.baseReplacement = true;
			}
			else
			{
				this.wrap = true;
			}
         this.baseComponent = this.unWrapReplacement(base);
      }

      this.hasSpecialEvent = this.eventList.size() > 0;
		this.linkTypical = this.componentParam == null && this.beforeInit == null
				&& this.initScript == null && this.componentList.size() == 0
            && !this.ignoreGlobalSetted;
      this.checkLinkTypical = this.linkTypical && this.directMatchMap != null;

      base = this.getPrimaryComponent(this.baseComponent, null);
      if ((!this.linkTypical || this.checkLinkTypical) && this.replacedList == null)
      {
         Iterator itr = base.getSubComponents();
         this.replacedList = new LinkedList();
         while (itr.hasNext())
         {
            this.replacedList.add(itr.next());
         }
      }
		if (this.linkTypical)
		{
         this.ignoreGlobalParam = base.isIgnoreGlobalParam();
			return;
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

   public void replaceComponent(EternaFactory factory, Component newReplace)
         throws ConfigurationException
   {
      ListIterator itr = this.replacedList.listIterator();
      while (itr.hasNext())
      {
         Component com = (Component) itr.next();
         if (com.getParent() != this && com.getName().equals(newReplace.getName()))
         {
            // ���parent��Ϊ���ؼ�, ��������ͬ��ʾ����Ҫ�滻��
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

   public Map getDirectMatchMap()
   {
      return this.directMatchMap;
   }

	public Component getBaseComponent()
	{
		return this.baseComponent;
	}

   /**
    * ��ʣ��ڵ��Զ����
    */
   private void dealAutoWrap(EternaFactory factory)
         throws ConfigurationException
   {
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
               ReplacementInfo rInfo = (ReplacementInfo) this.directMatchMap.get(name);
               if (rInfo != null && rInfo.base != null && rInfo.canReplace())
               {
                  boolean replaced = false;
                  if (rInfo.base instanceof Replacement)
                  {
                     Replacement myReplace = (Replacement) rInfo.base;
                     if (myReplace.getBaseComponent() == null)
                     {
								myReplace.initReplace(factory, sub, null);
                        itr.set(myReplace);
                        replaced = true;
                     }
                  }
                  if (!replaced)
                  {
                     ReplacementImpl ri = new ReplacementImpl();
                     ri.setName(name);
                     ri.initialize(factory, this);
							ri.initBase(factory, rInfo.base);
							if (ri.linkTypical)
							{
								ri.replacedList = null;
							}
                     itr.set(ri);
                  }
                  // �нڵ��滻, ��Ҫ��linkTypical��Ϊfalse
						this.changeLinkTypical();
                  continue;
               }
            }
            ReplacementImpl ri = new ReplacementImpl();
            ri.setName(name);
            ri.initialize(factory, this);
				ri.initReplace(factory, sub, null);
            itr.set(ri);
         }
      }

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
				if (pri.linkTypical)
				{
					pri.changeLinkTypical();
				}
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
	 * ���һ���ؼ��Ƿ�ΪReplacement.
	 * ����ֱ���⸲�Ŀؼ�, ��Ҫ��������ж�.
	 */
	private boolean isReplacement(Component com)
	{
		while (com instanceof ReplacementImpl)
		{
         ReplacementImpl tmp = (ReplacementImpl) com;
			if (!tmp.wrap)
			{
				return true;
			}
			com = tmp.getBaseComponent();
		}
		return com instanceof Replacement;
	}

	/**
	 * ��ֱ������baseComponentʱ, ����ת��Ϊ��ֱ������, ����ʼ������.
	 */
	private void changeLinkTypical()
			throws ConfigurationException
	{
		Component base = this.getBaseComponent();
		if (this.linkTypical && base != null)
		{
			this.linkTypical = false;
			this.componentParam = base.getComponentParam();
			this.beforeInit = base.getBeforeInit();
			this.initScript = base.getInitScript();
		}
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
         out.write(",typicalComponent:\"");
         this.stringCoder.toJsonString(out, idName);
         out.write('"');
      }
      else if (this.baseComponent != null)
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
			String[] names = StringTool.separateString(temp, ",", true);
			for (int i = 0; i < names.length; i++)
         {
            if (names[i].length() == 0)
            {
               continue;
            }
            tmpList.add(names[i]);
         }
         if (tmpList.size() > 0)
         {
            this.directMatchMap = new HashMap();
            Iterator itr = tmpList.iterator();
            while (itr.hasNext())
            {
               String tmpName = (String) itr.next();
					int tmpI = tmpName.indexOf(':');
					ReplacementInfo rInfo;
					int tmpIndex = -1;
					if (tmpI == -1)
					{
						rInfo = new ReplacementInfo(tmpName);
					}
					else
					{
						tmpIndex = Integer.parseInt(tmpName.substring(tmpI + 1));
						rInfo = new ReplacementInfo(tmpName.substring(0, tmpI),tmpIndex);
					}
					rInfo = (ReplacementInfo) this.directMatchMap.put(rInfo.name, rInfo);
               if (rInfo != null && rInfo.addIndex(tmpIndex))
               {
                  throw new ConfigurationException("The name \"" + tmpName
                        + "\" appeared more than once in config:[" + name + "].");
               }
            }
         }
      }
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
      return new PreFetchIterator(this.replacedList.iterator(), false);
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

	/**
	 * �������Ʊ��ʽ, ���һ����ؼ�����.
	 *
	 * @param nameExp  Ҫ���ҿؼ������Ʊ��ʽ
	 */
	protected Component findBaseComponent(String nameExp, EternaFactory factory)
			throws ConfigurationException
	{
		if (nameExp == null)
		{
			return null;
		}
		StringRef subName = new StringRef();
		IntegerRef subIndex = new IntegerRef(1);
		Component tmpCom;
		if (nameExp.startsWith("view:"))
		{
			String bName = this.parseBaseName(nameExp.substring(5), subName, subIndex);
			if (bName == null)
			{
				throw new ConfigurationException("Error base name expression [" + nameExp + "].");
			}
			ViewAdapter view = factory.createViewAdapter(bName);
			tmpCom = new ViewWrapComponent(view);
		}
		else
		{
			String bName;
			if (nameExp.startsWith("typical:"))
			{
				bName = this.parseBaseName(nameExp.substring(8), subName, subIndex);
			}
			else
			{
				bName = this.parseBaseName(nameExp, subName, subIndex);
			}
			if (bName == null)
			{
				throw new ConfigurationException("Error base name expression [" + nameExp + "].");
			}
			tmpCom = this.unWrapReplacement(factory.getTypicalComponent(bName));
			if (tmpCom == null)
			{
				throw new ConfigurationException("The Typical Component [" + nameExp + "] not found.");
			}
		}
		// ����Ҫ�ȶ�baseComponent��ʼ��, ��Ϊ������Ҫ��ʼ���õ�baseComponent
		// ���Ҫ��Component�ĳ�ʼ������Ҫ���ѳ�ʼ�����, �ж��Ƿ���Ҫִ�г�ʼ��
		tmpCom.initialize(factory, null);
		if (subName.getString() != null && subName.getString().length() > 0)
		{
      	tmpCom = this.findSubComponent(tmpCom, subName.getString(), subIndex.value, new IntegerRef());
			if (tmpCom == null)
			{
				throw new ConfigurationException("The Typical Component [" + nameExp + "] not found.");
			}
		}
		return tmpCom;
	}

	/**
	 * �������Ƽ�����ֵ����һ���ӿؼ�����.
	 */
	private Component findSubComponent(Component root, String name, int index, IntegerRef nowIndex)
			throws ConfigurationException
	{
      Iterator itr = root.getSubComponents();
      while (itr.hasNext())
      {
         Component sub = (Component) itr.next();
			if (!this.isReplacement(sub))
			{
				// ������һ��Replacement�ؼ�(�����⸲�ؼ�), ��Ҫ���
				sub = this.unWrapReplacement(sub);
			}
         if (name.equals(sub.getName()))
			{
				nowIndex.value++;
            if (nowIndex.value == index)
				{
					return this.unWrapReplacement(sub);
				}
			}
			Component tmp = this.findSubComponent(sub, name, index, nowIndex);
			if (tmp != null)
			{
				return tmp;
			}
      }
		return null;
	}

	/**
	 * ���������ؼ�������.
	 */
	private String parseBaseName(String baseName, StringRef subName, IntegerRef subIndex)
	{
		String[] names = StringTool.separateString(baseName, ":", true);
		if (names.length == 2)
		{
			subName.setString(names[1]);
		}
		else if (names.length == 3)
		{
			subName.setString(names[1]);
			subIndex.value = Integer.parseInt(names[2]);
		}
		else if (names.length > 3)
		{
			return null;
		}
		return names[0];
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

	protected static class ReplacementInfo
	{
		public final String name;
		private int appearenCount = 0;
		public int[] indexs = null;
		public Component base = null;

		public ReplacementInfo(String name)
		{
			this.name = name;
		}

		public ReplacementInfo(String name, int index)
		{
			this(name);
			this.indexs = new int[]{index};
		}

		public boolean addIndex(int index)
		{
			if (this.indexs == null)
			{
				return false;
			}
			if (this.hasIndex(index))
			{
				return false;
			}
			if (index <= 0)
			{
				return false;
			}
			int[] tmpArr = this.indexs;
			this.indexs = new int[tmpArr.length + 1];
			System.arraycopy(tmpArr, 0, this.indexs, 0, tmpArr.length);
			this.indexs[this.indexs.length - 1] = index;
			return true;
		}

      public boolean canReplace()
		{
			this.appearenCount++;
			return this.indexs == null || this.hasIndex(this.appearenCount);
		}

		private boolean hasIndex(int index)
		{
			for (int i = 0; i < this.indexs.length; i++)
			{
				if (this.indexs[i] == index)
				{
					return true;
				}
			}
			return false;
		}

	}

}
