package tool;
 
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ����h2���ݿ��Ƿ��ܹ���ȡ.
 */
public class H2Test 
{
	public static void main(String[] params) 
			throws SQLException, ClassNotFoundException
	{
		Class.forName("org.h2.Driver");
		String filePath = H2Test.class.getResource("H2Test.class").getFile();
		File thisFile = new File(filePath);
		// ��ȡ��Ŀ���ڵ�Ŀ¼ test/build/classes/tool/H2Test.class, ��Ҫ4��parent��testĿ¼
		File projectPath = thisFile.getParentFile().getParentFile().getParentFile().getParentFile();
		System.out.println("projectPath:" + projectPath);
		// ��ȡ���ݿ��ļ����ڵ�Ŀ¼, test//WebContent/WEB-INF/db
		String baseDir = projectPath + "/WebContent/WEB-INF/db";
		Connection conn = DriverManager.getConnection("jdbc:h2:" + baseDir + "/test", "sa", "sa"); 
		ResultSet rs = conn.createStatement().executeQuery("select * from t_student");
		while (rs.next())
		{
			System.out.print(rs.getString(1) + ",");
			System.out.print(rs.getString(2) + ",");
			System.out.print(rs.getString(3) + ",");
			System.out.print(rs.getString(4) + ",");
			System.out.println(rs.getString(5)); 
		}
		rs.close();
		rs = conn.createStatement().executeQuery("select * from my_table");
		while (rs.next())
		{
			System.out.print(rs.getString(1) + ",");
			System.out.print(rs.getString(2) + ",");
			System.out.println(rs.getString(3));
		}
		conn.close();
	}
	
}
