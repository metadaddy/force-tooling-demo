<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="header.jsp"/>
    <div class="row">
        <div class="span8 offset2">
		    <div class="btn-group">
				<a href="classes/c" class="btn btn-primary">Create</a>
			</div>
            <table class="table table-bordered table-striped">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>&nbsp;</th>
                </tr>
                </thead>
                <tbody>
                
                <c:forEach items="${records}" var="record">
                   <tr>
                       <td>
                       	<a href="classes/${record['Id']}">
                       		${record['Name']}
                       	</a>
                       </td>
                       <td>
                       	<a href="#" onClick="SFDC.deleteApexClass( '${record.metadata.name}', 
                       											   '${record['Id']}', 
                       											   '${record['Name']}')"
                       		class="btn btn-danger btn-mini">Delete</a>
                       </td>
                   </tr>
               </c:forEach>
               
               </tbody>
           </table>
        </div>
    </div>
    <script type="text/javascript">
    var SFDC = {
    		deleteApexClass: function(type, id, name) {
            if (!confirm("Are you sure you want to delete '" +  name + "'?")) {
                return false;
            }

            $.ajax({
                'url': 'classes/'+id,
                'type': 'DELETE',
                'success': function(data, textStatus, jqXHR) {
                    location.href = '/sfdc/classes'
                },
                'error': function(jqXHR, textStatus, errorThrown) {
                    alert('Failed to delete class.');
                }
            })
        }
    };
	</script>
<jsp:include page="footer.jsp"/>