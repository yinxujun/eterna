
package self.micromagic.eterna.digester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSetBase;
import org.xml.sax.Attributes;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.sql.QueryAdapterGenerator;
import self.micromagic.eterna.sql.ResultFormat;
import self.micromagic.eterna.sql.ResultReader;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.eterna.sql.SQLParameterGenerator;
import self.micromagic.eterna.sql.SQLParameterGroup;
import self.micromagic.eterna.sql.SpecialLog;
import self.micromagic.eterna.sql.UpdateAdapterGenerator;
import self.micromagic.eterna.sql.impl.QueryAdapterImpl;
import self.micromagic.eterna.sql.impl.ResultFormatGeneratorImpl;
import self.micromagic.eterna.sql.impl.ResultReaderGeneratorImpl;
import self.micromagic.eterna.sql.impl.SQLParameterGeneratorImpl;
import self.micromagic.eterna.sql.impl.UpdateAdapterImpl;
import self.micromagic.eterna.sql.preparer.ValuePreparerCreaterGenerator;
import self.micromagic.eterna.sql.preparer.ValuePreparerCreaterGeneratorImpl;

/**
 * SQLģ���ʼ���Ĺ���.
 */
public class SQLRuleSet extends RuleSetBase
{
   public SQLRuleSet()
   {
   }

