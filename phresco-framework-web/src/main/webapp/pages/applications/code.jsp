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

<%@ page import="java.util.List"%>

<%@ page import="org.apache.commons.collections.CollectionUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils"%>

<%@ page import="com.photon.phresco.util.Constants"%>
<%@ page import="com.photon.phresco.commons.FrameworkConstants"%>
<%@ page import="com.photon.phresco.commons.model.ApplicationInfo"%>
<%@ page import="com.photon.phresco.plugins.model.Mojos.Mojo.Configuration.Parameters.Parameter.PossibleValues.Value"%>
<%@ page import="com.photon.phresco.framework.model.Permissions"%>

<%
	ApplicationInfo appInfo = (ApplicationInfo) request.getAttribute(FrameworkConstants.REQ_APP_INFO);
	String appId = (String) appInfo.getId();
	List<Value> validateAgainstValues = (List<Value>) request.getAttribute(FrameworkConstants.REQ_VALIDATE_AGAINST_VALUES);
	List<Value> sourceValues = (List<Value>) request.getAttribute(FrameworkConstants.REQ_SOURCE_VALUES);
	String sonarError = (String) request.getAttribute(FrameworkConstants.REQ_ERROR);
	String clangReport =  (String) request.getAttribute(FrameworkConstants.CLANG_REPORT);
	List<String> projectModules = (List<String>) request.getAttribute(FrameworkConstants.REQ_PROJECT_MODULES);
	String requestIp = (String) request.getAttribute(FrameworkConstants.REQ_REQUEST_IP);
	String showIcons = (String) session.getAttribute(requestIp);
    String iconClass = "";
	if (!Boolean.parseBoolean(showIcons))  {
		iconClass = "hideIcons";
	}
%>  

<form id="code" class="codeList">
	<div class="operation">
		<%
			Permissions permissions = (Permissions) session.getAttribute(FrameworkConstants.SESSION_PERMISSIONS);
			String per_disabledStr = "";
			String per_disabledClass = "btn-primary";
			if (permissions != null && !permissions.canManageCodeValidation()) {
				per_disabledStr = "disabled";
				per_disabledClass = "btn-disabled";
			}
		%>
   		<input type="button" id="codeValidatePopup" class="btn <%= per_disabledClass %>" <%= per_disabledStr %> style=" float: left;" value="<s:text name='lbl.validate'/>" />
		
		<%
			if (CollectionUtils.isNotEmpty(projectModules)) {
		%>
			<strong class="validateType"><s:text name="lbl.module"/></strong>&nbsp;
        	<select id="projectModule">
        		<% for (String projectModule : projectModules) { %>
        			<option value="<%= projectModule %>"><%= projectModule %></option>
        		<% } %>
        	</select>
       	<%
			}
       	%>
		
        <strong id="validateType" class="validateType"><s:text name="lbl.sonar.report"/></strong>&nbsp;
        <select id="validateAgainst" name="validateAgainst">
            <%
                if (CollectionUtils.isNotEmpty(validateAgainstValues) && CollectionUtils.isNotEmpty(sourceValues)) {
			%>
					<optgroup label="<%= validateAgainstValues.get(0).getValue() %>">
			<%
                	for (Value value : sourceValues) {
            %>
						<option value="<%= value.getKey() %>" ><%= value.getValue() %></option>
            <%
                	}
			%>
					</optgroup>
			<%
					if (validateAgainstValues.size() > 1) {
			%>
						<option value="<%= validateAgainstValues.get(1).getKey() %>"><%= validateAgainstValues.get(1).getValue() %></option>
			<%		}
                } else if (CollectionUtils.isNotEmpty(validateAgainstValues)) {
                    for (Value value : validateAgainstValues) {
            %>
            			<option value="<%= value.getKey() %>"><%= value.getValue() %></option>
            <%
                    }
                }
            %>
		</select>
	</div>
	
	<% if (StringUtils.isNotEmpty(sonarError)) { %>
		<div class="alert alert-block sonarWarning">
			<img id="warning_icon" src="images/icons/warning_icon.png" />
            <s:label cssClass="sonarLabelWarn" key="sonar.not.started" />
        </div>
    <% } %>
