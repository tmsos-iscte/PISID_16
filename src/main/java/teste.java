import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class teste {

	public static double findMedian(List<Double> a) {
		Collections.sort(a);
		int s= a.size();
		if(s%2!=0)
			return (double) a.get(s/2);
		return (double) (a.get((s-1)/2)+a.get(s/2))/2.0;
	}

	public static boolean diferenceMinutes(Timestamp t1, Timestamp t2){
		long milliseconds= t2.getTime()-t1.getTime();
		int seconds= (int) milliseconds/1000;
		int minutes= (seconds%3600)/60;
		seconds =(seconds%3600)%60;
		if(minutes> 5)
			return true;
		return false;
	}

	static final String db = "sid";
	static final String DBuser = "root";
	static final String DBpass = "root";
	static Connection connectionSQL;

	public static void main(int idzona, int idcultura) throws ClassNotFoundException, SQLException, InterruptedException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		connectionSQL= DriverManager.getConnection("jdbc:mysql://localhost/" + db + "?useTimezone=true&serverTimezone=UTC", DBuser, DBpass);

		if(connectionSQL!=null)
			System.out.println("Ligação estabelecida");

		List<Double> t= new ArrayList<>();
		List<Double> h= new ArrayList<>();
		List<Double> l= new ArrayList<>();

		String stringT= "Select * FROM medicao where IDSensor=T"+idzona+" ORDER BY DESC LIMIT 10";
		String stringH= "Select * FROM medicao where IDSensor=H"+idzona+" ORDER BY DESC LIMIT 10";
		String stringL= "Select * FROM medicao where IDSensor=L"+idzona+" ORDER BY DESC LIMIT 10";

		PreparedStatement t1= connectionSQL.prepareStatement(stringT);
		ResultSet t2= t1.executeQuery();
		PreparedStatement h1= connectionSQL.prepareStatement(stringH);
		ResultSet h2= h1.executeQuery();
		PreparedStatement l1= connectionSQL.prepareStatement(stringL);
		ResultSet l2= l1.executeQuery();

		String query_alerta = "INSERT INTO alerta (idzona, idsensor, datahora, tipoalerta, nomecultura, descricao, utilizador, idcultura)" + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement preparedStmt = connectionSQL.prepareStatement(query_alerta);

		while(t2.next()) {
			Timestamp m_datahora= t2.getTimestamp("datahora");
			double m_leitura= t2.getDouble("leitura");
			String cultura= "SELECT * FROM cultura WHERE IDZzona=" + idzona;
			PreparedStatement c_t1= connectionSQL.prepareStatement(cultura);
			ResultSet c_t2 =c_t1.executeQuery();
			while(c_t2.next()) {
				int c_idparametro= c_t2.getInt("IDParametro");
				String c_nomecultura= c_t2.getString("NomeCultura");
				String c_utilizador= c_t2.getString("Utilizador");
				String parametro= "SELECT * FROM parametro WHERE IDParametro=" + c_idparametro;
				PreparedStatement p_t1= connectionSQL.prepareStatement(parametro);
				ResultSet p_t2= p_t1.executeQuery();
				while(p_t2.next()) {
					double p_maxtemp= p_t2.getDouble("maxtemperatura");
					double p_mintemp= p_t2.getDouble("mintemperatura");	
					double abs_t= Math.abs(p_maxtemp-p_mintemp);
					String alerta= "SELECT * FROM sid.alerta WHERE idcultura="+idcultura+" AND idsensor=T'"+idzona+"' ORDER BY IDAlerta DESC";
					PreparedStatement a_t1= connectionSQL.prepareStatement(alerta);
					ResultSet a_t2= a_t1.executeQuery();
					t.add(0,m_leitura);
					if(t.size()<=5) {
						List<Double> t_copy = new ArrayList<>(t);
						if(findMedian(t_copy)>p_maxtemp || findMedian(t_copy)<p_mintemp) {
							System.out.println("ALERTA H!");
							String s= "";
							System.out.println("EVALDO:" + a_t2.next());
							int a= a_t2.getRow();
							System.out.println("ELIAS"+a);
							if(a==0) {
								preparedStmt.setInt(1, idzona);
								preparedStmt.setString(2, "T"+idzona);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}else {
								System.out.println("ELIAS"+a);
								String a_tipoal= a_t2.getString("tipoalerta");
								System.out.println("GERALDES" + a_tipoal);
								Timestamp a_data= a_t2.getTimestamp("datahora");
								System.out.println("BOJINOV" + a_tipoal);
								if(diferenceMinutes(a_data, m_datahora)==true) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "T"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "H");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");
								}
							}
						}
						if(p_maxtemp>findMedian(t_copy)&&findMedian(t_copy)>p_maxtemp-abs_t*0.05) { 
							System.out.println("ALERTA H!");
							String s= "";
							System.out.println("EVALDO:" + a_t2.next());
							int a= a_t2.getRow();
							System.out.println("ELIAS"+a);
							if(a==0) {
								preparedStmt.setInt(1, idzona);
								preparedStmt.setString(2, "T"+idzona);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}else {
								System.out.println("ELIAS"+a);
								String a_tipoal= a_t2.getString("tipoalerta");
								System.out.println("GERALDES" + a_tipoal);
								Timestamp a_data= a_t2.getTimestamp("datahora");
								System.out.println("BOJINOV" + a_tipoal);
								if(diferenceMinutes(a_data, m_datahora)==true) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "T"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "H");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");
								}
							}
						}
						if(p_mintemp<findMedian(t_copy)&&findMedian(t_copy)<p_mintemp+abs_t*0.05) {
							System.out.println("ALERTA H!");
							String s= "";
							System.out.println("EVALDO:" + a_t2.next());
							int a= a_t2.getRow();
							System.out.println("ELIAS"+a);
							if(a==0) {
								preparedStmt.setInt(1, idzona);
								preparedStmt.setString(2, "T"+idzona);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}else {
								System.out.println("ELIAS"+a);
								String a_tipoal= a_t2.getString("tipoalerta");
								System.out.println("GERALDES" + a_tipoal);
								Timestamp a_data= a_t2.getTimestamp("datahora");
								System.out.println("BOJINOV" + a_tipoal);
								if(diferenceMinutes(a_data, m_datahora)==true) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "T"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "H");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");
								}
							}
						}
						if(p_maxtemp>findMedian(t_copy)&&findMedian(t_copy)>p_maxtemp-abs_t*0.1) {
							if(findMedian(t_copy)<=p_maxtemp-abs_t*0.05) {
								System.out.println("ALERTA M!");
								String s= "";
								System.out.println("EVALDO:" + a_t2.next());
								int a= a_t2.getRow();
								System.out.println("ELIAS"+a);
								if(a==0) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "T"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "M");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");

								}else {
									System.out.println("ELIAS"+a);
									String a_tipoal= a_t2.getString("tipoalerta");
									Timestamp a_data= a_t2.getTimestamp("datahora");
									System.out.println("BOJINOV" + a_tipoal);
									if(a_tipoal.equals("H")||a_tipoal.equals("M")) {
										if(diferenceMinutes(a_data, m_datahora)==true) {
											preparedStmt.setInt(1, idzona);
											preparedStmt.setString(2, "T"+idzona);
											preparedStmt.setTimestamp(3, m_datahora);
											preparedStmt.setString(4, "M");
											preparedStmt.setString(5, c_nomecultura);
											preparedStmt.setString(6, s);
											preparedStmt.setString(7, c_utilizador);
											preparedStmt.setInt(8, idcultura);
											preparedStmt.execute();
											System.out.println("AMORIM");
										}
									}else {
										preparedStmt.setInt(1, idzona);
										preparedStmt.setString(2, "T"+idzona);
										preparedStmt.setTimestamp(3, m_datahora);
										preparedStmt.setString(4, "M");
										preparedStmt.setString(5, c_nomecultura);
										preparedStmt.setString(6, s);
										preparedStmt.setString(7, c_utilizador);
										preparedStmt.setInt(8, idcultura);
										preparedStmt.execute();
										System.out.println("AMORIM");
									}
								}
							}
						}
						if(p_mintemp<findMedian(t_copy)&&findMedian(t_copy)<p_mintemp+abs_t*0.1) {
							if(findMedian(t_copy)>=p_mintemp+abs_t*0.05) {
								System.out.println("ALERTA M!");
								String s= "";
								System.out.println("EVALDO:" + a_t2.next());
								int a= a_t2.getRow();
								System.out.println("ELIAS"+a);
								if(a==0) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "T"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "M");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");

								}else {
									System.out.println("ELIAS"+a);
									String a_tipoal= a_t2.getString("tipoalerta");
									Timestamp a_data= a_t2.getTimestamp("datahora");
									System.out.println("BOJINOV" + a_tipoal);
									if(a_tipoal.equals("H")||a_tipoal.equals("M")) {
										if(diferenceMinutes(a_data, m_datahora)==true) {
											preparedStmt.setInt(1, idzona);
											preparedStmt.setString(2, "T"+idzona);
											preparedStmt.setTimestamp(3, m_datahora);
											preparedStmt.setString(4, "M");
											preparedStmt.setString(5, c_nomecultura);
											preparedStmt.setString(6, s);
											preparedStmt.setString(7, c_utilizador);
											preparedStmt.setInt(8, idcultura);
											preparedStmt.execute();
											System.out.println("AMORIM");
										}
									}else {
										preparedStmt.setInt(1, idzona);
										preparedStmt.setString(2, "T"+idzona);
										preparedStmt.setTimestamp(3, m_datahora);
										preparedStmt.setString(4, "M");
										preparedStmt.setString(5, c_nomecultura);
										preparedStmt.setString(6, s);
										preparedStmt.setString(7, c_utilizador);
										preparedStmt.setInt(8, idcultura);
										preparedStmt.execute();
										System.out.println("AMORIM");
									}
								}
							}
						}
						if(p_maxtemp>findMedian(t_copy)&&findMedian(t_copy)>p_maxtemp-abs_t*0.2) {
							if(findMedian(t_copy)<=p_maxtemp-abs_t*0.1) {
								System.out.println("ALERTA L!");
								String s= "";
								System.out.println("EVALDO:" + a_t2.next());
								int a= a_t2.getRow();
								System.out.println("ELIAS"+a);
								if(a==0) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "T"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "L");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");
								}else {
									System.out.println("ELIAS"+a);
									String a_tipoal= a_t2.getString("tipoalerta");
									Timestamp a_data= a_t2.getTimestamp("datahora");
									System.out.println("BOJINOV" + a_tipoal);
									if(diferenceMinutes(a_data, m_datahora)==true) {
										preparedStmt.setInt(1, idzona);
										preparedStmt.setString(2, "T"+idzona);
										preparedStmt.setTimestamp(3, m_datahora);
										preparedStmt.setString(4, "L");
										preparedStmt.setString(5, c_nomecultura);
										preparedStmt.setString(6, s);
										preparedStmt.setString(7, c_utilizador);
										preparedStmt.setInt(8, idcultura);
										preparedStmt.execute();
										System.out.println("AMORIM");
									}
								}
							}
						}
						if(p_mintemp<findMedian(t_copy)&&findMedian(t_copy)<p_mintemp+abs_t*0.2) {
							if(findMedian(t_copy)>=p_mintemp+abs_t*0.1) {
								System.out.println("ALERTA L!");
								String s= "";
								System.out.println("EVALDO:" + a_t2.next());
								int a= a_t2.getRow();
								System.out.println("ELIAS"+a);
								if(a==0) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "T"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "L");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");
								}else {
									System.out.println("ELIAS"+a);
									String a_tipoal= a_t2.getString("tipoalerta");
									Timestamp a_data= a_t2.getTimestamp("datahora");
									System.out.println("BOJINOV" + a_tipoal);
									if(diferenceMinutes(a_data, m_datahora)==true) {
										preparedStmt.setInt(1, idzona);
										preparedStmt.setString(2, "T"+idzona);
										preparedStmt.setTimestamp(3, m_datahora);
										preparedStmt.setString(4, "L");
										preparedStmt.setString(5, c_nomecultura);
										preparedStmt.setString(6, s);
										preparedStmt.setString(7, c_utilizador);
										preparedStmt.setInt(8, idcultura);
										preparedStmt.execute();
										System.out.println("AMORIM");
									}
								}
							}
						}
					}	
				}
			}
		}

		while(h2.next()) {
			Timestamp m_datahora= h2.getTimestamp("datahora");
			double m_leitura= h2.getDouble("leitura");
			String cultura= "SELECT * FROM cultura WHERE IDZzona=" + idzona;
			PreparedStatement c_h1= connectionSQL.prepareStatement(cultura);
			ResultSet c_h2 =c_h1.executeQuery();
			while(c_h2.next()) {
				int c_idparametro= c_h2.getInt("IDParametro");
				String c_nomecultura= c_h2.getString("NomeCultura");
				String c_utilizador= c_h2.getString("Utilizador");
				String parametro= "SELECT * FROM parametro WHERE IDParametro=" + c_idparametro;
				PreparedStatement p_h1= connectionSQL.prepareStatement(parametro);
				ResultSet p_h2= p_h1.executeQuery();
				while(p_h2.next()) {
					double p_maxhum= p_h2.getDouble("maxhumidade");
					double p_minhum= p_h2.getDouble("minhumidade");
					double abs_h= Math.abs(p_maxhum-p_minhum);
					String alerta= "SELECT * FROM sid.alerta WHERE idcultura="+idcultura+" AND idsensor=H'"+idzona+"' ORDER BY IDAlerta DESC";
					PreparedStatement a_h1= connectionSQL.prepareStatement(alerta);
					ResultSet a_h2= a_h1.executeQuery();
					h.add(0,m_leitura);
					if(t.size()<=5) {
						List<Double> h_copy = new ArrayList<>(h);
						if(findMedian(h_copy)>p_maxhum || findMedian(h_copy)<p_minhum) {
							System.out.println("ALERTA H!");
							String s= "";
							System.out.println("EVALDO:" + a_h2.next());
							int a= a_h2.getRow();
							System.out.println("ELIAS"+a);
							if(a==0) {
								preparedStmt.setInt(1, idzona);
								preparedStmt.setString(2, "H"+idzona);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}else {
								System.out.println("ELIAS"+a);
								String a_tipoal= a_h2.getString("tipoalerta");
								System.out.println("GERALDES" + a_tipoal);
								Timestamp a_data= a_h2.getTimestamp("datahora");
								System.out.println("BOJINOV" + a_tipoal);
								if(diferenceMinutes(a_data, m_datahora)==true) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "H"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "H");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");
								}
							}
						}
						if(p_maxhum>findMedian(h_copy)&&findMedian(h_copy)>p_maxhum-abs_h*0.05) { 
							System.out.println("ALERTA H!");
							String s= "";
							System.out.println("EVALDO:" + a_h2.next());
							int a= a_h2.getRow();
							System.out.println("ELIAS"+a);
							if(a==0) {
								preparedStmt.setInt(1, idzona);
								preparedStmt.setString(2, "H"+idzona);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}else {
								System.out.println("ELIAS"+a);
								String a_tipoal= a_h2.getString("tipoalerta");
								System.out.println("GERALDES" + a_tipoal);
								Timestamp a_data= a_h2.getTimestamp("datahora");
								System.out.println("BOJINOV" + a_tipoal);
								if(diferenceMinutes(a_data, m_datahora)==true) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "H"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "H");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");
								}
							}
						}
						if(p_minhum<findMedian(h_copy)&&findMedian(h_copy)<p_minhum+abs_h*0.05) {
							System.out.println("ALERTA H!");
							String s= "";
							System.out.println("EVALDO:" + a_h2.next());
							int a= a_h2.getRow();
							System.out.println("ELIAS"+a);
							if(a==0) {
								preparedStmt.setInt(1, idzona);
								preparedStmt.setString(2, "H"+idzona);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}else {
								System.out.println("ELIAS"+a);
								String a_tipoal= a_h2.getString("tipoalerta");
								System.out.println("GERALDES" + a_tipoal);
								Timestamp a_data= a_h2.getTimestamp("datahora");
								System.out.println("BOJINOV" + a_tipoal);
								if(diferenceMinutes(a_data, m_datahora)==true) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "H"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "H");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");
								}
							}
						}
						if(p_maxhum>findMedian(h_copy)&&findMedian(h_copy)>p_maxhum-abs_h*0.1) {
							if(findMedian(h_copy)<=p_maxhum-abs_h*0.05) {
								System.out.println("ALERTA M!");
								String s= "";
								System.out.println("EVALDO:" + a_h2.next());
								int a= a_h2.getRow();
								System.out.println("ELIAS"+a);
								if(a==0) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "H"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "M");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");

								}else {
									System.out.println("ELIAS"+a);
									String a_tipoal= a_h2.getString("tipoalerta");
									Timestamp a_data= a_h2.getTimestamp("datahora");
									System.out.println("BOJINOV" + a_tipoal);
									if(a_tipoal.equals("H")||a_tipoal.equals("M")) {
										if(diferenceMinutes(a_data, m_datahora)==true) {
											preparedStmt.setInt(1, idzona);
											preparedStmt.setString(2, "H"+idzona);
											preparedStmt.setTimestamp(3, m_datahora);
											preparedStmt.setString(4, "M");
											preparedStmt.setString(5, c_nomecultura);
											preparedStmt.setString(6, s);
											preparedStmt.setString(7, c_utilizador);
											preparedStmt.setInt(8, idcultura);
											preparedStmt.execute();
											System.out.println("AMORIM");
										}
									}else {
										preparedStmt.setInt(1, idzona);
										preparedStmt.setString(2, "H"+idzona);
										preparedStmt.setTimestamp(3, m_datahora);
										preparedStmt.setString(4, "M");
										preparedStmt.setString(5, c_nomecultura);
										preparedStmt.setString(6, s);
										preparedStmt.setString(7, c_utilizador);
										preparedStmt.setInt(8, idcultura);
										preparedStmt.execute();
										System.out.println("AMORIM");
									}
								}
							}
						}
						if(p_minhum<findMedian(h_copy)&&findMedian(h_copy)<p_minhum+abs_h*0.1) {
							if(findMedian(h_copy)>=p_minhum+abs_h*0.05) {
								System.out.println("ALERTA M!");
								String s= "";
								System.out.println("EVALDO:" + a_h2.next());
								int a= a_h2.getRow();
								System.out.println("ELIAS"+a);
								if(a==0) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "H"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "M");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");

								}else {
									System.out.println("ELIAS"+a);
									String a_tipoal= a_h2.getString("tipoalerta");
									Timestamp a_data= a_h2.getTimestamp("datahora");
									System.out.println("BOJINOV" + a_tipoal);
									if(a_tipoal.equals("H")||a_tipoal.equals("M")) {
										if(diferenceMinutes(a_data, m_datahora)==true) {
											preparedStmt.setInt(1, idzona);
											preparedStmt.setString(2, "H"+idzona);
											preparedStmt.setTimestamp(3, m_datahora);
											preparedStmt.setString(4, "M");
											preparedStmt.setString(5, c_nomecultura);
											preparedStmt.setString(6, s);
											preparedStmt.setString(7, c_utilizador);
											preparedStmt.setInt(8, idcultura);
											preparedStmt.execute();
											System.out.println("AMORIM");
										}
									}else {
										preparedStmt.setInt(1, idzona);
										preparedStmt.setString(2, "H"+idzona);
										preparedStmt.setTimestamp(3, m_datahora);
										preparedStmt.setString(4, "M");
										preparedStmt.setString(5, c_nomecultura);
										preparedStmt.setString(6, s);
										preparedStmt.setString(7, c_utilizador);
										preparedStmt.setInt(8, idcultura);
										preparedStmt.execute();
										System.out.println("AMORIM");
									}
								}
							}
						}
						if(p_maxhum>findMedian(h_copy)&&findMedian(h_copy)>p_maxhum-abs_h*0.2) {
							if(findMedian(h_copy)<=p_maxhum-abs_h*0.1) {
								System.out.println("ALERTA L!");
								String s= "";
								System.out.println("EVALDO:" + a_h2.next());
								int a= a_h2.getRow();
								System.out.println("ELIAS"+a);
								if(a==0) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "H"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "L");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");
								}else {
									System.out.println("ELIAS"+a);
									String a_tipoal= a_h2.getString("tipoalerta");
									Timestamp a_data= a_h2.getTimestamp("datahora");
									System.out.println("BOJINOV" + a_tipoal);
									if(diferenceMinutes(a_data, m_datahora)==true) {
										preparedStmt.setInt(1, idzona);
										preparedStmt.setString(2, "H"+idzona);
										preparedStmt.setTimestamp(3, m_datahora);
										preparedStmt.setString(4, "L");
										preparedStmt.setString(5, c_nomecultura);
										preparedStmt.setString(6, s);
										preparedStmt.setString(7, c_utilizador);
										preparedStmt.setInt(8, idcultura);
										preparedStmt.execute();
										System.out.println("AMORIM");
									}
								}
							}
						}
						if(p_minhum<findMedian(h_copy)&&findMedian(h_copy)<p_minhum+abs_h*0.2) {
							if(findMedian(h_copy)>=p_minhum+abs_h*0.1) {
								System.out.println("ALERTA L!");
								String s= "";
								System.out.println("EVALDO:" + a_h2.next());
								int a= a_h2.getRow();
								System.out.println("ELIAS"+a);
								if(a==0) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "H"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "L");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");
								}else {
									System.out.println("ELIAS"+a);
									String a_tipoal= a_h2.getString("tipoalerta");
									Timestamp a_data= a_h2.getTimestamp("datahora");
									System.out.println("BOJINOV" + a_tipoal);
									if(diferenceMinutes(a_data, m_datahora)==true) {
										preparedStmt.setInt(1, idzona);
										preparedStmt.setString(2, "H"+idzona);
										preparedStmt.setTimestamp(3, m_datahora);
										preparedStmt.setString(4, "L");
										preparedStmt.setString(5, c_nomecultura);
										preparedStmt.setString(6, s);
										preparedStmt.setString(7, c_utilizador);
										preparedStmt.setInt(8, idcultura);
										preparedStmt.execute();
										System.out.println("AMORIM");
									}
								}
							}
						}
					}	
				}
			}

		}

		while(l2.next()) {
			Timestamp m_datahora= l2.getTimestamp("datahora");
			double m_leitura= l2.getDouble("leitura");
			String cultura= "SELECT * FROM cultura WHERE IDZzona=" + idzona;
			PreparedStatement c_l1= connectionSQL.prepareStatement(cultura);
			ResultSet c_l2 =c_l1.executeQuery();
			while(c_l2.next()) {
				int c_idparametro= c_l2.getInt("IDParametro");
				String c_nomecultura= c_l2.getString("NomeCultura");
				String c_utilizador= c_l2.getString("Utilizador");
				String parametro= "SELECT * FROM parametro WHERE IDParametro=" + c_idparametro;
				PreparedStatement p_l1= connectionSQL.prepareStatement(parametro);
				ResultSet p_l2= p_l1.executeQuery();
				while(p_l2.next()) {
					double p_maxl= p_l2.getDouble("maxluz");
					double p_minl= p_l2.getDouble("minluz");
					double abs_l= Math.abs(p_maxl-p_minl);
					String alerta= "SELECT * FROM sid.alerta WHERE idcultura="+idcultura+" AND idsensor=L'"+idzona+"' ORDER BY IDAlerta DESC";
					PreparedStatement a_l1= connectionSQL.prepareStatement(alerta);
					ResultSet a_l2= a_l1.executeQuery();
					l.add(0,m_leitura);
					if(t.size()<=5) {
						List<Double> l_copy = new ArrayList<>(l);
						if(findMedian(l_copy)>p_maxl || findMedian(l_copy)<p_minl) {
							System.out.println("ALERTA H!");
							String s= "";
							System.out.println("EVALDO:" + a_l2.next());
							int a= a_l2.getRow();
							System.out.println("ELIAS"+a);
							if(a==0) {
								preparedStmt.setInt(1, idzona);
								preparedStmt.setString(2, "L"+idzona);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}else {
								System.out.println("ELIAS"+a);
								String a_tipoal= a_l2.getString("tipoalerta");
								System.out.println("GERALDES" + a_tipoal);
								Timestamp a_data= a_l2.getTimestamp("datahora");
								System.out.println("BOJINOV" + a_tipoal);
								if(diferenceMinutes(a_data, m_datahora)==true) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "L"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "H");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");
								}
							}
						}
						if(p_maxl>findMedian(l_copy)&&findMedian(l_copy)>p_maxl-abs_l*0.05) { 
							System.out.println("ALERTA H!");
							String s= "";
							System.out.println("EVALDO:" + a_l2.next());
							int a= a_l2.getRow();
							System.out.println("ELIAS"+a);
							if(a==0) {
								preparedStmt.setInt(1, idzona);
								preparedStmt.setString(2, "L"+idzona);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}else {
								System.out.println("ELIAS"+a);
								String a_tipoal= a_l2.getString("tipoalerta");
								System.out.println("GERALDES" + a_tipoal);
								Timestamp a_data= a_l2.getTimestamp("datahora");
								System.out.println("BOJINOV" + a_tipoal);
								if(diferenceMinutes(a_data, m_datahora)==true) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "L"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "H");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");
								}
							}
						}
						if(p_minl<findMedian(l_copy)&&findMedian(l_copy)<p_minl+abs_l*0.05) {
							System.out.println("ALERTA H!");
							String s= "";
							System.out.println("EVALDO:" + a_l2.next());
							int a= a_l2.getRow();
							System.out.println("ELIAS"+a);
							if(a==0) {
								preparedStmt.setInt(1, idzona);
								preparedStmt.setString(2, "L"+idzona);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}else {
								System.out.println("ELIAS"+a);
								String a_tipoal= a_l2.getString("tipoalerta");
								System.out.println("GERALDES" + a_tipoal);
								Timestamp a_data= a_l2.getTimestamp("datahora");
								System.out.println("BOJINOV" + a_tipoal);
								if(diferenceMinutes(a_data, m_datahora)==true) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "L"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "H");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");
								}
							}
						}
						if(p_maxl>findMedian(l_copy)&&findMedian(l_copy)>p_maxl-abs_l*0.1) {
							if(findMedian(l_copy)<=p_maxl-abs_l*0.05) {
								System.out.println("ALERTA M!");
								String s= "";
								System.out.println("EVALDO:" + a_l2.next());
								int a= a_l2.getRow();
								System.out.println("ELIAS"+a);
								if(a==0) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "L"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "M");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");

								}else {
									System.out.println("ELIAS"+a);
									String a_tipoal= a_l2.getString("tipoalerta");
									Timestamp a_data= a_l2.getTimestamp("datahora");
									System.out.println("BOJINOV" + a_tipoal);
									if(a_tipoal.equals("H")||a_tipoal.equals("M")) {
										if(diferenceMinutes(a_data, m_datahora)==true) {
											preparedStmt.setInt(1, idzona);
											preparedStmt.setString(2, "L"+idzona);
											preparedStmt.setTimestamp(3, m_datahora);
											preparedStmt.setString(4, "M");
											preparedStmt.setString(5, c_nomecultura);
											preparedStmt.setString(6, s);
											preparedStmt.setString(7, c_utilizador);
											preparedStmt.setInt(8, idcultura);
											preparedStmt.execute();
											System.out.println("AMORIM");
										}
									}else {
										preparedStmt.setInt(1, idzona);
										preparedStmt.setString(2, "L"+idzona);
										preparedStmt.setTimestamp(3, m_datahora);
										preparedStmt.setString(4, "M");
										preparedStmt.setString(5, c_nomecultura);
										preparedStmt.setString(6, s);
										preparedStmt.setString(7, c_utilizador);
										preparedStmt.setInt(8, idcultura);
										preparedStmt.execute();
										System.out.println("AMORIM");
									}
								}
							}
						}
						if(p_minl<findMedian(l_copy)&&findMedian(l_copy)<p_minl+abs_l*0.1) {
							if(findMedian(l_copy)>=p_minl+abs_l*0.05) {
								System.out.println("ALERTA M!");
								String s= "";
								System.out.println("EVALDO:" + a_l2.next());
								int a= a_l2.getRow();
								System.out.println("ELIAS"+a);
								if(a==0) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "L"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "M");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");

								}else {
									System.out.println("ELIAS"+a);
									String a_tipoal= a_l2.getString("tipoalerta");
									Timestamp a_data= a_l2.getTimestamp("datahora");
									System.out.println("BOJINOV" + a_tipoal);
									if(a_tipoal.equals("H")||a_tipoal.equals("M")) {
										if(diferenceMinutes(a_data, m_datahora)==true) {
											preparedStmt.setInt(1, idzona);
											preparedStmt.setString(2, "L"+idzona);
											preparedStmt.setTimestamp(3, m_datahora);
											preparedStmt.setString(4, "M");
											preparedStmt.setString(5, c_nomecultura);
											preparedStmt.setString(6, s);
											preparedStmt.setString(7, c_utilizador);
											preparedStmt.setInt(8, idcultura);
											preparedStmt.execute();
											System.out.println("AMORIM");
										}
									}else {
										preparedStmt.setInt(1, idzona);
										preparedStmt.setString(2, "L"+idzona);
										preparedStmt.setTimestamp(3, m_datahora);
										preparedStmt.setString(4, "M");
										preparedStmt.setString(5, c_nomecultura);
										preparedStmt.setString(6, s);
										preparedStmt.setString(7, c_utilizador);
										preparedStmt.setInt(8, idcultura);
										preparedStmt.execute();
										System.out.println("AMORIM");
									}
								}
							}
						}
						if(p_maxl>findMedian(l_copy)&&findMedian(l_copy)>p_maxl-abs_l*0.2) {
							if(findMedian(l_copy)<=p_maxl-abs_l*0.1) {
								System.out.println("ALERTA L!");
								String s= "";
								System.out.println("EVALDO:" + a_l2.next());
								int a= a_l2.getRow();
								System.out.println("ELIAS"+a);
								if(a==0) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "L"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "L");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");
								}else {
									System.out.println("ELIAS"+a);
									String a_tipoal= a_l2.getString("tipoalerta");
									Timestamp a_data= a_l2.getTimestamp("datahora");
									System.out.println("BOJINOV" + a_tipoal);
									if(diferenceMinutes(a_data, m_datahora)==true) {
										preparedStmt.setInt(1, idzona);
										preparedStmt.setString(2, "L"+idzona);
										preparedStmt.setTimestamp(3, m_datahora);
										preparedStmt.setString(4, "L");
										preparedStmt.setString(5, c_nomecultura);
										preparedStmt.setString(6, s);
										preparedStmt.setString(7, c_utilizador);
										preparedStmt.setInt(8, idcultura);
										preparedStmt.execute();
										System.out.println("AMORIM");
									}
								}
							}
						}
						if(p_minl<findMedian(l_copy)&&findMedian(l_copy)<p_minl+abs_l*0.2) {
							if(findMedian(l_copy)>=p_minl+abs_l*0.1) {
								System.out.println("ALERTA L!");
								String s= "";
								System.out.println("EVALDO:" + a_l2.next());
								int a= a_l2.getRow();
								System.out.println("ELIAS"+a);
								if(a==0) {
									preparedStmt.setInt(1, idzona);
									preparedStmt.setString(2, "L"+idzona);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "L");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("AMORIM");
								}else {
									System.out.println("ELIAS"+a);
									String a_tipoal= a_l2.getString("tipoalerta");
									Timestamp a_data= a_l2.getTimestamp("datahora");
									System.out.println("BOJINOV" + a_tipoal);
									if(diferenceMinutes(a_data, m_datahora)==true) {
										preparedStmt.setInt(1, idzona);
										preparedStmt.setString(2, "L"+idzona);
										preparedStmt.setTimestamp(3, m_datahora);
										preparedStmt.setString(4, "L");
										preparedStmt.setString(5, c_nomecultura);
										preparedStmt.setString(6, s);
										preparedStmt.setString(7, c_utilizador);
										preparedStmt.setInt(8, idcultura);
										preparedStmt.execute();
										System.out.println("AMORIM");
									}
								}
							}
						}
					}	
				}
			}
		}
	}
}