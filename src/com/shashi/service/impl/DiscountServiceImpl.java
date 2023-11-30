package com.shashi.service.impl;

import com.shashi.beans.DiscountBean;
import com.shashi.utility.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DiscountServiceImpl {
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
        }
        catch (SQLException e) {
			e.printStackTrace();
        }
        DBUtil.closeConnection(con);
        DBUtil.closeConnection(ps);
    }

    // Method to check if a discount already exists in the DB
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
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			DBUtil.closeConnection(con);
			DBUtil.closeConnection(ps);
		}

		return false; // Return false in case of an error or no discount found
	}

	// Given the discount id, return the discount associated with it
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
				discount.setDiscountPercentage(rs.getInt(2));
				discount.setStartDate(rs.getDate(3).toLocalDate());
				discount.setEndDate(rs.getDate(4).toLocalDate());
			}

		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return discount;
	}

	// Method to delete a discount from the DB
	public void deleteDiscountFromDB(String discountId) {
		Connection con = DBUtil.provideConnection();
		PreparedStatement ps = null;

		try {

			// Delete the discount entry
			String deleteQuery = "DELETE FROM discount WHERE discountId=?";
			ps = con.prepareStatement(deleteQuery);
			ps.setString(1, discountId);
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
	public List<DiscountBean> getAllDiscountItems() {
		List<DiscountBean> discountItems = new ArrayList<>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			// Establish a database connection
			connection = DBUtil.provideConnection();

			// SQL query to retrieve all discount items
			String sql = "SELECT discountId, discountName, discountPercentage, startDate, endDate FROM discounts";

			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				String discountId = resultSet.getString("discountId");
				String discountName = resultSet.getString("discountName");
				int discountPercentage = resultSet.getInt("discountPercentage");
				LocalDate startDate = resultSet.getDate("startDate").toLocalDate();
				LocalDate endDate = resultSet.getDate("endDate").toLocalDate();

				// Create a DiscountBean object and add it to the list
				DiscountBean discountItem = new DiscountBean(discountId, discountPercentage, startDate, endDate);
				discountItems.add(discountItem);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close the database resources
			DBUtil.closeConnection(connection);
		}

		return discountItems;
	}
}
