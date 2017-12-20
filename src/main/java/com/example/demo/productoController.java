package com.example.demo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

import model.Producto;

@Controller
public class productoController {
	@Autowired
	JdbcTemplate jdbcTemplate;

	@GetMapping("/cargaPro")
	public static String paginaCarga(HttpServletRequest request) throws SQLException {
		String usuarioLogueado = usuarioController.controlarMarquita(request);
		if ( usuarioLogueado == null ) {
			return "redirect:/login";
		}
		return "cargaproducto";
	}

	@PostMapping("/cargarProducto")
	public static String procesarInfoContacto(@RequestParam String nombreProducto, @RequestParam String proveedor,
			@RequestParam int stock, @RequestParam int preciocost, @RequestParam int precioventa,
			@RequestParam String categoria, @RequestParam String fecha_de_ingreso, Model template ,HttpServletRequest request) throws SQLException {
		
		String usuarioLogueado = usuarioController.controlarMarquita(request);
		if ( usuarioLogueado == null ) {
			return "redirect:/login";
		}
		
		
		if (nombreProducto.equals("") || proveedor.equals("") || stock == 0 || precioventa == 0 || preciocost == 0
				|| categoria.equals("") || fecha_de_ingreso.equals("")) { // si hubo algun error
			// Cargar formulario de vuelta
			template.addAttribute("mensajeError", "No puede haber campos vacios");
			template.addAttribute("nompro", nombreProducto);
			template.addAttribute("proveedorAnterior", proveedor);
			template.addAttribute("stockAnterior", stock);
			template.addAttribute("preciocostAnterior", preciocost);
			template.addAttribute("precioventaAnterior", precioventa);
			template.addAttribute("categoriaAnterior", categoria);
			template.addAttribute("fecha_de_ingresoAnterior", fecha_de_ingreso);

			return "cargaproducto";
		} else {
			HttpSession session = request.getSession();
			String usuario = (String) session.getAttribute("usuario");

			Connection connection;
			connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);

			PreparedStatement ps = connection.prepareStatement(
					"INSERT INTO producto(nombre, proveedor, stock, precio_costo, precio_venta,fecha_de_ingreso,categoria,usuario) VALUES(?,?,?,?,?,?,?,?);");
			ps.setString(1, nombreProducto);
			ps.setString(2, proveedor);
			ps.setInt(3, stock);
			ps.setInt(4, preciocost);
			ps.setInt(5, precioventa);
			ps.setString(6, fecha_de_ingreso);
			ps.setString(7, categoria);
			ps.setString(8, usuario);

			ps.executeUpdate();

			return "redirect:/productos";
		}
			}
	@GetMapping("/paseeliminar")
	public static String paginapaseeliminar(@RequestParam int d , Model template,HttpServletRequest request ) throws SQLException {
		String usuarioLogueado = usuarioController.controlarMarquita(request);
		if ( usuarioLogueado == null ) {
			return "redirect:/login";
		}
		
		HttpSession session = request.getSession();
		String usuario = (String) session.getAttribute("usuario");
		
		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM producto where id = ? AND usuario=?;");
		ps.setInt(1, d);
		ps.setString(2, usuario);
		ResultSet resultado = ps.executeQuery();
		
		
		if(resultado.next()) {
			template.addAttribute("id",d);
		template.addAttribute("nombre",resultado.getString("nombre"));
		template.addAttribute("proveedor", resultado.getString("proveedor"));
		template.addAttribute("stock", resultado.getString("stock"));
		template.addAttribute("precio_costo", resultado.getString("precio_costo"));
		template.addAttribute("precio_venta", resultado.getString("precio_venta"));
		template.addAttribute("fecha_de_ingreso", resultado.getString("fecha_de_ingreso"));
		template.addAttribute("categoria", resultado.getString("categoria"));
		}

		return "elimnarProductoPredefinido";
	}
	@PostMapping("/paseeliminar")
	public static String paginaPaseeliminar(@RequestParam int d , Model template,HttpServletRequest request ) throws SQLException {
		
		HttpSession session = request.getSession();
		String usuario = (String) session.getAttribute("usuario");
		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		
		
		
		PreparedStatement ps = connection.prepareStatement("DELETE  FROM producto where id = ? AND usuario=?;");
		ps.setInt(1, d);
		ps.setString(2, usuario);
		ps.executeUpdate();

		return "redirect:/productos";
	}
	@GetMapping("/editarPro")
	public static String paginaEditar(@RequestParam int d, Model template,HttpServletRequest request) throws SQLException {
		String usuarioLogueado = usuarioController.controlarMarquita(request);
		if ( usuarioLogueado == null ) {
			return "redirect:/login";
		}
		HttpSession session = request.getSession();
		String usuario = (String) session.getAttribute("usuario");
		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM producto where id = ? AND usuario =?;");
		
		ps.setInt(1, d);
		ps.setString(2, usuario);
		ResultSet resultado = ps.executeQuery();
		
		
		if(resultado.next()) {
			template.addAttribute("d",d);
		template.addAttribute("nombre",resultado.getString("nombre"));
		template.addAttribute("proveedor", resultado.getString("proveedor"));
		template.addAttribute("stock", resultado.getString("stock"));
		template.addAttribute("precio_costo", resultado.getString("precio_costo"));
		template.addAttribute("precio_venta", resultado.getString("precio_venta"));
		template.addAttribute("fecha_de_ingreso", resultado.getString("fecha_de_ingreso"));
		template.addAttribute("categoria", resultado.getString("categoria"));
		}
		return "editarproducto";
	}
	@PostMapping("/editarProducto")
	public static String editarProducto(
			@RequestParam int stock, @RequestParam int precio_costo,@RequestParam int d, @RequestParam int precio_venta,
			@RequestParam String categoria, Model template ,HttpServletRequest request) throws SQLException {
		if ( stock == 0 || precio_venta == 0 || precio_costo == 0
				|| categoria.equals("") ) { // si hubo algun error
			// Cargar formulario de vuelta
			template.addAttribute("mensajeError", "No puede haber campos vacios");
			
			template.addAttribute("stockAnterior", stock);
			template.addAttribute("preciocostAnterior", precio_costo);
			template.addAttribute("precioventaAnterior", precio_venta);
			template.addAttribute("categoriaAnterior", categoria);
			

			return "editarproducto";
		} else {

			HttpSession session = request.getSession();
			String usuario = (String) session.getAttribute("usuario");
			Connection connection;
			connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);

			PreparedStatement ps = connection.prepareStatement(
					"UPDATE producto SET stock=?, precio_costo=?, precio_venta=?,categoria=? WHERE id =? AND usuario=? ;");
			
			ps.setInt(1, stock);
			ps.setInt(2, precio_costo);
			ps.setInt(3, precio_venta);
			ps.setString(4, categoria);
			ps.setInt(5, d);
			ps.setString(6, usuario);


			ps.executeUpdate();

			return "redirect:/productos";
		}

	}
	@GetMapping("/catalogo")
	public static String paginaProductosPorCategoria(Model template,HttpServletRequest request) throws SQLException {
		String usuarioLogueado = usuarioController.controlarMarquita(request);
		if ( usuarioLogueado == null ) {
			return "redirect:/login";
		}
		HttpSession session = request.getSession();
		String usuario = (String) session.getAttribute("usuario");
		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		
		PreparedStatement ps = connection.prepareStatement("SELECT categoria FROM producto WHERE usuario=?;");
		ps.setString(1, usuario);
		ResultSet resultado = ps.executeQuery();
		
		ArrayList<String> listaCategorias;	
		listaCategorias= new ArrayList<String>();
		String a;
		resultado.next();

		String categoria = resultado.getString("categoria");
		a = resultado.getString("categoria");
		listaCategorias.add( categoria );
		do {
			
			categoria = resultado.getString("categoria");
			
			if(a.equals(categoria)) {
				
			}
			else { listaCategorias.add( categoria );
			
				a=resultado.getString("categoria");
			}
			
			
		}
		while(resultado.next());
		System.out.println(listaCategorias);
		
		/*ListIterator<String> it = listaCategorias.listIterator();

		
		while(it.hasNext()) {
			
			PreparedStatement ps2 = connection.prepareStatement("SELECT nombre FROM producto WHERE categoria="+it.next()+";");
			
			ResultSet resultado2 = ps2.executeQuery();
			
			
			
			int r=0;
			while(resultado2.next()) {r=r+1;}
			
			
			  System.out.println(it.next());
		System.out.println(r);}*/
		
		
		
		
		
		
		template.addAttribute("listaCategorias", listaCategorias);

		
		
		
		

		return "ProductosPorCategorias";
	}
	
	
	@PostMapping("/precesarsolicituddecatalogo")
	public static String precesarCatalogo(@RequestParam String categoria) {
		
		String c = categoria;
		
		
		return "redirect:/catalogos/"+c;
	}
	
	@GetMapping("/catalogos/{categoria}" )
	public static String ayuda( @PathVariable String categoria,Model template,HttpServletRequest request ) throws SQLException{
		String usuarioLogueado = usuarioController.controlarMarquita(request);
		if ( usuarioLogueado == null ) {
			return "redirect:/login";
		}
		HttpSession session = request.getSession();
		String usuario = (String) session.getAttribute("usuario");
	
		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM producto WHERE categoria=? AND usuario=?;");
		ps.setString(1, categoria);
		ps.setString(2, usuario);
		ResultSet resultado = ps.executeQuery();
		
		ArrayList<Producto> listaProducto;	
		listaProducto= new ArrayList<Producto>();
		
		while( resultado.next() ) {
			//template.addAttribute("nombreSabor", resultado.getString("nombre"));
			Producto miSabor = new Producto(	resultado.getInt("id"),
										resultado.getString("nombre"), 
									resultado.getString("Proveedor"),	
										resultado.getInt("stock"),
										resultado.getInt("precio_costo"),
										resultado.getInt("precio_venta"),
										resultado.getString("fecha_de_ingreso"),		
										resultado.getString("categoria"));
			System.out.println(resultado.getString("id"));
			listaProducto.add( miSabor );
		}
		
		template.addAttribute("listaProducto", listaProducto);
		System.out.println(listaProducto);
		
		
		
		return "productos";
		
	}	
	
	/*@GetMapping( "/catalogo/categoria" )
	public static String ayuda( @RequestParam String categoria,Model template ) throws SQLException{

	
		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM producto WHERE categoria=?;");
		ps.setString(1, categoria);
		ResultSet resultado = ps.executeQuery();
		
		ArrayList<Producto> listaProducto;	
		listaProducto= new ArrayList<Producto>();
		
		while( resultado.next() ) {
			//template.addAttribute("nombreSabor", resultado.getString("nombre"));
			Producto miSabor = new Producto(	resultado.getInt("id"),
										resultado.getString("nombre"), 
										resultado.getString("Proveedor"),	
										resultado.getInt("stock"),
										resultado.getInt("precio_costo"),
										resultado.getInt("precio_venta"),
										resultado.getString("fecha_de_ingreso"),
										resultado.getString("categoria"));
			System.out.println(resultado.getString("id"));
			listaProducto.add( miSabor );
		}
		
		template.addAttribute("listaProducto", listaProducto);
		System.out.println(listaProducto);
		
		
		
		return "productos";
		
	}	
*/
	
	@PostMapping("/eliminarCategoria")
	public static String eliminarCategoria(@RequestParam String categoria , Model template,HttpServletRequest request ) throws SQLException {
		HttpSession session = request.getSession();
		String usuario = (String) session.getAttribute("usuario");
		
		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		
		
		
		PreparedStatement ps = connection.prepareStatement("DELETE  FROM producto where categoria =? AND usuario=?;");
		ps.setString(1, categoria);
		ps.setString(2, usuario);
		ps.executeUpdate();

		return "redirect:/productos";
	}
	
	@GetMapping("/proveedores")
	public static String paginaProductosPorProvedores(Model template,HttpServletRequest request) throws SQLException {
		String usuarioLogueado = usuarioController.controlarMarquita(request);
		if ( usuarioLogueado == null ) {
			return "redirect:/login";
		}
		HttpSession session = request.getSession();
		String usuario = (String) session.getAttribute("usuario");
		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		
		PreparedStatement ps = connection.prepareStatement("SELECT proveedor FROM producto WHERE usuario=?;");
		ps.setString(1, usuario);
		ResultSet resultado = ps.executeQuery();
		
		ArrayList<String> listaCategorias;	
		listaCategorias= new ArrayList<String>();
		String a;
		resultado.next();

		String categoria = resultado.getString("proveedor");
		a = resultado.getString("proveedor");
		listaCategorias.add( categoria );
		do {
			
			categoria = resultado.getString("proveedor");
			
			if(a.equals(categoria)) {
				
			}
			else { listaCategorias.add( categoria );
			
				a=resultado.getString("proveedor");
			}
			
			
		}
		while(resultado.next());
		System.out.println(listaCategorias);
		
		/*ListIterator<String> it = listaCategorias.listIterator();

		
		while(it.hasNext()) {
			
			PreparedStatement ps2 = connection.prepareStatement("SELECT nombre FROM producto WHERE categoria="+it.next()+";");
			
			ResultSet resultado2 = ps2.executeQuery();
			
			
			
			int r=0;
			while(resultado2.next()) {r=r+1;}
			
			
			  System.out.println(it.next());
		System.out.println(r);}*/
		
		
		
		
		
		
		template.addAttribute("listaCategorias", listaCategorias);

		

		return "ProductosPorProveedores";
	}

	@PostMapping("/eliminarProveedor")
	public static String eliminarProveedor(@RequestParam String categoria , Model template ,HttpServletRequest request ) throws SQLException {
		
		HttpSession session = request.getSession();
		String usuario = (String) session.getAttribute("usuario");
		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		
		
		
		PreparedStatement ps = connection.prepareStatement("DELETE  FROM producto where proveedor = ? AND usuario = ?;");
		ps.setString(1, categoria);
		ps.setString(2, usuario);
		ps.executeUpdate();

		return "redirect:/productos";
	}
	@PostMapping("/precesarsolicituddeProveedor")
	public static String precesarProveedor(@RequestParam String categoria) {
		
		String c = categoria;
		
		
		return "redirect:/proveedor/"+c;
	}
	
	@GetMapping("/proveedor/{proveedor}" )
	public static String ayudap( @PathVariable String proveedor,Model template,HttpServletRequest request ) throws SQLException{
		String usuarioLogueado = usuarioController.controlarMarquita(request);
		if ( usuarioLogueado == null ) {
			return "redirect:/login";
		}
		HttpSession session = request.getSession();
		String usuario = (String) session.getAttribute("usuario");
		
		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM producto WHERE proveedor=? AND usuario=?;");
		ps.setString(1, proveedor);
		ps.setString(2, usuario);
		ResultSet resultado = ps.executeQuery();
		
		ArrayList<Producto> listaProducto;	
		listaProducto= new ArrayList<Producto>();
		
		while( resultado.next() ) {
			//template.addAttribute("nombreSabor", resultado.getString("nombre"));
			Producto miSabor = new Producto(	resultado.getInt("id"),
										resultado.getString("nombre"), 
										resultado.getString("Proveedor"),	
										resultado.getInt("stock"),
										resultado.getInt("precio_costo"),
										resultado.getInt("precio_venta"),
										resultado.getString("fecha_de_ingreso"),		
										resultado.getString("categoria"));
			System.out.println(resultado.getString("id"));
			listaProducto.add( miSabor );
		}
		
		template.addAttribute("listaProducto", listaProducto);
		System.out.println(listaProducto);
		
		
		
		return "productos";
		
	}	
}
