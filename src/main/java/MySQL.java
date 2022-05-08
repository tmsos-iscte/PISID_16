import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.Document;

//meter imports necessários
public class MySQL extends Thread{
	static final String db = "sid";
	static final String DBuser = "root";
	static final String DBpass = "root";
	static Connection connectionSQL;
	
	static final String db1 = "sid2022";
	static final String DBuser1 = "aluno";
	static final String DBpass1 = "aluno";
	static Connection connectionSQL1;

	private int port=5000;
	private ServerSocket ss;
	
	static Timestamp datahoraTimestamp(String datahora) {
		String T= datahora.replace('T', ' ');
		String Z= T.substring(0, T.length()-1);
		System.out.println(Z);
		Timestamp timestamp= null;
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(Z);
			timestamp = new Timestamp(date.getTime());

			System.out.println(timestamp); // 2021-12-15 00:34:56.789
		} catch (ParseException exception) {
			exception.printStackTrace();
		}
		return timestamp;
	}
	
	public MySQL() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		connectionSQL= DriverManager.getConnection("jdbc:mysql://localhost/" +db+ "?useTimezone=true&serverTimezone=UTC", DBuser, DBpass);
		connectionSQL1= DriverManager.getConnection("jdbc:mysql://194.210.86.10/" +db1+ "?useTimezone=true&serverTimezone=UTC", DBuser1, DBpass1);		

		try {
				this.ss = new ServerSocket(port);
				
				while(true) {
					
				Socket sock = ss.accept();
				
				DealWithInfo d = new DealWithInfo(sock);
				d.start();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private class DealWithInfo extends Thread{
		public Socket socket;
		private ObjectInputStream in;
		
		public DealWithInfo(Socket s) {
			// TODO Auto-generated constructor stub
			
			
			this.socket = s;
		    try {
				in= new ObjectInputStream (socket.getInputStream ());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
		@Override
		public void run() {
			Document doc= new Document();
			try {
				doc= (Document) in.readObject();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//meter aqui o resto das cenas do mysql
			String msg= doc.toString();
			System.out.println(msg + "Vitor");
			String[] aux= msg.split(",", 6);	
			String aux1= aux[1];
			String[] z= aux1.split("=", 2);
			String auxzona[]= z[1].split("Z", 2);
			String zona= auxzona[1];

			String aux2= aux[2];
			String[] s= aux2.split("=", 2);
			String idsensor= s[1];

			String aux3= aux[3];
			String[] hora= aux3.split("=", 2);
			String data= hora[1];
			Timestamp datahora= datahoraTimestamp(data);

			String aux4= aux[4];
			String[] l= aux4.split("=", 2);
			String aux_l= l[1];
			String auxleitura= aux_l.replace("}}", "");
			System.out.println("pote");

			System.out.println("zona1" + zona);        
			int idzona= Integer.parseInt(zona);
			float leitura= Float.parseFloat(auxleitura);
			
		
			System.out.println("Gerson magrao:" + leitura);

			String x[];
			int id=0;
			String sensor="";
			//if(getTime(datahora)==true) {
				if(idsensor.contains("T")) {
					x= idsensor.split("T");
					id= Integer.parseInt(x[1]);
					sensor= idsensor.substring(0, idsensor.length()-1);
				} else if(idsensor.contains("H")) {
					x= idsensor.split("H");
					id= Integer.parseInt(x[1]);
					sensor= idsensor.substring(0, idsensor.length()-1);
				}else if(idsensor.contains("L")) {
					x= idsensor.split("L");
					id= Integer.parseInt(x[1]);
					sensor= idsensor.substring(0, idsensor.length()-1);
				}

				String pre_mysql= "SELECT * FROM sensor WHERE idsensor="+id+" AND tipo='"+sensor+"'";
				
				PreparedStatement pre_mysql_aux1;
				ResultSet pre_mysql_aux2 = null;
				try {
					pre_mysql_aux1 = connectionSQL1.prepareStatement(pre_mysql);	
					pre_mysql_aux2 =pre_mysql_aux1.executeQuery();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				
				try {
					while(pre_mysql_aux2.next()) {

						double limitemin= pre_mysql_aux2.getDouble("limiteinferior");
						double limitemax= pre_mysql_aux2.getDouble("limitesuperior");
						if(limitemin<=leitura&&limitemax>=leitura) {
							String query = "INSERT INTO medicao (idzona, idsensor, datahora, leitura, valorvalido) VALUES ('" + idzona + "', '" + idsensor +"', '"+ datahora +"', '"+ leitura + "', '1');";
							System.out.println("---MYSQL---");
							System.out.println(query);
							try {
								connectionSQL.createStatement().executeUpdate(query);
								 System.out.println("SPORTING CP" + java.time.LocalDateTime.now());  
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return;
		}
		}
	//}
	public static void main(String[] args) {
		Thread threadTest;
		try {
			threadTest = new MySQL();
			threadTest.start();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//threadTest.run();
		
	}
}