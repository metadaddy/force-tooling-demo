<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp"/>
    <div class="row">
       	<p><a href="../classes">&lt; Class List</a></p>
       	<c:if test="${not empty compilerErrors}">
       		<h3>Compiler Errors:</h3>
            <c:forEach items="${compilerErrors}" var="err">
            		<p>Error in line ${err['line']} : ${err['problem']}</p>
            </c:forEach>
       	</c:if>
       	<c:if test="${not empty errorMsg}">
       		<h3>Errors</h3>
	       	<p>${errorMsg}</p>
       	</c:if>
		<form method="POST" action="">
	        <textarea name="body" rows="25" style="font-family: monospace; width: 800px;">${body}</textarea>
		    <div class="btn-group">
		        <input type="submit" value="Save" class="btn btn-primary">
		    </div>
	    </form>
    </div>
<jsp:include page="footer.jsp"/>