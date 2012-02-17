
package self.micromagic.eterna.digester;

/**
 * 当配置中没有设置需要的属性或子节点时, 会抛出此异常.
 */
public class InvalidAttributesException extends Exception
{
   public InvalidAttributesException()
   {
      super();
   }

   public InvalidAttributesException(String message)
   {
      super(message);
   }

}