   public void addRuleInstances(Digester digester)
   {
      PropertySetter setter;
      PropertySetter[] setters;
      Rule rule;


      //--------------------------------------------------------------------------------
      // ���ó���(constant)�Ķ�ȡ����
      digester.addRule("eterna-config/factory/objs/constant",
            new AttributeSetRule("addConstantValue", "name", "value", String.class));


      //--------------------------------------------------------------------------------
      // ��������׼��������(ValuePreparerCreate)�Ķ�ȡ����
      rule = new ObjectCreateRule(ValuePreparerCreaterGeneratorImpl.class.getName(),
            "className", ValuePreparerCreaterGenerator.class);
      digester.addRule("eterna-config/factory/objs/vpc", rule);

      setter = new StackPropertySetter("registerValuePreparerGenerator", ValuePreparerCreaterGenerator.class, 1);
      digester.addRule("eterna-config/factory/objs/vpc", new PropertySetRule(setter));

      setters = new PropertySetter[] {
         // ValuePreparerCreate������
         new StringPropertySetter("name", "setName", true)
      };
      digester.addRule("eterna-config/factory/objs/vpc",
            new PropertySetRule(setters, false));
      digester.addRule("eterna-config/factory/objs/vpc",
            new ObjectLogRule("name", "ValuePreparerCreaterGenerator"));
      digester.addRule("eterna-config/factory/objs/vpc/attribute",
            new AttributeSetRule());


      //--------------------------------------------------------------------------------
      // ����SpecialLog
      digester.addRule("eterna-config/factory/special-log",
            new ObjectCreateRule(null, "className", SpecialLog.class));
      setter = new StackPropertySetter("setSpecialLog", SpecialLog.class, 1);
      digester.addRule("eterna-config/factory/special-log", new PropertySetRule(setter));


      //--------------------------------------------------------------------------------
      // ���ý����ʽ����(ResultFormat)�Ķ�ȡ����
      setter = new GeneratorPropertySetter("generator", "addFormat",
            ResultFormatGeneratorImpl.class.getName(), ResultFormat.class, true);
      digester.addRule("eterna-config/factory/objs/format",
            new PropertySetRule(setter));
      digester.addRule("eterna-config/factory/objs/format",
            new ObjectLogRule("name", "ResultFormat"));

      setters = new PropertySetter[] {
         // ResultFormat������
         new StringPropertySetter("name", "setName", true),
         // ResultFormat������
         new StringPropertySetter("type", "setType", true),
         // ResultFormat�ĸ�ʽ��ģʽ
         new StringPropertySetter("pattern", "setPattern", false)
      };
      digester.addRule("eterna-config/factory/objs/format",
            new PropertySetRule(setters, false));
      // ResultFormat�ĸ�ʽ��ģʽ(��xml��body������)
      setter = new BodyPropertySetter("trimLine", "setPattern", true);
      digester.addRule("eterna-config/factory/objs/format/pattern",
            new PropertySetRule(setter, false));
      digester.addRule("eterna-config/factory/objs/format/attribute",
            new AttributeSetRule());


      //--------------------------------------------------------------------------------
      // ���ý����ȡ�߹�����(ResultReaderManager)�Ķ�ȡ����
      setter = new GeneratorPropertySetter(null, "addReaderManager",
            ReaderManagerGenerator.class.getName(), ResultReaderManager.class, true);
      digester.addRule("eterna-config/factory/objs/reader-manager",
            new PropertySetRule(setter));
      digester.addRule("eterna-config/factory/objs/reader-manager",
            new ObjectLogRule("name", "ResultReaderManager"));

      setters = new PropertySetter[] {
         // ResultReaderManager������
         new StringPropertySetter("name", "setName", true),
         // ResultReaderManager�ĸ�����
         new StringPropertySetter("parent", "setParentName", false),
         // ResultReaderManager������ʽ
         new StringPropertySetter("readerOrder", "setReaderOrder", false),
         // ResultReaderManager��ʵ����
         new StringPropertySetter("className", "setClassName", false)
      };
      digester.addRule("eterna-config/factory/objs/reader-manager",
            new PropertySetRule(setters, false));

      // ����ResultReaderManager��Reader
      this.setResultReaderRule(digester, "eterna-config/factory/objs/reader-manager/reader");


      //--------------------------------------------------------------------------------
      // ���ò�����(SQLParameterGroup)�Ķ�ȡ����
      setter = new GeneratorPropertySetter(null, "addParameterGroup",
            ParameterGroupGenerator.class.getName(), SQLParameterGroup.class, true);
      digester.addRule("eterna-config/factory/objs/parameter-group",
            new PropertySetRule(setter));
      digester.addRule("eterna-config/factory/objs/parameter-group",
            new ObjectLogRule("name", "SQLParameterGroup"));

      setter = new StringPropertySetter("name", "setName", true);
      digester.addRule("eterna-config/factory/objs/parameter-group",
            new PropertySetRule(setter, false));

      // ����SQLParameterGroup��parameter
      this.setParametersRule(digester, "eterna-config/factory/objs/parameter-group");


      //--------------------------------------------------------------------------------
      // ���ò�ѯ������������(QueryAdapterGenerator)�Ķ�ȡ����
      rule = new ObjectCreateRule(QueryAdapterImpl.class.getName(), "generator",
            QueryAdapterGenerator.class);
      digester.addRule("eterna-config/factory/objs/query", rule);

      setter = new StackPropertySetter("registerQueryAdapter", QueryAdapterGenerator.class, 1);
      digester.addRule("eterna-config/factory/objs/query", new PropertySetRule(setter));

      setters = new PropertySetter[] {
         // QueryAdapterGenerator������
         new StringPropertySetter("name", "setName", true),
         // ����sql��־��¼�ķ�ʽ
         new StringPropertySetter("logType", "setLogType", false),
         // ������Ƿ�ֻ����ǰ����(����sqlserver2000����ȡtext�����͵��ֶ�ʱ�α�ֻ�ܵ������)
         new BooleanPropertySetter("forwardOnly", "setForwardOnly", false),
         // ���� order by �Ӿ����ڵ�����ֵ
         new IntegerPropertySetter("orderIndex", "setOrderIndex", false)
      };
      digester.addRule("eterna-config/factory/objs/query",
            new PropertySetRule(setters, false));
      digester.addRule("eterna-config/factory/objs/query",
            new ObjectLogRule("name", "QueryAdapter"));
      digester.addRule("eterna-config/factory/objs/query/attribute",
            new AttributeSetRule());

      // ����QueryAdapterGenerator��Ԥ��sql���
      setter = new BodyPropertySetter("trimLine", "setPreparedSQL");
      digester.addRule("eterna-config/factory/objs/query/prepared-sql",
            new PropertySetRule(setter, false));

      // ����QueryAdapterGenerator��Reader
      setters = new PropertySetter[] {
         // Reader�б����ResultReaderManager
         new StringPropertySetter("baseReaderManager", "setReaderManagerName", false),
         // Reader�б������ʽ
         new StringPropertySetter("readerOrder", "setReaderOrder", false)
      };
      digester.addRule("eterna-config/factory/objs/query/readers",
            new PropertySetRule(setters, false));
      this.setResultReaderRule(digester, "eterna-config/factory/objs/query/readers/reader");

      // ����QueryAdapterGenerator��parameter
      this.setParametersRule(digester, "eterna-config/factory/objs/query/parameters");


      //--------------------------------------------------------------------------------
      // ���ø���������������(UpdateAdapterGenerator)�Ķ�ȡ����
      rule = new ObjectCreateRule(UpdateAdapterImpl.class.getName(), "generator",
            UpdateAdapterGenerator.class);
      digester.addRule("eterna-config/factory/objs/update", rule);

      setter = new StackPropertySetter("registerUpdateAdapter", UpdateAdapterGenerator.class, 1);
      digester.addRule("eterna-config/factory/objs/update", new PropertySetRule(setter));

      setters = new PropertySetter[] {
         // UpdateAdapterGenerator������
         new StringPropertySetter("name", "setName", true),
         // ����sql��־��¼�ķ�ʽ
         new StringPropertySetter("logType", "setLogType", false)
      };
      digester.addRule("eterna-config/factory/objs/update",
            new PropertySetRule(setters, false));
      digester.addRule("eterna-config/factory/objs/update",
            new ObjectLogRule("name", "UpdateAdapter"));
      digester.addRule("eterna-config/factory/objs/update/attribute",
            new AttributeSetRule());

      // ����UpdateAdapterGenerator��Ԥ��sql���
      setter = new BodyPropertySetter("trimLine", "setPreparedSQL");
      digester.addRule("eterna-config/factory/objs/update/prepared-sql",
            new PropertySetRule(setter));

      // ����UpdateAdapterGenerator��parameter
      this.setParametersRule(digester, "eterna-config/factory/objs/update/parameters");
   }

