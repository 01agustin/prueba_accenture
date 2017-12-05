package model;

public class TestSabor {

	public static void main(String[] args) {
		
		Producto unSabor;
		unSabor = new Producto(1, "producto1", "proveedor1", 25, 38, 36,"25/12/17");
		Producto t;
		t = new  Producto(2, "producto2", "proveedor2", 10, 100, 20,"25/12/17");
	
		System.out.println( unSabor.getNombre() );
		System.out.println( t.getNombre() );
		

	}

}
