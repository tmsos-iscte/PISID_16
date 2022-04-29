import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bson.Document;

public class MySQL extends Thread{
	static final String db = "sid";
	static final String DBuser = "root";
	static final String DBpass = "root";
	static Connection connectionSQL;
	static Socket socket;
	static ObjectInputStream in;

	public MySQL() {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			connectionSQL = DriverManager.getConnection("jdbc:mysql://localhost/" + db + "?useTimezone=true&serverTimezone=UTC", DBuser, DBpass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			socket = new Socket("localhost", 5000); 
			in = new ObjectInputStream((socket.getInputStream()));

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void run() {
		//while(true) {
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
			String msg= doc.toString();
			String[] aux= msg.split(",", 4);	
			String aux1= aux[0];
			String[] z= aux1.split("Z", 2);
			String zona= z[1];

			String idsensor= aux[1];

			String datahora= aux[2];

			String aux4= aux[3];
			String[] l= aux4.split("}}}}", 2);
			String auxleitura= l[0];
			System.out.println(auxleitura);
			System.out.println("pote");

			System.out.println("zona1" + zona);        
			int idzona= Integer.parseInt(zona);
			double leitura= Double.parseDouble(auxleitura);
			System.out.println("zona" + idzona);

			String query = "INSERT INTO medicao1 (idzona, idsensor, datahora, leitura, valorvalido) VALUES ('" + idzona + "', '" + idsensor +"', '"+ datahora +"', '"+ leitura + "', '1');";
			System.out.println("---MYSQL---");
			System.out.println(query);
			try {
				connectionSQL.createStatement().executeUpdate(query);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	//}
	
	public static void main(String[] args) {
		Thread threadTest = new MySQL();
		threadTest.run();
		threadTest.start();
	}
}
