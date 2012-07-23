<%@ include file="/WEB-INF/jsp/include/taglibs.jsp" %>
<html>
  <head>
    <title>TaskEdit - Manage Children</title>
    <link rel="stylesheet" href="css/generic.css" type="text/css"/>
  </head>
  <body>
    <form:form commandName="taskChildDTO" method="POST" action="task_children.do">
      <form:errors path="*"/>
      <form:hidden path="task.id"/>
      <form:hidden path="selectedGroupName"/>
      <table>
        <tr>
          <td align="right" valign="top">Children:</td>
          <td>
            <form:checkboxes path="childTaskIds" items="${childTasks}" itemValue="id" itemLabel="name" delimiter="<br/>"/>
          </td>
        </tr>
        <tr>
          <td align="right" valign="top">Available:</td>
          <td>
            <form:checkboxes path="candidateChildTaskIds" items="${candidateChildTasks}" itemValue="id" itemLabel="name" delimiter="<br/>"/>
          </td>
        </tr>
        <tr>
          <td/>
          <td align="right">
            <input type="submit" value="Update" name="update"/>
            <input type="submit" value="Cancel" name="cancel"/>
          </td>
        </tr>
      </table>
    </form:form>
  </body>
</html>