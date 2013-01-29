<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="header.jsp"/>
    <div class="row">
        <div class="span8 offset2">
            <table class="table table-bordered table-striped">
                <thead>
                <tr>
                    <th>Start Time</th>
                    <th>&nbsp;</th>
                </tr>
                </thead>
                <tbody>
                
                <c:forEach items="${records}" var="record">
                   <tr>
                       <td>
                       	<a href="logs/${record['Id']}">
                       		${record['StartTime']}
                       	</a>
                       </td>
                       <td>
                       	<a href="#" onClick="SFDC.deleteApexLog( '${record['Id']}', 
                       											 '${record['StartTime']}')"
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
    		deleteApexLog: function(id, name) {
            if (!confirm("Are you sure you want to delete '" +  name + "'?")) {
                return false;
            }

            $.ajax({
                'url': 'logs/'+id,
                'type': 'DELETE',
                'success': function(data, textStatus, jqXHR) {
                    location.href = '/sfdc/logs'
                },
                'error': function(jqXHR, textStatus, errorThrown) {
                    alert('Failed to delete log.');
                }
            })
        }
    };
	</script>
<jsp:include page="footer.jsp"/>