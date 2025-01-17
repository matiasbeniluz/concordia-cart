package com.shashi.service.impl;

import com.shashi.beans.DemandBean;
import com.shashi.beans.ProductBean;
import com.shashi.service.ProductService;
import com.shashi.utility.DBUtil;
import com.shashi.utility.IDUtil;
import com.shashi.utility.MailMessage;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class ProductServiceImpl implements ProductService {

	@Override
	public String addProduct(String prodName, String prodType, String prodInfo, double prodPrice, int prodQuantity,
							 InputStream prodImage, String discountId) {
		String status = null;
		
		String prodId = IDUtil.generateId();
		String usedProdId = prodId + "U";
		
		// Add original product
		ProductBean product = new ProductBean(prodId, prodName, prodType, prodInfo, prodPrice, prodQuantity, prodImage, false, usedProdId, discountId);

		status = addProduct(product);
		
		// Add used version of product
		ProductBean usedProduct = new ProductBean(usedProdId, prodName, prodType, prodInfo, prodPrice, prodQuantity, prodImage, true, null, null);
		status = addProduct(usedProduct);
			
		return status;
	}

	@Override
	public String addProduct(ProductBean product) {
		String status = "Product Registration Failed!";

		if (product.getProdId() == null)
			product.setProdId(IDUtil.generateId());

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("insert into product values(?,?,?,?,?,?,?,?,?,?);");
			ps.setString(1, product.getProdId());
			ps.setString(2, product.getProdName());
			ps.setString(3, product.getProdType());
			ps.setString(4, product.getProdInfo());
			ps.setDouble(5, product.getProdPrice());
			ps.setInt(6, product.getProdQuantity());
			ps.setBlob(7, product.getProdImage());
			ps.setBoolean(8, product.getIsUsed());
			ps.setString(9, product.getusedProdId());
			ps.setString(10, product.getDiscountId());

			int k = ps.executeUpdate();

			if(!product.getIsUsed())
			{
				if (k > 0) 
				{
					status = "Product Added Successfully with Product Id: " + product.getProdId();

				} 
				else 
				{
					status = "Product Updation Failed!";
				}	
			}

		} catch (SQLException e) {
			status = "Error: " + e.getMessage();
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return status;
	}

	@Override
	public String removeProduct(String prodId) {
		String status = "Product Removal Failed!";

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		PreparedStatement ps2 = null;

		try {
			ps = con.prepareStatement("delete from product where pid=?");
			ps.setString(1, prodId);

			int k = ps.executeUpdate();

			if (k > 0) {
				status = "Product Removed Successfully!";

				ps2 = con.prepareStatement("delete from usercart where prodid=?");

				ps2.setString(1, prodId);

				ps2.executeUpdate();

			}

		} catch (SQLException e) {
			status = "Error: " + e.getMessage();
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(ps2);
		
		return status;
	}

	@Override
	public String updateProduct(ProductBean prevProduct, ProductBean updatedProduct) {
		String status = "Product Updation Failed!";

		if (!prevProduct.getProdId().equals(updatedProduct.getProdId())) {

			status = "Both Products are Different, Updation Failed!";

			return status;
		}

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement(
					"update product set pname=?,ptype=?,pinfo=?,pprice=?,pquantity=?,image=? where pid=?");

			ps.setString(1, updatedProduct.getProdName());
			ps.setString(2, updatedProduct.getProdType());
			ps.setString(3, updatedProduct.getProdInfo());
			ps.setDouble(4, updatedProduct.getProdPrice());
			ps.setInt(5, updatedProduct.getProdQuantity());
			ps.setBlob(6, updatedProduct.getProdImage());
			ps.setString(7, prevProduct.getProdId());

			int k = ps.executeUpdate();

			if (k > 0)
				status = "Product Updated Successfully!";

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return status;
	}

	@Override
	public String updateProductPrice(String prodId, double updatedPrice) {
		String status = "Price Updation Failed!";

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("update product set pprice=? where pid=?");

			ps.setDouble(1, updatedPrice);
			ps.setString(2, prodId);

			int k = ps.executeUpdate();

			if (k > 0)
				status = "Price Updated Successfully!";
		} catch (SQLException e) {
			status = "Error: " + e.getMessage();
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return status;
	}

	@Override
	public List<ProductBean> getAllProducts() {
		List<ProductBean> products = new ArrayList<ProductBean>();

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select * from product WHERE pisused = false");

			rs = ps.executeQuery();

			while (rs.next()) {

				ProductBean product = new ProductBean();

				product.setProdId(rs.getString("pid"));
				product.setProdName(rs.getString("pname"));
				product.setProdType(rs.getString("ptype"));
				product.setProdInfo(rs.getString("pinfo"));
				product.setProdPrice(rs.getDouble("pprice"));
				product.setProdQuantity(rs.getInt("pquantity"));
				product.setProdImage(rs.getAsciiStream("image"));
				product.setIsUsed(rs.getBoolean("pisused"));
				product.setUsedProdId(rs.getString("pusedproductid"));
				product.setDiscountId(rs.getString("discountid"));

				products.add(product);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);

		return products;
	}

	@Override
	public List<ProductBean> getAllProductsByType(String type) {
		List<ProductBean> products = new ArrayList<ProductBean>();

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("SELECT * FROM `shopping-cart`.product where (lower(ptype) like ?) AND pisused = false;");
			ps.setString(1, "%" + type + "%");
			rs = ps.executeQuery();

			while (rs.next()) {

				ProductBean product = new ProductBean();

				product.setProdId(rs.getString("pid"));
				product.setProdName(rs.getString("pname"));
				product.setProdType(rs.getString("ptype"));
				product.setProdInfo(rs.getString("pinfo"));
				product.setProdPrice(rs.getDouble("pprice"));
				product.setProdQuantity(rs.getInt("pquantity"));
				product.setProdImage(rs.getAsciiStream("image"));
				product.setIsUsed(rs.getBoolean("pisused"));
				product.setUsedProdId(rs.getString("pusedproductid"));
				product.setDiscountId(rs.getString("discountid"));

				products.add(product);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);

		return products;
	}


	@Override
	public List<ProductBean> getAllUsedProducts() {
		List<ProductBean> products = new ArrayList<ProductBean>();

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select * from product where pisused is true AND pquantity > 0");

			rs = ps.executeQuery();

			while (rs.next()) {

				ProductBean product = new ProductBean();

				product.setProdId(rs.getString("pid"));
				product.setProdName(rs.getString("pname"));
				product.setProdType(rs.getString("ptype"));
				product.setProdInfo(rs.getString("pinfo"));
				product.setProdPrice(rs.getDouble("pprice"));
				product.setProdQuantity(rs.getInt("pquantity"));
				product.setProdImage(rs.getAsciiStream("image"));
				product.setIsUsed(rs.getBoolean("pisused"));
				product.setUsedProdId(rs.getString("pusedproductid"));
				product.setDiscountId(rs.getString("discountid"));

				products.add(product);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);

		return products;
	}

	@Override
	public List<ProductBean> getAllUsedProductsByType(String type) {
		List<ProductBean> products = new ArrayList<ProductBean>();

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("SELECT * FROM `shopping-cart`.product where pisused is true AND lower(ptype) like ? AND pquantity > 0;");
			ps.setString(1, "%" + type + "%");
			rs = ps.executeQuery();

			while (rs.next()) {

				ProductBean product = new ProductBean();

				product.setProdId(rs.getString("pid"));
				product.setProdName(rs.getString("pname"));
				product.setProdType(rs.getString("ptype"));
				product.setProdInfo(rs.getString("pinfo"));
				product.setProdPrice(rs.getDouble("pprice"));
				product.setProdQuantity(rs.getInt("pquantity"));
				product.setProdImage(rs.getAsciiStream("image"));
				product.setIsUsed(rs.getBoolean("pisused"));
				product.setUsedProdId(rs.getString("pusedproductid"));
				product.setDiscountId(rs.getString("discountid"));

				products.add(product);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);

		return products;
	}

	@Override
	public List<ProductBean> getLowStockProduct() {
		List<ProductBean> products = new ArrayList<>();
		int defaultThreshold = 3;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("SELECT * FROM `shopping-cart`.product p where (pquantity <= ? OR pquantity <= (SELECT SUM(quantity) FROM `shopping-cart`.orders o WHERE orderid IN(SELECT transid FROM `shopping-cart`.transactions t WHERE t.time BETWEEN ? AND ?))) AND pisused = false;");

			LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);

			LocalDate firstDayOfMonth = oneMonthAgo.with(TemporalAdjusters.firstDayOfMonth());
			LocalDate lastDayOfMonth = oneMonthAgo.with(TemporalAdjusters.lastDayOfMonth());

			ps.setInt(1, defaultThreshold);
			ps.setObject(2, LocalDateTime.of(firstDayOfMonth, LocalTime.MIDNIGHT));
			ps.setObject(3, LocalDateTime.of(lastDayOfMonth, LocalTime.MAX));
			rs = ps.executeQuery();

			while (rs.next()) {

				ProductBean product = new ProductBean();

				product.setProdId(rs.getString("pid"));
				product.setProdName(rs.getString("pname"));
				product.setProdType(rs.getString("ptype"));
				product.setProdInfo(rs.getString("pinfo"));
				product.setProdPrice(rs.getDouble("pprice"));
				product.setProdQuantity(rs.getInt("pquantity"));
				product.setProdImage(rs.getAsciiStream("image"));
				product.setIsUsed(rs.getBoolean("pisused"));
				product.setUsedProdId(rs.getString("pusedproductid"));
				product.setDiscountId(rs.getString("discountid"));

				products.add(product);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);

		return products;
	}

	@Override
	public List<ProductBean> getUnpopularProduct() {
		List<ProductBean> products = new ArrayList<>();
		int defaultThreshold = 3;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("SELECT * FROM product p WHERE p.discountid IS NULL AND p.pid IN (SELECT o.prodid FROM orders o GROUP BY o.prodid HAVING SUM(o.quantity) < ?);");

			ps.setInt(1, defaultThreshold);

			rs = ps.executeQuery();

			while (rs.next()) {

				ProductBean product = new ProductBean();

				product.setProdId(rs.getString("pid"));
				product.setProdName(rs.getString("pname"));
				product.setProdType(rs.getString("ptype"));
				product.setProdInfo(rs.getString("pinfo"));
				product.setProdPrice(rs.getDouble("pprice"));
				product.setProdQuantity(rs.getInt("pquantity"));
				product.setProdImage(rs.getAsciiStream("image"));
				product.setIsUsed(rs.getBoolean("pisused"));
				product.setUsedProdId(rs.getString("pusedproductid"));
				product.setDiscountId(rs.getString("discountid"));

				products.add(product);

			}


		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			DBUtil.closeConnection(con);
			DBUtil.closeConnection(ps);
			DBUtil.closeConnection(rs);
		}

		return products;
	}

	@Override
	public List<ProductBean> searchAllProducts(String search) {
		List<ProductBean> products = new ArrayList<ProductBean>();

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(
					"SELECT * FROM `shopping-cart`.product where (lower(ptype) like ? or lower(pname) like ? or lower(pinfo) like ?) AND pisused = false");
			search = "%" + search + "%";
			ps.setString(1, search);
			ps.setString(2, search);
			ps.setString(3, search);
			rs = ps.executeQuery();

			while (rs.next()) {

				ProductBean product = new ProductBean();

				product.setProdId(rs.getString("pid"));
				product.setProdName(rs.getString("pname"));
				product.setProdType(rs.getString("ptype"));
				product.setProdInfo(rs.getString("pinfo"));
				product.setProdPrice(rs.getDouble("pprice"));
				product.setProdQuantity(rs.getInt("pquantity"));
				product.setProdImage(rs.getAsciiStream("image"));
				product.setIsUsed(rs.getBoolean("pisused"));
				product.setUsedProdId(rs.getString("pusedproductid"));
				product.setDiscountId(rs.getString("discountid"));
				
				products.add(product);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);

		return products;
	}

	@Override
	public List<ProductBean> searchAllUsedProducts(String search) {
		List<ProductBean> products = new ArrayList<ProductBean>();

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(
					"SELECT * FROM `shopping-cart`.product where (lower(ptype) like ? or lower(pname) like ? or lower(pinfo) like ?) AND pisused = true AND pquantity > 0");
			search = "%" + search + "%";
			ps.setString(1, search);
			ps.setString(2, search);
			ps.setString(3, search);
			rs = ps.executeQuery();

			while (rs.next()) {

				ProductBean product = new ProductBean();

				product.setProdId(rs.getString("pid"));
				product.setProdName(rs.getString("pname"));
				product.setProdType(rs.getString("ptype"));
				product.setProdInfo(rs.getString("pinfo"));
				product.setProdPrice(rs.getDouble("pprice"));
				product.setProdQuantity(rs.getInt("pquantity"));
				product.setProdImage(rs.getAsciiStream("image"));
				product.setIsUsed(rs.getBoolean("pisused"));
				product.setUsedProdId(rs.getString("pusedproductid"));
				product.setDiscountId(rs.getString("discountid"));
				
				products.add(product);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);

		return products;
	}

	@Override
	public byte[] getImage(String prodId) {
		byte[] image = null;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select image from product where  pid=?");

			ps.setString(1, prodId);

			rs = ps.executeQuery();

			if (rs.next())
				image = rs.getBytes("image");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);

		return image;
	}

	@Override
	public ProductBean getProductDetails(String prodId) {
		ProductBean product = null;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select * from product where pid=?");

			ps.setString(1, prodId);
			rs = ps.executeQuery();

			if (rs.next()) {
				product = new ProductBean();
				product.setProdId(rs.getString("pid"));
				product.setProdName(rs.getString("pname"));
				product.setProdType(rs.getString("ptype"));
				product.setProdInfo(rs.getString("pinfo"));
				product.setProdPrice(rs.getDouble("pprice"));
				product.setProdQuantity(rs.getInt("pquantity"));
				product.setProdImage(rs.getAsciiStream("image"));
				product.setIsUsed(rs.getBoolean("pisused"));
				product.setUsedProdId(rs.getString("pusedproductid"));
				product.setDiscountId(rs.getString("discountid"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return product;
	}

	@Override
	public String updateProductWithoutImage(String prevProductId, ProductBean updatedProduct) {
		String status = "Product Updation Failed!";

		if (!prevProductId.equals(updatedProduct.getProdId())) {

			status = "Both Products are Different, Updation Failed!";

			return status;
		}

		int prevQuantity = new ProductServiceImpl().getProductQuantity(prevProductId);
		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("update product set pname=?,ptype=?,pinfo=?,pprice=?,pquantity=?,discountid=? where pid=?");

			ps.setString(1, updatedProduct.getProdName());
			ps.setString(2, updatedProduct.getProdType());
			ps.setString(3, updatedProduct.getProdInfo());
			ps.setDouble(4, updatedProduct.getProdPrice());
			ps.setInt(5, updatedProduct.getProdQuantity());
			ps.setString(6, updatedProduct.getDiscountId());
			ps.setString(7, prevProductId);

			int k = ps.executeUpdate();
			// System.out.println("prevQuantity: "+prevQuantity);
			if ((k > 0) && (prevQuantity < updatedProduct.getProdQuantity())) {
				status = "Product Updated Successfully!";
				// System.out.println("updated!");
				List<DemandBean> demandList = new DemandServiceImpl().haveDemanded(prevProductId);

				for (DemandBean demand : demandList) {

					String userFName = new UserServiceImpl().getFName(demand.getUserName());
					try {
						MailMessage.productAvailableNow(demand.getUserName(), userFName, updatedProduct.getProdName(),
								prevProductId);
					} catch (Exception e) {
						System.out.println("Mail Sending Failed: " + e.getMessage());
					}
					boolean flag = new DemandServiceImpl().removeProduct(demand.getUserName(), prevProductId);

					if (flag)
						status += " And Mail Send to the customers who were waiting for this product!";
				}
			} else if (k > 0)
				status = "Product Updated Successfully!";
			else
				status = "Product Not available in the store!";

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		// System.out.println("Prod Update status : "+status);

		return status;
	}
	
	@Override
	public String updateUsedProductWithoutImage(String usedProductID, int usedProductQuantity, ProductBean updatedProductInfo) {
		

		String status = "Product Updation Failed!";

		
		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("update product set pname=?,ptype=?,pinfo=?,pprice=?,pquantity=? where pid=?");

			ps.setString(1, updatedProductInfo.getProdName());
			ps.setString(2, updatedProductInfo.getProdType());
			ps.setString(3, updatedProductInfo.getProdInfo());
			ps.setDouble(4, updatedProductInfo.getProdPrice() * 0.7);
			ps.setInt(5, usedProductQuantity);
			ps.setString(6, usedProductID);
			
			int k = ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		// System.out.println("Prod Update status : "+status);

		return status;
	}

	@Override
	public double getProductPrice(String prodId) {
		double price = 0;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select * from product where pid=?");

			ps.setString(1, prodId);
			rs = ps.executeQuery();

			if (rs.next()) {
				price = rs.getDouble("pprice");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return price;
	}

	@Override
	public boolean sellNProduct(String prodId, int n) {
		boolean flag = false;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {

			ps = con.prepareStatement("update product set pquantity=(pquantity - ?) where pid=?");

			ps.setInt(1, n);

			ps.setString(2, prodId);

			int k = ps.executeUpdate();

			if (k > 0)
				flag = true;
		} catch (SQLException e) {
			flag = false;
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return flag;
	}

	@Override
	public int getProductQuantity(String prodId) {

		int quantity = 0;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select * from product where pid=?");

			ps.setString(1, prodId);
			rs = ps.executeQuery();

			if (rs.next()) {
				quantity = rs.getInt("pquantity");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return quantity;
	}

	/**
	 * Sorts a list of products by their sales, based on the given popularity criteria.
	 *
	 * @param products   the list of products to be sorted
	 * @param order the ordering criteria for sorting ("ASC" or "DESC")
	 * @return the sorted list of products
	 */
	@Override
	public List<ProductBean> sortProductsBySales(List<ProductBean> products, String order) {
		if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
			throw new IllegalArgumentException("Invalid popularity criteria: " + order);
		}
		if (products.isEmpty()) {
			return products;
		}

		Map<String, ProductBean> prodIdToProduct = new HashMap<>();
		List<ProductBean> orderedProducts = new ArrayList<>();

		String question_marks = String.join(", ", Collections.nCopies(products.size(), "?"));

		products.forEach(product -> prodIdToProduct.put(product.getProdId(), product));

		Connection con = DBUtil.provideConnection();
		PreparedStatement ps;
		ResultSet rs;
		String query = "SELECT prodid, SUM(quantity) as total_quantity FROM orders WHERE prodid IN (" +
				question_marks + ") GROUP by prodid ORDER BY total_quantity " + order;

		try {
			ps = con.prepareStatement(query);
			int index = 1;
			for (String prodId : prodIdToProduct.keySet()) {
				ps.setString(index++, prodId);
			}

			rs = ps.executeQuery();

			while (rs.next()) {
				String prodId = rs.getString("prodid");
				orderedProducts.add(prodIdToProduct.remove(prodId));
			}

			if ("ASC".equalsIgnoreCase(order)) {
				orderedProducts.addAll(0, prodIdToProduct.values());
			} else {
				orderedProducts.addAll(prodIdToProduct.values());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return orderedProducts;
	}
	
	public int getUsedProductCount(String prodId)
	{
		
		int usedProductCount = 0;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			// Quantity
			ps = con.prepareStatement("select pquantity from product where pid = (select pusedproductid from product where pid=?)");

			ps.setString(1, prodId);
			rs = ps.executeQuery();

			if (rs.next()) {
				usedProductCount = rs.getInt("pquantity");
			}
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		
		return usedProductCount;
	}

}
