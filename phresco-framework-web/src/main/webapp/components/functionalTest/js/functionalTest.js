define(["functionalTest/listener/functionalTestListener", "testResult/listener/testResultListener"], function() {
	Clazz.createPackage("com.components.functionalTest.js");

	Clazz.com.components.functionalTest.js.FunctionalTest = Clazz.extend(Clazz.WidgetWithTemplate, {
		
		// template URL, used to indicate where to get the template
		templateUrl: commonVariables.contexturl + "components/functionalTest/template/functionalTest.tmp",
		configUrl: "components/functionalTest/config/config.json",
		name : commonVariables.functionalTest,
		functionalTestListener : null,
		testsuiteResult : null,
		iframeResult : null,
		testResultListener : null,
		onDynamicPageEvent : null,
		onPerformActionEvent : null,
		onStopHubEvent : null,
		onStopNodeEvent : null,
		onStopAppiumEvent : null,
		validation : null,
		iframeAction : null,
		iframeUrlAlive : null,
		
		/***
		 * Called in initialization time of this class 
		 *
		 * @globalConfig: global configurations for this class
		 */
		initialize : function(globalConfig) {
			var self = this;
			commonVariables.testType = commonVariables.functional;
			if (self.functionalTestListener === null ) {
				self.functionalTestListener = new Clazz.com.components.functionalTest.js.listener.FunctionalTestListener();
			}
			if (self.testResultListener === null) {
				self.testResultListener = new Clazz.com.components.testResult.js.listener.TestResultListener();
			}
			
			if (self.onDynamicPageEvent === null) {
				self.onDynamicPageEvent = new signals.Signal();
			}
			self.onDynamicPageEvent.add(self.functionalTestListener.getDynamicParams, self.functionalTestListener);
			
			if (self.onPerformActionEvent === null) {
				self.onPerformActionEvent = new signals.Signal();
			}
			self.onPerformActionEvent.add(self.functionalTestListener.performAction, self.functionalTestListener);
			
			if (self.validation === null) {
				self.validation = new signals.Signal();
			}
			self.validation.add(self.testResultListener.mandatoryValidation, self.testResultListener);
			
			self.registerEvents();
		},
		
		registerEvents : function() {
			var self = this;
			// To enable/disable the test button when the functional framework is grid/appium and based on the hub/appium status
			Handlebars.registerHelper('enableDisable', function(functionalFramework, hubStatus, appiumStatus) {
				if (functionalFramework === "grid") {
					if (hubStatus) {
						return "";
					} else {
						return "disabled";
					}
				} else if (functionalFramework === "appium") {
					if (appiumStatus) {
						return "";
					} else {
						return "disabled";
					}
				} else {
					return "";
				}
			});
			
			//To show the start node and stat hub button only when the functional framework is grid
			Handlebars.registerHelper('showHide', function(functionalFramework) {
				if (functionalFramework === "grid") {
					return "";
				} else {
					return "hideContent";
				}
			});
			
			//To show the Start Appium button only when the functional framework is appium
			Handlebars.registerHelper('showHideAppium', function(functionalFramework) {
				if (functionalFramework === "appium") {
					return "";
				} else {
					return "hideContent";
				}
			});

			//To show the start node and stat hub button only when the functional framework is grid
			Handlebars.registerHelper('hubButton', function(hubStatus) {
				if (hubStatus) {
					return 'value="Stop Hub" id="stopHub"';
				} else {
					return 'value="Start Hub" id="startHub"';
				}
			});
			
			//To show the start node and stat hub button only when the functional framework is grid
			Handlebars.registerHelper('nodeButton', function(nodeStatus) {
				if (nodeStatus) {
					return 'value="Stop Node" id="stopNode"';
				} else {
					return 'value="Start Node" id="startNode"';
				}
			});

			//To show the Start Appium button only when the functional framework is appium
			Handlebars.registerHelper('appiumButton', function(appiumStatus) {
				if (appiumStatus) {
					return 'value="Stop Appium" id="stopAppium"';
				} else {
					return 'value="Start Appium" id="startAppium"';
				}
			});

			Handlebars.registerHelper('customIframeUrl', function(url) {
				return url + '&from=phresco';
			});


			Handlebars.registerHelper('iframeExistsCheck', function(key) {
				if (key === undefined) {
					return "false";
				} else {
					return "true";	
				}
			});
			
			Handlebars.registerHelper('displayProperty', function(iframeKey) {
				return (iframeKey !== undefined) ?  'display:block;' : 'display:none;';
			});

			Handlebars.registerHelper('iframeCss', function(iframeKey) {
				var width = "";
				width = (iframeKey !== undefined) ?  'width:236px;' : '';
				return width;
			});
			
			Handlebars.registerHelper('testResult', function(showDevice, testResult, firstValue, id) {
				var returnVal = "";
				if (showDevice && testResult !== undefined && testResult.length > 0 && !$.isEmptyObject(testResult)) {
					if (firstValue && id) {
						returnVal = testResult[0].split("#SEP#")[0];
					} else if (firstValue && !id) {
						returnVal = testResult[0].split("#SEP#")[1];
					} else {
						$.each(testResult, function(i, value){
							returnVal += '<li class="devicesOption"><a href="javascript:void(0)" name="testResult" deviceId="'+ value.split("#SEP#")[0] +'">'+ value.split("#SEP#")[0] +'</a></li>';
						});
						returnVal += '<li class="devicesOption"><a href="javascript:void(0)" name="testResult" deviceId="">All</a></li>';
					} 
				} 
				
				return returnVal;
			});
			
			//To show/hide device dropdown
			Handlebars.registerHelper('showDeviceDropDown', function(showDevice, testResult, options) {
				var returnVal = "";
				if (showDevice && testResult.length > 0) {
					return options.fn(this);
				} else {
					return options.inverse(this);
				} 
			});
		},
		
		/***
		 * Called in once the login is success
		 *
		 */
		loadPage : function(){
			Clazz.navigationController.push(this);
		},
		
		/***
		 * Called after the preRender() and bindUI() completes. 
		 * Override and add any preRender functionality here
		 *
		 * @element: Element as the result of the template + data binding
		 */
		postRender : function(element) {
			var self = this;
			commonVariables.navListener.getMyObj(commonVariables.testsuiteResult, function(retVal){
				self.testsuiteResult = retVal;
				Clazz.navigationController.jQueryContainer = '#testResult';
				Clazz.navigationController.push(self.testsuiteResult, false);
			});
		
			$("#formid").submit();
			 commonVariables.currentDevice = $('li a[name="testResult"]').attr("deviceid");
		},
		
		preRender: function(whereToRender, renderFunction) {
			var self = this;
			self.functionalTestListener.getFunctionalTestOptions(self.functionalTestListener.getActionHeader(self.projectRequestBody, "getFunctionalTestOptions"), function(response) {
				var responseData = response.data;
				var functionalTestOptions = {};
				functionalTestOptions.functionalFramework = responseData.functionalFramework;
				functionalTestOptions.hubStatus = responseData.hubStatus;
				functionalTestOptions.nodeStatus = responseData.nodeStatus;
				functionalTestOptions.appiumStatus = responseData.appiumStatus;
				functionalTestOptions.iframeUrlAlive = responseData.iframeUrlAlive;
				functionalTestOptions.iframeAction = responseData.iframeUrl;
				functionalTestOptions.testAgainsts = responseData.testAgainsts;
			    functionalTestOptions.resultAvailable = responseData.resultAvailable;
			    functionalTestOptions.showDevice = responseData.showDevice;
			    functionalTestOptions.testResultFiles = responseData.testResultFiles;
				functionalTestOptions.testResult = responseData.testResult;
				var userPermissions = JSON.parse(commonVariables.api.localVal.getSession('userPermissions'));
				functionalTestOptions.userPermissions = userPermissions;
				renderFunction(functionalTestOptions, whereToRender);
			});
		},
		
		/***
		 * Bind the action listeners. The bindUI() is called automatically after the render is complete 
		 *
		 */
		bindUI : function() {
			var self = this;
			$(".tooltiptop").tooltip();

			self.testResultListener.resizeTestResultDiv();
			$("#functionalTestBtn").unbind("click");
			$("#functionalTestBtn").click(function() {
				var openccObj = this;
				self.checkForLock("functional", '', '', function(response){
					if (response.status === "success" && response.responseCode === "PHR10C00002") {
						self.onDynamicPageEvent.dispatch(openccObj, $('#functionalTestDynCtrls'), 'functionalTest_popup', commonVariables.functionalTestGoal);
					} else if (response.status === "success" && response.responseCode === "PHR10C00001") {
						commonVariables.api.showError(self.getLockErrorMsg(response), 'error', true, true);
					}	
				});				
			});
			
			$("#startHub").unbind("click");
			$("#startHub").click(function() {
				self.onDynamicPageEvent.dispatch(this, $('#startHubDynCtrls'), 'startHub_popup', commonVariables.startHubGoal);
			});
			
			$("#stopHub").unbind("click");
			$("#stopHub").click(function() {
				self.onPerformActionEvent.dispatch("stopHub");
			});
			
			$("#startNode").unbind("click");
			$("#startNode").click(function() {
				self.onDynamicPageEvent.dispatch(this, $('#startNodeDynCtrls'), 'startNode_popup', commonVariables.startNodeGoal);
			});
			
			$("#stopNode").unbind("click");
			$("#stopNode").click(function() {
				self.onPerformActionEvent.dispatch("stopNode");
			});

			$("#startAppium").unbind("click");
			$("#startAppium").click(function() {
				self.onDynamicPageEvent.dispatch(this, $('#startAppiumDynCtrls'), 'startAppium_popup', commonVariables.startAppiumGoal);
			});
			
			$("#stopAppium").unbind("click");
			$("#stopAppium").click(function() {
				self.onPerformActionEvent.dispatch("stopAppium");
			});
			
			$("#testSuites").css("display", "none");
			$("#testCases").css("display", "none");
			$("#unitTestTab").css("display", "block");
			$(".unit_view").css("display", "none");
			$("#graphView").css("display", "none");
			
			Clazz.navigationController.mainContainer = commonVariables.contentPlaceholder;
			
			//To open the functional test directory
			$('#openFolder').unbind('click');
			$("#openFolder").click(function() {
				commonVariables.hideloading = true;
				var paramJson = {};
				paramJson.type =  commonVariables.typeFunctionalTest;
				commonVariables.navListener.openFolder(paramJson);
			});
			
			//To copy the path of functional test directory
			$('#copyPath').unbind('click');
			$("#copyPath").click(function() {
				commonVariables.hideloading = true;
				var paramJson = {};
				paramJson.type =  commonVariables.typeFunctionalTest;
				commonVariables.navListener.copyPath(paramJson);
			});
			
			//To run the Functional test
			$("#runFunctionalTest").unbind("click");
			$("#runFunctionalTest").click(function() {
				commonVariables.runType = 'functional';
				$('input[name=kill]').attr('disabled', true);
				self.validation.dispatch("functional-test", "functional-test", $('#functionalTestForm').serialize(), function (status) {
					if (status) {
						self.onPerformActionEvent.dispatch("runFunctionalTest", function() {
							commonVariables.logContent = $('#testConsole').html();
							$('#testResult').empty();
							Clazz.navigationController.jQueryContainer = '#testResult';
							Clazz.navigationController.push(self.testsuiteResult, false);
						});
					}
				});
			});
			
			//To start the Hub
			$("#executeStartHub").unbind("click");
			$("#executeStartHub").click(function() {
				commonVariables.runType = 'startHub';
				$('input[name=kill]').attr('disabled', true);
				self.onPerformActionEvent.dispatch("startHub");
			});
			
			//To start the Node
			$("#executeStartNode").unbind("click");
			$("#executeStartNode").click(function() {
				commonVariables.runType = 'startNode';
				$('input[name=kill]').attr('disabled', true);
				self.onPerformActionEvent.dispatch("startNode");
			});

			//To start the Appium Server
			$("#executeStartAppium").unbind("click");
			$("#executeStartAppium").click(function() {
				commonVariables.runType = 'startAppium';
				$('input[name=kill]').attr('disabled', true);
				self.onPerformActionEvent.dispatch("startAppium");
			});
			
			$("#iframe").unbind("click");
			$("#iframe").click(function() {
				$("iframeContent").show();

			});
			
			$('li a[name="testResult"]').unbind("click");
			$('li a[name="testResult"]').click(function() {
			   var previousDevice =  $("#deviceDropDown").attr("value");
			   commonVariables.currentDevice = $(this).attr("deviceid");
			if (previousDevice !== commonVariables.currentDevice) {
				$("#deviceDropDown").html($(this).text()  + '<b class="caret"></b>');
				$("#deviceDropDown").attr("value", commonVariables.currentDevice);
			  }
			  commonVariables.navListener.getMyObj(commonVariables.testsuiteResult, function(retVal) {
				self.testsuiteResult = retVal;
				Clazz.navigationController.jQueryContainer = $(commonVariables.contentPlaceholder).find('#testResult');
				Clazz.navigationController.push(self.testsuiteResult, false);
			});
			});
			

			//Shows the report view of the test result
			$(".report1").unbind("click");
			$(".report1").click(function() {
				commonVariables.navListener.getMyObj(commonVariables.testsuiteResult, function(retVal){
					commonVariables.reportView = true;
					self.testsuiteResult = retVal;
					Clazz.navigationController.jQueryContainer = '#testResult';
					Clazz.navigationController.push(self.testsuiteResult, false);
				});
			});
			
			//Shows the execute view of the test result
			$(".execute2").unbind("click");
			$(".execute2").click(function() {
				$("#testSuiteTable, #testResult, #graphView, #graphicalView, #tabularView, #reportView").hide();
				if ($('#iframeContent').size() === 1) {
					$('#iframeContent').show();
				} else  {
					$('.urlInactive').show();
				}
				$("#executeView").show();
			});
			
			self.tableScrollbar();
			self.customScroll($(".consolescrolldiv"));
		}
	});

	return Clazz.com.components.functionalTest.js.FunctionalTest;
});