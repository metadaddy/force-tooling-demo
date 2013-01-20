<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp"/>
    <div class="row">
        <div class="span8 offset2">
            <c:if test="${error != null}">
                <div class="alert">${error}</div>
            </c:if>

            <form method="POST" action="">
                <div class="btn-group">
                    <input type="submit" value="Save" class="btn btn-primary">
                </div>
                <table class="table table-striped table-condensed">
   		                <tr>
		                    <td><label for="name">Name</label></td>
		                    <td><input id="name"
                                       name="name"
                                       value=""/></td>
		                </tr>		                
                </table>
                <div class="btn-group">
                    <input type="submit" value="Save" class="btn btn-primary">
                </div>
            </form>
        </div>
    </div>
<jsp:include page="footer.jsp"/>