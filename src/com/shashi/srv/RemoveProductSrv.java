package com.shashi.srv;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.shashi.service.impl.ProductServiceImpl;
import com.shashi.utility.DBUtil;

@WebServlet("/RemoveProductSrv")
public class RemoveProductSrv extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RemoveProductSrv() {
		super();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

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

		// login checked

		String prodId = request.getParameter("prodid");

		ProductServiceImpl product = new ProductServiceImpl();
		
		// remove corresponding used product first since it relies on original product
		product.removeProduct(getusedProdId(prodId));

		String status = product.removeProduct(prodId);
		
		

		RequestDispatcher rd = request.getRequestDispatcher("removeProduct.jsp?message=" + status);

		rd.forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}
	
	private String getusedProdId(String prodId)
	{
			String usedprodId = null;

			Connection con = DBUtil.provideConnection();

			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				ps = con.prepareStatement("select pusedproductid from product where pid=?");

				ps.setString(1, prodId);
				rs = ps.executeQuery();

				if (rs.next()) {
					usedprodId = rs.getString("pusedproductid");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			DBUtil.closeConnection(con);
			DBUtil.closeConnection(ps);

		return usedprodId;
	}

}
