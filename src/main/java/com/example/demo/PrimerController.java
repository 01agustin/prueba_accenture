package com.example.demo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

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
	
	
	
	@GetMapping("/productos")
	public static String paginaProductos(Model template,HttpServletRequest request) throws SQLException {
		
		String usuarioLogueado = usuarioController.controlarMarquita(request);
		if ( usuarioLogueado == null ) {
			return "redirect:/login";
		}
		
		
		HttpSession session = request.getSession();
		String usuario = (String) session.getAttribute("usuario");

		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM producto WHERE usuario=?;");
		ps.setString(1, usuario);
		
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
	
	@GetMapping("/home")
	public static String paginaHome() {
		
		
		
		return "home";
	}
	
	
	
	@GetMapping("/quienessomos")
	public static String paginaQuienessomos() {
		
		
		
		return "quienessomos";
	}
	@GetMapping("/contacto")
	public static String paginaContacto() {
		
		
		
		return "contacto";
	}
	@GetMapping("/registrar")
	public static String paginaRegistro() {
		
		
		
		return "registro";
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
	
	
	
	
		
		
		 // TODO ;poner algo aca
	

	
	

	
	
	@GetMapping("/nosotros")
	public static String paginaNosotros(Model template)
	{
		template.addAttribute("claseNosotros", "active");
		return "nosotros";
	}
	
	

	@PostMapping("/recibirContacto")
	public static String procesarInfoContacto(	@RequestParam String nombre,
												@RequestParam String apellido,
												@RequestParam String comentario,
												@RequestParam String email,
												@RequestParam String telefono,
												Model template) throws SQLException {
		if (nombre.equals("") || comentario.equals("") || email.equals("")|| telefono.equals("")|| apellido.equals("")) { // si hubo algun error
			// Cargar formulario de vuelta
			template.addAttribute("mensajeError", "No puede haber campos vacios");
			template.addAttribute("nombreAnterior", nombre);
			template.addAttribute("emailAnterior", email);
			template.addAttribute("comentarioAnterior", comentario);
			template.addAttribute("telefonoAnterior", telefono);
			template.addAttribute("apellidooAnterior", apellido);

			return "contacto"; // Formulario vacio (quizas se enoje, pero bue)
		} else {
			enviarCorreo(
					"no-responder@pepito.com", 
					"agustin_9_d@hotmail.com", 
					"Mensaje de contacto de " + nombre+ " "+apellido, 
					"nombre: " + nombre +" "+apellido+ "  email: " + email + " comentario: " + comentario);
			enviarCorreo(
					"no-responder@pepito.com",
					email,
					"Gracias por contactarte!", 
					"Recibimos tu consulta, nos vamos a contactar con vos");
			
			Connection connection;
			connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
			
			PreparedStatement ps = 
					connection.prepareStatement("INSERT INTO contactos(nombre, apellido, telefono, email, comentario) VALUES(?,?,?,?,?);");
			ps.setString(1, nombre);
			ps.setString(2, apellido);
			ps.setString(3, email);
			ps.setString(4, telefono);
			ps.setString(5, comentario);

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
    @PostMapping("/recibirusuario")
	public static String procesarInfoUsuario(@RequestParam String usuario, @RequestParam String password,
			@RequestParam String email, Model template) throws SQLException {
		if (usuario.equals("") || password.equals("") || email.equals("")) {// si hubo algun error
			// cargar formulario de vuelta
			template.addAttribute("mensajeError", "No puede haber campos vacios");
			template.addAttribute("usuarioAnterior", usuario);
			template.addAttribute("passAnterior", password);
			template.addAttribute("emailAnterior", email);

			return "home";
		} else {

			enviarCorreo(email, "agustin_9_d@hotmail.com", "Mensaje de contacto de:" + usuario,
					"usuario:" + usuario + "  email:" + email);
			enviarCorreo("agustin_9_d@hotmail.com", email, " Gracias por contactarte!", " GRACIAS POR REGISTRARTE");

			Connection connection;
			connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
			PreparedStatement qs = connection
					.prepareStatement("INSERT INTO usuarios(email,usuario,password)  VALUES(?,?,?);");
			qs.setString(1, email);
			qs.setString(2, usuario);
			qs.setString(3, password);

			qs.executeUpdate();

			return "redirect:/login";
		}
	}
   
	
	
	
	
	

}
