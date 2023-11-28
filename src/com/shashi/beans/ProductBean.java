package com.shashi.beans;

import java.io.InputStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public class ProductBean implements Serializable {

	public ProductBean() {
	}

	private String prodId;
	private String prodName;
	private String prodType;
	private String prodInfo;
	private double prodPrice;
	private int prodQuantity;
	private InputStream prodImage;
	private Discount discount;
	private boolean isUsed;

	public ProductBean(String prodId, String prodName, String prodType, String prodInfo, double prodPrice,
			int prodQuantity, InputStream prodImage, boolean isUsed) {
		super();
		this.prodId = prodId;
		this.prodName = prodName;
		this.prodType = prodType;
		this.prodInfo = prodInfo;
		this.prodPrice = prodPrice;
		this.prodQuantity = prodQuantity;
		this.prodImage = prodImage;
		this.isUsed = isUsed;
	}

	// Constructor with Discount
	public ProductBean(String prodId, String prodName, String prodType, String prodInfo,
					   double prodPrice, int prodQuantity, InputStream prodImage, Discount discount) {
		this.prodId = prodId;
		this.prodName = prodName;
		this.prodType = prodType;
		this.prodInfo = prodInfo;
		this.prodPrice = prodPrice;
		this.prodQuantity = prodQuantity;
		this.prodImage = prodImage;
		this.discount = discount;
	}

	public String getProdId() {
		return prodId;
	}

	public void setProdId(String prodId) {
		this.prodId = prodId;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public String getProdType() {
		return prodType;
	}

	public void setProdType(String prodType) {
		this.prodType = prodType;
	}

	public String getProdInfo() {
		return prodInfo;
	}

	public void setProdInfo(String prodInfo) {
		this.prodInfo = prodInfo;
	}

	public double getProdPrice() {
		return prodPrice;
	}

	public void setProdPrice(double prodPrice) {
		this.prodPrice = prodPrice;
	}

	public int getProdQuantity() {
		return prodQuantity;
	}

	public void setProdQuantity(int prodQuantity) {
		this.prodQuantity = prodQuantity;
	}

	public InputStream getProdImage() {
		return prodImage;
	}

	public void setProdImage(InputStream prodImage) {
		this.prodImage = prodImage;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}
	public boolean getIsUsed() { return isUsed; }

	public void setIsUsed(boolean isUsed) { this.isUsed = isUsed; }

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ProductBean)) {
			return false;
		}

		return this.prodId.equals(((ProductBean) obj).prodId);
	}


	// Calculate the discounted price based on the sale percentage
	// Update the discountedPrice of the Discount obj. based on the calculated result
	public void calculateDiscountedPrice() {
		if (discount != null) {

			// If discount percentage is not zero, update the discounted price
			// Else, set the discount to null
			if (discount.getSalePercentage() != 0) {

				// Calculate discounted price based on the sale percentage
				discount.setDiscountedPrice((int)(prodPrice - (prodPrice * (- discount.getSalePercentage()) / 100.0)));

				// Debug:
				System.out.println("Discounted Price: " + discount.getDiscountedPrice());
				System.out.println("Discounted Start Date: " + discount.getStartDate());
				System.out.println("Discounted End Date: " + discount.getEndDate());
				System.out.println(discount.getRemainingTime());
			}
			else
				this.setDiscount(null);
		}
	}
}
