import java.sql.Timestamp;

public class Medicoes {
	double leitura;
	String idsensor;
	Timestamp datahora;
	
	public Medicoes(String idsensor, Timestamp datahora, double leitura) {
		this.leitura = leitura;
		this.idsensor = idsensor;
		this.datahora = datahora;
	}

	public double getLeitura() {
		return this.leitura;
	}
	
	public String getIdSensor() {
		return this.idsensor;
	}
	
	public Timestamp getDataHora() {
		return this.datahora;
	}
}
