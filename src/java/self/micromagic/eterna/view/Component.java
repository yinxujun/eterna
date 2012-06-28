
package self.micromagic.eterna.view;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.share.EternaFactory;

public interface Component
{
   /**
    * ��<code>EternaFactory</code>������view�⸲�ؼ�����������.
    */
   public static final String VIEW_WRPA_TYPE_FLAG = "view.wrpa.type";

   /**
    * ��<code>EternaFactory</code>������view�⸲�ؼ��Ƿ���Ҫִ��script����������.
    */
   public static final String VIEW_WRPA_NEED_SCRIPT_FLAG = "view.wrpa.needScript";

   /**
    * ���ô����ӽڵ��ʶ�ı�ǩ.
    */
   public static final String NO_SUB_FLAG = "noSub";

   /**
    * ��ͨ�Ľڵ�����: div.
    */
   public static final String NORMAL_TYPE_DIV = "div";

   /**
    * ����Ľڵ�����: none. <p>
    * һ���սڵ�, �˽ڵ㲻������, �Ὣ���ӽڵ�ֱ�ӹҵ����ĸ��ڵ���.
    */
   public static final String SPECIAL_TYPE_NONE = "none";

   /**
    * ����Ľڵ�����: loop. <p>
    * һ��ѭ���ڵ�, �˽ڵ㲻������, �Ὣ���ӽڵ�ѭ�����ɲ��ҵ���
    * �ĸ��ڵ���.
    */
   public static final String SPECIAL_TYPE_LOOP = "loop";

   /**
    * ����Ľڵ�����: inherit. <p>
    * �˽ڵ�����ͻ��Զ���Ϊģ��ڵ��еĶ�Ӧ����.
    */
   public static final String SPECIAL_TYPE_INHERIT = "inherit";

   /**
    * ���ڴ�ű�ǩ���Ƶ�����.
    */
   public static final String FLAG_TAG = "eFlag";

   /**
    * ����inherit����, ��Ҫ����ȫ�ֲ���ģ��ڵ�ı�־.
    */
   public static final String INHERIT_GLOBAL_SEARCH = "inheritGlobalSearch";

   void initialize(EternaFactory factory, Component parent) throws ConfigurationException;

   String getName() throws ConfigurationException;

   String getType() throws ConfigurationException;

   Component getParent() throws ConfigurationException;

   Iterator getSubComponents() throws ConfigurationException;

   Iterator getEvents() throws ConfigurationException;

   boolean isIgnoreGlobalParam() throws ConfigurationException;

   String getComponentParam() throws ConfigurationException;

   String getBeforeInit() throws ConfigurationException;

   String getInitScript() throws ConfigurationException;

   /**
    * ��ȡ��Componentĳ�����õ�����.
    */
   Object getAttribute(String name) throws ConfigurationException;

   /**
    * ��ȡ��Component���õ��������Ե�����.
    */
   String[] getAttributeNames() throws ConfigurationException;

   EternaFactory getFactory() throws ConfigurationException;

   ViewAdapter.ViewRes getViewRes() throws ConfigurationException;

   void print(Writer out, AppData data, ViewAdapter view) throws IOException, ConfigurationException;

   void printBody(Writer out, AppData data, ViewAdapter view) throws IOException, ConfigurationException;

   void printSpecialBody(Writer out, AppData data, ViewAdapter view) throws IOException, ConfigurationException;

   interface Event
   {
      void initialize(Component component) throws ConfigurationException;

      String getName() throws ConfigurationException;

      String getScriptParam() throws ConfigurationException;

      String getScriptBody() throws ConfigurationException;

      Component getComponent() throws ConfigurationException;

      ViewAdapter.ViewRes getViewRes() throws ConfigurationException;

   }

}
