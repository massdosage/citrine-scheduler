<%@ include file="/WEB-INF/jsp/include/taglibs.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<html>
  <head>
    <title>Citrine Admin</title>
    <link rel="stylesheet" href="css/generic.css" type="text/css"/>
    <meta http-equiv="refresh" content="30">
  </head>
  <body>
    <h2>Citrine Admin</h2>
    <p>|Current Scheduler Status: ${schedulerStatus}|
       <c:if test='${"STARTED" == schedulerStatus}'>
         <a href="admin.do?action=prepareForShutdown">Prepare for shutdown</a>|
       </c:if>
    </p>
    <p>Current Running Tasks:</p>
    <display:table name="${current_tasks}" id="task" class="outlined" requestURI="admin.do" defaultsort="2">
      <display:column title="Name" class="running" sortable="true">
        <a href="task_edit.do?id=${task.id}">${task.name}</a>
      </display:column>
      <display:column property="groupName" title="Group" class="outlined" sortable="true"/>
    </display:table>
  <p><a href="index.html">&lt; Back</a></p>
  <hr/>
  Citrine version: ${buildVersion}<br/>
  Citrine build date: ${buildDateTime}<br/>
  </body>
</html>