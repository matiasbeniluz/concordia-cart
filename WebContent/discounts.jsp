<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page
	import="com.shashi.service.impl.*, com.shashi.service.*,com.shashi.beans.*,java.util.*,javax.servlet.ServletOutputStream,java.io.*,java.time.*"%>
<!DOCTYPE html>
<html>
<head>
<title>Ellison Electronics</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
<link rel="stylesheet" href="css/changes.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
</head>
<body style="background-color: #E6F9E6;">

	<%
	/* Checking the user credentials */
	String userName = (String) session.getAttribute("username");
	String password = (String) session.getAttribute("password");
	String userType = (String) session.getAttribute("usertype");

	boolean isValidUser = true;

	if (userType == null || userName == null || password == null || !userType.equals("customer")) {

		isValidUser = false;
	}

	ProductServiceImpl prodDao = new ProductServiceImpl();
	List<ProductBean> products = new ArrayList<ProductBean>();
	DiscountServiceImpl dsi = new DiscountServiceImpl();
	List<DiscountBean> discounts = new ArrayList<DiscountBean>();

	String search = request.getParameter("search");
	String type = request.getParameter("type");
	String popularity = request.getParameter("popularity");
	String message = "Discount Products";
	if (search != null) {
		products = prodDao.searchAllProducts(search);
		message = "Showing Results for '" + search + "'";
	} else if (type != null) {
		products = prodDao.getAllProductsByType(type);
		message = "Showing Results for '" + type + "'";
	} else {
		products = prodDao.getAllProducts();
	}
	if (products.isEmpty()) {
		message = "No items found for the search '" + (search != null ? search : type) + "'";
		products = prodDao.getAllProducts();
	} else if (popularity != null && !popularity.isEmpty()) {
		products = prodDao.sortProductsBySales(products, popularity);
		if (popularity.equalsIgnoreCase("ASC")) {
			message += " from Least to Most Popular";
		} else {
			message += " from Most to Least Popular";
		}
	}
	%>

	<jsp:include page="header.jsp" />
	<div class="text-center" id="message"
		style="color: black; font-size: 14px; font-weight: bold;"><%=message%></div>
	<div id="order-dropdown" style="text-align: center;">
    	<form id="popularity-form" action="" method="get">
        	<label for="popularity" style="display: inline-block; margin-right: 10px;">Order products by popularity:</label>
			<select id="popularity" name="popularity" style="display: inline-block;" onchange="this.form.submit()">
				<option value="" <%= popularity == null || popularity.isEmpty() ? "selected" : "" %>>None</option>
				<option value="ASC"  <%= popularity != null && popularity.equals("ASC") ? "selected" : "" %>>Ascending</option>
           	 	<option value="DESC" <%= popularity != null && popularity.equals("DESC") ? "selected" : "" %>>Descending</option>
        	</select>
        	<% if(search != null && !search.trim().isEmpty()) { %>
            	<input type="hidden" name="search" value="<%= search %>">
        	<% } %>
        	<% if(type != null && !type.trim().isEmpty()) { %>
            	<input type="hidden" name="type" value="<%= type %>">
        	<% } %>
    	</form>
	</div>
	<!-- Start of Product Items List -->
	<div class="container">
		<div class="row text-center">

			<%
			for (ProductBean product : products) {
				int cartQty = new CartServiceImpl().getCartItemCount(userName, product.getProdId());
				String discountId = product.getProdId() +"_discount";
				int percentage = 50;
				LocalDate startDate = LocalDate.now().plusDays(1);

                double randomDouble = Math.random();
                int min = 0;
                int max = 100;
                int randomNumber = (int) (randomDouble * (max - min + 1)) + min;
                LocalDate endDate = LocalDate.now().plusDays(randomNumber);
                DiscountBean discount = new DiscountBean(discountId, percentage, startDate, endDate);
                discount.setDiscountPercentage(percentage);
                discounts.add(discount);
                product.setDiscountId(discountId);
                product.setDiscount(true,percentage*product.getProdPrice()*0.01);
                dsi.updateDiscountIntoDB(discount);
			%>
			<%

			%>
			<div class="col-sm-4" style='height: 450px;'>
				<div class="thumbnail">
					<img src="./ShowImage?pid=<%=product.getProdId()%>" alt="Product"
						style="height: 150px; max-width: 180px">
					<p class="productname"><%=product.getProdName()%>
					</p>
					<%
					String description = product.getProdInfo();
					description = description.substring(0, Math.min(description.length(), 100));
					%>
					<p class="productinfo"><%=description%>..
					</p>
					<p>
                        Discount ID:
                        <%=product.getDiscountId()%>
                    </p>
					<p class="price">
						On Sale: Rs
						<%=dsi.getDiscountDetails(product.getDiscountId()).discountedPrice(product.getProdPrice())%>
					</p>
					<p class="productname">
                        Original: Rs
                        <%=product.getProdPrice()%>
                    </p>
                    <p>
                        From:
                        <%=dsi.getDiscountDetails(product.getDiscountId()).getStartDate()%>
                         - Until:
                        <%=dsi.getDiscountDetails(product.getDiscountId()).getEndDate()%>
                    </p>
					<form method="post">
						<%
						if (cartQty == 0) {
						%>
						<button type="submit"
							formaction="./AddtoCart?uid=<%=userName%>&pid=<%=product.getProdId()%>&pqty=1"
							class="btn btn-success">Add to Cart</button>
						&nbsp;&nbsp;&nbsp;
						<button type="submit"
							formaction="./AddtoCart?uid=<%=userName%>&pid=<%=product.getProdId()%>&pqty=1"
							class="btn btn-primary">Buy Now</button>
						<%
						} else {
						%>
						<button type="submit"
							formaction="./AddtoCart?uid=<%=userName%>&pid=<%=product.getProdId()%>&pqty=0"
							class="btn btn-danger">Remove From Cart</button>
						&nbsp;&nbsp;&nbsp;
						<button type="submit" formaction="cartDetails.jsp"
							class="btn btn-success">Checkout</button>
						<%
						}
						%>
					</form>
					<br />
				</div>
			</div>

			<%
			}
			%>

		</div>
	</div>
	<!-- ENd of Product Items List -->


	<%@ include file="footer.html"%>

</body>
</html>