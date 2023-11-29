package com.shashi.srv;

import java.io.IOException;

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

		int salePercentage = Integer.parseInt(request.getParameter("salePercentage"));
		LocalDate saleStartDate = LocalDate.parse(request.getParameter("startDate"));
		LocalDate saleEndDate = LocalDate.parse(request.getParameter("endDate"));

		ProductBean product = new ProductBean();
		product.setProdId(prodId);
		product.setProdName(prodName);
		product.setProdInfo(prodInfo);
		product.setProdPrice(prodPrice);
		product.setProdQuantity(prodQuantity);
		product.setProdType(prodType);

		DiscountServiceImpl dsi = new DiscountServiceImpl();

		// Retrieve the product's previous details to get the discount ID
		ProductServiceImpl dao = new ProductServiceImpl();
		ProductBean prevProduct = dao.getProductDetails(prodId);
		String prevProdDiscountId = prevProduct.getDiscountId();

		// Create a DiscountBean object and update the relevant info
		DiscountBean discount;

		// If the product has no discount id, create one
		// Otherwise, get the previous one and update its components
		if (prevProdDiscountId == null)
			discount = new DiscountBean();
		else
			discount = dsi.getDiscountDetails(prevProdDiscountId);

		String status;

		// If the selected discount percentage is zero, set the product's discount id to null and delete the instance corresponding to this discount id from the discount table
		// Otherwise update all the relevant info of the discount and product
		if (salePercentage == 0) {

			// Set the product discount id to null
			product.setDiscountId(null);

			status = dao.updateProductWithoutImage(prodId, product);

			// Delete the instance associated with the discount id from the discount table
			dsi.deleteDiscountFromDB(discount.getDiscountId());
		}
		else {
			discount.setDiscountPercentage(salePercentage);
			discount.setStartDate(saleStartDate);
			discount.setEndDate(saleEndDate);

			// Set the product discount id
			product.setDiscountId(discount.getDiscountId());

			// Update the discount in the discount table
			dsi.updateDiscountIntoDB(discount);

			// Debug:
			System.out.println("The discount will be active for: " + discount.getRemainingTime());
			System.out.println("The discounted price is: " + discount.discountedPrice(product.getProdPrice()));

			status = dao.updateProductWithoutImage(prodId, product);
		}

		RequestDispatcher rd = request
				.getRequestDispatcher("updateProduct.jsp?prodid=" + prodId + "&message=" + status);
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
