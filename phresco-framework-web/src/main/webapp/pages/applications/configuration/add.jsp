<%--
  ###
  Framework Web Archive
  %%
  Copyright (C) 1999 - 2012 Photon Infotech Inc.
  %%
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  ###
  --%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>

<%@ page import="com.google.gson.Gson"%>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.collections.MapUtils" %>
<%@ page import="org.apache.commons.collections.CollectionUtils"%>

<%@ page import="com.photon.phresco.commons.model.SettingsTemplate"%>
<%@ page import="com.photon.phresco.commons.model.ApplicationInfo"%>
<%@ page import="com.photon.phresco.commons.FrameworkConstants"%>
<%@ page import="com.photon.phresco.configuration.Environment" %>
<%@ page import="com.photon.phresco.configuration.Configuration" %>
<%@ page import="com.photon.phresco.framework.actions.util.FrameworkActionUtil"%>
<%@ page import="com.photon.phresco.framework.api.Project" %>
<%@ page import="com.photon.phresco.framework.model.PropertyInfo"%>
<%@ page import="com.photon.phresco.framework.model.SettingsInfo"%>
<%@ page import="com.photon.phresco.util.Constants"%>

<%
    String selectedStr = "";
	String name = "";
	String description = "";
	String selectedType = "";
	String error = "";
	String envName = "";
	String desc = "";
    boolean isDefault = false;
	
    String currentEnv = (String) request.getAttribute(FrameworkConstants.REQ_CURRENTENV);
    Configuration configInfo = (Configuration) request.getAttribute(FrameworkConstants.REQ_CONFIG_INFO);
	request.setAttribute(FrameworkConstants.REQ_CONFIG_INFO , configInfo);
	
		if (configInfo != null) {
		    envName = configInfo.getEnvName();
			name = configInfo.getName();
			description = configInfo.getDesc();
			selectedType = configInfo.getType();
		}
	
		/* if (request.getAttribute(FrameworkConstants.REQ_FROM_PAGE) != null) {
			fromPage = (String) request.getAttribute(FrameworkConstants.REQ_FROM_PAGE);
		} */
		
	List<Environment> environments = (List<Environment>) request.getAttribute(FrameworkConstants.REQ_ENVIRONMENTS);
	List<SettingsTemplate> settingsTemplates = (List<SettingsTemplate>) request.getAttribute(FrameworkConstants.REQ_SETTINGS_TEMPLATES);
	String fromPage = request.getParameter(FrameworkConstants.REQ_FROM_PAGE);
	
	String title = FrameworkActionUtil.getTitle(FrameworkConstants.CONFIG, fromPage);
	String buttonLbl = FrameworkActionUtil.getButtonLabel(fromPage);
	String pageUrl = FrameworkActionUtil.getPageUrl(FrameworkConstants.CONFIG, fromPage);
	
	Gson gson = new Gson();
	String container = "subcontainer"; //load for configuration
	if (fromPage.contains(FrameworkConstants.REQ_SETTINGS)) {
		container = "container";
%>
<div class="page-header">
    <h1>
       <%= title %> 
    </h1>
</div>
<% } else { %>
	<h4>
		<%= title %> 
	</h4>
<% } %>

<form id="formConfigAdd" class="form-horizontal formClass">
	
	<div class="content_adder">
		<div class="control-group" id="configNameControl">
			<label class="control-label labelbold">
				<span class="mandatory">*</span>&nbsp;<s:text name='lbl.name'/>
			</label>
			<div class="controls">
				<input id="configName" placeholder="<s:text name='place.hldr.config.name'/>" 
					value="<%= name %>" maxlength="30" title="<s:text name='title.30.chars'/>" class="input-xlarge" type="text" name="name">
				<span class="help-inline" id="configNameError"></span>
			</div>
		</div> <!-- Name -->
			
		<div class="control-group">
			<label class="control-label labelbold">
				<s:text name='lbl.desc'/>
			</label>
			<div class="controls">
				<textarea id="configDesc"  placeholder="<s:text name='place.hldr.config.desc'/>" class="input-xlarge"
					maxlength="150" title="<s:text name='title.150.chars'/>" name="desc"><%= description %></textarea>
			</div>
		</div> <!-- Desc -->
		
		<div class="control-group" id="configEnvControl">
			<label class="control-label labelbold">
				<span class="mandatory">*</span>&nbsp;<s:text name='lbl.environment'/>
			</label>	
			<div class="controls">
				<select id="environment" name="environment">
				<% for (Environment env : environments) {
						if (env.getName().equals(envName)) {
							selectedStr = "selected";
							} else {
								selectedStr = "";
							}
				%>
					<option value='<%= gson.toJson(env) %>' <%= selectedStr %>><%= env.getName() %></option>
                <% 		 
                	} 
                %>
				</select>
				<span class="help-inline" id="configEnvError"></span>
			</div>
		</div> <!-- Environment -->
		
		<div class="control-group" id="configTypeControl">
			<label class="control-label labelbold">
				<span class="mandatory">*</span>&nbsp;<s:text name='lbl.type'/>
			</label>	
			<div class="controls">
				<select id="type" name="type">
				<% for (SettingsTemplate settingsTemplate : settingsTemplates) { 
					if(settingsTemplate.getName().equals(selectedType)) {
						selectedStr = "selected";
							} else {
								selectedStr = "";
							}	
				%>	
					<option value='<%= gson.toJson(settingsTemplate) %>' <%= selectedStr %>><%= settingsTemplate.getName() %></option>
                <% } %>
				</select>
				<span class="help-inline" id="configTypeError"></span>
			</div>
		</div> <!-- Type -->
		
		<div id="typeContainer"></div>
	</div>
	
	<div class="bottom_button">
		<input type="button" id="<%= pageUrl %>" class="btn btn-primary" value='<%= buttonLbl %>' />	
		<input type="button" id="downloadCancel" class="btn btn-primary" value="<s:text name='lbl.btn.cancel'/>" />
	</div>
	
	<!-- Hidden Fields -->
    <input type="hidden" name="fromPage" value="<%= fromPage %>"/>
    <input type="hidden" name="oldName" value="<%= name %>"/>