   private void setParametersRule(Digester digester, String path)
   {
      //eterna-config/factory/objs/query/parameters
      //eterna-config/factory/objs/update/parameters

      PropertySetter setter;
      PropertySetter[] setters;
      Rule rule;

      rule = new ObjectCreateRule(SQLParameterGeneratorImpl.class.getName(), "generator",
            SQLParameterGenerator.class);
      digester.addRule(path + "/parameter", rule);

      setter = new StackPropertySetter("addParameter", SQLParameterGenerator.class, 1);
      digester.addRule(path + "/parameter", new PropertySetRule(setter));

      setters = new PropertySetter[] {
         new StringPropertySetter("name", "setName", true),
         new StringPropertySetter("colName", "setColumnName", false),
         new StringPropertySetter("type", "setParamType", false),
         new StringPropertySetter("vpcName", "setParamVPC", false)
      };
      digester.addRule(path + "/parameter", new PropertySetRule(setters, false));

      //setter = new StringPropertySetter("className", "setParameterSetterClass", false);
      //digester.addRule(path + "/parameter/setter", new PropertySetRule(setter, false));
      digester.addRule(path + "/parameter/attribute", new AttributeSetRule());

      digester.addRule(path + "/parameter-ref",
            new PropertySetRule(new ParamRefSetter("addParameterRef"), false));
   }

   private void setResultReaderRule(Digester digester, String path)
   {
      //eterna-config/factory/objs/query/readers/reader
      //eterna-config/factory/objs/reader-manager/reader

      PropertySetter setter;
      PropertySetter[] setters;

      setter = new GeneratorPropertySetter("generator", "addResultReader",
            ResultReaderGeneratorImpl.class.getName(), ResultReader.class);
      digester.addRule(path, new PropertySetRule(setter));

      setters = new PropertySetter[] {
         new StringPropertySetter("name", "setName", true),
         new StringPropertySetter("colName", "setColumnName", false),
         new IntegerPropertySetter("colIndex", "setColumnIndex", false),
         new StringPropertySetter("format", "setFormatName", false),
         new StringPropertySetter("orderName", "setOrderName", false),
         new StringPropertySetter("caption", "setCaption", false),
         new IntegerPropertySetter("width", "setWidth", false),
         new StringPropertySetter("permissions", "setPermissions", false),
         new BooleanPropertySetter("htmlFilter", "setHtmlFilter", "true"),
         new StringPropertySetter("type", "setType", false),
         new BooleanPropertySetter("visible", "setVisible", "true")
      };
      digester.addRule(path, new PropertySetRule(setters, false));
      digester.addRule(path + "/attribute", new AttributeSetRule());
   }

