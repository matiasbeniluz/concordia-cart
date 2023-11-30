<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ page
        import="com.shashi.service.impl.*, com.shashi.service.*,com.shashi.beans.*,java.util.*,javax.servlet.ServletOutputStream,java.io.*"%>
<!DOCTYPE html>
<html>
<head>
    <title>Product Stocks</title>
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
    String userType = (String) session.getAttribute("usertype");
    String userName = (String) session.getAttribute("username");
    String password = (String) session.getAttribute("password");

    if (userType == null || !userType.equals("admin")) {

        response.sendRedirect("login.jsp?message=Access Denied, Login as admin!!");

    }

    else if (userName == null || password == null) {

        response.sendRedirect("login.jsp?message=Session Expired, Login Again!!");

    }
%>

<jsp:include page="header.jsp" />

<div class="text-center"
     style="color: green; font-size: 24px; font-weight: bold;">Discounts</div>
<div class="container-fluid">
    <div class="table-responsive ">
        <table class="table table-hover table-sm">
            <thead
                    style="background-color: #2c6c4b; color: white; font-size: 18px;">
            <tr>
                <th>DiscountId</th>
                <th>Name</th>
                <th>Percentage</th>
                <th>Start Date</th>
                <th>End Date</th>
                <th colspan="2" style="text-align: center">Actions</th>
            </tr>
            </thead>
            <tbody style="background-color: white; font-size: 16px;">

            <%
                DiscountServiceImpl discountDao = new DiscountServiceImpl();
                List<DiscountBean> discounts = discountDao.getAllDiscounts();

                for (DiscountBean discount : discounts) {
            %>

            <tr>
                <td>
                    <a href="./updateDiscount.jsp?discountid=<%=discount.getDiscountId()%>">
                        <%=discount.getDiscountId()%>
                    </a>
                </td>
                <td><%=discount.getDiscountName()%></td>
                <td><%=discount.getDiscountPercentage()%>%</td>
                <td><%=discount.getStartDate()%></td>
                <td><%=(discount.getEndDate() == null ? "" : discount.getEndDate())%></td>
                <td style="text-align: center">
                    <form method="post">
                        <button type="submit"
                                formaction="updateDiscount.jsp?discountid=<%=discount.getDiscountId()%>"
                                class="btn btn-primary">Update</button>
                    </form>
                </td>
                <td style="text-align: center">
                    <form method="post">
                        <button type="submit"
                                formaction="./RemoveDiscountSrv?discountid=<%=discount.getDiscountId()%>"
                                class="btn btn-danger">Remove</button>
                    </form>
                </td>

            </tr>

            <%
                }
            %>
            <%
                if (discounts.isEmpty()) {
            %>
            <tr style="background-color: grey; color: white;">
                <td colspan="7" style="text-align: center;">No Discounts
                    Available</td>

            </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="footer.html"%>
</body>
</html>