</form>

<script type="text/javascript">

	/* To check whether the device is ipad or not */
	$(document).ready(function() {
		hideLoadingIcon();//To hide the loading icon
		if (!isiPad()) {
			$(".content_adder").scrollbars(); //JQuery scroll bar
		}
	});
	
	$('#type').change(function() {
		var selectedConfigname = $('#configName').val();
		var envData = $.parseJSON($('#environment').val());
		var selectedEnv = envData.name;
		var typeData= $.parseJSON($('#type').val());
		var selectedType = typeData.name;
		var selectedConfigId = typeData.id;
		var fromPage = "<%= fromPage%>";
		var params = '{ ' + getBasicParamsAsJson() + ', "settingTemplate": ' + $('#type').val() + ' , "selectedConfigId": "' + selectedConfigId 
			+ '" , "selectedEnv": "' + selectedEnv + '" , "selectedType": "' + selectedType + '" , "fromPage": "' + fromPage + '" , "selectedConfigname": "' + selectedConfigname + '"}';
		loadJsonContent('configType', params,  $('#typeContainer'));
	}).triggerHandler("change");
	
		$("#" + '<%= pageUrl %>').click(function() {
		var name = $('#configName').val();
		var desc = $('#configDesc').val();
		var env = $('#environment').val();
		var jsonObject = $('#configProperties').toJSON();
		var configStr = JSON.stringify(jsonObject);
		var template = $.parseJSON($('#type').val());
		var type = template.name;
		var configId = template.id;
		var fromPage = "<%= fromPage%>";
		var jsonParam = '{ ' + getBasicParamsAsJson() + ', "configName": "' + name + '", "description": "' + desc + '", "configType": "' + type 
								+ '", "configId": "' + configId + '", "fromPage": "' + fromPage + '" , "environment" : ' + env + ', ' + configStr.substring(1);
		
		
		validateJson('saveConfiguration', '', $('#<%= container %>'), jsonParam);
	});
	
	//To show the validation error messages
	function findError(data) {
	
		if (!isBlank(data.configNameError)) {
			showError($("#configNameControl"), $("#configNameError"), data.configNameError);
		} else {
			hideError($("#configNameControl"), $("#configNameError"));
		}
		
		if (!isBlank(data.dynamicError)) {
	    	var dynamicErrors = data.dynamicError.split(",");
	    	for (var i = 0; i < dynamicErrors.length; i++) {
    		var dynErr = dynamicErrors[i].split(":");
	    		if (!isBlank(dynErr[1])) {
		    		showError($("#" + dynErr[0] + "Control"), $("#" + dynErr[0]+ "Error"), dynErr[1]);
		    	} else {
					hideError($("#" + dynErr[0] + "Control"), $("#" + dynErr[0]+ "Error"));
				}
    		}
		} 
		
		if (!isBlank(data.configEnvError)) {
			showError($("#configEnvControl"), $("#configEnvError"), data.configEnvError);
		} else {
			hideError($("#configEnvControl"), $("#configEnvError"));
		}
		
		if (!isBlank(data.configTypeError)) {
			showError($("#configTypeControl"), $("#configTypeError"), data.configTypeError);
		} else {
			hideError($("#configTypeControl"), $("#configTypeError"));
		} 
	}
	
</script>