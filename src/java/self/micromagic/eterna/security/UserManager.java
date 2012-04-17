
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
    * 初始化这个UserManager.
    */
   void initUserManager(EternaFactory factory) throws ConfigurationException;

   /**
    * 获得构造此UserManager的工厂.
    */
   EternaFactory getFactory() throws ConfigurationException;

   /**
    * 是否存在权限的编号. <p>
    * 是否可将权限名称转换成数字编号.
    */
   boolean hasPermissionId();

   /**
    * 将权限名称转换成数字编号. <p>
    * 如果无法转换, 或是不存在的名称, 则返回-1.
    */
   int getPermissionId(String permissionName);

   /**
    * 获得当前的User对象.
    */
   User getUser(AppData data);

   /**
    * 获得当前登入用户的id.
    */
   String getLoginId(AppData data);

   /**
    * 处理用户的登入, 并返回登入的User对象.
    *
    * @param userId     用户的id
    * @param password   密码
    * @param msg        登入失败的返回信息.
    * @return    如果登入失败的话, 则返回null, 否则返回User对象.
    */
   User login(AppData data, String userId, String password, StringRef msg);

   /**
    * 注销用户的登入.
    *
    * @param userId   用户的id
    */
   void logout(AppData data, String userId);

}


