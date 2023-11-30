package com.shashi.beans;

import java.time.LocalDate;

import com.shashi.utility.IDUtil;


public class DiscountBean {

    private String discountId;
    private String discountName;
    private int discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;

    // Constructors, getters, and setters

    // Default constructor
    public DiscountBean() {

        // Generate a discount ID when a discount is created
        this.discountId = IDUtil.generateId();
    }

    public DiscountBean(String discountId, String discountName, int discountPercentage, LocalDate startDate, LocalDate endDate) {
        this.discountId = discountId;
        this.discountName = discountName;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getDiscountId() {
        return discountId;
    }

    public void setDiscountId(String discountId) {
        this.discountId = discountId;
    }


    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DiscountBean)) {
            return false;
        }

        return this.discountId.equals(((DiscountBean) obj).getDiscountId());
    }
}
