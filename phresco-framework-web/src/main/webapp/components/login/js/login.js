define(["framework/widgetWithTemplate", "login/listener/loginListener"], function() {
	Clazz.createPackage("com.components.login.js");

	Clazz.com.components.login.js.Login = Clazz.extend(Clazz.WidgetWithTemplate, {
		onLoginEvent : null,
		loginListener : null,
		// template URL, used to indicate where to get the template
		templateUrl: commonVariables.contexturl + "components/login/template/login.tmp",
		configUrl: "components/login/config/config.json",
		name : commonVariables.login,
		localConfig: null,

		/***
		 * Called in initialization time of this class 
		 */
		initialize : function(){
			var self = this;
			
			if(self.onLoginEvent === null){
				self.onLoginEvent = new signals.Signal();
			}
			if(self.loginListener === null){
				self.loginListener = new Clazz.com.components.login.js.listener.LoginListener();
			}	
		},

		/***
		 * Called in once the login is success
		 *
		 */
		loadPage : function(){
			$(commonVariables.basePlaceholder).empty();
			Clazz.navigationController.jQueryContainer = commonVariables.basePlaceholder;
			this.onLoginEvent.add(this.loginListener.doLogin, this.loginListener);
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
			
			if(commonVariables.api.localVal.getSession('customerlogo') !== null &&
				commonVariables.api.localVal.getSession('customerlogo') !== ""){
				$('#loginlogo').attr("src", "data:image/png;base64," + self.loginListener.loginAPI.localVal.getSession('customerlogo'));
			} else {
				$('#loginlogo').attr("src", "themes/default/images/helios/logo_login.png");
			}
			
			if(commonVariables.api.localVal.getSession('loggedout') === "true"){
				$(".login_error_msg").text('Logged out');
				$(".login_error_msg").css('color','green');
			}
			commonVariables.api.localVal.deleteSession('loggedout');
			
			if(commonVariables.api.localVal.getSession('statusmsg') !== null){
				if(commonVariables.api.localVal.getSession('statusmsg') === "Customer Context Required"){
					$("#contex_er").show();
				}else{
					$(".login_error_msg").text(self.loginListener.loginAPI.localVal.getSession('statusmsg'));
				}
			}
		},

		/***
		 * Bind the action listeners. The bindUI() is called automatically after the render is complete 
		 *
		 */
		bindUI : function(){
			var self = this;
			
			//Login btn click Event
			$('#login').click(function(){
				self.onLoginEvent.dispatch();
				$(".login_error_msg").text('');
				$(".login_error_msg").css('color','red');
			});
			
			//Enter Key Press Event
			document.onkeydown = function(evt) {
				if(self.loginListener.enterKeyDisable) {
					return true;
				} else {
					evt = evt || window.event;
					if (evt.keyCode === 13) {
						self.onLoginEvent.dispatch();
					}
				}
			};
			
			//Key press Event
			$('#login, #rememberMe').keypress(function(e){
				if(e.keyCode === 13){
					self.onLoginEvent.dispatch();
				}	
			});
			
			// Control validation Event
			/* $('#username').focusout(function(){
				self.loginListener.userNameValidation();
			}); */
			
			// Control validation Event
			/* $('#password').focusout(function(){
				self.loginListener.passwordValidation();
			}); */
			
			//Set rememberMe chk box val
			if(commonVariables.api.localVal.getSession('rememberMe') === "true"){
				$('#rememberMe').prop('checked', true);
				$('#username').val(self.loginListener.loginAPI.localVal.getSession('username'));
				$('#password').val(self.loginListener.loginAPI.localVal.getSession('password'));
			}
			
			$('#username').focus();
			Clazz.navigationController.jQueryContainer = commonVariables.contentPlaceholder;
		}
	});

	return Clazz.com.components.login.js.Login;
});