   public static class ReaderManagerGenerator extends AbstractGenerator
         implements Generator
   {
      private String className = "self.micromagic.eterna.sql.impl.ResultReaderManagerImpl";
      private String parentName = null;
      private String readerOrder = null;
      private ArrayList readers = new ArrayList();
      private HashMap readerNameMap = new HashMap();

      public void setParentName(String parentName)
      {
         this.parentName = parentName;
      }

      public void setClassName(String className)
      {
         this.className = className;
      }

      public void setReaderOrder(String readerOrder)
      {
         this.readerOrder = readerOrder;
      }

      public void addResultReader(ResultReader reader)
         throws ConfigurationException
      {
         if (this.readerNameMap.containsKey(reader.getName()))
         {
            throw new ConfigurationException(
                  "Duplicate [ResultReader] name:" + reader.getName() + ".");
         }
         this.readers.add(reader);
         this.readerNameMap.put(reader.getName(), reader);
      }

      public Object create() throws ConfigurationException
      {
         ResultReaderManager rm;
         try
         {
            rm = (ResultReaderManager) ObjectCreateRule.createObject(this.className);
         }
         catch (Exception ex)
         {
            throw new ConfigurationException(ex.getMessage());
         }
         rm.setName(this.name);
         rm.setParentName(this.parentName);
         rm.setReaderOrder(this.readerOrder);
         Iterator itr = this.readers.iterator();
         while (itr.hasNext())
         {
            rm.addReader((ResultReader) itr.next());
         }
         return rm;
      }

   }

   public static class ParameterGroupGenerator extends AbstractGenerator
         implements Generator
   {
      private String className = "self.micromagic.eterna.sql.impl.SQLParameterGroupImpl";
      private ArrayList params = new ArrayList();
      private HashMap paramNameMap = new HashMap();

      public void setClassName(String className)
      {
         this.className = className;
      }

      public void addParameter(SQLParameterGenerator paramGenerator)
         throws ConfigurationException
      {
         if (this.paramNameMap.containsKey(paramGenerator.getName()))
         {
            throw new ConfigurationException(
                  "Duplicate [SQLParameter] name:" + paramGenerator.getName() + ".");
         }
         this.params.add(paramGenerator);
         this.paramNameMap.put(paramGenerator.getName(), paramGenerator);
      }

      public void addParameterRef(String groupName, String ignoreList)
            throws ConfigurationException
      {
         this.params.add(new String[]{groupName, ignoreList});
      }

      public Object create() throws ConfigurationException
      {
         SQLParameterGroup group;
         try
         {
            group = (SQLParameterGroup) ObjectCreateRule.createObject(this.className);
         }
         catch (Exception ex)
         {
            throw new ConfigurationException(ex.getMessage());
         }
         group.setName(this.name);
         Iterator itr = this.params.iterator();
         while (itr.hasNext())
         {
            Object tmp = itr.next();
            if (tmp instanceof SQLParameterGenerator)
            {
               group.addParameter((SQLParameterGenerator) tmp);
            }
            else
            {
               String[] arr = (String[]) tmp;
               group.addParameterRef(arr[0], arr[1]);
            }
         }
         return group;
      }

   }

   public static class ParamRefSetter extends SinglePropertySetter
   {
      public ParamRefSetter(String methodName)
      {
         super("groupName", methodName, null);
         this.type = new Class[]{String.class, String.class};
      }

      public Object prepareProperty(String namespace, String name, Attributes attributes)
            throws Exception
      {
         String groupName = this.getValue(namespace, name, attributes);
         String ignoreList = attributes.getValue("ignoreList");
         this.value = new String[]{groupName, ignoreList};
         return this.value;
      }

   }

}

