define(["framework/widget", "navigation/api/navigationAPI", "dynamicPage/dynamicPage", "projects/addproject", "projects/editproject", "application/application", "features/features", "codequality/codequality", "configuration/configuration", "build/build", "unittest/unittest", "configuration/editConfiguration", "ci/continuousDeliveryConfigure", "ci/continuousDeliveryView", "ci/jobTemplates"], function() {

	Clazz.createPackage("com.components.navigation.js.listener");

	Clazz.com.components.navigation.js.listener.navigationListener = Clazz.extend(Clazz.Widget, {
		navAPI : null,
		localStorageAPI : null,
		loadingScreen : null,
		header : null,
		footer : null,
		projectlist : null,
		addproject : null,
		editApplication : null,
		featurelist : null,
		codequality : null,
		configuration : null, 
		build : null,
		currentTab : null,
		editproject : null,
		unittest : null,
		dynamicpage : null,
		editConfiguration : null,
		jobTemplates : null,
		continuousDeliveryView : null,
		continuousDeliveryConfigure : null,
		
		/***
		 * Called in initialization time of this class 
		 *
		 * @config: configurations for this listener
		 */
		initialize : function(config) {
			var self = this;
			self.navAPI = new Clazz.com.components.navigation.js.api.navigationAPI();
		},
		
		onAddProject : function() {
			var self = this;
			self.getMyObj(commonVariables.addproject);
			Clazz.navigationController.jQueryContainer = commonVariables.contentPlaceholder;
			Clazz.navigationController.push(self.addproject, true);
		},
		
		landingPage : function(currentContent){
			var self = this;
			self.renderHeader();
			if(currentContent == undefined || currentContent == null){
				self.renderContent();
			} else if(currentContent != undefined && currentContent != null && currentContent != "") {
				self.dynamicContent(currentContent);
			}
			self.renderFooter();
		},

		getMyObj : function(keyword) {
			var self=this, retuenObj;

				switch(keyword){
				
					case commonVariables.header :
						
						if(self.header === null)
							self.header = new Clazz.com.commonComponents.modules.header.js.Header();
							
						retuenObj = self.header;
						break;
						
					case commonVariables.footer :
						
						if(self.footer === null)
							self.footer = new Clazz.com.commonComponents.modules.footer.js.Footer();
							
						retuenObj = self.footer;
						break;
						
					case commonVariables.projectlist :
						
						if(self.projectlist === null)
							self.projectlist = new Clazz.com.components.projectlist.js.ProjectList();
							
						retuenObj = self.projectlist;
						break;
						
					case commonVariables.editApplication :
						
						if(self.editApplication === null)
							self.editApplication = new Clazz.com.components.application.js.Application();
							
						retuenObj = self.editApplication;
						break;
						
					case commonVariables.featurelist :
						
						if(self.featurelist === null)
							self.featurelist = new Clazz.com.components.features.js.Features();
							
						retuenObj = self.featurelist;
						break;
						
					case commonVariables.codequality :
						
						if(self.codequality === null)
							self.codequality = new Clazz.com.components.codequality.js.CodeQuality();
							
						retuenObj = self.codequality;
						break;
						
					case commonVariables.configuration :
						
						if(self.configuration === null)
							self.configuration = new Clazz.com.components.configuration.js.Configuration();
							
						retuenObj = self.configuration;
						break;
						
					case commonVariables.build :
						
						if(self.build === null)
							self.build = new Clazz.com.components.build.js.Build();
							
						retuenObj = self.build;
						break;
						
					case commonVariables.addproject :
						
						if(self.addproject === null)
							self.addproject = new Clazz.com.components.projects.js.addProject();
							
						retuenObj = self.addproject;
						break;
					
					case commonVariables.editproject :
						
						if(self.editproject === null)
							self.editproject = new Clazz.com.components.projects.js.EditProject();
							
						retuenObj = self.editproject;
						break;
						
					case commonVariables.unittest :
						
						if(self.unittest === null)
							self.unittest = new Clazz.com.components.unittest.js.UnitTest();
							
						retuenObj = self.unittest;
						break;
					
					case commonVariables.editConfiguration :
						
						if(self.editConfiguration === null)
							self.editConfiguration = new Clazz.com.components.configuration.js.EditConfiguration();
							
						retuenObj = self.editConfiguration;
						break;

					case commonVariables.dynamicPage :
						
					   if(self.dynamicpage === null)
							self.dynamicpage = new Clazz.com.components.dynamicPage.js.DynamicPage();
							
						retuenObj = self.dynamicpage;
						break;

					case commonVariables.jobTemplates :
						if(self.jobTemplates === null)
							self.jobTemplates = new Clazz.com.components.ci.js.JobTemplates();
							
						retuenObj = self.jobTemplates;
						break;

					case commonVariables.continuousDeliveryConfigure :
						if(self.continuousDeliveryConfigure === null)
							self.continuousDeliveryConfigure = new Clazz.com.components.ci.js.ContinuousDeliveryConfigure();
							
						retuenObj = self.continuousDeliveryConfigure;
						break;

					case commonVariables.continuousDeliveryView :
						if(self.continuousDeliveryView === null)
							self.continuousDeliveryView = new Clazz.com.components.ci.js.ContinuousDeliveryView();
							
						retuenObj = self.continuousDeliveryView;
						break;
				}
			
			return retuenObj;
		},
		
		//To show/hide controls based on the component 
		showHideControls : function(keyword) {
			var self = this;
			switch(keyword) {
				case commonVariables.projectlist :
					$("#projectList").show();
					$("#createProject").hide();
					self.applyRBAC(keyword);
					break;
					
				case commonVariables.editApplication :
					$("#applicationedit").show();
			}
		},
		
		//To apply the RBAC to the users
		applyRBAC : function(keyword) {
			var self = this;
			var userPermissions = JSON.parse(self.navAPI.localVal.getSession('userPermissions'));
			switch(keyword) {
				case commonVariables.projectlist :
					if (!userPermissions.importApplication) {
						$("#importApp").prop("disabled", true);
					}
					if (!userPermissions.manageApplication) {
						$("#addproject").prop("disabled", true);
					}
					break;
					
				case commonVariables.configuration :
					if (!userPermissions.manageConfiguration) {
						$("input[name=env_pop]").prop("disabled", true);
					}
					break;
			}
		},
		
		//Handles the open folder action
		openFolder : function(actionBody) {
			var self = this;
			self.navigationAction(self.getActionHeader(actionBody, "openFolder"), function(response) {});
		},
		
		//Handles the copy path action
		copyPath : function(actionBody) {
			var self = this;
			self.navigationAction(self.getActionHeader(actionBody, "copyPath"), function(response) {});
		},
		
		renderHeader : function() {
			var self = this;
			Clazz.navigationController.jQueryContainer = commonVariables.headerPlaceholder;
			self.getMyObj(commonVariables.header);
			self.header.data = JSON.parse(self.navAPI.localVal.getSession('userInfo'));
			Clazz.navigationController.push(self.header, false);
		},
		
		renderContent : function(){
			var self = this;
			Clazz.navigationController.jQueryContainer = commonVariables.contentPlaceholder;
			self.getMyObj(commonVariables.projectlist);
			self.header.headerListener.currentTab = "Projects";
			self.currentTab = commonVariables.projectlist;
			Clazz.navigationController.push(self.projectlist, true);
		},
		
		dynamicContent : function(contentObj){
			var self = this, current;
			Clazz.navigationController.jQueryContainer = commonVariables.contentPlaceholder;
			current = self.getMyObj(contentObj);
			self.currentTab = contentObj;
			Clazz.navigationController.push(current, true);
		},
		
		renderFooter : function(){
			var self = this;
			Clazz.navigationController.jQueryContainer = commonVariables.footerPlaceholder;
			self.getMyObj(commonVariables.footer);
			Clazz.navigationController.push(self.footer, false);
		},
		
		onMytabEvent : function(keyword) {
			var self=this, currentObj;
			if (self.currentTab !== commonVariables.editApplication && keyword === commonVariables.editApplication){
				currentObj = self.getMyObj(commonVariables.editApplication);
			} else if (self.currentTab !== commonVariables.featurelist && keyword === commonVariables.featurelist) {
				currentObj = self.getMyObj(commonVariables.featurelist);
			} else if (self.currentTab !== commonVariables.codequality && keyword === commonVariables.codequality) {
				currentObj = self.getMyObj(commonVariables.codequality);
			} else if (self.currentTab !== commonVariables.configuration && keyword === commonVariables.configuration) {
				currentObj = self.getMyObj(commonVariables.configuration);
			} else if (self.currentTab !== commonVariables.build && keyword === commonVariables.build) {
				currentObj = self.getMyObj(commonVariables.build);
			} else if (self.currentTab !== commonVariables.jobTemplates && keyword === commonVariables.jobTemplates) {
				currentObj = self.getMyObj(commonVariables.jobTemplates);
			} else if (self.currentTab !== commonVariables.continuousDeliveryView && keyword === commonVariables.continuousDeliveryView) {
				currentObj = self.getMyObj(commonVariables.continuousDeliveryView);
			} else if (self.currentTab !== commonVariables.continuousDeliveryConfigure && keyword === commonVariables.continuousDeliveryConfigure) {
				currentObj = self.getMyObj(commonVariables.continuousDeliveryConfigure);
			}

			if(currentObj != undefined && currentObj != null){
				self.currentTab = keyword;
				Clazz.navigationController.jQueryContainer = commonVariables.contentPlaceholder;
				Clazz.navigationController.push(currentObj, true);
			}
		},
		
		onQualitytab : function(keyword) {
			var self=this, currentObj;
			if (keyword === commonVariables.unittest){	
				currentObj = self.getMyObj(commonVariables.unittest);
			}
			
			if(currentObj != undefined && currentObj != null){
				self.currentTab = keyword;
				Clazz.navigationController.jQueryContainer = commonVariables.contentPlaceholder;
				Clazz.navigationController.push(currentObj, true);
			}
		},
		
		/***
		 * provides the request header
		 *
		 * @synonymRequestBody: request body of synonym
		 * @return: returns the contructed header
		 */
		getActionHeader : function(requestBody, action) {
			var self=this, header, data = {}, userId;
			var type = requestBody.type;
			var appDirName = self.navAPI.localVal.getJson("appDirName");
			header = {
				contentType: "application/json",				
				dataType: "json",
				webserviceurl: ''
			}
					
			if (action == "openFolder") {
				header.requestMethod = "GET";
				header.webserviceurl = commonVariables.webserviceurl + commonVariables.openFolderContext + "?type=" + type + "&appDirName=" + appDirName;				
			} else if (action == "copyPath") {
				header.requestMethod = "GET";
				header.webserviceurl = commonVariables.webserviceurl + commonVariables.copyPathContext + "?type=" + type + "&appDirName=" + appDirName;
			}
			return header;
		},
		
		navigationAction : function(header, callback) {
			var self = this;			
			try {
				self.navAPI.donavigation(header,
					function(response) {
						if (response != null ) {
							callback(response);						
						} else {
							callback({ "status" : "service failure"});
						}
					}
				);
			} catch(exception) {
				self.loadingScreen.removeLoading();
			}
		},
	});

	return Clazz.com.components.navigation.js.listener.navigationListener;
});