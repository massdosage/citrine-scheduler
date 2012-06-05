<%@ include file="/WEB-INF/jsp/include/taglibs.jsp" %>
<html>
  <head>
    <title>Log File</title>
    <script src="javascript/generic.js" language="JavaScript"></script>
    <link rel="stylesheet" href="css/generic.css" type="text/css"/>
    <meta http-equiv="refresh" content="30">
  </head>
  <body>
    <h2>Log File</h2>
    <pre>
${contents}  
    </pre>
    <c:choose>
      <c:when test='${taskId != null}'> 
        <a href="task_runs.do?taskId=${taskId}&selectedGroupName=${selectedGroupName}">&lt; Back</a>
      </c:when>
    <c:otherwise> <a href="logs.do">&lt; Back</a> </c:otherwise>
    </c:choose>
     
    </body>
</html>