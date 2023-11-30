package com.shashi.srv;

import java.time.LocalDate;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.shashi.beans.ProductBean;
import com.shashi.beans.DiscountBean;

import com.shashi.service.impl.ProductServiceImpl;
import com.shashi.service.impl.DiscountServiceImpl;


import java.io.IOException;

/**
 * Servlet implementation class UpdateProductSrv
 */
@WebServlet("/UpdateDiscountSrv")
public class UpdateDiscountSrv extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public UpdateDiscountSrv() {
		super();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
		// Login session
		HttpSession session = request.getSession();
		String userType = (String) session.getAttribute("usertype");
		String userName = (String) session.getAttribute("username");
		String password = (String) session.getAttribute("password");

		if (userType == null || !userType.equals("admin")) {

			response.sendRedirect("login.jsp?message=Access Denied, Login As Admin!!");
			return;

		} else if (userName == null || password == null) {

			response.sendRedirect("login.jsp?message=Session Expired, Login Again!!");
			return;
		}

		// Update Discount

		String did = request.getParameter("did");
		int disPercentage = Integer.parseInt(request.getParameter("percent"));
		String disName = request.getParameter("name");
		LocalDate startDate = LocalDate.parse(request.getParameter("startDate"));
		LocalDate endDate = LocalDate.parse(request.getParameter("endDate"));

		DiscountBean discount = new DiscountBean();
		discount.setDiscountId(did);
		discount.setDiscountPercentage(disPercentage);
		discount.setDiscountName(disName);
		discount.setStartDate(startDate);
		discount.setEndDate(endDate);
		
		// Call update 
		DiscountServiceImpl dao = new DiscountServiceImpl();
		String status = dao.updateDiscountIntoDB(did, discount);
		

		RequestDispatcher rd = request
				.getRequestDispatcher("updateDiscount.jsp?discountid=" + did + "&message=" + status);
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}
}