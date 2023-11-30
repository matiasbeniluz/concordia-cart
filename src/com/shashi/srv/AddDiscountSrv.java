package com.shashi.srv;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.shashi.service.impl.ProductServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

import com.shashi.service.impl.DiscountServiceImpl;

/**
 * Servlet implementation class AddProductSrv
 */
@WebServlet("/AddDiscountSrv")
public class AddDiscountSrv extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

    	// Session information
		HttpSession session = request.getSession();
		String userType = (String) session.getAttribute("usertype");
		String userName = (String) session.getAttribute("username");
		String password = (String) session.getAttribute("password");

		if (userType == null || !userType.equals("admin")) {

			response.sendRedirect("login.jsp?message=Access Denied!");

		}

		else if (userName == null || password == null) {

			response.sendRedirect("login.jsp?message=Session Expired, Login Again to Continue!");
		}

		
		// Creating new discount
		String status = "Discount Registration Failed!";
		
		System.out.println(request.getParameter("name"));
		
		// discount values
		int disPercentage = Integer.parseInt(request.getParameter("percent"));
		String disName = request.getParameter("name");
		LocalDate startDate = LocalDate.parse(request.getParameter("start"));
		LocalDate endDate = LocalDate.parse(request.getParameter("end"));

		DiscountServiceImpl discount = new DiscountServiceImpl();
		
		// Initial Product
		status = discount.addDiscount(disName, disPercentage, startDate, endDate);
		RequestDispatcher rd = request.getRequestDispatcher("addDiscount.jsp?message=" + status);
		rd.forward(request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
