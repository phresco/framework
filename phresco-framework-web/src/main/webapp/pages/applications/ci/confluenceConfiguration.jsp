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
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.Map"%>
<%@ page import="org.apache.commons.collections.CollectionUtils"%>
<%@	page import="org.apache.commons.lang.StringUtils"%>

<%@ page import="com.photon.phresco.commons.FrameworkConstants" %>
<%@ page import="com.photon.phresco.commons.model.ApplicationInfo"%>
<%@ page import="org.codehaus.jettison.json.JSONArray"%>
<%@ page import="org.codehaus.jettison.json.JSONObject"%>

<%
	JSONArray jsonArray = (JSONArray)request.getAttribute("confluenceConfigurations");
%>
<form id="configureConfluenceForm" name="ciDetails" action="" method="post" class="ci_form form-horizontal">
 <fieldset>
	<div class="baseWrapper">
		<% if (jsonArray != null) {
	 		for (int i = 0; i < jsonArray.length(); i++ ) {
	 			JSONObject json = (JSONObject)jsonArray.get(i);
				%>
				
				<fieldset>
					<div class = "wrapperDiv" style="border-width: 0.01px; border-spacing: 5%;">		
						<div class="control-group">
							<label class="control-label labelbold popupLbl">
								<s:text name='lbl.confluence.url' />
							</label>
							<div class="controls">
								<input type="text" name="confluenceUrl" id="confluenceUrl" class="input-xlarge" value='<%= (String)json.get("url") %>'>
								&nbsp&nbsp<img src = "images/smalldelete.png" class="removeMe">
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label labelbold popupLbl">
								<s:text name='lbl.confluence.username' />
							</label>
							<div class="controls">
								<input type="text" name="confluenceUsername" id="confluenceUsername" class="input-xlarge" value='<%= (String)json.get("username") %>'>
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label labelbold popupLbl">
								<s:text name='lbl.confluence.password' />
							</label>
							<div class="controls">
								<input type="password" name="confluencePassword" id="confluencePassword" class="input-xlarge" value='<%= (String)json.get("password") %>'>
							</div>
						</div>
					</div>
					<br>
					</fieldset>	
			<%	}
			 } else  { 
			 %>
			 <fieldset>
					<div class = "wrapperDiv" style="border-width: 0.01px; border-spacing: 5%;">		
						<div class="control-group">
							<label class="control-label labelbold popupLbl">
								<s:text name='lbl.confluence.url' />
							</label>
							<div class="controls">
								<input type="text" name="confluenceUrl" id="confluenceUrl" class="input-xlarge" value=''>
								&nbsp&nbsp<img src = "images/smalldelete.png" class="removeMe">
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label labelbold popupLbl">
								<s:text name='lbl.confluence.username' />
							</label>
							<div class="controls">
								<input type="text" name="confluenceUsername" id="confluenceUsername" class="input-xlarge" value=''>
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label labelbold popupLbl">
								<s:text name='lbl.confluence.password' />
							</label>
							<div class="controls">
								<input type="password" name="confluencePassword" id="confluencePassword" class="input-xlarge" value=''>
							</div>
						</div>
					</div>	
					</fieldset>
			 <% } %>
		 </div>
		 </fieldset>
	
		 <div class="baseWrapperRes">
		 <fieldset>
				<div class = "wrapperDiv" style="border-width: 0.01px; border-spacing: 5%;">		
					<div class="control-group">
						<label class="control-label labelbold popupLbl">
							<s:text name='lbl.confluence.url' />
						</label>
						<div class="controls">
							<input type="text" name="confluenceUrl" id="confluenceUrl" class="input-xlarge" value=''>
							&nbsp&nbsp<img src = "images/smalldelete.png" class="removeMe">
						</div>
					</div>
					
					<div class="control-group">
						<label class="control-label labelbold popupLbl">
							<s:text name='lbl.confluence.username' />
						</label>
						<div class="controls">
							<input type="text" name="confluenceUsername" id="confluenceUsername" class="input-xlarge" value=''>
						</div>
					</div>
					
					<div class="control-group">
						<label class="control-label labelbold popupLbl">
							<s:text name='lbl.confluence.password' />
						</label>
						<div class="controls">
							<input type="password" name="confluencePassword" id="confluencePassword" class="input-xlarge" value=''>
						</div>
					</div>
				</div>	
				<br>
				</fieldset>
			</div>
			
		<div>
		<label class="control-label labelbold popupLbl">
						
					</label>
					<div class="controls">
					 	<input id="addOneMore" type="button" value="<s:text name="lbl.add.one.more.confluence.configuration"/>" class="btn btn-primary">
					</div>
		</div>
</form>

<script type="text/javascript">
	$(document).ready(function() {
		$('.baseWrapperRes').hide();
		
		hidePopuploadingIcon();
		$('#addOneMore').click(function() {
			var Html = $('.baseWrapperRes').html();
			$('.baseWrapper').append(Html);
		});
		
		$('.removeMe').live('click',function() {
			$(this).closest(".wrapperDiv").parent().remove();
		});
		
	});
	
	function showHideRemoveMe() {
		if ($('.removeMe').size() > 1) {
			$('.removeMe').show();
		} else {
			$('.removeMe').hide();
		}
	}
	
	function constructJsonString() {
		if (validateConfigurations()) {
		$('.baseWrapperRes').remove();
		var properties = [];
		$(".wrapperDiv").each(function() {
			var propertyValues = {};
			var confluenceUrl = $(this).find('input[name=confluenceUrl]').val();
			var confluenceUsername = $(this).find('input[name=confluenceUsername]').val();
			var confluencePassword = $(this).find('input[name=confluencePassword]').val();
			propertyValues.confluenceUrl = confluenceUrl;
			propertyValues.confluenceUsername = confluenceUsername;
			propertyValues.confluencePassword = confluencePassword;
			properties.push(propertyValues);
		});
		var values = '{"values":'+ JSON.stringify(properties) +'}';
		var params = getBasicParams();
		params = params.concat("&values=");
		params = params.concat(values);
		loadContent('saveConfluenceConfiguration', '', $('#subcontainer'), params, true, false);
		} 
	}
	
	function validateConfigurations() {
		var boolValue = true;
		var RegExp = /(ftp|http|https|git):\/\/(\w+:{0,1}\w*@@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@@!\-\/]))?/;
		var obj = $(".baseWrapper").find('.wrapperDiv');
		obj.each(function() { // list of input element
			var url = $(this).find('input[name=confluenceUrl]').val();
			var username = $(this).find('input[name=confluenceUsername]').val();
			var password = $(this).find('input[name=confluencePassword]').val();
			if (!RegExp.test(url)) {
				boolValue = false;
				$('#errMsg').html("Invalid Url");
				hidePopuploadingIcon();
				$(this).find('input[name=confluenceUrl]').focus();
				return false;
			} else if (isBlank(username)) {
				boolValue = false;
				$('#errMsg').html("Username is empty");
				hidePopuploadingIcon();
				$(this).find('input[name=confluenceUsername]').focus();
				return false;
			} else if (isBlank(password)) {
				boolValue = false;
				$('#errMsg').html("Password is empty");
				hidePopuploadingIcon();
				$(this).find('input[name=confluencePassword]').focus();
				return false;
			}
		});
		return boolValue;
	}
	
</script>