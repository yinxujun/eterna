
package self.micromagic.eterna.security;

import org.apache.commons.logging.Log;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.util.StringRef;
import self.micromagic.util.Utility;

public interface UserManager
{
   public final static Log log = Utility.createLog("user");
   public final static String ETERNA_USER = "self.micromagic.eterna.user";

   /**
    * ��ʼ�����UserManager.
    */
   void initUserManager(EternaFactory factory) throws ConfigurationException;

   /**
    * ��ù����UserManager�Ĺ���.
    */
   EternaFactory getFactory() throws ConfigurationException;

   /**
    * �Ƿ����Ȩ�޵ı��. <p>
    * �Ƿ�ɽ�Ȩ������ת�������ֱ��.
    */
   boolean hasPermissionId();

   /**
    * ��Ȩ������ת�������ֱ��. <p>
    * ����޷�ת��, ���ǲ����ڵ�����, �򷵻�-1.
    */
   int getPermissionId(String permissionName);

   /**
    * ��õ�ǰ��User����.
    */
   User getUser(AppData data);

   /**
    * ��õ�ǰ�����û���id.
    */
   String getLoginId(AppData data);

   /**
    * �����û��ĵ���, �����ص����User����.
    *
    * @param userId     �û���id
    * @param password   ����
    * @param msg        ����ʧ�ܵķ�����Ϣ.
    * @return    �������ʧ�ܵĻ�, �򷵻�null, ���򷵻�User����.
    */
   User login(AppData data, String userId, String password, StringRef msg);

   /**
    * ע���û��ĵ���.
    *
    * @param userId   �û���id
    */
   void logout(AppData data, String userId);

}


