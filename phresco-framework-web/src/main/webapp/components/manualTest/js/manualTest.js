define(["manualTest/listener/manualTestListener", "testResult/listener/testResultListener"], function() {
	Clazz.createPackage("com.components.manualTest.js");

	Clazz.com.components.manualTest.js.ManualTest = Clazz.extend(Clazz.WidgetWithTemplate, {
		
		// template URL, used to indicate where to get the template
		templateUrl: commonVariables.contexturl + "components/manualTest/template/manualTest.tmp",
		configUrl: "components/manualTest/config/config.json",
		name : commonVariables.manualTest,
		manualTestListener : null,
		testsuiteResult : null,
		testResultListener : null,
		onTabularViewEvent : null,
		onGraphicalViewEvent : null,
		onDynamicPageEvent : null,
		onRunManualTestEvent : null,
		addManualTestSuite : null,
		addManualTestcase  : null,
		/***
		 * Called in initialization time of this class 
		 *
		 * @globalConfig: global configurations for this class
		 */
		initialize : function(globalConfig) {
			var self = this;
			commonVariables.testType = commonVariables.manual;
		
			if (self.manualTestListener === null ) {
				self.manualTestListener = new Clazz.com.components.manualTest.js.listener.manualTestListener();
				self.registerEvents(self.manualTestListener);
			}
			
			if (self.testResultListener === null) {
				self.testResultListener = new Clazz.com.components.testResult.js.listener.TestResultListener();
			}
		},

		registerEvents : function(manualTestListener) {
			var self=this;
			self.addManualTestcase = new signals.Signal();
			self.addManualTestcase.add(manualTestListener.addManualTestcase, manualTestListener);
			
			self.addManualTestSuite = new signals.Signal();
			self.addManualTestSuite.add(manualTestListener.addManualTestSuite, manualTestListener);
		},
		
		/***
		 * Called in once the login is success
		 *
		 */
		loadPage : function(needAnimation) {
			Clazz.navigationController.mainContainer = commonVariables.contentPlaceholder;
			Clazz.navigationController.push(this, needAnimation);
		},
		
		/***
		 * Called after the preRender() and bindUI() completes. 
		 * Override and add any preRender functionality here
		 *
		 * @element: Element as the result of the template + data binding
		 */
		postRender : function(element) {
			var self = this;
			commonVariables.navListener.getMyObj(commonVariables.testsuiteResult, function(retVal) {
				self.testsuiteResult = retVal;
				Clazz.navigationController.jQueryContainer = $(commonVariables.contentPlaceholder).find('#testResult');
				Clazz.navigationController.push(self.testsuiteResult, false);
			});
			self.manualTestListener.createUploader();
			
		},
		
		preRender: function(whereToRender, renderFunction) {
			var self = this;
			var data = {};
			var userPermissions = JSON.parse(commonVariables.api.localVal.getSession('userPermissions'));
			data.userPermissions = userPermissions;
			renderFunction(data, whereToRender);
		},
		
		/***
		 * Bind the action listeners. The bindUI() is called automatically after the render is complete 
		 *
		 */
		bindUI : function() {
			var self = this;
			$(".tooltiptop").tooltip();
			self.windowResize();
			//show manual TestSuite popup
			$("#addTestSuite").click(function() {
				self.openccpl(this, 'show_manualTestSuite_popup','');
				$('#testSuiteId').focus();
				var currentTestsuiteName = commonVariables.testSuiteName;
				if (currentTestsuiteName !== null) {
					$('#testSuiteName').attr('value', currentTestsuiteName);
				}
			});
			
			//show manual TestCase popup
			$("#addTestCase").click(function() {
				self.openccpl(this, 'show_manualTestCase_popup','');
				var currentTestsuiteName = commonVariables.testSuiteName;
				$(this).next('div').find("form").find("textarea").val("");
				$("#status").val('');
				$("#featureId").val('');
				$("#testCaseId").val('');
				$("#status").selectpicker('refresh');
//				$('#status').find('option:first').attr('selected', 'selected');
				$('input[name=testSuiteName]').attr('value', currentTestsuiteName);
			});
			
			//show download template popup
			$('#show_downloadTemplate_popup').click(function() {
				self.openccpl(this, 'template_download','');
			});
			
			//show upload template popup
			$('#show_uploadTemplate_popup').click(function() {
				self.openccpl(this, 'template_upload','');
				
			});
			
			//download template
			$('#downloadTemplate').click(function() {
				var format = $('input[name="format"]:checked').val();
				self.manualTestListener.downloadTemplate(format);
			});
			
			//add testsuite 
			$("input[name=saveTestSuite]").click(function() {
				var testSuiteId = $('#testSuiteId').val();
				self.addManualTestSuite.dispatch(testSuiteId);
			});
			
			//add testcases to testsuite
			$("input[name=saveTestCase]").unbind("click");
			$("input[name=saveTestCase]").click(function() {
				var testSuiteName = commonVariables.testSuiteName;
				self.addManualTestcase.dispatch(testSuiteName);
			});
			
			//To open the unit test directory
			$('#openFolder').unbind('click');
			$("#openFolder").click(function() {
				commonVariables.hideloading = true;
				var paramJson = {};
				paramJson.type =  commonVariables.typeManualTest;
				commonVariables.navListener.openFolder(paramJson);
			});
			

			$("input[name='pdfName']").unbind('input');
			$("input[name='pdfName']").bind('input propertychange', function(){
				var str = $(this).val();
				str = str.replace(/[^a-zA-Z 0-9\-\_]+/g, '');
				str = str.replace(/\s+/g, '');
				$(this).val(str);
			});
			
			//To copy the path of unit test directory
			$('#copyPath').unbind('click');
			$("#copyPath").click(function() {
				commonVariables.hideloading = true;
				var paramJson = {};
				paramJson.type =  commonVariables.typeManualTest;
				commonVariables.navListener.copyPath(paramJson);
			});
			
			Clazz.navigationController.mainContainer = commonVariables.contentPlaceholder;
		}
	});

	return Clazz.com.components.manualTest.js.manualTest;
});