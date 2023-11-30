package com.shashi.service;

import com.shashi.beans.DiscountBean;

import java.util.List;

public interface DiscountService {
    List<DiscountBean> getActiveAndUpcomingDiscounts();

    List<DiscountBean> getActiveDiscounts();

    void deleteExpiredDiscounts();
}
