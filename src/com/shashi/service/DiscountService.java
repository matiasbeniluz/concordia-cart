package com.shashi.service;

import com.shashi.beans.DiscountBean;

import java.util.List;

public interface DiscountService {

    /**
     * Update discount information
     *
     * @param discount discount to update
     */
    void updateDiscountIntoDB(DiscountBean discount);

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
}
