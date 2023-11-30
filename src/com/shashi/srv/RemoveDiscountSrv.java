package com.shashi.srv;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.shashi.service.impl.ProductServiceImpl;
import com.shashi.service.impl.DiscountServiceImpl;

import java.io.IOException;

@WebServlet("/RemoveDiscountSrv")
public class RemoveDiscountSrv extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RemoveDiscountSrv() {
		super();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// login check
		HttpSession session = request.getSession();
		String userType = (String) session.getAttribute("usertype");
		String userName = (String) session.getAttribute("username");
		String password = (String) session.getAttribute("password");

		if (userType == null || !userType.equals("admin")) {

			response.sendRedirect("login.jsp?message=Access Denied, Login As Admin!!");

		}

		else if (userName == null || password == null) {

			response.sendRedirect("login.jsp?message=Session Expired, Login Again!!");
		}

		
		// discount removal
		String discountId = request.getParameter("discountid");

		DiscountServiceImpl discount = new DiscountServiceImpl();

		String status = discount.removeDiscount(discountId);
		
		

		RequestDispatcher rd = request.getRequestDispatcher("removeProduct.jsp?message=" + status);
		rd.forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}
}
