package com.shashi.beans;

import com.shashi.service.DiscountService;
import com.shashi.service.impl.DiscountServiceImpl;

import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;

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
	private boolean isUsed;
	private String usedProdId;
	private String discountId;

	public ProductBean(String prodId, String prodName, String prodType, String prodInfo, double prodPrice,
			int prodQuantity, InputStream prodImage, boolean isUsed, String usedProdId, String discountId) {

		super();
		this.prodId = prodId;
		this.prodName = prodName;
		this.prodType = prodType;
		this.prodInfo = prodInfo;
		this.prodPrice = prodPrice;
		this.prodQuantity = prodQuantity;
		this.prodImage = prodImage;
		this.isUsed = isUsed;
		this.usedProdId = usedProdId;
		this.discountId = discountId;
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

	public boolean getIsUsed() { return isUsed; }

	public void setIsUsed(boolean isUsed) { this.isUsed = isUsed; }
	
	public String getusedProdId() {
		return usedProdId;
	}

	public void setUsedProdId(String usedProdId) {
		this.usedProdId = usedProdId;
	}

	public String getDiscountId() { return discountId; }

	public void setDiscountId(String discountId) { this.discountId = discountId; }

	public double getCurrentPrice() {
		if (this.discountId == null) {
			return this.prodPrice;
		}
		DiscountService discountService = new DiscountServiceImpl();
		DiscountBean discount = discountService.getDiscountDetails(this.discountId);

		return discountService.isActiveDiscount(discount) ? Math.round(this.prodPrice - (this.prodPrice * discount.getDiscountPercentage() / 100.0))
				: this.prodPrice;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ProductBean)) {
			return false;
		}

		return this.prodId.equals(((ProductBean) obj).prodId);
	}
}
