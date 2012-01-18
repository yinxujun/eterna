
package self.micromagic.eterna.digester;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.apache.commons.digester.Rule;

/**
 * Ϊ�˽������ڵ�ʹ��ͬһ��rule��ɳ�ͻ�����⣬���������rule
 */
public abstract class StackRule extends Rule
{
   private int stackIndex = 0;

   public abstract Rule createRule() throws Exception;

   private ArrayList stack = new ArrayList();

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
      if (!ViewRuleSet.checkRoot(this.digester.getMatch()))
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
      if (!ViewRuleSet.checkRoot(this.digester.getMatch()))
      {
         return;
      }
      this.getRule(this.stackIndex - 1).body(namespace, name, text);
   }

   public void end(String namespace, String name)
         throws Exception
   {
      if (!ViewRuleSet.checkRoot(this.digester.getMatch()))
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
