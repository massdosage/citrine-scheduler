<%@ include file="/WEB-INF/jsp/include/taglibs.jsp" %>
<html>
  <head>
    <title>Logs</title>
    <script src="javascript/generic.js" language="JavaScript"></script>
    <link rel="stylesheet" href="css/generic.css" type="text/css"/>
    <meta http-equiv="refresh" content="30">
  </head>
  <body>
    <h2>Logs</h2>
    <ul>
    <c:forEach var="logFile" items="${logFiles}">
    <li><a href="logs.do?action=display&logFile=${logFile}">${logFile}</a></li>  
    </c:forEach>
    </ul>
    <a href="index.html">&lt; Back</a> 
    </body>
</html>