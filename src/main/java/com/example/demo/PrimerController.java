package com.example.demo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
public class PrimerController 
{
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	
	@GetMapping("/esqueleto")
	public static String paginaEsqueleto() {
		
		
		
		return "esqueleto";
	}
	
	@GetMapping("/home")
	public static String paginaHome() {
		
		
		
		return "home";
	}
	@GetMapping("/contacto")
	public static String paginaContacto() {
		
		
		
		return "contacto";
	}
	
	
	@GetMapping("/quienessomos")
	public static String paginaQuienessomos() {
		
		
		
		return "quienessomos";
	}
	
		
	@GetMapping("/tabla")
	public static String paginaPrincipal(Model template) throws SQLException
	{
		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM producto;");
		
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
										resultado.getString("fecha_de_ingreso"));
			System.out.println();
			listaProducto.add( miSabor );
		}
		
		template.addAttribute("listaProducto", listaProducto);
		System.out.println(listaProducto);
		return "nuevoejemplo";
	}
	
	@GetMapping("/admin/editar")
	public static String paginaAdmin(HttpServletRequest request)
	{
		
		if(controlarMarquita(request)) {
			return "admin";
			
		}
		else {
			return "redirect:/admin/login";
		}
		
		
	
	}
	
	@GetMapping("/admin/login")
	public static String paginaPruebab()
	{
		return "adminlogin";
	}
	@GetMapping("/admin/logout")
	public static String logoutAdmin( HttpServletRequest request)
	{
		
		HttpSession session = request.getSession();
		session.setAttribute("marquita", null);
		
		return "redirect:/";
	}
	
	
	public static boolean controlarMarquita(HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		String marquita = (String) session.getAttribute("marquita");
		
		
		if(marquita != null && marquita.equals("AUTORIZADO")) {
			return true;
		}
		else { 
			return false;
		}
		
	}
	
	
	
	@PostMapping("/admin/procesarLogin")
	public static String procesarLoginAdmi(@RequestParam String usuario , 
										   @RequestParam  String password,
										   HttpServletRequest request)
	{
		if(usuario.equals("admin") && password.equals("comunidad_it")) {
			//aceptado
			
			HttpSession session = request.getSession();
			session.setAttribute("marquita", "AUTORIZADO");
			
			
			
			
			
			return "redirect:/admin/editar";
		} else {
			//rechazado
			return "adminLogin";
		}
		
		
		 // TODO ;poner algo aca
	}

	@GetMapping("/enviar")
	public static String paginaPrincipa(Model template) throws SQLException
	{
		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM producto;");
		
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
										resultado.getString("fecha_de_ingreso"));
			
			listaProducto.add( miSabor );
		}
		
		template.addAttribute("listaSabores", listaProducto);
		return "pruebatabla";
	}

	
	@GetMapping("/layaout")
	public static String paginaLayout()
	{
		
		return "index";
	}
	
	@GetMapping("/sucursales")
	public static String paginaSucursales()
	{
		
		return "sucursales";
	}

/*	@GetMapping("/sabores")
	public static String paginaSabores(Model template) throws SQLException
	{
		/* Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM producto;");
		
		ResultSet resultado = ps.executeQuery();
		
		ArrayList<Producto> listaProducto;
		listaProducto = new ArrayList<Producto>();
		
		while( resultado.next() ) {
			//template.addAttribute("nombreSabor", resultado.getString("nombre"));
			Producto miProducto = new Producto(	resultado.getInt("id"),
					resultado.getString("nombre"), 
					resultado.getString("Proveedor"),
					resultado.getInt("stock"),
					resultado.getInt("precio_costo"),
					resultado.getInt("precio_venta"),
					resultado.getString("fecha_de_ingreso"));
			
			listaProducto.add( miProducto);
		}
		
		template.addAttribute("listaProducto", listaProducto);

		return "tables";
	} */
	
	@GetMapping("/nosotros")
	public static String paginaNosotros(Model template)
	{
		template.addAttribute("claseNosotros", "active");
		return "nosotros";
	}
	
	@GetMapping("/contacto")
	public static String PaginaContacto()
	{
		return "contacto"; // Formulario vacio
	}

	@PostMapping("/recibirContacto")
	public static String procesarInfoContacto(	@RequestParam String nombre, 
												@RequestParam String comentario,
												@RequestParam String email,
												Model template) throws SQLException {
		if (nombre.equals("") || comentario.equals("") || email.equals("")) { // si hubo algun error
			// Cargar formulario de vuelta
			template.addAttribute("mensajeError", "No puede haber campos vacios");
			template.addAttribute("nombreAnterior", nombre);
			template.addAttribute("emailAnterior", email);
			template.addAttribute("comentarioAnterior", comentario);

			return "contacto"; // Formulario vacio (quizas se enoje, pero bue)
		} else {
			/*enviarCorreo(
					"no-responder@pepito.com", 
					"francisco.j.laborda@gmail.com", 
					"Mensaje de contacto de " + nombre, 
					"nombre: " + nombre + "  email: " + email + " comentario: " + comentario);
			enviarCorreo(
					"no-responder@pepito.com",
					email,
					"Gracias por contactarte!", 
					"Recibimos tu consulta, nos vamos a contactar con vos");
			*/
			Connection connection;
			connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
			
			PreparedStatement ps = 
					connection.prepareStatement("INSERT INTO contactos(nombre, email, comentario) VALUES(?,?,?);");
			ps.setString(1, nombre);
			ps.setString(2, email);
			ps.setString(3, comentario);

			ps.executeUpdate();
			
			return "graciasContacto";			
		}	
	}
	
    public static void enviarCorreo(String de, String para, String asunto, String contenido){
        Email from = new Email(de);
        String subject = asunto;
        Email to = new Email(para);
        Content content = new Content("text/plain", contenido);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid("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");
        Request request = new Request();
        try {
          request.method = Method.POST;
          request.endpoint = "mail/send";
          request.body = mail.build();
          Response response = sg.api(request);
          System.out.println(response.statusCode);
          System.out.println(response.body);
          System.out.println(response.headers);
        } catch (IOException ex) {	
          System.out.println(ex.getMessage()); ;
        }
    }
	
	
	
	
	

}
