<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="/tld/extremecomponents" prefix="ec" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ taglib uri="continuum" prefix="c1" %>
<ww:i18n name="localization.Continuum">
  <ec:table items="projects"
            var="project"
            showExports="false"
            showPagination="false"
            showStatusBar="false"
            filterable="false">
    <ec:row highlightRow="true">
      <ec:column property="state" title="&nbsp;" width="1%" cell="org.apache.maven.continuum.web.view.StateCell"/>
      <ec:column property="name" title="summary.projectTable.name" width="48%">
        <a href="<ww:url action="projectView">
                  <ww:param name="projectId" value="${project.id}"/>
                </ww:url>">${project.name}</a>
      </ec:column>
      <ec:column property="version" title="summary.projectTable.version" width="13%"/>
      <ec:column property="buildNumber" title="summary.projectTable.build" width="5%"
                 cell="org.apache.maven.continuum.web.view.BuildCell"/>
      <ec:column property="projectGroupName" title="summary.projectTable.group" width="13%"/>
      <ec:column property="buildNowAction" title="&nbsp;" width="1%"
                 cell="org.apache.maven.continuum.web.view.BuildNowCell" sortable="false"/>
      <ec:column property="buildHistoryAction" title="&nbsp;" width="1%" sortable="false">
        <c:choose>
          <c:when test="${pageScope.project.latestBuildId > 0}">
            <a href='<ww:url action="buildResults">
                      <ww:param name="projectId" value="${project.id}"/>
                    </ww:url>'><img src="<ww:url value='/images/buildhistory.gif'/>" alt="Build History"
                                    title="Build History" border="0"></a>
          </c:when>
          <c:otherwise>
            <img src="<ww:url value='/images/buildhistory_disabled.gif'/>" alt="Build History" title="Build History"
                 border="0">
          </c:otherwise>
        </c:choose>
      </ec:column>
      <ec:column property="workingCopyAction" title="&nbsp;" width="1%" sortable="false">
        <c:choose>
          <c:when
              test="${pageScope.project.state == 10 || pageScope.project.state == 2 || pageScope.project.state == 3 || pageScope.project.state == 4 || pageScope.project.state == 6}">
            <a href='<ww:url action="workingCopy">
                      <ww:param name="projectId" value="${project.id}"/>
                    </ww:url>'><img src="<ww:url value='/images/workingcopy.gif'/>" alt="Working Copy"
                                    title="Working Copy" border="0"></a>
          </c:when>
          <c:otherwise>
            <img src="<ww:url value='/images/workingcopy_disabled.gif'/>" alt="Working Copy" title="Working Copy"
                 border="0">
          </c:otherwise>
        </c:choose>
      </ec:column>
      <ec:column property="deleteAction" title="&nbsp;" width="1%" sortable="false">
        <c:choose>
          <c:when
              test="${pageScope.project.state == 1 || pageScope.project.state == 10 || pageScope.project.state == 2 || pageScope.project.state == 3 || pageScope.project.state == 4}">
            <a href='<ww:url value="/deleteProject!default.action">
                    <ww:param name="projectId" value="${project.id}"/>
                    <ww:param name="projectName" value="${project.name}"/>
                  </ww:url>'>
              <img src="<ww:url value='/images/delete.gif'/>" alt="Delete" title="Delete" border="0">
            </a>
          </c:when>
          <c:otherwise>
            <img src="<ww:url value='/images/delete_disabled.gif'/>" alt="Delete" title="Delete" border="0">
          </c:otherwise>
        </c:choose>
      </ec:column>
    </ec:row>
  </ec:table>
</ww:i18n>