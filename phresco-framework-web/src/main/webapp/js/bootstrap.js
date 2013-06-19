
var commonVariables = {
	globalconfig : "",
	webserviceurl : "rest/api/",
	contexturl : "",
	
	navListener : null,
	appDirName : null,
	goal : null,
	
	header : "header",
	headerContext : "",
	
	footer : "footer",
	footerContext : "",
	
	environmentName : "",
	techId : "",
	
	login : "login",
	loginContext : "login",

	projectlist : "projectlist",
	projectlistContext : "project",

	addproject : "addproject",
	projectContext : "projects",
	editproject : "editproject",
	
	dynamicPage : "dynamicPage",
	dynamicPageContext : "dynamic",
	
	paramaterContext : "parameter",
	dependencyContext : "dependency",	

	featurePage : "features",
	featurePageContext : "features",
	
	editApplication : "appinfo",
	
	featurelist : "featurelist",
	
	codequality : "codequality",
	
	configuration : "configuration",
	editConfiguration : "editConfiguration",
	
	qualityContext : "quality",
	testType : null,
	unit : "unit",
	unitTest : "unitTest",
	component : "component",
	componentTest : "componentTest",
	functional : "functional",
	functionalTest : "functionalTest",
	from : null,
	testSuiteName : null,
	testSuites : null,
	
	testResult : "testResult",

	ci : "ci",
	jobTemplates : "jobTemplates",
	continuousDeliveryView : "continuousDeliveryView",
	continuousDeliveryConfigure : "continuousDeliveryConfigure",
	
	customerTheme : null,
	defaultcustomer : "Photon",
	customerInfoContext : "technology/customerinfo?customerName=",
	
	build : "build",
	
	mavenService : "mavenService",
	mavenServiceContext : "mavenService",
	
	openFolderContext : "util/openFolder",
	copyPathContext : "util/copyPath",
	copyToClipboardContext : "util/copyToClipboard",
	
	/******* Open folder and copy path constants *******/
	typeBuild : "build",
	typeUnitTest : "unitTest",
	typeFunctionalTest : "functionalTest",
	typeManualTest : "manualTest",
	typeLoadTest : "loadTest",
	typePerformanceTest : "performanceTest",
	typeComponentTest : "componentTest",
	
	/******* Technology options constants *******/
	optionsCode : "Code",
	optionsReports : "Reports",
	optionsUnitTest : "Unit_Test",
	optionsComponentTest : "Component_Test",
	optionsFunctionalTest : "Functional_Test",
	optionsPerformanceTest : "Performance_Test",
	optionsLoadTest : "Load_Test",
	optionsManualTest : "Manual_Test",
	optionsCI : "CI",
	optionsRunAgainstSrc : "Run_Against_Source",
	optionsMinification : "Minification",
	optionsBuild : "Build",
	optionsDeploy : "Deploy",
	optionsExeDownload : "Exe_Download",
	optionsFeatureConfig : "Feature_Config",
	optionsComponentConfig : "Component_Config",
	optionsProcessBuild : "Process_Build",
	optionsRemoteDeployment : "Remote_Deployment",
	optionsEmbedApplication : "Embed_Application",
	optionsThemeBuilder : "Theme_Builder",
	
	/******* Phase *********/
	ciPhase : "ci-",

	/******* Goals *********/
	codeValidateGoal : "validate-code",
	packageGoal : "package",
	deployGoal : "deploy",
	unitTestGoal : "unit-test",
	componentTestGoal : "component-test",
	functionalTestGoal : "functional-test",
	performanceTestGoal : "performance-test",
	loadTestGoal : "load-test",
	pdfReportGoal : "pdf-report",

	startHubGoal : "start-hub",
	startNodeGoal : "start-node",
	
	/******* mvn Context *********/
	mvnlogService : "app/readlog",
	mvnBuild : "app/build",
	mvnDeploy : "app/deploy",
	mvnUnitTest : "app/runUnitTest",
	mvnComponentTest : "app/runComponentTest",
	mvnCodeValidation : "app/codeValidate",
	mvnRunagainstSource : "app/runAgainstSource",
	mvnStopServer : "app/stopServer",
	mvnRestartServer : "app/restartServer",
	mvnPerformanceTest : "app/performanceTest",
	mvnLoadTest : "app/loadTest",
	mvnFunctionalTest : "app/runFunctionalTest",
	mvnMinification : "app/minification",
	mvnStartHub : "app/startHub",
	mvnStopHub : "app/stopHub",
	mvnCheckHub : "app/checkForHub",
	mvnShowStartedHub : "app/showStartedHubLog",
	mvnStartNode : "app/startNode",
	mvnStopNode : "app/stopNode",
	mvnCheckNode : "app/checkForNode",
	mvnShowStartedNode : "app/showStartedNodeLog",
	
	/******** mvn Context end****/
	
	edit : "Edit",
	create : "Create",
	deleted : "Delete",
	
	loadingScreen : null,
	
	basePlaceholder : "basepage\\:widget",
	headerPlaceholder : $("<header\\:widget></header\\:widget>"),
	navigationPlaceholder : $("<navigation\\:widget></navigation\\:widget>"),
	contentPlaceholder : $("<content\\:widget></content\\:widget>"),
	footerPlaceholder : $("<footer\\:widget></footer\\:widget>"),
	
	openccmini : function(e,place) {
		var clicked = $(e);
		var target = $("#" + place);
		var twowidth = window.innerWidth/1.5;;

		if (clicked.offset().left < twowidth) {	
			$(target).toggle();
			var t=clicked.offset().top - target.height()/2 + 10;
			var l=clicked.offset().left + clicked.width()+ 15;
			$(target).offset({
				top: t,
				left: l
			});
			
			$(target).addClass('speakstyleleft').removeClass('speakstyleright');
		}
		else {
			$(target).toggle();
			var t=clicked.offset().top - target.height()/2 + 10;
			var l=clicked.offset().left - (target.width()+15);
			$(target).offset({
				top: t,
				left: l
			});
			
			$(target).addClass('speakstyleright').removeClass('speakstyleleft');

		}
		
		$(document).keyup(function(e) {
			if(e.which === 27){
				$("#" + place).hide();
			}
		});
		
		$('.dyn_popup_close').click( function() {
			$("#" + place).hide();
		});
	},
	
	getParameterByName : function (name) {
		name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
		var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),	results = regex.exec(location.search);
		return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
	}
};

