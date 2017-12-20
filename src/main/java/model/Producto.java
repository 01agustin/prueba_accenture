package model;

public class Producto {
	int id;
	String nombre;
	String proveedor;
	int stock;
	int precio_costo;
	int precio_venta;
	String fecha_de_ingreso;
	String categoria;


	
	public Producto(int id, String nombre, String t, int d, int s, int a,String r,String m){
		this.id = id;
		this.nombre = nombre;
		this.proveedor = t;
		this.stock = d;
		this.precio_costo = s;
		this.precio_venta = a;
		this.fecha_de_ingreso = r;
		this.categoria = m;
		
	}
	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getProveedor() {
		return proveedor;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	public int getPrecio_costo() {
		return precio_costo;
	}

	public void setPrecio_de_costo(int precio_de_costo) {
		this.precio_costo = precio_de_costo;
	}

	public int getPrecio_venta() {
		return precio_venta;
	}

	public void setPrecio_venta(int precio_de_venta) {
		this.precio_venta = precio_de_venta;
	}

	public String getFecha_de_ingreso() {
		return fecha_de_ingreso;
	}

	public void setFecha_de_ingreso(String fecha_de_ingreso) {
		this.fecha_de_ingreso = fecha_de_ingreso;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public String getNombre() {
		return this.nombre;
	}
	
	public void setNombre(String nuevoNombre){
		this.nombre = nuevoNombre;
	}

	

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}


	
}
