package com.shashi.service;

import java.io.InputStream;
import java.util.List;

import com.shashi.beans.ProductBean;

public interface ProductService {

	public String addProduct(String prodName, String prodType, String prodInfo, double prodPrice, int prodQuantity,
			InputStream prodImage, boolean isUsed);

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

	public double getProductPrice(String prodId);

	public boolean sellNProduct(String prodId, int n);

	public int getProductQuantity(String prodId);

	List<ProductBean> orderProductsByPopularity(List<ProductBean> products, String popularity);
}
