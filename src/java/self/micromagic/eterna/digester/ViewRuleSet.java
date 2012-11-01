
package self.micromagic.eterna.digester;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSetBase;
import org.xml.sax.Attributes;
import self.micromagic.eterna.view.Component;
import self.micromagic.eterna.view.ComponentGenerator;
import self.micromagic.eterna.view.DataPrinter;
import self.micromagic.eterna.view.Function;
import self.micromagic.eterna.view.Resource;
import self.micromagic.eterna.view.StringCoder;
import self.micromagic.eterna.view.TableForm;
import self.micromagic.eterna.view.TableFormGenerator;
import self.micromagic.eterna.view.TableList;
import self.micromagic.eterna.view.TableListGenerator;
import self.micromagic.eterna.view.ViewAdapterGenerator;
import self.micromagic.eterna.view.impl.ComponentImpl;
import self.micromagic.eterna.view.impl.DataPrinterImpl;
import self.micromagic.eterna.view.impl.FunctionImpl;
import self.micromagic.eterna.view.impl.ReplacementImpl;
import self.micromagic.eterna.view.impl.ResourceImpl;
import self.micromagic.eterna.view.impl.StringCoderImpl;
import self.micromagic.eterna.view.impl.TR;
import self.micromagic.eterna.view.impl.TableFormImpl;
import self.micromagic.eterna.view.impl.TableListImpl;
import self.micromagic.eterna.view.impl.ViewAdapterImpl;

/**
 * viewģ���ʼ���Ĺ���.
 *
 * @author micromagic@sina.com
 */
public class ViewRuleSet extends RuleSetBase
{
   public ViewRuleSet()
   {
   }

