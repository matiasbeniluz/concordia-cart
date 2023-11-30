<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ page
        import="com.shashi.service.impl.*, com.shashi.service.*,com.shashi.beans.*,java.util.*,javax.servlet.ServletOutputStream,java.io.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>Update Product</title>
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
    String utype = (String) session.getAttribute("usertype");
    String uname = (String) session.getAttribute("username");
    String pwd = (String) session.getAttribute("password");
    String discountid = request.getParameter("discountid");
    
    DiscountBean discount = new DiscountServiceImpl().getDiscountDetails(discountid);
    
    
    if (discountid == null || discount == null) {
        response.sendRedirect("updateDiscountById.jsp?message=Please Enter a valid discount Id");
        return;
    }
    else if (utype == null || !utype.equals("admin")) {
        response.sendRedirect("login.jsp?message=Access Denied, Login as admin!!");
        return;
    }
    else if (uname == null || pwd == null) {
        response.sendRedirect("login.jsp?message=Session Expired, Login Again!!");
        return;
    }
%>

<jsp:include page="header.jsp"/>

<%
    String message = request.getParameter("message");
%>
<div class="container">
    <div class="row"
         style="margin-top: 5px; margin-left: 2px; margin-right: 2px;">
        <form action="./UpdateDiscountSrv" method="post"
              class="col-md-6 col-md-offset-3"
              style="border: 2px solid black; border-radius: 10px; background-color: #FFE5CC; padding: 10px;">
            <div style="font-weight: bold;" class="text-center">
                <div class="form-group">
                    <h2 style="color: green;">Discount Update Form</h2>
                </div>

                <%
                    if (message != null) {
                %>
                <p style="color: blue;">
                    <%=message%>
                </p>
                <%
                    }
                %>
            </div>
            <div class="row">
                <input type="hidden" name="did" class="form-control"
                       value="<%=discount.getDiscountId()%>" id="discount_id" required>
            </div>
            <div class="row">
                <div class="col-md-6 form-group">
                    <label for="discount_percentage">Percentage</label>
                    <input type="number"
                           placeholder="Enter Percentage" name="percent" class="form-control"
                           value="<%=discount.getDiscountPercentage()%>" id="discount_percentage" required>
                </div>
                <div class="col-md-6 form-group">
                    <label for="discount_name">Name</label>
                    <input type="text"
                           placeholder="Enter Name" name="name" class="form-control"
                           value="<%=discount.getDiscountName()%>" id="discount_name" required>
                </div>
            </div>
            <div class="row">
                <div class="col-md-6 form-group">
                    <label for="start_date">Start Date</label>
                    <input type="date"
                           placeholder="Enter Start Date" name="startDate" class="form-control"
                           value="<%=discount.getStartDate()%>" id="start_date"
                           onchange="updateEndDateMin()"
                           required>
                </div>
                <div class="col-md-6 form-group">
                    <label for="end_date">End Date</label>
                    <input type="date"
                           placeholder="Enter End Date" name="endDate" class="form-control"
                           value="<%=discount.getEndDate()%>" id="end_date">
                </div>
            </div>
            <div class="row text-center">
                <div class="col-md-6" style="margin-bottom: 2px;">
                    <button formaction="manageDiscounts.jsp" class="btn btn-danger">Cancel</button>
                </div>
                <div class="col-md-6">
                    <button type="submit" class="btn btn-success">
                        Update Discount
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<script>
    function updateEndDateMin() {
        const endDate = document.querySelector("#end_date");
        const startDate = document.querySelector("#start_date");

        endDate.min = startDate.value;

        if (endDate.value < startDate.value) {
            endDate.value = startDate.value;
        }
    }

    $(document).ready(function () {
        updateEndDateMin();
    });
</script>

<%@ include file="footer.html" %>
</body>
</html>