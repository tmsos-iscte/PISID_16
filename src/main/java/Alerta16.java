import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Alerta16 {
	static final String db = "sid";
	static final String DBuser = "root";
	static final String DBpass = "root";
	static Connection connectionSQL;

	static final String db1 = "sid2022";
	static final String DBuser1 = "aluno";
	static final String DBpass1 = "aluno";
	static Connection connectionSQL1;
	
	static int i;
	static Timestamp aux_medicao= null;

	public static boolean diferenceMinutes(Timestamp t1, Timestamp t2){
		long milliseconds= t2.getTime()-t1.getTime();
		int seconds= (int) milliseconds/1000;
		int minutes= (seconds%3600)/60;
		seconds =(seconds%3600)%60;
		if(minutes> 5)
			return true;
		return false;
	}
	
	public static ArrayList<Double>[] getListasT(int x) {
		ArrayList<Double>[] t= (ArrayList<Double>[]) new ArrayList[i];
		for(int index= 0;index<x; index++) {
			t[index] = new ArrayList<Double>();
		}
		System.out.println("ROCHEMBACK" + t.length);
		return t;
	}

	public static ArrayList<Double>[] getListasH(int x) {
		ArrayList<Double>[] h= (ArrayList<Double>[]) new ArrayList[i];
		for(int index= 0;index<x; index++) {
			h[index] = new ArrayList<Double>();
		}
		System.out.println("ROCHEMBACK" + h.length);
		return h;
	}

	public static ArrayList<Double>[] getListasL(int x) {
		ArrayList<Double>[] l= (ArrayList<Double>[]) new ArrayList[i];
		for(int index= 0;index<x; index++) {
			l[index] = new ArrayList<Double>();
		}
		System.out.println("ROCHEMBACK" + l.length);
		return l;
	}

	public static void Alerta(List<Double> t, List<Double> h, List<Double> l) throws ClassNotFoundException, SQLException, InterruptedException {

		Class.forName("com.mysql.cj.jdbc.Driver");
		connectionSQL= DriverManager.getConnection("jdbc:mysql://localhost/" + db + "?useTimezone=true&serverTimezone=UTC", DBuser, DBpass);

		if(connectionSQL!=null)
			System.out.println("Ligação estabelecida");

		String query_alerta = "INSERT INTO alerta (idzona, idsensor, datahora, tipoalerta, nomecultura, descricao, utilizador, idcultura)" + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement preparedStmt = connectionSQL.prepareStatement(query_alerta);

		String descricao= null;

		String medicao= "SELECT * FROM medicao ORDER BY IDMedicao DESC";
		PreparedStatement m_aux1 = connectionSQL.prepareStatement(medicao);
		ResultSet m_aux2 = m_aux1.executeQuery();
		m_aux2.next();

		int idmedicao= m_aux2.getInt("idmedicao");
		int m_idzona= m_aux2.getInt("idzona");
		String m_idsensor= m_aux2.getString("idsensor");
		Timestamp m_datahora= m_aux2.getTimestamp("datahora");
		double m_leitura= m_aux2.getDouble("leitura");

		System.out.println("---medicao--");
		System.out.println("id:" + idmedicao + " zona:" + m_idzona + " sensor:" + m_idsensor + " DataHora:" + m_datahora + " leitura:" + m_leitura);

		String cultura= "SELECT * FROM cultura WHERE idzona=" + m_idzona;
		PreparedStatement c_aux1 = connectionSQL.prepareStatement(cultura);
		ResultSet c_aux2 =c_aux1.executeQuery();

		while(c_aux2.next()) {

			int c_idzona= c_aux2.getInt("idzona");
			int c_idparametro= c_aux2.getInt("idparametro");
			int c_idcultura= c_aux2.getInt("idcultura");
			String c_nomecultura = c_aux2.getString("nomecultura");
			String c_utilizador = c_aux2.getString("utilizador");
			System.out.println("---cultura---");
			System.out.println("zona:" + c_idzona);
			System.out.println("parametro:" + c_idparametro);
			System.out.println(c_aux2.next());
			String parametro= "SELECT * FROM parametro WHERE idparametro=" + c_idparametro;
			PreparedStatement p_aux1 = connectionSQL.prepareStatement(parametro);
			ResultSet p_aux2 =p_aux1.executeQuery();				

			String alerta= "SELECT * FROM alerta WHERE idcultura="+c_idcultura+" AND idsensor='"+m_idsensor+"' ORDER BY idalerta DESC";
			PreparedStatement a_aux1= connectionSQL.prepareStatement(alerta);
			ResultSet a_aux2= a_aux1.executeQuery();
			a_aux2.next();

			while(p_aux2.next()) {

				int p_idparametro= p_aux2.getInt("idparametro");						
				double p_maxtemp= p_aux2.getDouble("maxtemperatura");
				double p_mintemp= p_aux2.getDouble("mintemperatura");	
				double p_maxhum= p_aux2.getDouble("maxhumidade");
				double p_minhum=p_aux2.getDouble("minhumidade");
				double p_maxl= p_aux2.getDouble("maxluz");
				double p_minl=p_aux2.getDouble("minluz");

				System.out.println("meli" + c_idcultura);
				System.out.println("viola"+ m_idsensor);


				System.out.println("---parametro---");
				System.out.println("parametro:" + p_idparametro);
				System.out.println("maxluz:" + m_idsensor);
				System.out.println(p_aux2.next());
				System.out.println("pote");

				if(m_idsensor.contains("T")) {	
					System.out.println("Sensor temperatura");

					if(m_leitura>p_maxtemp) {
						descricao = "ALERTA TEMPERATURA MÁXIMA ULTRAPASSADA";
						preparedStmt.setInt(1, m_idzona);
						preparedStmt.setString(2, m_idsensor);
						preparedStmt.setTimestamp(3, m_datahora);
						preparedStmt.setString(4, "T");
						preparedStmt.setString(5, c_nomecultura);
						preparedStmt.setString(6, descricao);
						preparedStmt.setString(7, c_utilizador);
						preparedStmt.setInt(8, c_idcultura);
						preparedStmt.execute();
						String query = "UPDATE sid.cultura SET Estado=0 WHERE IDCultura=" + c_idcultura +";" ;
						connectionSQL.createStatement().executeUpdate(query);
						System.out.println("AMORIM");
					}
					if(m_leitura<p_mintemp) {
						descricao = "ALERTA TEMPERATURA MÍNIMA ULTRAPASSADA";
						preparedStmt.setInt(1, m_idzona);
						preparedStmt.setString(2, m_idsensor);
						preparedStmt.setTimestamp(3, m_datahora);
						preparedStmt.setString(4, "T");
						preparedStmt.setString(5, c_nomecultura);
						preparedStmt.setString(6, descricao);
						preparedStmt.setString(7, c_utilizador);
						preparedStmt.setInt(8, c_idcultura);
						preparedStmt.execute();
						String query = "UPDATE sid.cultura SET Estado=0 WHERE IDCultura=" + c_idcultura +";" ;
						connectionSQL.createStatement().executeUpdate(query);
						System.out.println("AMORIM");
					}

					t.add(m_leitura);
					if(t.size()==600) {
						Double first=t.get(0);
						Double last= t.get(599);
						if(last*1.2==first||first*1.2==last) {
							descricao = "TEMPERATURA AUMENTOU 20% EM 5 MINUTOS";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "T");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
							System.out.println("AMORIM");
						}
						t.remove(599);
					}

					if(m_leitura >=p_maxtemp*0.85 && m_leitura < p_maxtemp*0.90 ) {  
						int a= a_aux2.getRow();
						System.out.println("BOJINOV" + a);
						if(a==0) { 
							descricao = "ALERTA TEMPERATURA A 15% DO MÁXIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "T");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
							System.out.println("AMORIM");
						} else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA TEMPERATURA A 15% DO MÁXIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "T");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}
						}
					}
					if(m_leitura<=p_mintemp*0.15+p_mintemp && m_leitura>p_mintemp*0.10+p_mintemp  ) { 
						int a= a_aux2.getRow();
						System.out.println("BOJINOV" + a);
						if(a==0) {
							descricao = "ALERTA TEMPERATURA A 15% DO MÍNIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "T");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
							System.out.println("AMORIM");

						}
						else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA TEMPERATURA A 15% DO MÍNIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "T");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}
						}
					}
					if(m_leitura>=p_maxtemp*0.9 && m_leitura<p_maxtemp*0.99) {
						int a= a_aux2.getRow();
						System.out.println("BOJINOV" + a);
						if(a==0) {
							descricao = "ALERTA TEMPERATURA A 10% DO MÁXIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "T");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
							System.out.println("AMORIM");
						}
						else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA TEMPERATURA A 10% DO MÁXIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "T");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}
						}
					}
					if(m_leitura<=p_mintemp*0.1+p_mintemp && m_leitura>p_mintemp*0.01+p_mintemp) {
						int a= a_aux2.getRow();
						if(a==0) {
							descricao = "ALERTA TEMPERATURA A 10% DO MÍNIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "T");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
							System.out.println("AMORIM");

						}
						else {
							System.out.println("PIRIS:" + a_aux2.getInt(1));
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA TEMPERATURA A 10% DO MÍNIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "T");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}
						}
					}
					if(m_leitura>=p_maxtemp*0.99 && m_leitura<p_maxtemp) {  
						int a= a_aux2.getRow();
						if(a==0) {
							descricao = "ALERTA TEMPERATURA A 1% DO MÁXIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "T");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
							System.out.println("AMORIM");

						}
						else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA TEMPERATURA A 1% DO MÁXIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "T");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}
						}
					}
					if(m_leitura<=p_mintemp*0.01+p_mintemp && m_leitura>p_mintemp) {
						int a= a_aux2.getRow();
						System.out.println("BOJINOV" + a);
						if(a==0) {
							descricao = "ALERTA TEMPERATURA A 1% DO MÍNIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "T");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
							System.out.println("AMORIM");

						}else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA TEMPERATURA A 1% DO MÍNIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "T");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}
						}
					}		
				}

				if(m_idsensor.contains("H")) {
					System.out.println("Sensor humidade");

					if(m_leitura>p_maxhum) {
						descricao = "ALERTA HUMIDADE MÁXIMA ULTRAPASSADA";
						descricao = "ALERTA TEMPERATURA MÁXIMA ULTRAPASSADA";
						preparedStmt.setInt(1, m_idzona);
						preparedStmt.setString(2, m_idsensor);
						preparedStmt.setTimestamp(3, m_datahora);
						preparedStmt.setString(4, "H");
						preparedStmt.setString(5, c_nomecultura);
						preparedStmt.setString(6, descricao);
						preparedStmt.setString(7, c_utilizador);
						preparedStmt.setInt(8, c_idcultura);
						preparedStmt.execute();
						String query = "UPDATE sid.cultura SET Estado=0 WHERE IDCultura=" + c_idcultura +";" ;
						connectionSQL.createStatement().executeUpdate(query);
						System.out.println("AMORIM");
					}

					if(m_leitura<p_minhum) {
						descricao = "ALERTA HUMIDADE MÍNIMA ULTRAPASSADA";
						preparedStmt.setInt(1, m_idzona);
						preparedStmt.setString(2, m_idsensor);
						preparedStmt.setTimestamp(3, m_datahora);
						preparedStmt.setString(4, "H");
						preparedStmt.setString(5, c_nomecultura);
						preparedStmt.setString(6, descricao);
						preparedStmt.setString(7, c_utilizador);
						preparedStmt.setInt(8, c_idcultura);
						preparedStmt.execute();
						String query = "UPDATE sid.cultura SET Estado=0 WHERE IDCultura=" + c_idcultura +";" ;
						connectionSQL.createStatement().executeUpdate(query);
						System.out.println("AMORIM");
					}

					h.add(m_leitura);
					if(h.size()==600) {
						Double first=h.get(0);
						Double last= h.get(599);
						if(last*1.2==first||first*1.2==last) {
							descricao = "HUMIDADE AUMENTOU 20% EM 5 MINUTOS";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "H");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
							System.out.println("AMORIM");
						}
						h.remove(599);
					}

					if(m_leitura >=p_maxhum*0.85 && m_leitura < p_maxhum*0.90 ) {  
						int a= a_aux2.getRow();
						if(a==0) {
							descricao = "ALERTA HUMIDADE A 15% DO MÁXIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "H");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
						}
						else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA HUMIDADE A 15% DO MÁXIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
							}
						}
					}
					if(m_leitura<=p_minhum*0.15+p_minhum && m_leitura>p_minhum*0.10+p_minhum) {
						int a= a_aux2.getRow();
						if(a==0) {
							descricao = "ALERTA HUMIDADE A 15% DO MÍNIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "H");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
						}
						else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA HUMIDADE A 15% DO MÍNIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
							}
						}
					}
					if(m_leitura>=p_maxhum*0.9 && m_leitura<p_maxhum*0.99) {
						int a= a_aux2.getRow();
						if(a==0) {
							descricao = "ALERTA HUMIDADE A 10% DO MÁXIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "H");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
						}
						else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA HUMIDADE A 10% DO MÁXIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
							}
						}
					}
					if(m_leitura<=p_minhum*0.1+p_minhum && m_leitura>p_minhum*0.01+p_minhum) {
						int a= a_aux2.getRow();
						if(a==0) {
							descricao = "ALERTA HUMIDADE A 10% DO MÍNIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "H");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
						}
						else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA HUMIDADE A 10% DO MÍNIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
							}
						}
					}
					if(m_leitura>=p_maxhum*0.99 && m_leitura<p_maxhum) {  
						int a= a_aux2.getRow();
						if(a==0) {
							descricao = "ALERTA HUMIDADE A 1% DO MÁXIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "H");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
						}
						else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA HUMIDADE A 1% DO MÁXIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
							}
						}
					}
					if(m_leitura<=p_minhum*0.01+p_minhum && m_leitura>p_minhum) {
						int a= a_aux2.getRow();
						if(a==0) {
							descricao = "ALERTA HUMIDADE A 1% DO MÍNIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "H");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
						}
						else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA HUMIDADE A 1% DO MÍNIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
							}
						}
					}
				}

				if(m_idsensor.contains("L")) {
					System.out.println("Sensor luz");

					if(m_leitura>p_maxl) {
						descricao = "ALERTA LUZ MÁXIMA ULTRAPASSADA";
						descricao = "ALERTA TEMPERATURA MÁXIMA ULTRAPASSADA";
						preparedStmt.setInt(1, m_idzona);
						preparedStmt.setString(2, m_idsensor);
						preparedStmt.setTimestamp(3, m_datahora);
						preparedStmt.setString(4, "L");
						preparedStmt.setString(5, c_nomecultura);
						preparedStmt.setString(6, descricao);
						preparedStmt.setString(7, c_utilizador);
						preparedStmt.setInt(8, c_idcultura);
						preparedStmt.execute();
						String query = "UPDATE sid.cultura SET Estado=0 WHERE IDCultura=" + c_idcultura +";" ;
						connectionSQL.createStatement().executeUpdate(query);
						System.out.println("AMORIM");
					}	
					if(m_leitura<p_minl) {
						descricao = "ALERTA LUZ MÍNIMA ULTRAPASSADA";
						preparedStmt.setInt(1, m_idzona);
						preparedStmt.setString(2, m_idsensor);
						preparedStmt.setTimestamp(3, m_datahora);
						preparedStmt.setString(4, "L");
						preparedStmt.setString(5, c_nomecultura);
						preparedStmt.setString(6, descricao);
						preparedStmt.setString(7, c_utilizador);
						preparedStmt.setInt(8, c_idcultura);
						preparedStmt.execute();
						String query = "UPDATE sid.cultura SET Estado=0 WHERE IDCultura=" + c_idcultura +";" ;
						connectionSQL.createStatement().executeUpdate(query);
						System.out.println("AMORIM");
					}

					l.add(m_leitura);
					if(l.size()==600) {
						Double first=l.get(0);
						Double last= l.get(599);
						if(last*1.2==first||first*1.2==last) {
							descricao = "LUZ AUMENTOU 20% EM 5 MINUTOS";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "L");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
							System.out.println("AMORIM");
						}
						l.remove(599);
					}
					if(m_leitura >=p_maxl*0.85 && m_leitura < p_maxl*0.90 ) {  
						int a= a_aux2.getRow();
						if(a==0) {
							descricao = "ALERTA LUZ A 15% DO MÁXIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "L");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
						}
						else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA LUZ A 15% DO MÁXIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "L");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
							}
						}
					}
					if(m_leitura<=p_minl*0.15+p_minl && m_leitura>p_minl*0.10+p_minl) {
						int a= a_aux2.getRow();
						if(a==0) {
							descricao = "ALERTA LUZ A 15% DO MÍNIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "L");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
						}
						else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA LUZ A 15% DO MÍNIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "L");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
							}
						}
					}
					if(m_leitura>=p_maxl*0.9 && m_leitura<p_maxl*0.99) {
						int a= a_aux2.getRow();
						if(a==0) {
							descricao = "ALERTA LUZ A 10% DO MÁXIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "L");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
						}
						else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA LUZ A 10% DO MÁXIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "L");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
							}
						}
					}
					if(m_leitura<=p_minl*0.1+p_minl && m_leitura>p_minl*0.01+p_minl) {
						int a= a_aux2.getRow();
						if(a==0) {
							descricao = "ALERTA LUZ A 10% DO MÍNIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "L");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
						}
						else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA LUZ A 10% DO MÍNIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "L");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
							}
						}
					}
					if(m_leitura>=p_maxl*0.99 && m_leitura<p_maxl) {  
						int a= a_aux2.getRow();
						if(a==0) {
							descricao = "ALERTA LUZ A 1% DO MÁXIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "L");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
						}
						else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA LUZ A 1% DO MÁXIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "L");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
							}
						}
					}
					if(m_leitura<=p_minl*0.01+p_minl && m_leitura>p_minl) {
						int a= a_aux2.getRow();
						if(a==0) {
							descricao = "ALERTA LUZ A 1% DO MÍNIMO";
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "L");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, descricao);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, c_idcultura);
							preparedStmt.execute();
						}
						else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								descricao = "ALERTA LUZ A 1% DO MÍNIMO";
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "L");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, descricao);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, c_idcultura);
								preparedStmt.execute();
							}
						}
					}											
				}
			}
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		connectionSQL= DriverManager.getConnection("jdbc:mysql://localhost/" + db + "?useTimezone=true&serverTimezone=UTC", DBuser, DBpass);
		connectionSQL1= DriverManager.getConnection("jdbc:mysql://194.210.86.10/" +db1+ "?useTimezone=true&serverTimezone=UTC", DBuser1, DBpass1);
		
		String listas= "SELECT * FROM sensor ORDER BY idsensor DESC";
		PreparedStatement l_aux1 = connectionSQL1.prepareStatement(listas);
		ResultSet l_aux2 = l_aux1.executeQuery();
		l_aux2.next();
		i= l_aux2.getInt("idsensor");
		System.out.println(i);
		ArrayList<Double>[] t= getListasT(i);
		ArrayList<Double>[] h= getListasH(i);
		ArrayList<Double>[] l= getListasL(i);

		for(;;) {
			String medicao= "SELECT * FROM medicao ORDER BY IDMedicao DESC";
			PreparedStatement m_aux1 = connectionSQL.prepareStatement(medicao);
			ResultSet m_aux2 = m_aux1.executeQuery();
			m_aux2.next();
			int zona= m_aux2.getInt("IDZona");
			Timestamp time= m_aux2.getTimestamp("DataHora");

			String cultura= "SELECT * FROM cultura WHERE IDZona=" + zona;
			PreparedStatement c_aux1 = connectionSQL.prepareStatement(cultura);
			ResultSet c_aux2 = c_aux1.executeQuery();
			c_aux2.next();
			int estado= c_aux2.getInt("Estado");
			if(estado==1) {	
				if(!time.equals(aux_medicao)) {
					Alerta(t[zona], h[zona], l[zona]);
					aux_medicao= time;
				}
			}
		}
	}
}