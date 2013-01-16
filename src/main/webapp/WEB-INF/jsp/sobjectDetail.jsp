<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp"/>
    <div class="row">
        <div class="span8 offset2">
                <table class="table table-bordered table-striped">
                    <thead>
                    <tr>
                        <th>Field Name</th>
                        <th>Field Type</th>
                    </tr>
                    </thead>
                    <tbody>
                    
                    <c:forEach items="${detail.fields}" var="field">
                        <tr>
                            <td>
                            		${field.name}
                            </td>
                            <td>
                            		${field.type}
                            </td>
                        </tr>
                    </c:forEach>
                    
                    </tbody>
                </table>
        </div>
    </div>

<jsp:include page="footer.jsp"/>