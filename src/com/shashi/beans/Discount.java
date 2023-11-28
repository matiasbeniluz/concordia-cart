package com.shashi.beans;

import java.time.LocalDate;
import java.time.Period;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.shashi.utility.DBUtil;

public class Discount {
    private String productId;
    private double discountedPrice;
    private LocalDate startDate;
    private LocalDate endDate;

    private double salePercentage;


    // Constructors, getters, and setters

    // Default constructor
    public Discount() { }

    public Discount(String productId, double discountedPrice, LocalDate startDate, LocalDate endDate) {
        this.productId = productId;
        this.discountedPrice = discountedPrice;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
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

    public double getSalePercentage() {
        return salePercentage;
    }

    public void setSalePercentage(double salePercentage) {
        this.salePercentage = salePercentage;

        // If sale percentage is zero, remove the discount information from the database
        if (salePercentage == 0) {
            removeDiscountFromDatabase();
        }
    }

    private void removeDiscountFromDatabase() {
        if (productId != null) {
            Connection con = DBUtil.provideConnection();
            PreparedStatement ps = null;

            try {

                // Delete the discount information from the database based on the product ID
                ps = con.prepareStatement("DELETE FROM discount WHERE productId = ?");
                ps.setString(1, productId);
                ps.executeUpdate();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                DBUtil.closeConnection(con);
                DBUtil.closeConnection(ps);
            }
        }
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