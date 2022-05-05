
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MysqlConnection {
	static final String db = "sid2022";
	static final String DBuser = "aluno";
	static final String DBpass = "aluno";
	static Connection connectionSQL;

	public static void main(String[] args) throws SQLException, ClassNotFoundException, InterruptedException, ParseException {

		Class.forName("com.mysql.cj.jdbc.Driver");
		connectionSQL= DriverManager.getConnection("jdbc:mysql://194.210.86.10/" +db+ "?useTimezone=true&serverTimezone=UTC", DBuser, DBpass);		
		if(connectionSQL!=null) {
			System.out.println("Ligação estabelecida");
		}
		
		String parametro= "SELECT * FROM sensor";
		PreparedStatement p_aux1 = connectionSQL.prepareStatement(parametro);
		ResultSet p_aux2 =p_aux1.executeQuery();
		
		while(p_aux2.next()) {
			
				double x= p_aux2.getDouble("limitesuperior");
				System.out.println(x);
			
		}
	
//		DateFormat dateFormatSQL = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
//		
//		int i= 4000;
//		int idzona = 1;
//		int idsensor = 1;
//	
//		for (;;) {
//			Date now = new Date();
//			String hora = dateFormatSQL.format(now);
//			Random r = new Random();
//			double leitura = r.nextDouble()*3.0 + 20.0;
//			//double leitura = 20.0;
//			String query = "INSERT INTO medicao1 (idmedicao, idzona, idsensor, datahora, leitura, valorvalido) VALUES ('" + i + "', '"+ idzona + "', '" + idsensor +"', '"+ hora +"', '"+ leitura + "', '1');";
//			String query1="INSERT INTO parametro (maxluz, maxtemperatura, maxhumidade, minluz, mintemperatura, minhumidade) values ('10', '10', '10', '10', '10', '10')";
//			System.out.println(query1);
//			connectionSQL.createStatement().executeUpdate(query1);
//			i++;
//			idzona++;
//			idsensor++;
			//Thread.sleep(2000);
		//}
	}
}
