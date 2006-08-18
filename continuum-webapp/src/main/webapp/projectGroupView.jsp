<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="/tld/extremecomponents" prefix="ec" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="continuum" prefix="c1" %>
<html>
  <ww:i18n name="localization.Continuum">
    <head>
        <title><ww:text name="projectView.page.title"/></title>
    </head>
    <body>
      <div id="h3">
        <div>
          <p style="border-top: 1px solid transparent; border-bottom: 1px solid #DFDEDE;">
            <ww:url id="projectGroupViewUrl" action="projectGroupView">
              <ww:param name="projectGroupId" value="projectGroupId"/>
            </ww:url>
            <ww:url id="projectGroupBuildDefinitionUrl" action="projectGroupBuildDefinition">
              <ww:param name="projectGroupId" value="projectGroupId"/>
            </ww:url>
            <ww:url id="projectGroupNotifierUrl" action="projectGroupNotifier">
              <ww:param name="projectGroupId" value="projectGroupId"/>
            </ww:url>

            <b style="border: 1px solid #DFDEDE; padding-left: 1em; padding-right: 1em;">Info</b>
            <ww:a cssStyle="border: 1px solid #DFDEDE; padding-left: 1em; padding-right: 1em; text-decoration: none;" href="%{projectGroupBuildDefinitionUrl}">Build Definitions</ww:a>
            <ww:a cssStyle="border: 1px solid #DFDEDE; padding-left: 1em; padding-right: 1em; text-decoration: none;" href="%{projectGroupNotifierUrl}">Notifiers</ww:a>
          </p>
        </div>
        <h3>Project Group Information</h3>
            
        <div class="axial">
          <table border="1" cellspacing="2" cellpadding="3" width="100%">
            <c1:data label="%{getText('projectView.project.name')}" name="projectGroup.name"/>
            <c1:data label="Group Id" name="projectGroup.groupId"/>
            <c1:data label="Description" name="projectGroup.description"/>
           </table>
        </div>

        <h3>Project Group Actions</h3>

        <div class="functnbar3">
          <!-- switch these to action urls -->
          <ww:form action="buildProjectGroup" method="post">
            <input type="hidden" name="projectGroupId" value="<ww:property value="projectGroupId"/>"/>
            <ww:submit value="Build"/>
          </ww:form>
          <ww:form action="removeProjectGroup" method="post">
            <input type="hidden" name="projectGroupId" value="<ww:property value="projectGroupId"/>"/>
            <ww:submit value="Remove"/>
          </ww:form>
        </div>

        <h3>Member Projects</h3>

        <ww:action name="projectSummary" executeResult="true" namespace="component">
          <ww:param name="projectGroupId" value="%{projectGroupId}"/>
        </ww:action>

      </div>
    </body>
  </ww:i18n>
</html>