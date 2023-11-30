package test.java;

import com.shashi.beans.DiscountBean;
import com.shashi.service.impl.DiscountServiceImpl;
import com.shashi.utility.DBUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiscountServiceImplTest {
    static Connection con;

    private Savepoint savepoint;

    private DiscountServiceImpl discountService;

    private List<DiscountBean> discounts= new ArrayList<>();

    @BeforeEach
    void setUp() {
        con = DBUtil.provideConnection();

        try {
            con.setAutoCommit(false);
            savepoint = con.setSavepoint();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        discountService = new DiscountServiceImpl();


        for (int i = 1; i <= 3; i++) {
            String discountId = String.valueOf(i);
            String discountName = "Discount " + i;
            int discountPercentage = (i * 5) + 5;  // Generating a percentage based on the index
            LocalDate startDate = LocalDate.now().plusDays(i * 10);
            LocalDate endDate = LocalDate.now().plusDays((i * 10) + 30);

            DiscountBean discount = new DiscountBean(discountId,  discountPercentage, startDate, endDate);
            discounts.add(discount);
        }

        for (DiscountBean discount : discounts) {
            discountService.updateDiscountIntoDB(discount);
        }
    }

    @AfterEach
    void tearDown() {
        try {
            con.rollback(savepoint);
            con.setAutoCommit(true);
            DBUtil.closeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    void getDiscountById_ShouldReturnNullForNonExistentDiscount() {
        DiscountBean result = discountService.getDiscountDetails("999");
        assertNull(result);
    }

    @Test
    void updateDiscount_ShouldUpdateDiscount() {
        DiscountBean discountToUpdate = discountService.getDiscountDetails("1");
        discountToUpdate.setDiscountPercentage(20);

        discountService.updateDiscountIntoDB(discountToUpdate);


        DiscountBean updatedDiscount = discountService.getDiscountDetails("1");
        assertNotNull(updatedDiscount);
        assertEquals(20, updatedDiscount.getDiscountPercentage());
    }

    @Test
    void updateDiscount_ShouldReturnFalseForNonExistentDiscount() {
        DiscountBean nonExistentDiscount = new DiscountBean("Non-Existent Discount", 5, LocalDate.now(), LocalDate.now().plusDays(30));

        discountService.updateDiscountIntoDB(nonExistentDiscount);
    }

    @Test
    void deleteDiscount_ShouldDeleteDiscount() {
        discountService.deleteDiscountFromDB("1");

        DiscountBean deletedDiscount = discountService.getDiscountDetails("1");
        assertNull(deletedDiscount);
    }

    @Test
    void deleteDiscount_ShouldReturnFalseForNonExistentDiscount() {
        discountService.deleteDiscountFromDB("999");
    }

    
}