</form>

<div id="sonar_report" class="sonar_report">

</div>

<script>
$('.control-group').addClass("valReportLbl");
    $(document).ready(function() {
    	showLoadingIcon();
    	
    	$('#codeValidatePopup').click(function() {
    		var params = getBasicParams();
    		params = params.concat("&actionType=");
    		params = params.concat('<%= FrameworkConstants.REQ_CODE %>');
    		loadContent("checkForLock", '', '', params, true, true);
    	});

    	<% if (permissions != null && permissions.canManageCodeValidation()) { %>
	    	// To enable/disable the validate button based on the sonar startup
	    	<% if (StringUtils.isNotEmpty(sonarError)) { %>
		    	$("#codeValidatePopup").removeClass("btn-primary").addClass("btn-disabled"); 
		        hideLoadingIcon();
	    	<% } else { %>
		    	$("#codeValidatePopup").removeClass("btn-disabled").addClass("btn-primary"); 
		        sonarReport();
	    	<% } %>
	    	
	    	// when this is iphone tech there will be a path in clangReport request
	    	<% if (StringUtils.isEmpty(clangReport) && StringUtils.isNotEmpty(sonarError)) { %>
				$("#codeValidatePopup").attr("disabled", true);
	    	<% } %>
    	<% } else { %>
    		sonarReport();
    	<% } %>
    	
		$('#validateAgainst, #projectModule').change(function() {
			sonarReport();
  		});
		
		$(".close").click(function() {
			removeLock('<%= FrameworkConstants.REQ_CODE %>');
		});
    });
    
    function popupOnOk(obj) {
    	var okUrl = $(obj).attr("id");
        var params = getBasicParams();
    	mandatoryValidation(okUrl, $("#generateBuildForm"), '', 'validate-code', 'validate-code', '<%= FrameworkConstants.REQ_CODE %>', '<%= appId %>', '', '<%= showIcons %>');
    }
    
    function sonarReport() {
        $("#sonar_report").empty();
        var reportValue = $('#validateAgainst').val();
        var selectedModule = $('#projectModule').val();
        var params = getBasicParams() + '&';
        params = params.concat("validateAgainst=");
        params = params.concat(reportValue);
        if (selectedModule != undefined && !isBlank(selectedModule)) {
        	params = params.concat("&selectedModule=");
        	params = params.concat(selectedModule);
        }
        loadContent('check', $('#code'), $('#sonar_report'), params, '', true);
    }
    
	function checkObj(obj) {
		if(obj == "null" || obj == undefined) {
			return "";
		} else {
			return obj;
		}
	}
	
	function popupOnClose(obj) {
		removeLock('<%= FrameworkConstants.REQ_CODE %>');
		var closeUrl = $(obj).attr("id");
		var params = getBasicParams();
		params = params.concat("&actionType=");
		params = params.concat(closeUrl);
		loadContent("code", '', $("#subcontainer"), params, '', true);
	}
	
	function successEvent(pageUrl, data) {
		if (pageUrl == "checkForLock") {
			if (!data.locked) {
				validateDynamicParam('showCodeValidatePopup', '<s:text name="popup.hdr.code.validate"/>', 'codeValidate','<s:text name="lbl.btn.ok"/>', '', '<%= Constants.PHASE_VALIDATE_CODE %>');
			} else {
				var warningMsg = '<s:text name="lbl.app.warnin.msg"/> ' + data.lockedBy + ' at ' + data.lockedDate +".";
				showWarningMsg('<s:text name="lbl.app.warnin.title"/>', warningMsg);
			}
		}
	}
</script>