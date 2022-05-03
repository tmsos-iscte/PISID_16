

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import org.bson.Document;
import java.util.regex.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;



public class MongoDBMysqlConnection {
static String db = "sid";
		static String DBuser = "root";
		static String DBpass = "root";
		
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
		
	public static void main(String[] args) throws SQLException, ClassNotFoundException, InterruptedException {
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connectionSQL = DriverManager.getConnection("jdbc:mysql://localhost/" + db + "?useTimezone=true&serverTimezone=UTC", DBuser, DBpass);
		
		
		String urlLocal = "mongodb://localhost:27017";
		String database = "sid2022";
		String collectiont1 = "medicoes";
		
		MongoClient localMongoClient = new MongoClient(new MongoClientURI(urlLocal));
	    MongoDatabase localMongoDatabase = localMongoClient.getDatabase(database);
	    MongoCollection<Document> localMongoCollection = localMongoDatabase.getCollection(collectiont1);
	    MongoCursor<Document> cursor = localMongoCollection.find().iterator();

	    
	    
	    try {
			//ENQUANTO PODER CORRER, ELE CORRE
		    while (cursor.hasNext()) {
		    	Document document = new Document();

		    	String dados = cursor.next().toString(); 
		    	
		    	String[] parts = dados.split(",");
		    	String[] zona = parts[1].split("=");
		    	String[] sensor = parts[2].split("=");
		    	String[] data = parts[3].split("=");
		    	String[] medicao = parts[4].split("=");
		    	
		    	System.out.println(zona[0]);
		    	String[] z = zona[1].split("Z"); //Vai devolver ou 1, ou 2
		    	int idzona= Integer.parseInt(z[1]);
		    	
		        String[] l= medicao[1].split("}}}}", 2);
		        String auxleitura= l[0];
		        double leitura= Double.parseDouble(auxleitura);
		    	
		    	String idsensor= sensor[1];
		    	
		    	String auxdatahora= data[1];
		    	Timestamp datahora= datahoraTimestamp(auxdatahora);
		    	
		    	String query = "INSERT INTO medicao (idzona, idsensor, datahora, leitura, valorvalido) VALUES ('" + idzona + "', '" + idsensor +"', '"+ datahora +"', '"+ leitura + "', '1');";
				System.out.println("---MYSQL---");
				System.out.println(query);
				try {
					connectionSQL.createStatement().executeUpdate(query);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("slimani");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	
		    }
		} finally {
		    cursor.close();
		}
	    
		}
	}

