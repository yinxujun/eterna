
package self.micromagic.eterna.security;

import java.util.Arrays;

import self.micromagic.eterna.security.Permission;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.digester.ConfigurationException;

public class PermissionSet
{
   private int[] permissionIds = null;
   private String[] permissionNames = null;

   private int hashCode = 0;

   public PermissionSet(int[] permissionIds)
   {
      if (permissionIds != null && permissionIds.length > 0)
      {
         int count = permissionIds.length;
         this.permissionIds = new int[count];
         System.arraycopy(permissionIds, 0, this.permissionIds, 0, count);
         Arrays.sort(this.permissionIds);
      }
   }

   public PermissionSet(String[] permissionNames)
   {
      if (permissionNames != null && permissionNames.length > 0)
      {
         int count = permissionNames.length;
         this.permissionNames = new String[count];
         System.arraycopy(permissionNames, 0, this.permissionNames, 0, count);
         for (int i = 0; i < this.permissionNames.length; i++)
         {
            if (this.permissionNames[i] == null)
            {
               this.permissionNames[i] = "";
            }
         }
         Arrays.sort(this.permissionNames);
      }
   }

   public void initialize(EternaFactory factory)
         throws ConfigurationException
   {
      if (this.permissionIds == null && this.permissionNames != null)
      {
         UserManager um = factory.getUserManager();
         if (um != null && um.hasPermissionId())
         {
            this.permissionIds = new int[this.permissionNames.length];
            for (int i = 0; i < this.permissionNames.length; i++)
            {
               this.permissionIds[i] = um.getPermissionId(this.permissionNames[i]);
            }
         }
      }
   }

   /**
    * ������permission���Ƿ������Ȩ�޼����е�ĳ��Ȩ��.
    *
    * ע:
    * �������Ϊpermission��, �򷵻�true.
    * �����Ȩ�޼���Ϊ��, ��Ҳ����true.
    */
   public boolean checkPermission(Permission permission)
   {
      if (permission == null)
      {
         return true;
      }
      String[] pnames = this.permissionNames;
      int[] pids = this.permissionIds;
      if (pnames == null && pids == null)
      {
         return true;
      }
      if (pids != null)
      {
         for (int i = 0; i < pids.length; i++)
         {
            if (permission.hasPermission(pids[i]))
            {
               return true;
            }
         }
      }
      else
      {
         for (int i = 0; i < pnames.length; i++)
         {
            if (permission.hasPermission(pnames[i]))
            {
               return true;
            }
         }
      }
      return false;
   }

   public int hashCode()
   {
      if (this.hashCode == 0)
      {
         if (this.permissionIds != null)
         {
            for (int i = 0; i < this.permissionIds.length; i++)
            {
               this.hashCode += this.permissionIds[i];
            }
         }
         else if (this.permissionNames != null)
         {
            for (int i = 0; i < this.permissionNames.length; i++)
            {
               this.hashCode += this.permissionNames[i].hashCode();
            }
         }
      }
      return this.hashCode;
   }

   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (obj instanceof PermissionSet)
      {
         PermissionSet other = (PermissionSet) obj;
         if (this.permissionIds != null && other.permissionIds != null)
         {
            if (this.permissionIds.length == other.permissionIds.length)
            {
               for (int i = 0; i < this.permissionIds.length; i++)
               {
                  if (this.permissionIds[i] != other.permissionIds[i])
                  {
                     return false;
                  }
               }
               return true;
            }
         }
         else if (this.permissionNames != null && other.permissionNames != null)
         {
            if (this.permissionNames.length == other.permissionNames.length)
            {
               for (int i = 0; i < this.permissionNames.length; i++)
               {
                  if (!this.permissionNames[i].equals(other.permissionNames[i]))
                  {
                     return false;
                  }
               }
               return true;
            }
         }
      }
      return false;
   }

}
