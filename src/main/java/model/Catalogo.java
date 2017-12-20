package model;

public class Catalogo {
	String nombreCatalogo;
	int numeroDePro;
	
	
	public Catalogo(String a , int b) {
		this.nombreCatalogo=a;
		this.numeroDePro=b;
	}


	public String getNombreCatalogo() {
		return nombreCatalogo;
	}


	public void setNombreCatalogo(String nombreCatalogo) {
		this.nombreCatalogo = nombreCatalogo;
	}


	public int getNumeroDePro() {
		return numeroDePro;
	}


	public void setNumeroDePro(int numeroDePro) {
		this.numeroDePro = numeroDePro;
	}
	

	
	
}
