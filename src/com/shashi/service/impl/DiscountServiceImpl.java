package com.shashi.service.impl;

import com.shashi.beans.DiscountBean;
import com.shashi.service.DiscountService;
import com.shashi.utility.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DiscountServiceImpl implements DiscountService {
    @Override
    public void updateDiscountIntoDB(DiscountBean discount) {
        Connection con = DBUtil.provideConnection();

        PreparedStatement ps = null;

        try {
            if (discountExists(discount.getDiscountId())) {

                //Update the existing discount
                String updateQuery = "UPDATE discount SET discountPercentage=?, startDate=?, endDate=? WHERE discountId=?";
                ps = con.prepareStatement(updateQuery);
                ps.setDouble(1, discount.getDiscountPercentage());
                ps.setDate(2, java.sql.Date.valueOf(discount.getStartDate()));
                ps.setDate(3, java.sql.Date.valueOf(discount.getEndDate()));
                ps.setString(4, discount.getDiscountId());
            }
            else {

                // Insert a new discount
                String insertQuery = "INSERT INTO discount (discountId, discountPercentage, startDate, endDate) VALUES (?, ?, ?, ?)";
                ps = con.prepareStatement(insertQuery);
                ps.setString(1, discount.getDiscountId());
                ps.setDouble(2, discount.getDiscountPercentage());
                ps.setDate(3, java.sql.Date.valueOf(discount.getStartDate()));
                ps.setDate(4, java.sql.Date.valueOf(discount.getEndDate()));
            }

            // Execute the update or insert query
            int rowsAffected = ps.executeUpdate();

//			// Check the result (optional)
//			if (rowsAffected > 0) {
//				System.out.println("Discount updated/inserted successfully.");
//			} else {
//				System.out.println("Failed to update/insert discount.");
//			}
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBUtil.closeConnection(con);
        DBUtil.closeConnection(ps);
    }

    /**
     * Check if a discount already exists in the DB
     *
     * @param discountId discountId
     * @return isFound
     */
    private boolean discountExists(String discountId) {
        Connection con = DBUtil.provideConnection();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            // Query to check if a discount exists in the DB
            String query = "SELECT COUNT(*) FROM discount WHERE discountId=?";
            ps = con.prepareStatement(query);
            ps.setString(1, discountId);
            rs = ps.executeQuery();

            // Check if any rows were returned (discount exists)
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeConnection(con);
            DBUtil.closeConnection(ps);
        }

        return false; // Return false in case of an error or no discount found
    }

    @Override
    public DiscountBean getDiscountDetails(String discountId) {
        DiscountBean discount = null;

        Connection con = DBUtil.provideConnection();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement("select * from discount where discountId=?");

            ps.setString(1, discountId);
            rs = ps.executeQuery();

            if (rs.next()) {
                discount = new DiscountBean();
                discount.setDiscountId(rs.getString(1));
                discount.setDiscountName(rs.getString(2));
                discount.setDiscountPercentage(rs.getInt(3));
                discount.setStartDate(rs.getObject(4, LocalDate.class));
                discount.setEndDate(rs.getObject(5, LocalDate.class));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        DBUtil.closeConnection(con);
        DBUtil.closeConnection(ps);

        return discount;
    }

    @Override
    public List<DiscountBean> getAllDiscounts() {
        List<DiscountBean> discounts = new ArrayList<>();

        Connection con = DBUtil.provideConnection();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement("select * from discount;");

            rs = ps.executeQuery();

            while (rs.next()) {

                DiscountBean discount = new DiscountBean(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getObject(4, LocalDate.class),
                        rs.getObject(5, LocalDate.class)
                );

                discounts.add(discount);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        DBUtil.closeConnection(con);
        DBUtil.closeConnection(ps);
        DBUtil.closeConnection(rs);

        return discounts;
    }

    @Override
    public void deleteDiscountFromDB(String discountId) {
        Connection con = DBUtil.provideConnection();
        PreparedStatement ps = null;

        try {

            // Delete the discount entry
            String deleteQuery = "DELETE FROM discount WHERE discountId=?";
            ps = con.prepareStatement(deleteQuery);
            ps.setString(1, discountId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeConnection(con);
            DBUtil.closeConnection(ps);
        }
    }

    @Override
    public List<DiscountBean> getActiveAndUpcomingDiscounts() {
        Date currentDate = Date.valueOf(LocalDate.now());
        String query = "SELECT * FROM `discount` WHERE `endDate` >= ?";
        List<DiscountBean> discounts = new ArrayList<>();

        try (Connection con = DBUtil.provideConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setDate(1, currentDate);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DiscountBean discount = new DiscountBean();
                discount.setDiscountId(rs.getString("discountId"));
                discount.setDiscountName(rs.getString("discountName"));
                discount.setDiscountPercentage(rs.getInt("discountPercentage"));
                discount.setStartDate(rs.getDate("startDate").toLocalDate());
                discount.setEndDate(rs.getDate("endDate").toLocalDate());

                discounts.add(discount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return discounts;
    }

    @Override
    public List<DiscountBean> getActiveDiscounts() {
        Date currentDate = Date.valueOf(LocalDate.now());
        String query = "SELECT * FROM `discount` WHERE `startDate` <= ? AND `endDate` >= ?";
        List<DiscountBean> discounts = new ArrayList<>();

        try (Connection con = DBUtil.provideConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setDate(1, currentDate);
            ps.setDate(2, currentDate);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DiscountBean discount = new DiscountBean();
                discount.setDiscountId(rs.getString("discountId"));
                discount.setDiscountName(rs.getString("discountName"));
                discount.setDiscountPercentage(rs.getInt("discountPercentage"));
                discount.setStartDate(rs.getDate("startDate").toLocalDate());
                discount.setEndDate(rs.getDate("endDate").toLocalDate());

                discounts.add(discount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return discounts;
    }

    @Override
    public void deleteExpiredDiscounts() {
        Date currentDate = Date.valueOf(LocalDate.now());
        String query = "DELETE FROM `discount` WHERE `endDate` < ?";

        try (Connection con = DBUtil.provideConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setDate(1, currentDate);
            int rowsAffected = ps.executeUpdate();

            System.out.println("Deleted " + rowsAffected + " expired discounts.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
