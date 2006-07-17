<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="/tld/extremecomponents" prefix="ec" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<html>
  <ww:i18n name="localization.Continuum">
    <head>
        <title><ww:text name="summary.page.title"/></title>
    </head>
    <body>
      <div id="h3">
        <h3><ww:text name="summary.section.title"/></h3>
        <ww:set name="projects" value="projects" scope="request"/>
        <ec:table items="projects"
                  var="project"
                  showExports="false"
                  showPagination="false"
                  showStatusBar="false"
                  filterable="false">
          <ec:row highlightRow="true">
            <ec:column property="state" title="&nbsp;" width="1%" cell="org.apache.maven.continuum.web.view.StateCell"/>
            <ec:column property="name" title="summary.projectTable.name" width="48%">
                <a href="${pageContext.request.contextPath}/projectView.action?projectId=${pageScope.project.id}">${pageScope.project.name}</a>
            </ec:column>
            <ec:column property="version" title="summary.projectTable.version" width="13%"/>
            <ec:column property="buildNumber" title="summary.projectTable.build" width="5%" cell="org.apache.maven.continuum.web.view.BuildCell"/>
            <ec:column property="projectGroupName" title="summary.projectTable.group" width="13%"/>
            <ec:column property="buildNowAction" title="&nbsp;" width="1%" cell="org.apache.maven.continuum.web.view.BuildNowCell" sortable="false"/>
            <ec:column property="buildHistoryAction" title="&nbsp;" width="1%" sortable="false">
              <c:choose>
                <c:when test="${pageScope.project.latestBuildId > 0}">
                  <a href="<ww:url value="/buildResults.action?projectId="/>${pageScope.project.id}&projectName=${pageScope.project.name}"><img src="<ww:url value="/images/buildhistory.gif"/>" alt="Build History" title="Build History" border="0"></a>
                </c:when>
                <c:otherwise>
                  <img src="<ww:url value="/images/buildhistory_disabled.gif"/>" alt="Build History" title="Build History" border="0">
                </c:otherwise>
              </c:choose>
            </ec:column>
            <ec:column property="workingCopyAction" title="&nbsp;" width="1%" sortable="false">
              <c:choose>
                <c:when test="${pageScope.project.state == 10 || pageScope.project.state == 2 || pageScope.project.state == 3 || pageScope.project.state == 4 || pageScope.project.state == 6}">
                  <a href="<ww:url value="/workingCopy.action?projectId="/>${pageScope.project.id}&projectName=${pageScope.project.name}"><img src="<ww:url value="/images/workingcopy.gif"/>" alt="Working Copy" title="Working Copy" border="0"></a>
                </c:when>
                <c:otherwise>
                  <img src="<ww:url value="/images/workingcopy_disabled.gif"/>" alt="Working Copy" title="Working Copy" border="0">
                </c:otherwise>
              </c:choose>
            </ec:column>
            <ec:column property="deleteAction" title="&nbsp;" width="1%" sortable="false">
              <c:choose>
                <c:when test="${pageScope.project.state == 1 || pageScope.project.state == 10 || pageScope.project.state == 2 || pageScope.project.state == 3 || pageScope.project.state == 4}">
                  <a href="${pageContext.request.contextPath}/deleteProject!default.action?projectId=${pageScope.project.id}&projectName=${pageScope.project.name}"><img src="<ww:url value="/images/delete.gif"/>" alt="Delete" title="Delete" border="0"></a>
                </c:when>
                <c:otherwise>
                  <img src="<ww:url value="/images/delete_disabled.gif"/>" alt="Delete" title="Delete" border="0">
                </c:otherwise>
              </c:choose>
            </ec:column>
          </ec:row>
        </ec:table>
        <div class="functnbar3">
          <ww:form action="buildProject.action" method="post">
            <ww:submit value="%{getText('summary.buildAll')}">
              <ww:param name="before">
                <img src="<ww:url value="/images/icon_success_sml.gif"/>" alt="<ww:text name="message.success"/>" title="<ww:text name="message.success"/>"/> <ww:property value="nbSuccesses"/>
                <img src="<ww:url value="/images/icon_warning_sml.gif"/>" alt="<ww:text name="message.failed"/>" title="<ww:text name="message.failed"/>"/> <ww:property value="nbFailures"/>
                <img src="<ww:url value="/images/icon_error_sml.gif"/>" alt="<ww:text name="message.error"/>" title="<ww:text name="message.error"/>"/> <ww:property value="nbErrors"/>
              </ww:param>
            </ww:submit>
          </ww:form>
        </div>
      </div>
    </body>
  </ww:i18n>
</html>