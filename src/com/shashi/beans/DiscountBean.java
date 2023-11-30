package com.shashi.beans;

import java.time.LocalDate;
import java.time.Period;

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

    // Return the discounted price based on the discount percentage
	public double discountedPrice(double realPrice) {
        return (int)(realPrice - (realPrice * (- this.getDiscountPercentage()) / 100.0));
	}

    // Calculate remaining time of the sale
    public String getRemainingTime() {
        if (startDate != null && endDate != null) {
            LocalDate currentDate = LocalDate.now();
            if (currentDate.isEqual(startDate) || currentDate.isAfter(startDate)) {
                if (currentDate.isBefore(endDate)) {
                    Period remainingPeriod = Period.between(currentDate, endDate);
                    int remainingDays = remainingPeriod.getDays();
                    int remainingMonths = remainingPeriod.getMonths();
                    int remainingYears = remainingPeriod.getYears();
                    if (remainingYears > 0)
                        return String.format("%d years, %d months, %d days", remainingYears, remainingMonths, remainingDays);
                    else if (remainingMonths > 0)
                        return String.format("%d months, %d days", remainingMonths, remainingDays);
                    else
                        return String.format("%d days", remainingDays);
                }
                else
                    return "Sale has ended";
            }
            else
                return "Sale has not started yet";
        }
        else
            return "Sale dates are not set";
    }
}
