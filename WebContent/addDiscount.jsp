<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Product</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
    <script
            src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script
            src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="css/changes.css">
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

<jsp:include page="header.jsp"/>

<%
    String message = request.getParameter("message");
%>
<div class="container">
    <div class="row"
         style="margin-top: 5px; margin-left: 2px; margin-right: 2px;">
        <form action="./AddDiscountSrv" method="post"
              enctype="multipart/form-data" class="col-md-6 col-md-offset-3"
              style="border: 2px solid black; border-radius: 10px; background-color: #FFE5CC; padding: 10px;">
            <div style="font-weight: bold;" class="text-center">
                <h2 style="color: green;">Discount Addition Form</h2>
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
                <div class="col-md-6 form-group">
                    <label for="discount_percentage">Percentage</label>
                    <input type="number"
                           placeholder="Enter Percentage" name="discount_percentage" class="form-control"
                           id="discount_percentage" required>
                </div>
            </div>
            <div class="row">
                <div class="col-md-6 form-group">
                    <label for="start_date">Start Date</label>
                    <input type="date"
                           placeholder="Enter Start Date" name="start_date" class="form-control"
                           id="start_date"
                           onchange="updateEndDateMin()"
                           required>
                </div>
                <div class="col-md-6 form-group">
                    <label for="end_date">End Date</label>
                    <input type="date"
                           placeholder="Enter Start Date" name="start_date" class="form-control"
                           id="end_date">
                </div>
            </div>
            <div class="row">
                <div class="col-md-6 text-center" style="margin-bottom: 2px;">
                    <button type="reset" class="btn btn-danger">Reset</button>
                </div>
                <div class="col-md-6 text-center">
                    <button type="submit" class="btn btn-success">Add Discount</button>
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
</script>

<%@ include file="footer.html" %>
</body>
</html>