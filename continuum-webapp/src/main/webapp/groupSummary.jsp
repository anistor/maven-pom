<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="/tld/extremecomponents" prefix="ec" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<html>
<ww:i18n name="localization.Continuum">
  <head>
    <title><ww:text name="groups.page.title"/></title>
  </head>

  <body>
  <div id="h3">

    <c:forEach items="${groups}" var="group">

      <ww:set name="group" value="${group}"/> 
      <ww:url id="projectSummaryUrl" action="summary" namespace="/">
        <ww:param name="projectGroupId" value="%{'${group.id}'}"/>
      </ww:url>

      <h3><ww:text name="groups.section.title"/>&nbsp;<ww:a href="%{projectSummaryUrl}">${group.name}</ww:a></h3>

      <ec:table items="group.projects"
                var="project"
                showExports="false"
                showPagination="false"
                showStatusBar="false"
                filterable="false">
        <ec:row highlightRow="true">
          <ec:column property="state" title="&nbsp;" width="1%" cell="org.apache.maven.continuum.web.view.StateCell"/>
          <ec:column property="name" title="summary.projectTable.name" width="48%">
            <a href='<ww:url action="projectView">
              <ww:param name="projectId" value="${project.id}"/>
            </ww:url>'>${project.name}</a>
          </ec:column>
          <ec:column property="version" title="summary.projectTable.version" width="13%"/>
          <ec:column property="buildNumber" title="summary.projectTable.build" width="5%"
                     cell="org.apache.maven.continuum.web.view.BuildCell"/>
          <ec:column property="buildNowAction" title="&nbsp;" width="1%"
                     cell="org.apache.maven.continuum.web.view.BuildNowCell" sortable="false"/>
          <ec:column property="buildHistoryAction" title="&nbsp;" width="1%" sortable="false">
            <c:choose>
              <c:when test="${pageScope.project.latestBuildId > 0}">
                <a href="<ww:url value="/buildResults.action?projectId="/>${pageScope.project.id}&projectName=${pageScope.project.name}"><img
                    src="<ww:url value="/images/buildhistory.gif"/>" alt="Build History" title="Build History"
                    border="0"></a>
              </c:when>
              <c:otherwise>
                <img src="<ww:url value="/images/buildhistory_disabled.gif"/>" alt="Build History" title="Build History"
                     border="0">
              </c:otherwise>
            </c:choose>
          </ec:column>
        </ec:row>
      </ec:table>
      <div class="functnbar3">
        <ww:form action="buildProject.action" method="post">
          <ww:submit value="%{getText('summary.buildAll')}">
            <ww:param name="before">
              <img src="<ww:url value="/images/icon_success_sml.gif"/>" alt="<ww:text name="message.success"/>"
                   title="<ww:text name="message.success"/>"/>
              <ww:property value="${group.numSuccesses}"/>
              <img src="<ww:url value="/images/icon_warning_sml.gif"/>" alt="<ww:text name="message.failed"/>"
                   title="<ww:text name="message.failed"/>"/>
              <ww:property value="${group.numFailures}"/>
              <img src="<ww:url value="/images/icon_error_sml.gif"/>" alt="<ww:text name="message.error"/>"
                   title="<ww:text name="message.error"/>"/>
              <ww:property value="${group.numErrors}"/>
            </ww:param>
          </ww:submit>
        </ww:form>
      </div>
    </c:forEach>


  </div>
  </body>
</ww:i18n>
</html>