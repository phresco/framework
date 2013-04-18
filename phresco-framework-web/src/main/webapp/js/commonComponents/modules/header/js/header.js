define(["framework/widgetWithTemplate", "header/listener/headerListener"] , function(template) {

	Clazz.createPackage("com.commonComponents.modules.header");

	Clazz.com.commonComponents.modules.header.js.Header = Clazz.extend(Clazz.WidgetWithTemplate, {
		headerEvent : null,
		
		// template URL, used to indicate where to get the template
		templateUrl: commonVariables.contexturl + "/js/commonComponents/modules/header/template/header.tmp",
		configUrl: "../../js/commonComponents/modules/header/config/config.json",
		name : "header",
		//Events, to fire a function
		onButtonClick: null,
		
		initialize : function(globalConfig){
			var self = this;
			self.globalConfig = globalConfig;
			self.headerListener = new Clazz.com.commonComponents.modules.header.js.listener.HeaderListener(); 
			self.registerEvents(self.headerListener);
		},
		
		loadPage :function(){
			var self = this;
		},
		
		/***
		 * 
		 */
		postRender : function(element) {
		},
		
		/***
         * Called once to register all the events 
         *
         * @facetsListener: HeaderListener methods getting registered
         */
        registerEvents : function (headerListener) {
            var self = this;
        },
		
		/***
		 * Bind the action listeners. The bindUI() is called automatically after the render is complete 
		 */
		bindUI : function(){
		}
	});

	return Clazz.com.commonComponents.modules.header.js.Header;
});