package moo.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import moo.UserAuth;

@WebServlet(urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String user = request.getParameter("usr");
        String pass = request.getParameter("pwd");
        if (validateUser(user, pass)) {
            HttpSession session = request.getSession();
            session.setAttribute("userAuth", new UserAuth(user));
            request.getRequestDispatcher("/pages/success.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/pages/fail.jsp").forward(request, response);
        }
    }

    private boolean validateUser(String user, String pass) {
        if (user != null) {
            user = user.trim();
        }
        return user.equals("jsp") && pass.equals("1");
    }
}
