import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class teste {
	
	static final String db = "sid";
	static final String DBuser = "root";
	static final String DBpass = "root";
	static Connection connectionSQL;
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.cj.jdbc.Driver");
		connectionSQL= DriverManager.getConnection("jdbc:mysql://localhost/" + db + "?useTimezone=true&serverTimezone=UTC", DBuser, DBpass);
		if(connectionSQL!=null)
			System.out.println("Ligação estabelecida");
		
		String cultura= "select * from cultura";
		PreparedStatement c_aux1 = connectionSQL.prepareStatement(cultura);
		ResultSet c_aux2 = c_aux1.executeQuery();
		
		while(c_aux2.next()) {
			int idcultura= c_aux2.getInt("idcultura");
			String nomecultura= c_aux2.getString("nomecultura");
			String utilizador= c_aux2.getString("utilizador");
			int estado= c_aux2.getInt("estado");
			int idzona= c_aux2.getInt("idzona");
			int idparametro= c_aux2.getInt("idparametro");
			
			System.out.println(idcultura + nomecultura + utilizador + estado + idzona + idparametro);
		}

	}

}
