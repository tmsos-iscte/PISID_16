import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class teste1 {
	public static void main(String[] args) throws ParseException {
		String d1= "2022-05-12T00:03:30Z";
			String T= d1.replace('T', ' ');
			String Z= T.substring(0, T.length()-1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dateRecebida = sdf.parse(Z);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			Date dateAtual = new Date();  
			System.out.println("SHIKABALA"+dateAtual);

			if(dateAtual.after(dateRecebida)){//||(dateAtual2.after(dateRecebida2))){
				System.out.println("JEFFREN"+Z);
				System.out.println("VAI TOMA");
			}
			System.out.println("NAO VAI");
	}
}
