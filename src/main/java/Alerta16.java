import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Alerta16 {
	static final String db = "sid";
	static final String DBuser = "root";
	static final String DBpass = "root";
	static Connection connectionSQL;

	public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException {

		Class.forName("com.mysql.cj.jdbc.Driver");
		connectionSQL= DriverManager.getConnection("jdbc:mysql://localhost/" + db + "?useTimezone=true&serverTimezone=UTC", DBuser, DBpass);

		if(connectionSQL!=null)
			System.out.println("Ligação estabelecida");

		String medicao= "select * from medicao";
		PreparedStatement m_aux1 = connectionSQL.prepareStatement(medicao);
		ResultSet m_aux2 = m_aux1.executeQuery();


		while(m_aux2.next()) {
			int idmedicao= m_aux2.getInt("idmedicao");
			int m_idzona= m_aux2.getInt("idzona");
			String m_idsensor= m_aux2.getString("idsensor");
			String m_datahora= m_aux2.getString("datahora");
			double m_leitura= m_aux2.getDouble("leitura");

			System.out.println("---medicao--");
			System.out.println("id:" + idmedicao + " zona:" + m_idzona + " sensor:" + m_idsensor + " DataHora:" + m_datahora + " leitura:" + m_leitura);
			if(m_idsensor.contains("T"))
				System.out.println("Sensor temperatura");
			if(m_idsensor.contains("H"))
				System.out.println("Sensor humidade");

			String cultura= "SELECT * FROM cultura WHERE idzona=" + m_idzona;
			PreparedStatement c_aux1 = connectionSQL.prepareStatement(cultura);
			ResultSet c_aux2 =c_aux1.executeQuery();

			while(c_aux2.next()) {
				int c_idzona= c_aux2.getInt("idzona");
				int c_idparametro= c_aux2.getInt("idparametro");
				System.out.println("---cultura---");
				System.out.println("zona:" + c_idzona);
				System.out.println("parametro:" + c_idparametro);
				System.out.println(c_aux2.next());
				String parametro= "SELECT * FROM parametro WHERE idparametro=" + c_idparametro;
				PreparedStatement p_aux1 = connectionSQL.prepareStatement(parametro);
				ResultSet p_aux2 =p_aux1.executeQuery();

				while(p_aux2.next()) {
					int p_idparametro= p_aux2.getInt("idparametro");						
					double p_maxtemp= p_aux2.getDouble("maxtemperatura");
					double p_mintemp= p_aux2.getDouble("mintemperatura");	
					double p_maxhum= p_aux2.getDouble("maxhumidade");
					double p_minhum=p_aux2.getDouble("minhumidade");
					double p_maxl= p_aux2.getDouble("maxluz");
					double p_minl=p_aux2.getDouble("minluz");
					System.out.println("---parametro---");
					System.out.println("parametro:" + p_idparametro);
					System.out.println("maxluz:" + m_idsensor);
					System.out.println(p_aux2.next());
					System.out.println("pote");
					if(m_idsensor.contains("T")) {	
						System.out.println("Sensor temperatura");
						if(m_leitura>p_maxtemp || m_leitura<p_mintemp)
							System.out.println("ALERTA TEMPERATURA");
//						if(m_leitura==p_maxtemp*0.8 || m_leitura==p_mintemp*0.2+p_mintemp)
//							System.out.println("ALERTA TEMPERATURA 20%");
						if(m_leitura==p_maxtemp*0.85 || m_leitura==p_mintemp*0.15+p_mintemp)
							System.out.println("ALERTA TEMPERATURA 85%");
						if(m_leitura==p_maxtemp*0.9 || m_leitura==p_mintemp*0.1+p_mintemp)
							System.out.println("ALERTA TEMPERATURA 90%");
						if(m_leitura==p_maxtemp*0.99 || m_leitura==p_mintemp*0.01+p_mintemp)
							System.out.println("ALERTA TEMPERATURA 99%");
					}
					if(m_idsensor.contains("H")) {
						System.out.println("Sensor humidade");
						if(m_leitura>p_maxhum || m_leitura<p_minhum)
							System.out.println("ALERTA HUMIDADE");
//						if(m_leitura==p_maxhum*0.8 || m_leitura==p_minhum*0.2+p_minhum)
//							System.out.println("ALERTA HUMIDADE 20%");
						if(m_leitura==p_maxhum*0.85 || m_leitura==p_minhum*0.15+p_minhum)
							System.out.println("ALERTA HUMIDADE 85%");
						if(m_leitura==p_maxhum*0.9 || m_leitura==p_minhum*0.1+p_minhum)
							System.out.println("ALERTA HUMIDADE 90%");
						if(m_leitura==p_maxhum*0.01 || m_leitura==p_minhum*0.01+p_minhum)
							System.out.println("ALERTA HUMIDADE 99%");
					}
					if(m_idsensor.contains("L")) {
						System.out.println("Sensor luz");
						if(m_leitura>p_maxl || m_leitura<p_minl)
							System.out.println("ALERTA LUZ");
//						if(m_leitura==p_maxl*0.8 || m_leitura==p_minl*0.2+p_minl)
//							System.out.println("ALERTA LUZ 20%");
						if(m_leitura==p_maxl*0.85 || m_leitura==p_minl*0.15+p_minl)
							System.out.println("ALERTA HUMIDADE 85%");
						if(m_leitura==p_maxl*0.9 || m_leitura==p_minl*0.1+p_minl)
							System.out.println("ALERTA HUMIDADE 90%");
						if(m_leitura==p_maxl*0.01 || m_leitura==p_minl*0.01+p_minl)
							System.out.println("ALERTA HUMIDADE 99%");
						
					}
				}
			}
			Thread.sleep(1000);				
		}
	}
}
