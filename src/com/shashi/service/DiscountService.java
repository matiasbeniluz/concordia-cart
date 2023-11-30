package com.shashi.service;

import com.shashi.beans.DiscountBean;
import com.shashi.beans.ProductBean;

import java.io.InputStream;
import java.util.List;

public interface DiscountService {

	/**
	 * Update discount information
	 * @param discount discount to update
	 */
	public void updateDiscountIntoDB(DiscountBean discount);

	/**
	 * Given the discount id, return the discount associated with it
	 * @param discountId discount id to search
	 * @return the discount
	 */
	public DiscountBean getDiscountDetails(String discountId);

	/**
	 * Get all discounts
	 * @return list of discounts
	 */
	public List<DiscountBean> getAllDiscounts();
}
