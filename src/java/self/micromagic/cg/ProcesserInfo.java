
package self.micromagic.cg;

/**
 * ��������Ϣ��.
 */
class ProcesserInfo
{
   public final String name;
   public final Class type;
   public final Object processer;

   public ProcesserInfo(String name, Class type, Object processer)
   {
      this.name = name;
      this.type = type;
      this.processer = processer;
   }

}