<%--

    Framework Web Archive

    Copyright (C) 1999-2013 Photon Infotech Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<%@ page import="com.photon.phresco.commons.FrameworkConstants"%>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.collections.CollectionUtils"%>
<%@ page import="com.phresco.pom.site.Reports"%>
<%@ page import="com.phresco.pom.site.ReportCategories"%>
<%@ page import="com.photon.phresco.framework.model.Permissions"%>

<script src="js/reader.js" ></script>

<%
	String appId = (String)request.getAttribute(FrameworkConstants.REQ_APP_ID);
    List<Reports> selectedReports = (List<Reports>) request.getAttribute(FrameworkConstants.REQ_SITE_SLECTD_REPORTS);
	String requestIp = (String) request.getAttribute(FrameworkConstants.REQ_REQUEST_IP);
	String showIcons = (String) session.getAttribute(requestIp);
    Permissions permissions = (Permissions) session.getAttribute(FrameworkConstants.SESSION_PERMISSIONS);
    String per_disabledStr = "";
	String per_disabledClass = "btn-primary";
	if (permissions != null && !permissions.canManageMavenReports()) {
		per_disabledStr = "disabled";
		per_disabledClass = "btn-disabled";
	}
%>

<form id="formReportList" class="reportList">
	<div class="operation">
		<!-- Generate Report Button --> 
		 <input type="button" name="generate" id="generate" class="btn" additionalParam="getBasicParams();" value="<s:text name='lbl.site.report.generate'/>"/>
		 <input type="button" class="btn <%= per_disabledClass %>"  <%= per_disabledStr %> id="configurePopup" additionalParam="getBasicParams();" value="<s:text name='lbl.configure'/>"/>        
	</div>
	
	<s:if test="hasActionMessages()">
			<div class="alert alert-success alert-message" id="successmsg" >
				<s:actionmessage />
			</div>
	</s:if>

	<div id="site_report" class="site_report">
		
	</div>
</form>

<script>
	// To enable/disable the Generate button based on the site configured
	<% if (permissions != null && !permissions.canManageMavenReports()) { %>
			toDisableGenerateBtn();
	<% } else {	
			if (CollectionUtils.isEmpty(selectedReports)) {
	%>
				toDisableGenerateBtn();
	<%
			} else {
				for (Reports reports : selectedReports) {
					if (FrameworkConstants.REQ_SITE_SLECTD_REPORTSCATEGORIES.equals(reports.getArtifactId())) {
	%>
						toEnableGenerateBtn();
	<%
					break;
					} else {
	%>
						toDisableGenerateBtn();						
	<%				}
			    }
			}
		}
	%>
	
    $(document).ready(function() {
    	$('#configurePopup').click(function() {
    		hidePopuploadingIcon();
    		yesnoPopup('siteConfigure', '<s:text name="header.site.report.configure"/>', 'createReportConfig', '<s:text name="lbl.btn.ok"/>');
    	});
    	
    	$('#generate').click(function() {
    		progressPopup('generateReport', '<%= appId %>', '<%= FrameworkConstants.REQ_SITE_REPORT %>', '', '', getBasicParams(), '', '', '<%= showIcons %>');
    	});

    	hideLoadingIcon();
    	enableScreen();
    	checkForSiteReport();
    });
    
    
    function toEnableGenerateBtn() {
    	$("#generate").removeAttr("disabled", true);
    	$("#generate").removeClass("btn-disabled").addClass("btn-primary");
    }
    
	function toDisableGenerateBtn() {
		$("#generate").attr("disabled", true);
        $("#generate").removeClass("btn-primary").addClass("btn-disabled");
    }
	
	function checkForSiteReport() {
    	$("#site_report").empty();
    	var params = getBasicParams();
    	loadContent('checkForSiteReport', '', $('#site_report'), params, '', true);
    } 
    
    function popupOnOk(obj) {
    	var okUrl = $(obj).attr("id");
		if (okUrl === "createReportConfig") {
			$('input:checkbox[value="maven-project-info-reports-plugin"]').prop('checked', true);
		    $('input:checkbox[value="index"]').removeAttr('disabled', true);
		    var params = getBasicParams();
			$("#popupPage").modal('hide');//To hide popup
		    loadContent('createReportConfig',$('#formConfigureList'), $('#subcontainer'), params, '', true);
		}
	}
    
   	function popupOnClose(obj) {
   		var closeUrl = $(obj).attr("id");
    	showParentPage();
		if (closeUrl === "generateReport") {
 		    var params = getBasicParams();
		    loadContent('veiwSiteReport', $('#formReportList'), $('#subcontainer'), params, '', true);
		}
	}
</script>