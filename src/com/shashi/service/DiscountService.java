package com.shashi.service;

import com.shashi.beans.DiscountBean;

import java.time.LocalDate;
import java.util.List;

public interface DiscountService {

	String addDiscount(String discountName, int discountPercent, LocalDate startDate, LocalDate endDate);
	
	String addDiscount(DiscountBean discount);
	
    /**
     * Update discount information
     *
     * @param discount discount to update
     */
	public String updateDiscountIntoDB(String did, DiscountBean discount);

    /**
     * Given the discount id, return the discount associated with it
     *
     * @param discountId discount id to search
     * @return the discount
     */
    DiscountBean getDiscountDetails(String discountId);

    /**
     * Get all discounts
     *
     * @return list of discounts
     */
    List<DiscountBean> getAllDiscounts();

    /**
     * Get active and upcoming discounts
     * @return list of active and upcoming discounts
     */
    List<DiscountBean> getActiveAndUpcomingDiscounts();

    /**
     * Method to delete a discount from the DB
     * @param discountId discount id
     */
    String removeDiscount(String discountId);

    /**
     * Get active discounts
     * @return list of active discounts
     */
    List<DiscountBean> getActiveDiscounts();

    /**
     * Get expired discounts
     */
    List<DiscountBean>  getExpiredDiscounts();

    /**
     * Remove expired discounts
     */
    void deleteExpiredDiscounts();
}
