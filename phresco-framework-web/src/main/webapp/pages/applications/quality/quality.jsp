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
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.IOException"%>

<%@ page import="com.photon.phresco.exception.PhrescoException"%>
<%@ page import="com.photon.phresco.commons.FrameworkConstants" %>
<%@ page import="com.photon.phresco.framework.api.Project" %>
<%@ page import="com.photon.phresco.util.TechnologyTypes"%>
<%@ page import="com.photon.phresco.commons.model.ApplicationInfo"%>
<%@ page import="com.photon.phresco.framework.model.SettingsInfo"%>

<%
    List<SettingsInfo> serverSettings = (List<SettingsInfo>)request.getAttribute(FrameworkConstants.REQ_ENV_SERVER_SETTINGS);
	String testTypeSelected = (String)request.getAttribute(FrameworkConstants.REQ_TEST_TYPE_SELECTED);
   	Project project = (Project)request.getAttribute(FrameworkConstants.REQ_PROJECT);
    ApplicationInfo selectedInfo = null; 
    String projectCode = null;
    String techId = "";
    if (project != null) {
        selectedInfo = project.getApplicationInfo();
        projectCode = selectedInfo.getCode();
        techId = selectedInfo.getTechInfo().getVersion();
    }
%>

<s:if test="hasActionMessages()">
    <div class="alert-message success"  id="successmsg">
        <s:actionmessage />
    </div>
</s:if>

<div id="subTabcontent">
	<div id="navigation">
		<ul>
			<li><a href="#" class="active" name="qualityTab" id="unit"><s:text name="label.unit"/></a></li>
			<li><a href="#" class="inactive" name="qualityTab" id="functional"><s:text name="label.funtional"/></a></li>
			<%
				if (!TechnologyTypes.IPHONES.contains(techId)) {
			%>
					<li><a href="#" class="inactive" name="qualityTab" id="performance"><s:text name="label.performance"/></a></li>
			<%
				}
				if (!(TechnologyTypes.ANDROIDS.contains(techId) || TechnologyTypes.IPHONES.contains(techId))) {
			%>
					<li><a href="#" class="inactive" name="qualityTab" id="load"><s:text name="label.load"/></a></li>
			<%
				}
			%>
		</ul>
	</div>
	<div id="subTabcontainer">

	</div>
</div>

<!-- <div class="popup_div_per" id="generateJmeter">
</div>  -->

<script type="text/javascript">
    $(document).ready(function() {
    	showLoadingIcon();
    	var params = getBasicParams();
    	clickMenu($("a[name='qualityTab']"), $("#subTabcontainer"), '', params);//handles the click event of the quality sub tabs
		loadContent("unit", '', $("#subTabcontainer"), params, '', true);//
		activateMenu($("#unit"));
		
    	//Must be removed
    	hideLoadingIcon();
    	
		var testType = "<%= testTypeSelected%>"
		
		if(testType == "null"){
			testType = "unit";
			$("a[id='unit']").attr("class", "selected");	
		} else {
			$("a[id='" + testType + "']").attr("class", "selected");
		}		

// 		changeTesting(testType);

		$("a[name='quality']").click(function() {
			$("a[name='quality']").attr("class", "unselected");
			$(this).attr("class", "selected");
			var testingType = $(this).attr("id");
			disableScreen();
			showLoadingIcon($("#loadingIconDiv"));
			changeTesting(testingType);
		});

	});

	//This function is to handle the change event for testing
	function changeTesting(testingType, fromPage) {
		$("#subTabcontainer").empty(); 
     	//$("#subTabcontainer").html("<div><img class='popupLoadingIcon' style='display: block'></div>");
     	//getCurrentCSS();
 		var params = "";
    	if (!isBlank($('form').serialize())) {
    		params = $('form').serialize() + "&";
    	}
		params = params.concat("testType=");
		params = params.concat(testingType);
        if (fromPage != undefined) {
            params = params.concat("&fromPage=");
            params = params.concat(fromPage);
        }
		performAction('testType', params, $('#subTabcontainer'));
		//$("#subTabcontainer").css("display","block");
	}
	
    /** This method is to fill data in the appropriate controls **/
    function fillSelectData(type, data) {
    	if(type == "Server") {
    		$('#' + type).val(data);
    	} else {
	    	$('#' + type).find('option').remove();
            for (i in data) {
            	$('#' + type).append($("<option></option>").attr("value", data[i]).text(data[i]));
            }
    	}
    }
</script>