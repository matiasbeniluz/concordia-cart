package test.java;

import com.shashi.beans.OrderBean;
import com.shashi.beans.ProductBean;
import com.shashi.beans.TransactionBean;
import com.shashi.service.OrderService;
import com.shashi.service.ProductService;
import com.shashi.service.impl.OrderServiceImpl;
import com.shashi.service.impl.ProductServiceImpl;
import com.shashi.utility.DBUtil;
import com.shashi.utility.IDUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceImplTest {
    static Connection con;

    private Savepoint savepoint;

    private ProductService productService;

    private OrderService orderService;

    private List<ProductBean> products;

    private List<OrderBean> orders;


    @BeforeEach
    void setUp() {
        con = DBUtil.provideConnection();

        try {
            con.setAutoCommit(false);
            savepoint = con.setSavepoint();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        productService = new ProductServiceImpl();
        orderService = new OrderServiceImpl();

        products = Arrays.asList(
                new ProductBean(IDUtil.generateId() + "1", "Test Prod 1", "mobile", "testInfo 1", 100, 1, null, false),
                new ProductBean(IDUtil.generateId() + "2", "Test Prod 2", "camera", "testInfo 2", 200, 2, null, false),
                new ProductBean(IDUtil.generateId() + "3", "Test Prod 3", "tv", "testInfo 3", 300, 3, null, false),
                new ProductBean(IDUtil.generateId() + "4", "Test Prod 4", "tv", "testInfo 4", 400, 5, null, false),
                new ProductBean(IDUtil.generateId() + "5", "Test Prod 5", "mobile", "testInfo 5", 100, 5, null, true),
                new ProductBean(IDUtil.generateId() + "6", "Test Prod 6", "camera", "testInfo 6", 200, 6, null, true),
                new ProductBean(IDUtil.generateId() + "7", "Test Prod 7", "tv", "testInfo 7", 300, 7, null, true),
                new ProductBean(IDUtil.generateId() + "8", "Test Prod 8", "tv", "testInfo 8", 400, 8, null, true),
                new ProductBean(IDUtil.generateId() + "5", "Test Prod 5", "tv", "testInfo 5", 500, 10, null)
        );

        orders = Arrays.asList(
                new OrderBean("1", products.get(0).getProdId(), 1, 100.0),
                new OrderBean("2", products.get(0).getProdId(), 4, 100.0),
                new OrderBean("3", products.get(1).getProdId(), 2, 200.0),
                new OrderBean("4", products.get(2).getProdId(), 3, 300.0),
                new OrderBean("5", products.get(3).getProdId(), 4, 400.0)
        );
        for (ProductBean product : products) {
            productService.addProduct(product);
        }

        for (OrderBean order : orders) {
            orderService.addOrder(order);
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
    void getLowStockProduct_ShouldReturnProductsLowInStock() {
        List<ProductBean> result;
        List<ProductBean> expectedProducts = products.stream()
                .filter(product -> product.getProdQuantity() <= 3)
                .collect(Collectors.toList());

        result = productService.getLowStockProduct();

        assertFalse(result.isEmpty());
        for (ProductBean product : expectedProducts) {
            assertTrue(result.contains(product));
        }
    }

    @Test
    void getLowStockProduct_ShouldReturnProductsLowInStockUsingSalesData() {
        List<ProductBean> result;
        ProductBean expectedProduct = products.stream()
                .filter(product -> product.getProdQuantity() > 3)
                .findFirst()
                .orElse(null);
        generateOrder();

        result = productService.getLowStockProduct();

        assertFalse(result.isEmpty());
        assertTrue(result.contains(expectedProduct));
    }

    @Test
    void testSortProductsBySalesAsc() {
        List<ProductBean> result = productService.sortProductsBySales(products, "ASC");
        Map<String, Integer> orderedMap = new HashMap<>();

        for (OrderBean order : orders) {
            orderedMap.put(order.getProductId(), orderedMap.getOrDefault(order.getProductId(), 0) + order.getQuantity());
        }

        products.sort(Comparator.comparingInt(p -> orderedMap.getOrDefault(p.getProdId(), 0)));
        assertNotNull(result);
        assertEquals(result, products);
    }

    @Test
    void testSortProductsBySalesDesc() {
        List<ProductBean> result = productService.sortProductsBySales(products, "DESC");
        Map<String, Integer> orderedMap = new HashMap<>();

        for (OrderBean order : orders) {
            orderedMap.put(order.getProductId(), orderedMap.getOrDefault(order.getProductId(), 0) + order.getQuantity());
        }

        products.sort((p1, p2) -> orderedMap.get(p2.getProdId()) - orderedMap.getOrDefault(p1.getProdId(), 0));
        assertNotNull(result);
        assertEquals(result, products);
    }

    @Test
    void testSortProductsBySalesInvalidOrder() {
        assertThrows(IllegalArgumentException.class, () -> productService.sortProductsBySales(products, "invalid"));
    }

    @Test
    void testSortProductsBySalesEmptyProduct() {
        products = Collections.emptyList();
        List<ProductBean> result = productService.sortProductsBySales(products, "ASC");
        assertEquals(0, result.size());
    }

    @Test
    void getAllUsedProducts_ShouldReturnAllUsedProductsUsingIsUsedField() {
        List<ProductBean> result;
        result = productService.getAllUsedProducts();

        assertTrue(result.stream().allMatch(ProductBean::getIsUsed));
    }

    /**
     * Generate test orders
     */
    private void generateOrder() {
        OrderService orderService = new OrderServiceImpl();

        TransactionBean transaction = new TransactionBean(
                "guest@gmail.com",
                Timestamp.valueOf(LocalDate.now().minusMonths(1).atStartOfDay()),
                100
        );
        String transactionId = transaction.getTransactionId();

        ProductBean product = products.stream()
                .filter(p -> p.getProdQuantity() > 3)
                .findFirst()
                .orElse(null);

        int quantity = product.getProdQuantity() + 1;
        double amount = new ProductServiceImpl().getProductPrice(product.getProdId()) * quantity;
        OrderBean order = new OrderBean(transactionId, product.getProdId(), quantity, amount);
        boolean ordered = orderService.addOrder(order);
        if (ordered) {
            new OrderServiceImpl().addTransaction(transaction);
        }
    }
}