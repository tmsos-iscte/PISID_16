import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class alertas {
	static final String db = "sid";
	static final String DBuser = "root";
	static final String DBpass = "root";
	static Connection connectionSQL;

	static int i;
	static int aux_medicao= 0;

	public static double findMedian(List<Double> a) {
		Collections.sort(a);
		return (double) (a.get((20-1)/2)+a.get(20/2))/2.0;
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

	public static ArrayList<Double>[] getListasT(int x) {
		ArrayList<Double>[] t= (ArrayList<Double>[]) new ArrayList[i];
		for(int index= 0;index<x; index++) {
			t[index] = new ArrayList<Double>();
		}
		return t;
	}

	public static ArrayList<Double>[] getListasH(int x) {
		ArrayList<Double>[] h= (ArrayList<Double>[]) new ArrayList[i];
		for(int index= 0;index<x; index++) {
			h[index] = new ArrayList<Double>();
		}
		return h;
	}

	public static ArrayList<Double>[] getListasL(int x) {
		ArrayList<Double>[] l= (ArrayList<Double>[]) new ArrayList[i];
		for(int index= 0;index<x; index++) {
			l[index] = new ArrayList<Double>();
		}
		return l;
	}

	private static void Alerta10(List<Double> t, List<Double> h, List<Double> l) throws ClassNotFoundException, SQLException, InterruptedException {
		// TODO Auto-generated method stub
		Class.forName("com.mysql.cj.jdbc.Driver");
		connectionSQL= DriverManager.getConnection("jdbc:mysql://localhost/" + db + "?useTimezone=true&serverTimezone=UTC", DBuser, DBpass);

		if(connectionSQL!=null)
			System.out.println("Ligação estabelecida");

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

		c_aux2.next();

		int c_idparametro= c_aux2.getInt("idparametro");
		int idcultura=c_aux2.getInt("IDCultura");
		String c_nomecultura= c_aux2.getString("nomecultura");
		String c_utilizador= c_aux2.getString("utilizador");
		String parametro= "SELECT * FROM parametrocultura WHERE idparametro=" + c_idparametro;
		PreparedStatement p_aux1 = connectionSQL.prepareStatement(parametro);
		ResultSet p_aux2 =p_aux1.executeQuery();

		p_aux2.next();
		String query_alerta = "INSERT INTO alerta (idzona, idsensor, datahora, tipoalerta, nomecultura, descricao, utilizador, idcultura)" + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement preparedStmt = connectionSQL.prepareStatement(query_alerta);

		double p_maxtemp= p_aux2.getDouble("maxtemperatura");
		double p_mintemp= p_aux2.getDouble("mintemperatura");	
		double p_maxhum= p_aux2.getDouble("maxhumidade");
		double p_minhum= p_aux2.getDouble("minhumidade");
		double p_maxl= p_aux2.getDouble("maxluz");
		double p_minl= p_aux2.getDouble("minluz");
		double abs_t= Math.abs(p_maxtemp-p_mintemp);
		double abs_h= Math.abs(p_maxhum-p_minhum);
		double abs_l= Math.abs(p_maxl-p_minl);

		String alerta= "SELECT * FROM sid.alerta WHERE idcultura="+idcultura+" AND idsensor='"+m_idsensor+"' ORDER BY idalerta DESC";
		PreparedStatement a_aux1= connectionSQL.prepareStatement(alerta);
		ResultSet a_aux2= a_aux1.executeQuery();
		a_aux2.next();
		System.out.println("BOECK"+a_aux2.getRow());

		if(m_idsensor.contains("T")) {
			t.add(0,m_leitura);
			System.out.println("T:" + t);
			if(t.size()==20) {
				List<Double> t_copy = new ArrayList<>(t);
				if(findMedian(t_copy)>p_maxtemp || findMedian(t_copy)<p_mintemp) {
					String s= "";
					preparedStmt.setInt(1, m_idzona);
					preparedStmt.setString(2, m_idsensor);
					preparedStmt.setTimestamp(3, m_datahora);
					preparedStmt.setString(4, "H");
					preparedStmt.setString(5, c_nomecultura);
					preparedStmt.setString(6, s);
					preparedStmt.setString(7, c_utilizador);
					preparedStmt.setInt(8, idcultura);
					preparedStmt.execute();
					System.out.println("ALERTA DO TIPO H");
					String query = "UPDATE sid.cultura SET Estado=0 WHERE IDCultura=" + idcultura +";" ;
					connectionSQL.createStatement().executeUpdate(query);
				}

				if(p_maxtemp>findMedian(t_copy)&&findMedian(t_copy)>p_maxtemp-abs_t*0.05) { 
					String s= "";
					int a= a_aux2.getRow();
					if(a==0) {
						preparedStmt.setInt(1, m_idzona);
						preparedStmt.setString(2, m_idsensor);
						preparedStmt.setTimestamp(3, m_datahora);
						preparedStmt.setString(4, "H");
						preparedStmt.setString(5, c_nomecultura);
						preparedStmt.setString(6, s);
						preparedStmt.setString(7, c_utilizador);
						preparedStmt.setInt(8, idcultura);
						preparedStmt.execute();
						System.out.println("ALERTA DO TIPO H");
					}else {
						String a_tipoal= a_aux2.getString("tipoalerta");
						Timestamp a_data= a_aux2.getTimestamp("datahora");
						if(a_tipoal.equals("H")) {
							if(diferenceMinutes(a_data, m_datahora)==true) {
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("ALERTA DO TIPO H");
							}
						} else {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "H");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, s);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, idcultura);
							preparedStmt.execute();
							System.out.println("ALERTA DO TIPO H");
						}
					}
				}

				if(p_mintemp<findMedian(t_copy)&&findMedian(t_copy)<p_mintemp+abs_t*0.05) {
					String s= "";
					int a= a_aux2.getRow();
					if(a==0) {
						preparedStmt.setInt(1, m_idzona);
						preparedStmt.setString(2, m_idsensor);
						preparedStmt.setTimestamp(3, m_datahora);
						preparedStmt.setString(4, "H");
						preparedStmt.setString(5, c_nomecultura);
						preparedStmt.setString(6, s);
						preparedStmt.setString(7, c_utilizador);
						preparedStmt.setInt(8, idcultura);
						preparedStmt.execute();
						System.out.println("ALERTA DO TIPO H");;
					}else {
						String a_tipoal= a_aux2.getString("tipoalerta");
						Timestamp a_data= a_aux2.getTimestamp("datahora");
						if(a_tipoal.equals("H")) {
							if(diferenceMinutes(a_data, m_datahora)==true) {
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("ALERTA DO TIPO H");
							}
						} else {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "H");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, s);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, idcultura);
							preparedStmt.execute();
							System.out.println("ALERTA DO TIPO H");
						}
					}
				}

				if(p_maxtemp>findMedian(t_copy)&&findMedian(t_copy)>p_maxtemp-abs_t*0.1) {
					if(findMedian(t_copy)<=p_maxtemp-abs_t*0.05) {
						String s= "";
						int a= a_aux2.getRow();
						if(a==0) {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "M");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, s);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, idcultura);
							preparedStmt.execute();
							System.out.println("ALERTA DO TIPO M");
						}else {
							String a_tipoal= a_aux2.getString("tipoalerta");
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(a_tipoal.equals("H")||a_tipoal.equals("M")) {
								if(diferenceMinutes(a_data, m_datahora)==true) {
									preparedStmt.setInt(1, m_idzona);
									preparedStmt.setString(2, m_idsensor);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "M");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("ALERTA DO TIPO M");
								}
							}else {
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "M");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("ALERTA DO TIPO M");
							}
						}
					}
				}

				if(p_mintemp<findMedian(t_copy)&&findMedian(t_copy)<p_mintemp+abs_t*0.1) {
					if(findMedian(t_copy)>=p_mintemp+abs_t*0.05) {
						String s= "";
						int a= a_aux2.getRow();
						if(a==0) {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "M");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, s);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, idcultura);
							preparedStmt.execute();
							System.out.println("ALERTA DO TIPO M");
						}else {
							String a_tipoal= a_aux2.getString("tipoalerta");
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(a_tipoal.equals("H")||a_tipoal.equals("M")) {
								if(diferenceMinutes(a_data, m_datahora)==true) {
									preparedStmt.setInt(1, m_idzona);
									preparedStmt.setString(2, m_idsensor);
									preparedStmt.setTimestamp(3, m_datahora);
									preparedStmt.setString(4, "M");
									preparedStmt.setString(5, c_nomecultura);
									preparedStmt.setString(6, s);
									preparedStmt.setString(7, c_utilizador);
									preparedStmt.setInt(8, idcultura);
									preparedStmt.execute();
									System.out.println("ALERTA DO TIPO M");
								}
							}else {
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "M");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("ALERTA DO TIPO M");
							}
						}
					}
				}

				if(p_maxtemp>findMedian(t_copy)&&findMedian(t_copy)>p_maxtemp-abs_t*0.2) {
					if(findMedian(t_copy)<=p_maxtemp-abs_t*0.1) {
						String s= "";
						int a= a_aux2.getRow();
						if(a==0) {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "L");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, s);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, idcultura);
							preparedStmt.execute();
							System.out.println("ALERTA DO TIPO L");
						}else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "L");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("ALERTA DO TIPO L");
							}
						}
					}
				}
				if(p_mintemp<findMedian(t_copy)&&findMedian(t_copy)<p_mintemp+abs_t*0.2) {
					if(findMedian(t_copy)>=p_mintemp+abs_t*0.1) {
						String s= "";
						int a= a_aux2.getRow();
						if(a==0) {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "L");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, s);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, idcultura);
							preparedStmt.execute();
							System.out.println("ALERTA DO TIPO L");
						}else {
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							if(diferenceMinutes(a_data, m_datahora)==true) {
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "L");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("ALERTA DO TIPO L");
							}
						}
					}
				}
				System.out.println("BRUMA" + t);
				System.out.println("DOUGLAS" + t.get(19)+t.get(18)+t.get(17)+t.get(16)+t.get(15)+t.get(14)+t.get(13)+t.get(12)+t.get(11)+t.get(10));
				t.remove(19); t.remove(18); t.remove(17); t.remove(16); t.remove(15); t.remove(14); t.remove(13); t.remove(12); t.remove(11); t.remove(10);						
				System.out.println("JUS" + t);
			}	
		}
		if(m_idsensor.contains("H")) {
			System.out.println("Sensor humidade");
			h.add(0, m_leitura);
			System.out.println("GRIMI" + h.size());
			System.out.println("H:" + h);
			if(h.size()==20) {
				List<Double> h_copy = new ArrayList<>(h);
				if(findMedian(h_copy)>p_maxtemp || findMedian(h_copy)<p_mintemp) {
					String s= "";
					System.out.println("EVALDO:" + a_aux2.next());
					int a= a_aux2.getRow();
					System.out.println("ELIAS"+a);
					preparedStmt.setInt(1, m_idzona);
					preparedStmt.setString(2, m_idsensor);
					preparedStmt.setTimestamp(3, m_datahora);
					preparedStmt.setString(4, "H");
					preparedStmt.setString(5, c_nomecultura);
					preparedStmt.setString(6, s);
					preparedStmt.setString(7, c_utilizador);
					preparedStmt.setInt(8, idcultura);
					preparedStmt.execute();
					String query = "UPDATE sid.cultura SET Estado=0 WHERE IDCultura=" + idcultura +";" ;
					connectionSQL.createStatement().executeUpdate(query);
					System.out.println("AMORIM");
				}

				if(p_maxtemp>findMedian(h_copy)&&findMedian(h_copy)>p_maxtemp-abs_h*0.05) { 
					System.out.println("ALERTA H!");
					String s= "";
					System.out.println("EVALDO:" + a_aux2.next());
					int a= a_aux2.getRow();
					System.out.println("ELIAS"+a);
					if(a==0) {
						preparedStmt.setInt(1, m_idzona);
						preparedStmt.setString(2, m_idsensor);
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
						String a_tipoal= a_aux2.getString("tipoalerta");
						Timestamp a_data= a_aux2.getTimestamp("datahora");
						System.out.println("BOJINOV" + a_tipoal);
						if(a_tipoal.equals("H")) {
							if(diferenceMinutes(a_data, m_datahora)==true) {
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
								preparedStmt.setTimestamp(3, m_datahora);
								preparedStmt.setString(4, "H");
								preparedStmt.setString(5, c_nomecultura);
								preparedStmt.setString(6, s);
								preparedStmt.setString(7, c_utilizador);
								preparedStmt.setInt(8, idcultura);
								preparedStmt.execute();
								System.out.println("AMORIM");
							}
						}else {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
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
				if(p_mintemp<findMedian(h_copy)&&findMedian(h_copy)<p_mintemp+abs_h*0.05) {
					System.out.println("ALERTA H!");
					String s= "";
					System.out.println("EVALDO:" + a_aux2.next());
					int a= a_aux2.getRow();
					System.out.println("ELIAS"+a);
					if(a==0) {
						preparedStmt.setInt(1, m_idzona);
						preparedStmt.setString(2, m_idsensor);
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
						String a_tipoal= a_aux2.getString("tipoalerta");
						Timestamp a_data= a_aux2.getTimestamp("datahora");
						System.out.println("BOJINOV" + a_tipoal);
						if(a_tipoal.equals("H")) {
						if(diferenceMinutes(a_data, m_datahora)==true) {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "H");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, s);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, idcultura);
							preparedStmt.execute();
							System.out.println("AMORIM");
						}
						}else {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
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
				if(p_maxtemp>findMedian(h_copy)&&findMedian(h_copy)>p_maxtemp+abs_h*0.1) {
					if(findMedian(h_copy)<=p_maxtemp-abs_t*0.05) {
						System.out.println("ALERTA M!");
						String s= "";
						System.out.println("EVALDO:" + a_aux2.next());
						int a= a_aux2.getRow();
						System.out.println("ELIAS"+a);
						if(a==0) {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
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
							String a_tipoal= a_aux2.getString("tipoalerta");
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							System.out.println("BOJINOV" + a_tipoal);
							if(a_tipoal.equals("H")||a_tipoal.equals("M")) {
								if(diferenceMinutes(a_data, m_datahora)==true) {
									preparedStmt.setInt(1, m_idzona);
									preparedStmt.setString(2, m_idsensor);
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
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
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
				if(p_mintemp<findMedian(h_copy)&&findMedian(h_copy)<p_mintemp+abs_h*0.1) {
					if(findMedian(h_copy)>=p_mintemp+abs_h*0.05) {
						System.out.println("ALERTA M!");
						String s= "";
						System.out.println("EVALDO:" + a_aux2.next());
						int a= a_aux2.getRow();
						System.out.println("ELIAS"+a);
						if(a==0) {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
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
							String a_tipoal= a_aux2.getString("tipoalerta");
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							System.out.println("BOJINOV" + a_tipoal);
							if(a_tipoal.equals("H")||a_tipoal.equals("M")) {
								if(diferenceMinutes(a_data, m_datahora)==true) {
									preparedStmt.setInt(1, m_idzona);
									preparedStmt.setString(2, m_idsensor);
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
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
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
				if(p_maxtemp>findMedian(h_copy)&&findMedian(h_copy)>p_maxtemp+abs_h*0.2) {
					if(findMedian(h_copy)<=p_maxtemp-abs_t*0.1) {
						System.out.println("ALERTA L!");
						String s= "";
						System.out.println("EVALDO:" + a_aux2.next());
						int a= a_aux2.getRow();
						System.out.println("ELIAS"+a);
						if(a==0) {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
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
							String a_tipoal= a_aux2.getString("tipoalerta");
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							System.out.println("BOJINOV" + a_tipoal);
							if(diferenceMinutes(a_data, m_datahora)==true) {
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
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
				if(p_mintemp<findMedian(h_copy)&&findMedian(h_copy)<p_mintemp+abs_h*0.2) {
					if(findMedian(h)<=p_mintemp+abs_h*0.1) {
						System.out.println("ALERTA L!");
						String s= "";
						System.out.println("EVALDO:" + a_aux2.next());
						int a= a_aux2.getRow();
						System.out.println("ELIAS"+a);
						if(a==0) {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
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
							String a_tipoal= a_aux2.getString("tipoalerta");
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							System.out.println("BOJINOV" + a_tipoal);
							if(diferenceMinutes(a_data, m_datahora)==true) {
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
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
				h.remove(19); h.remove(18); h.remove(17); h.remove(16); h.remove(15); h.remove(14); h.remove(13); h.remove(12); h.remove(11); h.remove(10);
			}	
		}
		if(m_idsensor.contains("L")) {
			System.out.println("Sensor luz");
			l.add(0,m_leitura);
			System.out.println("L: " + l);
			System.out.println("GRIMI" + l.size());
			if(l.size()==20) {
				List<Double> l_copy = new ArrayList<>(l);
				if(findMedian(l_copy)>p_maxtemp || findMedian(l_copy)<p_mintemp) {
					System.out.println("ALERTA H!");
					String s= "";
					System.out.println("EVALDO:" + a_aux2.next());
					int a= a_aux2.getRow();
					System.out.println("ELIAS"+a);
					preparedStmt.setInt(1, m_idzona);
					preparedStmt.setString(2, m_idsensor);
					preparedStmt.setTimestamp(3, m_datahora);
					preparedStmt.setString(4, "H");
					preparedStmt.setString(5, c_nomecultura);
					preparedStmt.setString(6, s);
					preparedStmt.setString(7, c_utilizador);
					preparedStmt.setInt(8, idcultura);
					preparedStmt.execute();
					String query = "UPDATE sid.cultura SET Estado=0 WHERE IDCultura=" + idcultura +";" ;
					connectionSQL.createStatement().executeUpdate(query);
					System.out.println("AMORIM");
				}
				
				if(p_maxtemp>findMedian(l_copy)&&findMedian(l_copy)>p_maxtemp+abs_l*0.05) { 
					System.out.println("ALERTA H!");
					String s= "";
					System.out.println("EVALDO:" + a_aux2.next());
					int a= a_aux2.getRow();
					System.out.println("ELIAS"+a);
					if(a==0) {
						preparedStmt.setInt(1, m_idzona);
						preparedStmt.setString(2, m_idsensor);
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
						String a_tipoal= a_aux2.getString("tipoalerta");
						Timestamp a_data= a_aux2.getTimestamp("datahora");
						System.out.println("BOJINOV" + a_tipoal);
						if(a_tipoal.equals("H")) {
						if(diferenceMinutes(a_data, m_datahora)==true) {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "H");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, s);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, idcultura);
							preparedStmt.execute();
							System.out.println("AMORIM");
						}
						} else {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
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
				if(p_mintemp<findMedian(l_copy)&&findMedian(l_copy)<p_mintemp+abs_l*0.05) {
					System.out.println("ALERTA H!");
					String s= "";
					System.out.println("EVALDO:" + a_aux2.next());
					int a= a_aux2.getRow();
					System.out.println("ELIAS"+a);
					if(a==0) {
						preparedStmt.setInt(1, m_idzona);
						preparedStmt.setString(2, m_idsensor);
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
						String a_tipoal= a_aux2.getString("tipoalerta");
						Timestamp a_data= a_aux2.getTimestamp("datahora");
						System.out.println("BOJINOV" + a_tipoal);
						if(a_tipoal.equals("H")) {
						if(diferenceMinutes(a_data, m_datahora)==true) {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
							preparedStmt.setTimestamp(3, m_datahora);
							preparedStmt.setString(4, "H");
							preparedStmt.setString(5, c_nomecultura);
							preparedStmt.setString(6, s);
							preparedStmt.setString(7, c_utilizador);
							preparedStmt.setInt(8, idcultura);
							preparedStmt.execute();
							System.out.println("AMORIM");
						}
						}else {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
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
				if(p_maxtemp>findMedian(l_copy)&&findMedian(l_copy)>p_maxtemp+abs_l*0.1) {
					if(findMedian(l_copy)<=p_maxtemp-abs_t*0.05) {
						System.out.println("ALERTA M!");
						String s= "";
						System.out.println("EVALDO:" + a_aux2.next());
						int a= a_aux2.getRow();
						System.out.println("ELIAS"+a);
						if(a==0) {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
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
							String a_tipoal= a_aux2.getString("tipoalerta");
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							System.out.println("BOJINOV" + a_tipoal);
							if(a_tipoal.equals("H")||a_tipoal.equals("M")) {
								if(diferenceMinutes(a_data, m_datahora)==true) {
									preparedStmt.setInt(1, m_idzona);
									preparedStmt.setString(2, m_idsensor);
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
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
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
				if(p_mintemp<findMedian(l_copy)&&findMedian(l_copy)<p_mintemp+abs_l*0.1) {
					if(findMedian(l_copy)>=p_mintemp+abs_l*0.05) {
						System.out.println("ALERTA M!");
						String s= "";
						System.out.println("EVALDO:" + a_aux2.next());
						int a= a_aux2.getRow();
						System.out.println("ELIAS"+a);
						if(a==0) {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
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
							String a_tipoal= a_aux2.getString("tipoalerta");
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							System.out.println("BOJINOV" + a_tipoal);
							if(a_tipoal.equals("H")||a_tipoal.equals("M")) {
								if(diferenceMinutes(a_data, m_datahora)==true) {
									preparedStmt.setInt(1, m_idzona);
									preparedStmt.setString(2, m_idsensor);
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
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
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
				if(p_maxtemp>findMedian(l_copy)&&findMedian(l_copy)>p_maxtemp+abs_l*0.2) {
					if(findMedian(l_copy)<=p_maxtemp-abs_t*0.1) {
						System.out.println("ALERTA L!");
						String s= "";
						System.out.println("EVALDO:" + a_aux2.next());
						int a= a_aux2.getRow();
						System.out.println("ELIAS"+a);
						if(a==0) {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
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
							String a_tipoal= a_aux2.getString("tipoalerta");
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							System.out.println("BOJINOV" + a_tipoal);
							if(diferenceMinutes(a_data, m_datahora)==true) {
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
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
				if(p_mintemp<findMedian(l_copy)&&findMedian(l_copy)<p_mintemp+abs_l*0.2) {
					if(findMedian(l_copy)>=p_mintemp+abs_l*0.1) {
						System.out.println("ALERTA L!");
						String s= "";
						System.out.println("EVALDO:" + a_aux2.next());
						int a= a_aux2.getRow();
						System.out.println("ELIAS"+a);
						if(a==0) {
							preparedStmt.setInt(1, m_idzona);
							preparedStmt.setString(2, m_idsensor);
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
							String a_tipoal= a_aux2.getString("tipoalerta");
							Timestamp a_data= a_aux2.getTimestamp("datahora");
							System.out.println("BOJINOV" + a_tipoal);
							if(diferenceMinutes(a_data, m_datahora)==true) {
								preparedStmt.setInt(1, m_idzona);
								preparedStmt.setString(2, m_idsensor);
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
				l.remove(19); l.remove(18); l.remove(17); l.remove(16); l.remove(15); l.remove(14); l.remove(13); l.remove(12); l.remove(11); l.remove(10);
			}	
		}


		Thread.sleep(1000);				
	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException {

		Class.forName("com.mysql.cj.jdbc.Driver");
		connectionSQL= DriverManager.getConnection("jdbc:mysql://localhost/" + db + "?useTimezone=true&serverTimezone=UTC", DBuser, DBpass);
		String listas= "SELECT * FROM cultura ORDER BY IDCultura DESC";
		PreparedStatement l_aux1 = connectionSQL.prepareStatement(listas);
		ResultSet l_aux2 = l_aux1.executeQuery();
		l_aux2.next();
		i= l_aux2.getInt("IDCultura");
		System.out.println(i);
		ArrayList<Double>[] t= getListasT(i);
		ArrayList<Double>[] h= getListasH(i);
		ArrayList<Double>[] l= getListasL(i);

		for(;;) {
			String medicao= "SELECT * FROM medicao ORDER BY IDMedicao DESC";
			PreparedStatement m_aux1 = connectionSQL.prepareStatement(medicao);
			ResultSet m_aux2 = m_aux1.executeQuery();
			m_aux2.next();
			int idmedicao= m_aux2.getInt("IDMedicao"); 
			int zona= m_aux2.getInt("IDZona");

			String cultura= "SELECT * FROM cultura WHERE IDZona=" + zona;
			PreparedStatement c_aux1 = connectionSQL.prepareStatement(cultura);
			ResultSet c_aux2 = c_aux1.executeQuery();
			c_aux2.next();
			if(c_aux2.getRow()!=0) {
				int estado= c_aux2.getInt("Estado");
				if(estado==1) {
					String Bmedicao= "SELECT * FROM medicao WHERE IDMedicao=" + idmedicao;
					PreparedStatement Bm_aux1 = connectionSQL.prepareStatement(Bmedicao);
					ResultSet Bm_aux2 = Bm_aux1.executeQuery();
					Bm_aux2.next();

					if(idmedicao!=aux_medicao) {
						Alerta10(t[zona], h[zona], l[zona]);
						aux_medicao= idmedicao;
					}else {
						Thread.sleep(1000);
					}
				} else {
					Thread.sleep(1000);
				}
			} else {
				Thread.sleep(1000);
			}
		}
	}
}