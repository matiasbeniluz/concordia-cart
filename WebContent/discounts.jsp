<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.shashi.service.impl.ProductServiceImpl, com.shashi.beans.ProductBean" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Product Discounts</title>
    <!-- Include your CSS and JavaScript dependencies here -->
</head>
<body>
    <div class="container">
        <h1>Product Discounts</h1>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Product Name</th>
                    <th>Original Price</th>
                    <th>Discounted Price</th>
                </tr>
            </thead>
            <tbody>
                <%
                    ProductServiceImpl prodDao = new ProductServiceImpl();
                    List<ProductBean> discountedProducts = prodDao.getDiscountedProducts(); // Modify this method as per your implementation

                    for (ProductBean product : discountedProducts) {
                %>
                <tr>
                    <td><%= product.getProdName() %></td>
                    <td>Rs <%= product.getProdPrice() %></td>
                    <td>Rs <%= product.getDiscountedPrice() %></td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>
    </div>
</body>
</html>