   public void addRuleInstances(Digester digester)
   {
      PropertySetter setter;
      PropertySetter[] setters;
      Rule rule;

      // ���е�beforeInit, initScript, *param����Ҫ����intern, ��Ϊ�ڷ���
      // BaseManager.dealScriptPart�л�������

      //--------------------------------------------------------------------------------
      // ����ViewAdapter
      rule = new ObjectCreateRule(ViewAdapterImpl.class.getName(), "generator",
            ViewAdapterGenerator.class);
      digester.addRule("eterna-config/factory/objs/view", rule);

      setter = new StackPropertySetter("registerViewAdapter", ViewAdapterGenerator.class, 1);
      digester.addRule("eterna-config/factory/objs/view", new PropertySetRule(setter));

      setters = new PropertySetter[] {
         new StringPropertySetter("name", "setName", true),
         new StringPropertySetter("dataPrinterName", "setDataPrinterName", false),
         new StringPropertySetter("defaultDataType", "setDefaultDataType", false),
         new StringPropertySetter("width", "setWidth", false),
         new StringPropertySetter("height", "setHeight", false),
         new StringPropertySetter("beforeInit", "setBeforeInit", false, false),
         new StringPropertySetter("initScript", "setInitScript", false, false),
         new IntegerPropertySetter("debug", "setDebug", false)
      };
      digester.addRule("eterna-config/factory/objs/view",
            new PropertySetRule(setters, false));
      digester.addRule("eterna-config/factory/objs/view",
            new ObjectLogRule("name", "View"));
      digester.addRule("eterna-config/factory/objs/view/attribute",
            new AttributeSetRule());
      // ����view�Ķ�̬������Դ�б�
      setter = new BodyPropertySetter("trimLine", "setDynamicViewRes", true, false);
      digester.addRule("eterna-config/factory/objs/view/view-res",
            new PropertySetRule(setter, false));


      //--------------------------------------------------------------------------------
      // ����StringCoder
      rule = new ObjectCreateRule(StringCoderImpl.class.getName(), "className",
            StringCoder.class);
      digester.addRule("eterna-config/factory/string-coder", rule);
      setter = new StackPropertySetter("setStringCoder", StringCoder.class, 1);
      digester.addRule("eterna-config/factory/string-coder", new PropertySetRule(setter));
      digester.addRule("eterna-config/factory/string-coder",
            new ObjectLogRule("name", "StringCoder"));


      //--------------------------------------------------------------------------------
      // ����DataPrinter
      setter = new GeneratorPropertySetter("generator", "addDataPrinter",
            DataPrinterImpl.class.getName(), DataPrinter.class, true);
      digester.addRule("eterna-config/factory/objs/data-printer",
            new PropertySetRule(setter));

      setter = new StringPropertySetter("name", "setName", true);
      digester.addRule("eterna-config/factory/objs/data-printer",
            new PropertySetRule(setter, false));
      digester.addRule("eterna-config/factory/objs/data-printer",
            new ObjectLogRule("name", "DataPrinter"));
      digester.addRule("eterna-config/factory/objs/data-printer/attribute",
            new AttributeSetRule());


      //--------------------------------------------------------------------------------
      // �����ı���Դ(Resource)�Ķ�ȡ����
      setter = new GeneratorPropertySetter("generator", "addResource",
            ResourceImpl.class.getName(), Resource.class, true);
      digester.addRule("eterna-config/factory/objs/resource",
            new PropertySetRule(setter));
      digester.addRule("eterna-config/factory/objs/resource",
            new ObjectLogRule("name", "Resource"));

      setters = new PropertySetter[] {
         // Resource������
         new StringPropertySetter("name", "setName", true),
         new BooleanPropertySetter("trimLine", "setTrimLine", false)
      };
      digester.addRule("eterna-config/factory/objs/resource",
            new PropertySetRule(setters, false));
      // Resource���ı�(��xml��body������)
      setter = new BodyPropertySetter("trimLine", "setResourceText", false, true);
      ((BodyPropertySetter) setter).setNoLine("noLine", true);
      ((BodyPropertySetter) setter).setNeedResolve("resolve", true);
      digester.addRule("eterna-config/factory/objs/resource",
            new PropertySetRule(setter, false));


      //--------------------------------------------------------------------------------
      // ����TypicalComponent
      setter = new GeneratorPropertySetter("generator", "addTypicalComponent",
            ComponentImpl.class.getName(), Component.class, true);
      digester.addRule("eterna-config/factory/objs/typical-component",
            new PropertySetRule(setter));
      digester.addRule("eterna-config/factory/objs/typical-component",
            new ObjectLogRule("name", "TypicalComponent"));

      setters = new PropertySetter[] {
         new StringPropertySetter("name", "setName", true),
         new StringPropertySetter("type", "setType", true),
         new BooleanPropertySetter("ignoreGlobal", "setIgnoreGlobalParam", false),
         new StringPropertySetter("beforeInit", "setBeforeInit", false, false),
         new StringPropertySetter("initScript", "setInitScript", false, false),
         new StringPropertySetter("comParam", "setComponentParam", false, false),
         new StringPropertySetter("attributes", "setAttributes", false)
      };
      digester.addRule("eterna-config/factory/objs/typical-component",
            new PropertySetRule(setters, false));


      //--------------------------------------------------------------------------------
      // ����TypicalReplacement
      setter = new GeneratorPropertySetter("generator", "addTypicalComponent",
            ReplacementImpl.class.getName(), Component.class, true);
      digester.addRule("eterna-config/factory/objs/typical-replacement",
            new PropertySetRule(setter));
      digester.addRule("eterna-config/factory/objs/typical-replacement",
            new ObjectLogRule("name", "TypicalReplacement"));

      setters = new PropertySetter[] {
         new StringPropertySetter("name", "setName", true),
         new BooleanPropertySetter("ignoreGlobal", "setIgnoreGlobalParam", false),
         new StringPropertySetter("baseComponentName", "setBaseComponentName", true),
         new StringPropertySetter("beforeInit", "setBeforeInit", false, false),
         new StringPropertySetter("initScript", "setInitScript", false, false),
         new StringPropertySetter("comParam", "setComponentParam", false, false),
         new StringPropertySetter("attributes", "setAttributes", false)
      };
      digester.addRule("eterna-config/factory/objs/typical-replacement",
            new PropertySetRule(setters, false));


      //--------------------------------------------------------------------------------
      // ����Function
      setter = new GeneratorPropertySetter("generator", "addFunction",
            FunctionImpl.class.getName(), Function.class, true);
      digester.addRule("eterna-config/factory/objs/function",
            new PropertySetRule(setter));
      digester.addRule("eterna-config/factory/objs/function",
            new ObjectLogRule("name", "Function"));

      setters = new PropertySetter[] {
         new StringPropertySetter("name", "setName", true),
         new StringPropertySetter("param", "setParam", false)
      };
      digester.addRule("eterna-config/factory/objs/function", new PropertySetRule(setters, false));
      setter = new BodyPropertySetter("trimLine", "setBody", true, false);
      digester.addRule("eterna-config/factory/objs/function", new PropertySetRule(setter, false));


      //--------------------------------------------------------------------------------
      // ����Replacement
      digester.addRule("*/replacement", new StackRule(){
         public Rule createRule() throws Exception
         {
            Class[] types = {ComponentGenerator.class, ViewAdapterGenerator.class};
            PropertySetter setter = new CheckParentSetter("generator", "addComponent",
                  ReplacementImpl.class.getName(), Component.class, types);
            return new PropertySetRule(setter);
         }
      });
      digester.addRule("*/replacement", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter[] setters = new PropertySetter[] {
               new StringPropertySetter("name", "setName", true),
               new BooleanPropertySetter("ignoreGlobal", "setIgnoreGlobalParam", false),
               new StringPropertySetter("baseComponentName", "setBaseComponentName", false),
               new StringPropertySetter("beforeInit", "setBeforeInit", false, false),
               new StringPropertySetter("initScript", "setInitScript", false, false),
               new StringPropertySetter("comParam", "setComponentParam", false, false),
               new StringPropertySetter("attributes", "setAttributes", false)
            };
            return new PropertySetRule(setters, false);
         }
      });


      //--------------------------------------------------------------------------------
      // ����Component
      digester.addRule("*/component", new StackRule(){
         public Rule createRule() throws Exception
         {
            Class[] types = {ComponentGenerator.class, ViewAdapterGenerator.class};
            PropertySetter setter = new CheckParentSetter("generator", "addComponent",
                  ComponentImpl.class.getName(), Component.class, types);
            return new PropertySetRule(setter);
         }
      });
      digester.addRule("*/component", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter[] setters = new PropertySetter[] {
               new StringPropertySetter("name", "setName", true),
               new StringPropertySetter("type", "setType", true),
               new BooleanPropertySetter("ignoreGlobal", "setIgnoreGlobalParam", false),
               new StringPropertySetter("beforeInit", "setBeforeInit", false, false),
               new StringPropertySetter("initScript", "setInitScript", false, false),
               new StringPropertySetter("comParam", "setComponentParam", false, false),
               new StringPropertySetter("attributes", "setAttributes", false)
            };
            return new PropertySetRule(setters, false);
         }
      });

      digester.addRule("*/before-init", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter setter = new BodyPropertySetter("trimLine", "setBeforeInit", true, false);
            return new PropertySetRule(setter, false);
         }
      });
      // ����Component �� һЩscript
      digester.addRule("*/init-script", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter setter = new BodyPropertySetter("trimLine", "setInitScript", true, false);
            return new PropertySetRule(setter, false);
         }
      });
      // ����Component �� һЩparam����attr��css��className
      digester.addRule("*/component-param", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter setter = new BodyPropertySetter("trimLine", "setComponentParam", true, false);
            return new PropertySetRule(setter, false);
         }
      });

      // ����Component events
      digester.addRule("*/events/event", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter setter = new CheckParentSetter("generator", "addEvent",
                  ComponentImpl.EventImpl.class.getName(), Component.Event.class,
                  new Class[]{ComponentGenerator.class});
            return new PropertySetRule(setter);
         }
      });
      digester.addRule("*/events/event", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter[] setters = new PropertySetter[] {
               new StringPropertySetter("name", "setName", true),
               new StringPropertySetter("scriptParam", "setScriptParam", false, false)
            };
            return new PropertySetRule(setters, false);
         }
      });
      digester.addRule("*/events/event", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter setter = new BodyPropertySetter("trimLine", "setScriptBody", true, false);
            return new PropertySetRule(setter, false);
         }
      });


      //--------------------------------------------------------------------------------
      // ����TableForm
      digester.addRule("*/table-form", new StackRule(){
         public Rule createRule() throws Exception
         {
            Class[] types = {ComponentGenerator.class, ViewAdapterGenerator.class};
            PropertySetter setter = new CheckParentSetter("generator", "addComponent",
                  TableFormImpl.class.getName(), Component.class, types);
            return new PropertySetRule(setter);
         }
      });
      digester.addRule("*/table-form", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter[] setters = new PropertySetter[] {
               new StringPropertySetter("name", "setName", true),
               new BooleanPropertySetter("ignoreGlobal", "setIgnoreGlobalParam", false),
               new BooleanPropertySetter("autoArrange", "setAutoArrange", false),
               new BooleanPropertySetter("percentWidth", "setPercentWidth", false),
               new BooleanPropertySetter("caculateWidth", "setCaculateWidth", false),
               new IntegerPropertySetter("caculateWidthFix", "caculateWidthFix", false),
               new StringPropertySetter("columns", "setColumns", true),
               new StringPropertySetter("baseName", "setBaseName", false),
               new StringPropertySetter("dataName", "setDataName", false),
               new StringPropertySetter("beforeInit", "setBeforeInit", false, false),
               new StringPropertySetter("initScript", "setInitScript", false, false),
               new StringPropertySetter("comParam", "setComponentParam", false, false),
               new StringPropertySetter("attributes", "setAttributes", false)
            };
            return new PropertySetRule(setters, false);
         }
      });

      // ����TableForm tr
      digester.addRule("*/table-form/tr", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter setter = new CheckParentSetter("generator", "setTR",
                  TR.class.getName(), Component.class, new Class[]{TableFormGenerator.class});
            return new PropertySetRule(setter);
         }
      });
      digester.addRule("*/table-form/tr", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter[] setters = new PropertySetter[] {
               new StringPropertySetter("name", "setName", TableForm.TR_NAME_PERFIX),
               new StringPropertySetter("type", "setType", "tr"),
               new BooleanPropertySetter("ignoreGlobal", "setIgnoreGlobalParam", false),
               new StringPropertySetter("beforeInit", "setBeforeInit", false, false),
               new StringPropertySetter("initScript", "setInitScript", false, false),
               new StringPropertySetter("comParam", "setComponentParam", false, false),
               new StringPropertySetter("attributes", "setAttributes", false)
            };
            return new PropertySetRule(setters, false);
         }
      });


      //  ����TableForm cells��parent
      digester.addRule("*/table-form/cells", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter[] setters = new PropertySetter[] {
               new StringPropertySetter("cellOrder", "setCellOrder", false)
            };
            return new PropertySetRule(setters, false);
         }
      });

      // ����TableForm cells
      digester.addRule("*/table-form/cells/cell", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter setter = new CheckParentSetter("generator", "addCell",
                  TableFormImpl.CellImpl.class.getName(), TableForm.Cell.class,
                  new Class[]{TableFormGenerator.class});
            return new PropertySetRule(setter);
         }
      });
      digester.addRule("*/table-form/cells/cell", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter[] setters = new PropertySetter[] {
               new StringPropertySetter("name", "setName", true),
               new IntegerPropertySetter("titleSize", "setTitleSize", false),
               new IntegerPropertySetter("containerSize", "setContainerSize", false),
               new IntegerPropertySetter("rowSpan", "setRowSpan", false),
               new BooleanPropertySetter("ignoreGlobalTitle", "setIgnoreGlobalTitleParam", false),
               new BooleanPropertySetter("ignoreGlobal", "setIgnoreGlobalParam", false),
               new StringPropertySetter("caption", "setCaption", false, false),
               new StringPropertySetter("defaultValue", "setDefaultValue", false, false),
               new StringPropertySetter("srcName", "setSrcName", false),
               new BooleanPropertySetter("required", "setRequired", false),
               new BooleanPropertySetter("newRow", "setNewRow", "false"),
               new BooleanPropertySetter("needIndex", "setNeedIndex", false),
               new StringPropertySetter("typicalComponentName", "setTypicalComponentName", false),
               new StringPropertySetter("beforeInit", "setBeforeInit", false, false),
               new StringPropertySetter("initScript", "setInitScript", false, false),
               new StringPropertySetter("comParam", "setComponentParam", false, false),
               new StringPropertySetter("titleParam", "setTitleParam", false, false),
               new StringPropertySetter("initParam", "setInitParam", false, false),
               new BooleanPropertySetter("ignore", "setIgnore", "false"),
               new StringPropertySetter("attributes", "setAttributes", false)
            };
            return new PropertySetRule(setters, false);
         }
      });
      digester.addRule("*/table-form/cells/cell/init-param", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter setter = new BodyPropertySetter("trimLine", "setInitParam", true, false);
            return new PropertySetRule(setter, false);
         }
      });
      digester.addRule("*/table-form/cells/cell/title-param", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter setter = new BodyPropertySetter("trimLine", "setTitleParam", true, false);
            return new PropertySetRule(setter, false);
         }
      });


      //--------------------------------------------------------------------------------
      // ����TableList
      digester.addRule("*/table-list", new StackRule(){
         public Rule createRule() throws Exception
         {
            Class[] types = {ComponentGenerator.class, ViewAdapterGenerator.class};
            PropertySetter setter = new CheckParentSetter("generator", "addComponent",
                  TableListImpl.class.getName(), Component.class, types);
            return new PropertySetRule(setter);
         }
      });
      digester.addRule("*/table-list", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter[] setters = new PropertySetter[] {
               new StringPropertySetter("name", "setName", true),
               new BooleanPropertySetter("ignoreGlobal", "setIgnoreGlobalParam", false),
               new BooleanPropertySetter("autoArrange", "setAutoArrange", false),
               new BooleanPropertySetter("percentWidth", "setPercentWidth", false),
               new BooleanPropertySetter("caculateWidth", "setCaculateWidth", false),
               new IntegerPropertySetter("caculateWidthFix", "caculateWidthFix", false),
               new StringPropertySetter("baseName", "setBaseName", false),
               new StringPropertySetter("dataName", "setDataName", false),
               new StringPropertySetter("beforeInit", "setBeforeInit", false, false),
               new StringPropertySetter("initScript", "setInitScript", false, false),
               new StringPropertySetter("comParam", "setComponentParam", false, false),
               new StringPropertySetter("attributes", "setAttributes", false)
            };
            return new PropertySetRule(setters, false);
         }
      });

      // ����TableList tr
      digester.addRule("*/table-list/tr", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter setter = new CheckParentSetter("generator", "setTR",
                  TR.class.getName(), Component.class, new Class[]{TableListGenerator.class});
            return new PropertySetRule(setter);
         }
      });
      digester.addRule("*/table-list/tr", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter[] setters = new PropertySetter[] {
               new StringPropertySetter("name", "setName", TableList.TR_NAME_PERFIX),
               new StringPropertySetter("type", "setType", "tr"),
               new BooleanPropertySetter("ignoreGlobal", "setIgnoreGlobalParam", false),
               new StringPropertySetter("beforeInit", "setBeforeInit", false, false),
               new StringPropertySetter("initScript", "setInitScript", false, false),
               new StringPropertySetter("comParam", "setComponentParam", false, false),
               new StringPropertySetter("attributes", "setAttributes", false)
            };
            return new PropertySetRule(setters, false);
         }
      });


      //  ����TableList columns��parent
      digester.addRule("*/table-list/columns", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter[] setters = new PropertySetter[] {
               new StringPropertySetter("columnOrder", "setColumnOrder", false)
            };
            return new PropertySetRule(setters, false);
         }
      });

      // ����TableList columns
      digester.addRule("*/table-list/columns/column", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter setter = new CheckParentSetter("generator", "addColumn",
                  TableListImpl.ColumnImpl.class.getName(), TableList.Column.class,
                  new Class[]{TableListGenerator.class});
            return new PropertySetRule(setter);
         }
      });
      digester.addRule("*/table-list/columns/column", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter[] setters = new PropertySetter[] {
               new StringPropertySetter("name", "setName", true),
               new IntegerPropertySetter("width", "setWidth", false),
               new StringPropertySetter("caption", "setCaption", false, false),
               new BooleanPropertySetter("ignoreGlobalTitle", "setIgnoreGlobalTitleParam", false),
               new BooleanPropertySetter("ignoreGlobal", "setIgnoreGlobalParam", false),
               new StringPropertySetter("defaultValue", "setDefaultValue", false, false),
               new StringPropertySetter("srcName", "setSrcName", false),
               new StringPropertySetter("typicalComponentName", "setTypicalComponentName", false),
               new StringPropertySetter("beforeInit", "setBeforeInit", false, false),
               new StringPropertySetter("initScript", "setInitScript", false, false),
               new StringPropertySetter("comParam", "setComponentParam", false, false),
               new StringPropertySetter("titleParam", "setTitleParam", false, false),
               new StringPropertySetter("initParam", "setInitParam", false, false),
               new BooleanPropertySetter("ignore", "setIgnore", "false") ,
               new StringPropertySetter("attributes", "setAttributes", false)
            };
            return new PropertySetRule(setters, false);
         }
      });
      digester.addRule("*/table-list/columns/column/init-param", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter setter = new BodyPropertySetter("trimLine", "setInitParam", true, false);
            return new PropertySetRule(setter, false);
         }
      });
      digester.addRule("*/table-list/columns/column/title-param", new StackRule(){
         public Rule createRule() throws Exception
         {
            PropertySetter setter = new BodyPropertySetter("trimLine", "setTitleParam", true, false);
            return new PropertySetRule(setter, false);
         }
      });
   }

   static class CheckParentSetter extends GeneratorPropertySetter
   {
      private Class[] parentTypes;

      public CheckParentSetter(String attributeName, String methodName,
            String className, Class classType, Class[] parentTypes)
      {
         super(attributeName, methodName, className, classType, false);
         this.parentTypes = parentTypes;
      }

      public Object prepareProperty(String namespace, String name, Attributes attributes)
            throws Exception
      {
         if (StackRule.checkRoot(this.digester.getMatch(), StackRule.VIEW_ROOT_PATHS))
         {
            boolean parentPass = false;
            Object obj = this.digester.peek(this.objectIndex);
            for (int i = 0; i < this.parentTypes.length; i++)
            {
               Class parentType = parentTypes[i];
               if (parentType.isInstance(obj))
               {
                  parentPass = true;
                  break;
               }
            }
            if (parentPass)
            {
               Object tmpObj = super.prepareProperty(namespace, name, attributes);
               return tmpObj;
            }
            else
            {
               throw new ConfigurationException("Error component path:" + this.digester.getMatch() + ".");
            }
         }
         else
         {
            log.error("Error component path:" + this.digester.getMatch() + ".");
            return null;
         }
      }

      protected void setMyValue()
            throws Exception
      {
         if (this.generator == null)
         {
            this.value = null;
         }
         else
         {
            super.setMyValue();
         }
      }

   }

}
