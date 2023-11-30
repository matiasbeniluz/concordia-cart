package com.shashi.srv;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
import com.shashi.service.impl.DiscountServiceImpl;
import com.shashi.service.impl.ProductServiceImpl;
import com.shashi.utility.DBUtil;

/**
 * Servlet implementation class UpdateProductSrv
 */
@WebServlet("/UpdateProductSrv")
public class UpdateProductSrv extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public UpdateProductSrv() {
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
			return;

		} else if (userName == null || password == null) {

			response.sendRedirect("login.jsp?message=Session Expired, Login Again!!");
			return;
		}

		// Login success

		String prodId = request.getParameter("pid");
		String prodName = request.getParameter("name");
		String prodType = request.getParameter("type");
		String prodInfo = request.getParameter("info");
		Double prodPrice = Double.parseDouble(request.getParameter("price"));
		Integer prodQuantity = Integer.parseInt(request.getParameter("quantity"));
		Integer usedQuantity = Integer.parseInt(request.getParameter("usedQuantity"));
		String discountId = request.getParameter("discountId");

		ProductBean product = new ProductBean();
		product.setProdId(prodId);
		product.setProdName(prodName);
		product.setProdInfo(prodInfo);
		product.setProdPrice(prodPrice);
		product.setProdQuantity(prodQuantity);
		product.setProdType(prodType);
		product.setDiscountId(discountId.isEmpty() ? null : discountId);
		String usedProdId = getusedProdId(product.getProdId());

		ProductServiceImpl dao = new ProductServiceImpl();

		// update used product info
		if(usedProdId != null && usedQuantity > 0)
		{
			product.setProdQuantity(prodQuantity - usedQuantity);
			product.setUsedProdId(usedProdId);
			
			dao.updateUsedProductWithoutImage(product.getusedProdId(), usedQuantity, product);
		}
		
		String status = dao.updateProductWithoutImage(prodId, product);

		RequestDispatcher rd = request
				.getRequestDispatcher("updateProduct.jsp?prodid=" + prodId + "&message=" + status);
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
