import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
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
			System.out.println(msg);
			String[] aux= msg.split(",", 4);	
			String aux1= aux[0];
			String[] z= aux1.split("=", 2);
			String auxzona[]= z[1].split("Z", 2);
			String zona= auxzona[1];

			String aux2= aux[1];
			String[] s= aux2.split("=", 2);
			String idsensor= s[1];

			String aux3= aux[2];
			String[] hora= aux3.split("=", 2);
			String data= hora[1];
			Timestamp datahora= datahoraTimestamp(data);

			String aux4= aux[3];
			String[] l= aux4.split("=", 2);
			String aux_l= l[1];
			String auxleitura= aux_l.replace("}}}}}}", "");
			System.out.println("pote");

			System.out.println("zona1" + zona);        
			int idzona= Integer.parseInt(zona);
			double leitura= Double.parseDouble(auxleitura);
			System.out.println("zona" + idzona);

			String query = "INSERT INTO medicao (idzona, idsensor, datahora, leitura, valorvalido) VALUES ('" + idzona + "', '" + idsensor +"', '"+ datahora +"', '"+ leitura + "', '1');";
			System.out.println("---MYSQL---");
			System.out.println(query);
			try {
				connectionSQL.createStatement().executeUpdate(query);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
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