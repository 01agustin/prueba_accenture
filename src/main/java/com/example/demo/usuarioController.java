package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

@Controller
public class usuarioController {
	@Autowired
	JdbcTemplate jdbcTemplate;

	@GetMapping("/login")
	public static String paginaLogin() {
		
		
		
		return "login";
	}
	@GetMapping("/verificarLogin")
	public static String procesarlogin(@RequestParam String Usuario, @RequestParam String Password,
			HttpServletRequest request) throws SQLException {

		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		PreparedStatement as = connection.prepareStatement("SELECT * FROM usuarios WHERE usuario =? AND password=?;");

		as.setString(1, Usuario);
		as.setString(2, Password);

		ResultSet resultado = as.executeQuery();

		if (resultado.next()) {
			// ACEPTADO
			String marquitaNueva = UUID.randomUUID().toString();

			HttpSession session = request.getSession();

			session.setAttribute("marquita", marquitaNueva);
			session.setAttribute("usuario", Usuario);

			as = connection.prepareStatement("UPDATE usuarios SET marquita=? WHERE usuario=?");
			as.setString(1, marquitaNueva);
			as.setString(2, Usuario);
			as.executeUpdate();

			return "redirect:/productos";

		} else {

			return "mensajeError";

		}

	}

	@GetMapping("/logout")
	public static String logoutAdmin(HttpServletRequest request) throws SQLException {
		HttpSession session = request.getSession();
		String marca = (String) session.getAttribute("marquita");

		session.setAttribute("marquita", null);
		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);

		PreparedStatement ps = connection.prepareStatement("UPDATE usuarios SET marquita=null WHERE marquita=?");
		ps.setString(1, marca);

		ps.executeUpdate();

		return "redirect:/login";
	}

	public static String controlarMarquita(HttpServletRequest request) throws SQLException {
		HttpSession session = request.getSession();
		String marquita = (String) session.getAttribute("marquita");

		if (marquita != null) {
			Connection connection;
			connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);

			PreparedStatement ps = connection.prepareStatement("SELECT * FROM usuarios WHERE marquita = ?");
			ps.setString(1, marquita);

			ResultSet resultado = ps.executeQuery();
			if (resultado.next()) {
				return resultado.getString("usuario");
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@GetMapping("/cambiarMail")
	public static String cambiarMail(HttpServletRequest request, Model template) throws SQLException {

		HttpSession session = request.getSession();

		String usuario = (String) session.getAttribute("usuario");

		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		PreparedStatement as = connection.prepareStatement("SELECT * FROM usuarios WHERE usuario =? ;");

		as.setString(1, usuario);

		ResultSet resultado = as.executeQuery();
		resultado.next();
		template.addAttribute("usuario", usuario);
		template.addAttribute("mail", resultado.getString("email"));

		return "cambiarMail";
	}

	@PostMapping("/cambiarMail")
	public static String cambiarMail(@RequestParam String mailNuevo, @RequestParam String pass, Model template,
			HttpServletRequest request) throws SQLException {

		String usuarioLogueado = usuarioController.controlarMarquita(request);
		if (usuarioLogueado == null) {
			return "redirect:/login";
		}
		HttpSession session = request.getSession();

		String usuario = (String) session.getAttribute("usuario");

		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM usuarios WHERE usuario =? ;");

		ps.setString(1, usuario);
		ResultSet resultado = ps.executeQuery();
		resultado.next();
		if (pass != resultado.getString("password") && mailNuevo.equals(null)) { // si hubo algun error
			// Cargar formulario de vuelta
			template.addAttribute("usuario", usuario);
			template.addAttribute("mail", resultado.getString("email"));

			return "cambiarMail";
		} else {

			connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);

			PreparedStatement as = connection.prepareStatement("UPDATE usuarios SET email=? WHERE usuario=? ");

			as.setString(1, mailNuevo);
			as.setString(2, usuario);

			as.executeUpdate();

			return "redirect:/productos";
		}
	}

	@GetMapping("/cambiarContraseña")
	public static String cambiarContraseña(HttpServletRequest request, Model template) throws SQLException {

		HttpSession session = request.getSession();

		String usuario = (String) session.getAttribute("usuario");

		template.addAttribute("usuario", usuario);

		return "cambiarContraseña";
	}

	@PostMapping("/cambiarpass")
	public static String cambiarContrseña(@RequestParam String passNuevo, @RequestParam String pass, Model template,
			HttpServletRequest request) throws SQLException {

		String usuarioLogueado = usuarioController.controlarMarquita(request);
		if (usuarioLogueado == null) {
			return "redirect:/login";
		}
		HttpSession session = request.getSession();

		String usuario = (String) session.getAttribute("usuario");

		Connection connection;
		connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM usuarios WHERE usuario =? ;");

		ps.setString(1, usuario);
		ResultSet resultado = ps.executeQuery();
		resultado.next();
		System.out.println(resultado.getString("password"));
		System.out.println(pass);
		String a = resultado.getString("password");
		if (!(pass.equals(a))) { // si hubo algun error
			// Cargar formulario de vuelta
			template.addAttribute("usuario", usuario);

			return "cambiarContraseña";
		} else {

			connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);

			PreparedStatement as = connection.prepareStatement("UPDATE usuarios SET password=? WHERE usuario=? ");

			as.setString(1, passNuevo);
			as.setString(2, usuario);

			as.executeUpdate();

			return "redirect:/productos";
		}
	}

	@GetMapping("/recibirAyuda")
	public static String ayuda(	@RequestParam String nombre,
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

return "ayuda"; // Formulario vacio (quizas se enoje, pero bue)
} else {
PrimerController.enviarCorreo(
"no-responder@pepito.com", 
"agustin_9_d@hotmail.com", 
"Mensaje de contacto de " + nombre+ " "+apellido, 
"nombre: " + nombre +" "+apellido+ "  email: " + email + " comentario: " + comentario);
PrimerController.enviarCorreo(
"no-responder@pepito.com",
email,
"Gracias por contactarte!", 
"Recibimos tu consulta, nos vamos a contactar con vos");

Connection connection;
connection = DriverManager.getConnection(Settings.db_url, Settings.db_user, Settings.db_password);

PreparedStatement ps = 
connection.prepareStatement("INSERT INTO ayuda(nombre, apellido, telefono, email, comentario) VALUES(?,?,?,?,?);");
ps.setString(1, nombre);
ps.setString(2, apellido);
ps.setString(3, email);
ps.setString(4, telefono);
ps.setString(5, comentario);

ps.executeUpdate();

return "graciasContacto2";			
	}
	
}
	@GetMapping("/ayuda")
	public static String paginaContacto() {
		
		
		
		return "ayuda";
	}
	}
