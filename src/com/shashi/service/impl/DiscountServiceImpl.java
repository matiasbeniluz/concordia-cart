package com.shashi.service.impl;

import com.shashi.beans.DiscountBean;
import com.shashi.service.DiscountService;
import com.shashi.utility.DBUtil;
import com.shashi.utility.IDUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DiscountServiceImpl implements DiscountService {
	
	
	@Override
	public String addDiscount(String discountName, int discountPercent, LocalDate startDate, LocalDate endDate)
	{
		String result = "Null";
		
		String discountId = IDUtil.generateDiscountId();
		
		DiscountBean discount = new DiscountBean(discountId, discountName, discountPercent, startDate, endDate);
		
		result = addDiscount(discount);
		
		return result;
	}
	
	
	@Override
	public String addDiscount(DiscountBean discount)
	{
		String status = "Product Registration Failed!";

		if (discount.getDiscountId() == null)
			discount.setDiscountId(IDUtil.generateId());

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("insert into discount values(?,?,?,?,?);");
			ps.setString(1, discount.getDiscountId());
			ps.setString(2, discount.getDiscountName());
			ps.setDouble(3, discount.getDiscountPercentage());
			ps.setDate(4, java.sql.Date.valueOf(discount.getStartDate()));
			ps.setDate(5, java.sql.Date.valueOf(discount.getEndDate()));

			int k = ps.executeUpdate();

			if (k > 0) 
			{
				status = "Discount Added Successfully with Discount Id: " + discount.getDiscountId();

			} 
			else 
			{
				status = "Discount Addition Failed!";
			}	

		} catch (SQLException e) {
			status = "Error: " + e.getMessage();
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return status;
	}
	
	
    @Override
    public String updateDiscountIntoDB(String did, DiscountBean discount) {
        Connection con = DBUtil.provideConnection();
        PreparedStatement ps = null;
        
        String result = "Error in updating the discount";

        try {
           
        	//Update the existing discount
            String updateQuery = "UPDATE discount SET discountName=?, discountPercentage=?, startDate=?, endDate=? WHERE discountId=?;";
            ps = con.prepareStatement(updateQuery);
            
            ps.setString(1, discount.getDiscountName());
            ps.setDouble(2, discount.getDiscountPercentage());
            ps.setDate(3, java.sql.Date.valueOf(discount.getStartDate()));
            ps.setDate(4, java.sql.Date.valueOf(discount.getEndDate()));
            ps.setString(5, discount.getDiscountId());
            

            // Execute the update or insert query
            int k = ps.executeUpdate();
            
            if (k > 0) 
			{
				result = "Discount Updated Successfully";

			} 
			else 
			{
				result = "Discount Updation Failed!";
			}

        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBUtil.closeConnection(con);
        DBUtil.closeConnection(ps);
        
        return result;
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
    public String removeDiscount(String discountId) {
        Connection con = DBUtil.provideConnection();
        PreparedStatement ps = null;
        
        String result = "Error in removing discount";

        try {

            // Delete the discount entry
            String deleteQuery = "DELETE FROM discount WHERE discountId=?";
            ps = con.prepareStatement(deleteQuery);
            ps.setString(1, discountId);
            int k = ps.executeUpdate();
            
            if (k > 0) 
			{
				result = "Discount Removed Successfully";

			} 
			else 
			{
				result = "Discount Removal Failed!";
			}
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeConnection(con);
            DBUtil.closeConnection(ps);
        }
        
        return result;
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
    public List<DiscountBean> getExpiredDiscounts() {
        Date currentDate = Date.valueOf(LocalDate.now());
        String query = "SELECT * FROM `discount` WHERE `endDate` < ?";
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

    @Override
    public boolean isActiveDiscount(DiscountBean discount) {
        return !discount.getStartDate().isAfter(LocalDate.now()) && !discount.getEndDate().isBefore(LocalDate.now());
    }
}
