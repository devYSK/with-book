package controller;

import java.util.Collection;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpSession;
import model.User;

public class ListUserController extends AbstractController {

	@Override
	public void doGet(HttpRequest request, HttpResponse response) {
		if (!isLogined(request.getSession())) {
			response.sendRedirect("/user/login.html");
			return;
		}

		Collection<User> users = DataBase.findAll();
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		for (User user : users) {
			sb.append("<tr>");
			sb.append("<td>").append(user.getUserId()).append("</td>");
			sb.append("<td>").append(user.getName()).append("</td>");
			sb.append("<td>").append(user.getEmail()).append("</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		response.forwardBody(sb.toString());
	}

	private static boolean isLogined(HttpSession session) {
		Object user = session.getAttribute("user");
		if (user == null) {
			return false;
		}
		return true;
	}
}
