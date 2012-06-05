<%@ include file="/WEB-INF/jsp/include/taglibs.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<html>
  <head>
    <title>Tasks</title>
    <link rel="stylesheet" href="css/generic.css" type="text/css"/>
    <meta http-equiv="refresh" content="30">
  </head>
  <body>
    <h2>Tasks Overview</h2>
    <p>
     | 
    Filter by group:
    <select name="selectedGroupName" onChange="location='tasks.do?selectedGroupName=' + this.options[this.selectedIndex].value;">
      <option value="-" <c:if test="${'-' == selectedGroupName}">selected</c:if>>-</option>
      <c:forEach var="groupName" items="${groupNames}">
        <option value="${groupName}" <c:if test="${groupName == selectedGroupName}">selected</c:if>>${groupName}</option>
      </c:forEach>
    </select> |
    <a href="task_edit.do?selectedGroupName=${selectedGroupName}">Create New Task</a> |
    </p>
    <display:table name="${tasks}" id="task" class="outlined" requestURI="tasks.do" defaultsort="2">
      <display:column title="Name" class="${recentStatus[task.id]}" sortable="true">
        <a href="task_edit.do?id=${task.id}&selectedGroupName=${selectedGroupName}">${task.name}</a>
      </display:column>
      <display:column property="groupName" title="Group" class="${recentStatus[task.id]}" sortable="true"/>
      <display:column property="description" class="${recentStatus[task.id]}"/>
      <display:column property="beanName" class="${recentStatus[task.id]}"/>
      <display:column property="timerSchedule" class="${recentStatus[task.id]}"/>
      <display:column property="enabled" class="${recentStatus[task.id]}"/>
      <display:column title="Task Runs" class="${recentStatus[task.id]}">
        <a href="task_runs.do?action=list&taskId=${task.id}&selectedGroupName=${selectedGroupName}">Task Runs</a>
      </display:column>
      <display:column title="Actions" class="${recentStatus[task.id]}">
        <c:if test="${'running' ne recentStatus[task.id]}">
          <c:if test='${"STARTED" == schedulerStatus}'>
            <a href="tasks.do?action=run&taskId=${task.id}&selectedGroupName=${selectedGroupName}">Start</a>
          </c:if>
        </c:if>
        <a href="tasks.do?action=reset&taskId=${task.id}&selectedGroupName=${selectedGroupName}">Reset</a>
      </display:column>
    </display:table>
    <p><a href="index.html">&lt; Back</a></p>
  </body>
</html>