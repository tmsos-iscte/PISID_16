import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class teste {
	
	static final String db = "sid";
	static final String DBuser = "root";
	static final String DBpass = "root";
	static Connection connectionSQL;
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {

		 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	        Date date = new Date();
	        System.out.println(dateFormat.format(date));
	        Timestamp timestamp = new Timestamp(date.getTime());
	        System.out.println(timestamp);
	    }
}
