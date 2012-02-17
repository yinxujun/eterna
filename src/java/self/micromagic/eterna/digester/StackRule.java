
package self.micromagic.eterna.digester;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.apache.commons.digester.Rule;

/**
 * ��Ҫ�������ڵ�ʹ��ͬһ����ʼ��������ɳ�ͻ�����⣬����ʹ�������ʼ������. <p>
 *
 * ʹ�ô˹����������������:
 * <blockquote><pre>
 * new StackRule() {
 *    public Rule createRule() throws Exception
 *    {
 *       PropertySetter setter = new BodyPropertySetter("trimLine",
 *             "setBeforeInit", true, false);
 *       return new PropertySetRule(setter, false);
 *    }
 * };
 * </pre></blockquote>
 * ͨ������һ��������, ʵ��createRule����, �ڴ˷���������һ��������Ҫ�ĳ�ʼ��
 * ����. ��Ϊÿһ���ڵ���Ҫ��ͬ�ĳ�ʼ������ʵ��, �����ʼ��������Ǵ�������Ҫ
 * �ĵط������µ�ʵ��.
 */
public abstract class StackRule extends Rule
{
   static final String[] VIEW_ROOT_PATHS = {
      "eterna-config/factory/objs/view",
      "eterna-config/factory/objs/typical-replacement",
      "eterna-config/factory/objs/typical-component",
   };
   static final String[] ALL_ROOT_PATHS = VIEW_ROOT_PATHS;

   private int stackIndex = 0;
   private ArrayList stack = new ArrayList();

   /**
    * ��鵱ǰ�ڵ��·���Ƿ���ϴ˳�ʼ������.
    *
    * @param path      ������·��
    * @param paths     ����������·����
    */
   static boolean checkRoot(String path, String[] paths)
   {
      for (int i = 0; i < paths.length; i++)
      {
         if (path.startsWith(paths[i]))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * ������Ҫ�ĳ�ʼ�������ʵ��, ʵ�ֵķ�������Ҫ����һ���µĳ�ʼ������,
    * ����ͻᷢ�ͳ�ͻ.
    */
   public abstract Rule createRule() throws Exception;

   private Rule getRule(int stackIndex)
         throws Exception
   {
      if (this.stack.size() == stackIndex)
      {
         Rule rule = this.createRule();
         rule.setDigester(this.getDigester());
         rule.setNamespaceURI(this.getNamespaceURI());
         this.stack.add(rule);
      }
      return (Rule) this.stack.get(stackIndex);
   }

   public void begin(String namespace, String name, Attributes attributes)
         throws Exception
   {
      if (!checkRoot(this.digester.getMatch(), ALL_ROOT_PATHS))
      {
         this.digester.getLogger().error("Error component path:" + this.digester.getMatch() + ".");
         return;
      }
      this.stackIndex++;
      this.getRule(this.stackIndex - 1).begin(namespace, name, attributes);
   }

   public void body(String namespace, String name, String text)
         throws Exception
   {
      if (!checkRoot(this.digester.getMatch(), ALL_ROOT_PATHS))
      {
         return;
      }
      this.getRule(this.stackIndex - 1).body(namespace, name, text);
   }

   public void end(String namespace, String name)
         throws Exception
   {
      if (!checkRoot(this.digester.getMatch(), ALL_ROOT_PATHS))
      {
         return;
      }
      this.stackIndex--;
      this.getRule(this.stackIndex).end(namespace, name);
   }

   public void finish() throws Exception
   {
      this.getRule(0).finish();
   }

}