$(document).ready(function(){
	$.get($("basepage\\:widget").attr("config"), function(data) {
		commonVariables.globalconfig = data;
		configJson = {
			// comment out the below line for production, this one is so require doesn't cache the result
			urlArgs: "time=" +  (new Date()).getTime(),
			baseUrl: "js",
			
			paths : {
				framework : "framework",
				listener : "commonComponents/listener",
				api : "api",
				lib : "../lib",
				common : "commonComponents/common",
				modules: "commonComponents/modules",
				footer: "commonComponents/modules/footer",
				header: "commonComponents/modules/header",
				Clazz : "framework/class",
				components: "../components"				
			}
		};

		$.each(commonVariables.globalconfig.components, function(index, value){
			configJson.paths[index] = value.path;
		});

		// setup require.js
		requirejs.config(configJson);
		
		require(["framework/class", "framework/widget", "common/loading", "framework/widgetWithTemplate", "framework/navigationController", "login/login"], function () {
		 	Clazz.config = data;
			Clazz.navigationController = new Clazz.NavigationController({
				mainContainer : "basepage\\:widget",
				transitionType : Clazz.config.navigation.transitionType,
				cancelTransitionType : Clazz.config.navigation.cancelTransitionType,
				isNative : Clazz.config.navigation.isNative
			});

			//Apply customer based theme
			if(localStorage.getItem('customertheme') !== null && localStorage.getItem('customertheme') !== ""){
				JSS.css(eval('(' + localStorage.getItem('customertheme') + ')'));
			}
			
			commonVariables.loadingScreen =new Clazz.com.js.widget.common.Loading();
			app.initialize();
		});
	}, "json");
});

 app = {
    initialize: function() {
        this.bind();
    },
    bind: function() {
        //router  
        hasher.initialized.add(this.handleChanges);
        hasher.changed.add(this.handleChanges);
        hasher.init();
        if(!window.location.hash){
        }  
    },
    handleChanges: function(newHash, oldHash){
		var flag=false;

		if(newHash !== undefined && newHash !== null && newHash !==""){
			if(localStorage.getItem("userInfo") === null){
				location.hash = '';
				var loginView = new Clazz.com.components.login.js.Login();
				loginView.loadPage();
			} else {
				var loginlistenerObj = new Clazz.com.components.login.js.listener.LoginListener();
				if(loginlistenerObj !== undefined && loginlistenerObj !== null && loginlistenerObj !== ""){
					loginlistenerObj.pageRefresh(commonVariables.projectlist);
				}
			}
		}else if((newHash === undefined || newHash === null || newHash === "") && 
		(oldHash === undefined || oldHash === null || oldHash === "")){
			var loginView = new Clazz.com.components.login.js.Login();
			loginView.loadPage();
		}
	}
};


