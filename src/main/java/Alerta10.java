import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Alerta10 {
	static final String db = "sid";
	static final String DBuser = "root";
	static final String DBpass = "root";
	static Connection connectionSQL;
	
	public static double findMedian(List<Double> a) {
		Collections.sort(a);
		return (double)(a.get((20-1)/2) + a.get(20/2)) / 2.0;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException {

		Class.forName("com.mysql.cj.jdbc.Driver");
		connectionSQL= DriverManager.getConnection("jdbc:mysql://localhost/" + db + "?useTimezone=true&serverTimezone=UTC", DBuser, DBpass);

		if(connectionSQL!=null)
			System.out.println("Ligação estabelecida");

		String medicao= "select * from medicao";
		PreparedStatement m_aux1 = connectionSQL.prepareStatement(medicao);
		ResultSet m_aux2 = m_aux1.executeQuery();
		List<Double> t= new ArrayList<Double>(20);
		List<Double> h= new ArrayList<Double>(20);
		List<Double> l= new ArrayList<Double>(20);

		while(m_aux2.next()) {
			int idmedicao= m_aux2.getInt("idmedicao");
			int m_idzona= m_aux2.getInt("idzona");
			String m_idsensor= m_aux2.getString("idsensor");
			String m_datahora= m_aux2.getString("datahora");
			double m_leitura= m_aux2.getDouble("leitura");

			System.out.println("---medicao--");
			System.out.println("id:" + idmedicao + " zona:" + m_idzona + " sensor:" + m_idsensor + " DataHora:" + m_datahora + " leitura:" + m_leitura);

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
					double p_minhum= p_aux2.getDouble("minhumidade");
					double p_maxl= p_aux2.getDouble("maxluz");
					double p_minl= p_aux2.getDouble("minluz");
					double abs_t= Math.abs(p_maxtemp-p_mintemp);
					double abs_h= Math.abs(p_maxhum-p_minhum);
					double abs_l= Math.abs(p_maxl-p_minl);
					System.out.println("---parametro---");
					System.out.println("parametro:" + p_idparametro);
					System.out.println("maxluz:" + m_idsensor);
					System.out.println(p_aux2.next());
					System.out.println("pote");
					if(m_idsensor.contains("T")) {
						System.out.println("Sensor temperatura");
						if(t.size()==20)
							t.remove(19);
						t.add(0,m_leitura);
						System.out.println("T:" + t);
						if(t.size()==20) {
							if (findMedian(t)>p_maxtemp || findMedian(t)<p_mintemp)
								System.out.println("ALERTA H!");
							if(p_maxtemp>findMedian(t)&&findMedian(t)>p_maxtemp+abs_t*0.05)
								System.out.println("ALERTA H!");
							if(p_mintemp<findMedian(t)&&findMedian(t)<p_mintemp+abs_t*0.05)
								System.out.println("ALERTA H!");
							if(p_maxtemp>findMedian(t)&&findMedian(t)>p_maxtemp+abs_t*0.1)
								System.out.println("ALERTA M!");
							if(p_mintemp<findMedian(t)&&findMedian(t)<p_mintemp+abs_t*0.1)
								System.out.println("ALERTA M!");
							if(p_maxtemp>findMedian(t)&&findMedian(t)>p_maxtemp+abs_t*0.2)
								System.out.println("ALERTA L!");
							if(p_mintemp<findMedian(t)&&findMedian(t)<p_mintemp+abs_t*0.2)
								System.out.println("ALERTA L!");
						}	
					}
					if(m_idsensor.contains("H")) {
						System.out.println("Sensor humidade");
						if(h.size()==20)
							h.remove(19);
						h.add(m_leitura);
						System.out.println("H:" + h);
						if(h.size()==20) {
							if (findMedian(h)>p_maxhum || findMedian(h)<p_minhum)
								System.out.println("ALERTA H!");
							if(p_maxhum>findMedian(h)&&findMedian(h)>p_maxhum+abs_h*0.05)
								System.out.println("ALERTA H!");
							if(p_minhum<findMedian(h)&&findMedian(h)<p_minhum+abs_h*0.05)
								System.out.println("ALERTA H!");
							if(p_maxhum>findMedian(h)&&findMedian(h)>p_maxhum+abs_h*0.1)
								System.out.println("ALERTA M!");
							if(p_minhum<findMedian(h)&&findMedian(h)<p_minhum+abs_h*0.1)
								System.out.println("ALERTA M!");
							if(p_maxhum>findMedian(h)&&findMedian(h)>p_maxhum+abs_h*0.2)
								System.out.println("ALERTA L!");
							if(p_minhum<findMedian(h)&&findMedian(h)<p_minhum+abs_h*0.2)
								System.out.println("ALERTA L!");
						}	
					}
					if(m_idsensor.contains("L")) {
						System.out.println("Sensor luz");
						if(l.size()==20)
							l.remove(19);
						l.add(0,m_leitura);
						System.out.println("L: " + l);
						System.out.println(l.size());
						if(l.size()==20) {
							if (findMedian(l)>p_maxl || findMedian(l)<p_minl)
								System.out.println("ALERTA H!");
							if(p_maxl>findMedian(l)&&findMedian(l)>p_maxl+abs_l*0.05)
								System.out.println("ALERTA H!");
							if(p_minl<findMedian(l)&&findMedian(l)<p_minl+abs_l*0.05)
								System.out.println("ALERTA H!");
							if(p_maxl>findMedian(l)&&findMedian(l)>p_maxl+abs_l*0.1)
								System.out.println("ALERTA M!");
							if(p_minl<findMedian(l)&&findMedian(l)<p_minl+abs_l*0.1)
								System.out.println("ALERTA M!");
							if(p_maxl>findMedian(l)&&findMedian(l)>p_maxl+abs_l*0.2)
								System.out.println("ALERTA L!");
							if(p_minl<findMedian(l)&&findMedian(l)<p_minl+abs_l*0.2)
								System.out.println("ALERTA L!");
						}	
					}
				}
			}
			Thread.sleep(1000);				
		}
	}
}
