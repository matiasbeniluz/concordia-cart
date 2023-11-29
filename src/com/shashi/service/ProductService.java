package com.shashi.service;

import java.io.InputStream;
import java.util.List;

import com.shashi.beans.ProductBean;

public interface ProductService {

	public String addProduct(String prodName, String prodType, String prodInfo, double prodPrice, int prodQuantity,
			InputStream prodImage);

    public String addProduct(ProductBean product);

	public String removeProduct(String prodId);

	public String updateProduct(ProductBean prevProduct, ProductBean updatedProduct);

	public String updateProductPrice(String prodId, double updatedPrice);

	public List<ProductBean> getAllProducts();

	public List<ProductBean> getAllProductsByType(String type);

	/**
	 * Get all used products
	 * @return Products that are used
	 */
	public List<ProductBean> getAllUsedProducts();

	/**
	 * Get all used products by type
	 * @return Products that are used filtered by type
	 */
	public List<ProductBean> getAllUsedProductsByType(String type);


	/**
	 * Get products that are low in stock
	 * @return Products low in stock
	 */
	public List<ProductBean> getLowStockProduct();

	public List<ProductBean> searchAllProducts(String search);

	/**
	 * Get all used products filtered by a search string
	 * @return Products that are used filtered by search string
	 */
	public List<ProductBean> searchAllUsedProducts(String search);

	public byte[] getImage(String prodId);

	public ProductBean getProductDetails(String prodId);

	public String updateProductWithoutImage(String prevProductId, ProductBean updatedProduct);
	
	public String updateUsedProductWithoutImage(String usedProductID, int usedProductQuantity, ProductBean updatedProductInfo);

	public double getProductPrice(String prodId);

	public boolean sellNProduct(String prodId, int n);

	public int getProductQuantity(String prodId);

	/**
	 * Sorts a list of products by their sales/order.
	 *
	 * @param products    The list of products to be sorted.
	 * @param order  The criteria by which to sort the products.
	 *                    Accepted values are "ASC" and "DESC".
	 * @return A sorted list of products based on the specified order criteria.
	 *         If the order value is not valid, an exception will be returned.
	 */
	List<ProductBean> sortProductsBySales(List<ProductBean> products, String order);
}
