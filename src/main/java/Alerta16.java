import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Alerta16 {
	static final String db = "sid";
	static final String DBuser = "root";
	static final String DBpass = "root";
	static Connection connectionSQL;

	public static boolean diferenceMinutes(Timestamp t1, Timestamp t2){
		long milliseconds= t2.getTime()-t1.getTime();
		int seconds= (int) milliseconds/1000;
		int minutes= (seconds%3600)/60;
		seconds =(seconds%3600)%60;
		if(minutes> 5)
			return true;
		return false;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException {

		Class.forName("com.mysql.cj.jdbc.Driver");
		connectionSQL= DriverManager.getConnection("jdbc:mysql://localhost/" + db + "?useTimezone=true&serverTimezone=UTC", DBuser, DBpass);

		if(connectionSQL!=null)
			System.out.println("Ligação estabelecida");

		String medicao= "select * from medicao";
		PreparedStatement m_aux1 = connectionSQL.prepareStatement(medicao);
		ResultSet m_aux2 = m_aux1.executeQuery();
		String descricao="";

		while(m_aux2.next()) {
			String alerta1= "SELECT * FROM alerta";
			PreparedStatement x_aux1 = connectionSQL.prepareStatement(alerta1);
			ResultSet x_aux2 = x_aux1.executeQuery();

			int count= 0;
			while(x_aux2.next()) {
				int idalerta= x_aux2.getInt(1);
				count++;
				System.out.println("FOKOBO" + idalerta + ""+ x_aux2.getRow());
			}

			int idmedicao= m_aux2.getInt("idmedicao");
			int m_idzona= m_aux2.getInt("idzona");
			String m_idsensor= m_aux2.getString("idsensor");
			Timestamp m_datahora= m_aux2.getTimestamp("datahora");
			double m_leitura= m_aux2.getDouble("leitura");

			Medicoes medicaoatual = new Medicoes(m_idsensor, m_datahora, m_leitura);
			ArrayList<Medicoes> medicoes = new ArrayList<Medicoes>();
			medicoes.add(medicaoatual);
			long minutes = 10*60*1000;
			long datahora = m_datahora.getTime();
			long datahoraanterior1 = datahora - minutes;
			Timestamp m_datahoraanterior = new Timestamp(datahoraanterior1);


			System.out.println("---medicao--");
			System.out.println("id:" + idmedicao + " zona:" + m_idzona + " sensor:" + m_idsensor + " DataHora:" + m_datahora + " leitura:" + m_leitura);
			if(m_idsensor.contains("T"))
				System.out.println("Sensor temperatura");
			if(m_idsensor.contains("H"))
				System.out.println("Sensor humidade");
			if(m_idsensor.contains("L"))
				System.out.println("Sensor luz");

			String cultura= "SELECT * FROM cultura WHERE idzona=" + m_idzona;
			PreparedStatement c_aux1 = connectionSQL.prepareStatement(cultura);
			ResultSet c_aux2 =c_aux1.executeQuery();

			while(c_aux2.next()) {


				int c_idzona= c_aux2.getInt("idzona");
				int c_idparametro= c_aux2.getInt("idparametro");
				int c_idcultura= c_aux2.getInt("idcultura");
				String c_nomecultura = c_aux2.getString("nomecultura");
				String c_utilizador = c_aux2.getString("utilizador");
				String tipoAlerta;
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


				while(p_aux2.next()) {
					String query_alerta = "INSERT INTO alerta (idzona, idsensor, datahora, tipoalerta, nomecultura, descricao, utilizador, idcultura)" + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
					PreparedStatement preparedStmt = connectionSQL.prepareStatement(query_alerta);

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
							System.out.println("AMORIM");
							break;
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
							System.out.println("AMORIM");
							break;
						}
						for(int i =0; i< medicoes.size()-1; i++) {
							String idsensoranterior = medicoes.get(i).getIdSensor();
							Timestamp datahoraanterior= medicoes.get(i).getDataHora();
							if(idsensoranterior.equals(m_idsensor)  && datahoraanterior.equals(m_datahoraanterior)) {
								if(m_leitura== medicoes.get(i).getLeitura()*0.8) {
									descricao= "ALERTA TEMPERATURA DIMINUIU 20% EM 10 MINUTOS";
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
								else if(m_leitura==medicoes.get(i).getLeitura()*0.2+medicoes.get(i).getLeitura()) {
									descricao= "ALERTA TEMPERATURA AUMENTOU 20% EM 10 MINUTOS";
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

						if(m_leitura >=p_maxtemp*0.85 && m_leitura < p_maxtemp*0.90 ) {  
							int a= a_aux2.getRow();
							System.out.println("BOJINOV" + a);
							if(count==0) { 
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
								while(a_aux2.next()) {
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
									break;
								}
							}
						}
						if(m_leitura<=p_mintemp*0.15+p_mintemp && m_leitura>p_mintemp*0.10+p_mintemp  ) { 
							int a= a_aux2.getRow();
							System.out.println("BOJINOV" + a);
							if(count==0) {
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
								while(a_aux2.next()) {
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
									break;
								}
							}
						}
						if(m_leitura>=p_maxtemp*0.9 && m_leitura<p_maxtemp*0.99) {
							int a= a_aux2.getRow();
							System.out.println("BOJINOV" + a);
							if(count==0) {
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
								while(a_aux2.next()) {
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
									break;
								}
							}
						}
						if(m_leitura<=p_mintemp*0.1+p_mintemp && m_leitura>p_mintemp*0.01+p_mintemp) {
							int a= a_aux2.getRow();
							if(count==0) {
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
								while(a_aux2.next()) {
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
									break;
								}
							}
						}
						if(m_leitura>=p_maxtemp*0.99 && m_leitura<p_maxtemp) {  
							int a= a_aux2.getRow();
							if(count==0) {
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
								while(a_aux2.next()) {
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
									break;
								}
							}
						}
						if(m_leitura<=p_mintemp*0.01+p_mintemp && m_leitura>p_mintemp) {
							int a= a_aux2.getRow();
							System.out.println("BOJINOV" + a);
							if(count==0) {
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
								while(a_aux2.next()) {
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
									break;
								}
							}
						}		
					}

					if(m_idsensor.contains("H")) {
						tipoAlerta = "H";
						System.out.println("Sensor humidade");

						if(m_leitura>p_maxhum)
							descricao = "ALERTA HUMIDADE MÁXIMA ULTRAPASSADA";
						if(m_leitura<p_minhum)
							descricao = "ALERTA HUMIDADE MÍNIMA ULTRAPASSADA";
						for(int i =0; i< medicoes.size()-1; i++) {
							String idsensoranterior = medicoes.get(i).getIdSensor();
							Timestamp datahoraanterior= medicoes.get(i).getDataHora();
							if(idsensoranterior.equals(m_idsensor)  && datahoraanterior.equals(m_datahoraanterior)) {
								if(m_leitura== medicoes.get(i).getLeitura()*0.8) 
									System.out.println("ALERTA HUMIDADE DIMINUIU 20% EM 10 MINUTOS");
								else if(m_leitura==medicoes.get(i).getLeitura()*0.2+medicoes.get(i).getLeitura())
									System.out.println("ALERTA HUMIDADE AUMENTOU 20% EM 10 MINUTOS");
							}
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
						tipoAlerta = "L";
						System.out.println("Sensor luz");

						if(m_leitura>p_maxl)
							descricao = "ALERTA LUZ MÁXIMA ULTRAPASSADA";
						if(m_leitura<p_minl)
							descricao = "ALERTA LUZ MÍNIMA ULTRAPASSADA";
						for(int i =0; i< medicoes.size()-1; i++) {
							String idsensoranterior = medicoes.get(i).getIdSensor();
							Timestamp datahoraanterior= medicoes.get(i).getDataHora();
							if(idsensoranterior.equals(m_idsensor)  && datahoraanterior.equals(m_datahoraanterior) ) {
								if(m_leitura== medicoes.get(i).getLeitura()*0.8) 
									System.out.println("ALERTA LUZ DIMINUIU 20% EM 10 MINUTOS");
								else if(m_leitura==medicoes.get(i).getLeitura()*0.2+medicoes.get(i).getLeitura())
									System.out.println("ALERTA LUZ AUMENTOU 20% EM 10 MINUTOS");
							}
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
			//Thread.sleep(10000);		
		}
	}
}