define(["features/listener/featuresListener"], function() {
	Clazz.createPackage("com.components.features.js");

	Clazz.com.components.features.js.Features = Clazz.extend(Clazz.WidgetWithTemplate, {
		// template URL, used to indicate where to get the template
		templateUrl: commonVariables.contexturl + "components/features/template/features.tmp",
		configUrl: "components/projects/config/config.json",
		name : commonVariables.featurelist,
		featuresListener: null,
		onPreviousEvent: null,
		onSearchEvent: null,
		onCancelEvent: null,
		featureRequestBody: {},
		id : null,
		featureUpdatedArray : null,
		header: {
			contentType: null,
			requestMethod: null,
			dataType: null,
			requestPostBody: null,
			webserviceurl: null
		},
	
		/***
		 * Called in initialization time of this class 
		 *
		 * @globalConfig: global configurations for this class
		 */
		initialize : function(globalConfig){
			var self = this;
			if(self.featuresListener == null){
				self.featuresListener = new Clazz.com.components.features.js.listener.FeaturesListener(globalConfig);
			}
			self.registerEvents();
			self.registerHandlebars();
		},
		
		loadPage :function() {
			var self = this;
			Clazz.navigationController.jQueryContainer = commonVariables.contentPlaceholder;
			Clazz.navigationController.push(this, true);
		},
		
		registerEvents : function () {
			var self = this;
			if(self.onSearchEvent == null ){
				self.onSearchEvent = new signals.Signal();				
			}
			self.onSearchEvent.add(self.featuresListener.search, self.featuresListener);
			if(self.onCancelEvent == null ){
				self.onCancelEvent = new signals.Signal();
			}
			self.onCancelEvent.add(self.featuresListener.cancelUpdate, self.featuresListener);
		},

		registerHandlebars : function () {
			var self = this;
			Handlebars.registerHelper('versiondata', function(versions, id) {
				var selectedList = self.featuresListener.featuresAPI.localVal.getSession("selectedFeatures");				
				var fieldset;
				if(versions.length > 0){
					$.each(versions, function(index, value){
						if(JSON.stringify(value.appliesTo) !== "null"){
							$.each(value.appliesTo, function(index, value){
								if(value.required == true){
									fieldset = '<fieldset class="switch switchOn" id="feature_'+ id +'" value="false"><label class="off" name="on_off" value="false"></label><label class="on" name="on_off" value="true"></label></fieldset>';
								} else {							
									fieldset = '<fieldset class="switch switchOff" id="feature_'+ id +'" value="false"><label class="off" name="on_off" value="false"></label><label class="on" name="on_off" value="true"></label></fieldset>';
								}
							});							
						}else {							
							fieldset = '<fieldset class="switch switchOff" id="feature_'+ id +'" value="false"><label class="off" name="on_off" value="false"></label><label class="on" name="on_off" value="true"></label></fieldset>';
						}
					});
				}else {							
					fieldset = '<fieldset class="switch switchOff" id="feature_'+ id +'" value="false"><label class="off" name="on_off" value="false"></label><label class="on" name="on_off" value="true"></label></fieldset>';
				}
				return fieldset;
			});
			
			Handlebars.registerHelper('versionShowHide', function(versions, id) {
				var fieldset;
				if(versions.length > 0){
					$.each(versions, function(index, value){
						if(JSON.stringify(value.appliesTo) !== "null"){
							$.each(value.appliesTo, function(index, value){
								if(value.required == true){
									fieldset = '<div class="flt_right" id="version_'+ id +'" style="display:block;">';
								}else{							
									fieldset = '<div class="flt_right" id="version_'+ id +'" style="display:none;">';
								}
							});
						}		
					});
					return fieldset;
				}
			});
			
			Handlebars.registerHelper('idtrime', function(id) {
				var uniqueid;
				uniqueid = $.trim(id.replace(/ /g,''));
				return uniqueid;
			});			
		},

		/***
		 * Called after the preRender() and bindUI() completes. 
		 * Override and add any preRender functionality here
		 *
		 * @element: Element as the result of the template + data binding
		 */
		postRender : function(element) {
			var self = this;
			self.featuresListener.getFeaturesList(self.featuresListener.getRequestHeader(self.featureRequestBody, "SELECTED"), function(response) {
				var responseData = response.data;
				$.each(response.data, function(index, value){
					$("#feature_"+this.moduleId).addClass("switchOn").removeClass("switchOff");
					$("#version_"+this.moduleId).show();					
				});
				self.selectedCount();
			}); 
		},
		
		
		selectedCount : function(){
			var jsLibCount = $("#jsibrariesContent").find(".switchOn").size(), 
			moduleCount = $("#moduleContent").find(".switchOn").size(),
			componentCount = $("#componentsContent").find(".switchOn").size();
			$(".totalModules").text(moduleCount);
			$(".totalComponent").text(componentCount);
			$(".totalJslibraries").text(jsLibCount);
		},

		preRender: function(whereToRender, renderFunction){
			var self = this, moduleTotalCount;
			var collection = {};
			self.featuresListener.showLoad();
			self.getFeatures(collection, function(responseData){
				renderFunction(responseData, whereToRender);
				self.featuresListener.hideLoad();
				setTimeout(function(){
					$(".ftotal").text(responseData.featureslist.length);
					$(".jstotal").text(responseData.jsibrarielist.length);
					$(".comptotal").text(responseData.componentList.length);			
				},600);
			});
		},

		getFeatures : function(collection, callback){
			var self = this;
			self.featuresListener.getFeaturesList(self.featuresListener.getRequestHeader(self.featureRequestBody, "FEATURE"), function(response) {
				collection.featureslist = response.data;
				var userPermissions = JSON.parse(self.featuresListener.featuresAPI.localVal.getSession('userPermissions'));
				collection.userPermissions = userPermissions;
				self.getLibraries(collection, callback);
			});
		},

		getLibraries : function(collection, callback){
			var self = this;
			self.featuresListener.getFeaturesList(self.featuresListener.getRequestHeader(self.featureRequestBody, "JAVASCRIPT"), function(response) {
				collection.jsibrarielist = response.data;	
				self.getComponents(collection, callback);
			});
		},

		getComponents : function(collection, callback){
			var self = this;
			self.featuresListener.getFeaturesList(self.featuresListener.getRequestHeader(self.featureRequestBody, "COMPONENT"), function(response) {
				collection.componentList = response.data;	
				callback(collection);
			});
		},
		
		getSelectedFeatures : function(collection, callback){
			var self = this;
			self.featuresListener.getFeaturesList(self.featuresListener.getRequestHeader(self.featureRequestBody, "SELECTED"), function(response) {
				collection.selectedlist = response.data;
				callback(collection);
			});
		},
		
		/***
		 * Bind the action listeners. The bindUI() is called automatically after the render is complete 
		 *
		 */
		bindUI : function(){
			var self=this;
			var counter=0;
			$(window).resize(function() {
				$(".dyn_popup").hide();
				var height = $(this).height();
				$('.box_div').height(height - 306);
				self.windowResize();
			  });

			$('.switch').css('background', 'url("themes/default/images/helios/on_off_switch.png")');
			$("label[name=on_off]").click(function() {
				self.bcheck(this);
			});
			 
			$("input[name=on_off]").click(function() {
				var button = $(this).val();
				if(button == 'off'){ $(this).closest('fieldset').css('background-position', 'right'); }
				if(button == 'on'){ $(this).closest('fieldset').css('background-position', 'left'); }	
			});

			self.featuresListener.scrollbarEnable();
			
			$('#module').keyup(function(event) {
				var txtSearch = $('#module').val();
				var divId = "moduleContent";
				self.onSearchEvent.dispatch(txtSearch, divId);
           	});

           	$('#jsibraries').keyup(function(event) {
				var txtSearch = $('#jsibraries').val();
				var divId = "jsibrariesContent";
				self.onSearchEvent.dispatch(txtSearch, divId);
           	});

           	$('#components').keyup(function(event) {
				var txtSearch = $('#components').val();
				var divId = "componentsContent";
				self.onSearchEvent.dispatch(txtSearch, divId);
           	});

           	$('#switchoffbutton').on("click", function(event) {
           		self.showSelected();
           	});
			
			$('label.on').click(function() {
				$(this).parent().next('div').show();	      		
           	});
			
			$('label.off').click(function() {
				$(this).parent().next('div').hide();
				var value = $('#switchoffbutton').parent().attr('class');
				if (value === "switch switchOn") {
					self.showSelected();	      		
				}
           	});
			
           	$('#switchonbutton').on("click", function(event) {
           		$("ul li").show();
           		self.featuresListener.scrollbarUpdate();
           	});

       		$('#cancelUpdateFeature').click(function() {
				self.onCancelEvent.dispatch();
           	});
       		var flag=1,temp1,temp2;
          	$('.featureinfo_img').on("click", function(event) {	
				var descid = $(this).attr("artifactGroupId");
				temp2=descid;
				var currentObj = this;				
				self.featuresListener.getFeaturesList(self.featuresListener.getRequestHeader(self.featureRequestBody, "desc", descid), function(response) {
					var descriptionid = $.trim(descid.replace(/ /g,''));
					var divhtml = '<div id="'+descriptionid+'" class="dyn_popup featureinfo"><h1>Description</h1><a href="#" class="dyn_popup_close">X</a><div class="features_cont"><span><img src="themes/default/images/helios/feature_info_logo.png" width="42" height="42" border="0" alt=""></span><span class="features_desc_content">'+response.data+'</span></div></div>';
					$("#desc").children().remove();
					$("#desc").append(divhtml);
					commonVariables.temp = currentObj;
					commonVariables.openccmini(currentObj,descriptionid);
					 if(flag==1){
						temp1=descid;
						$("#"+descid).show();	
						flag=0;
						self.featuresListener.scrollbarEnable();
					 }
					else{					 
						$("#"+descid).hide();	
						flag=1;
						if(temp1!=temp2)
							$("#"+temp2).show();
					}
				});
           	});
			
			$('#featureUpdate').on("click", function() {
				self.featureUpdatedArray = [];
				$(".switchOn").each(function(index, currentVal) {
					var featureUpdatedata = {};
					if($(currentVal).parent().attr("type") !== undefined){
						featureUpdatedata.name = $(currentVal).parent().attr("type");
						featureUpdatedata.dispName = $(currentVal).parent().attr("dispName");
						featureUpdatedata.packaging = $(currentVal).parent().attr("packaging");
						featureUpdatedata.type = $(currentVal).parent().attr("type");					
						featureUpdatedata.defaultModule = true;
						featureUpdatedata.scope = $(currentVal).parent().children('div.flt_right').children('select.input-mini').find(':selected').attr("scope");
						featureUpdatedata.versionID = $(currentVal).parent().children('div.flt_right').children('select.input-mini').find(':selected').val();
						featureUpdatedata.dispValue = $(currentVal).parent().children('div.flt_right').children('select.input-mini').find(':selected').text();
						var moduleId = $(currentVal).parent().children('div.flt_right').children('.moduleId').val();
						featureUpdatedata.moduleId = moduleId; 
						featureUpdatedata.artifactGroupId = moduleId;
						self.featureUpdatedArray.push(featureUpdatedata);
					}
				});				
				
				self.featuresListener.getFeaturesUpdate(self.featuresListener.getRequestHeader(self.featureUpdatedArray, "UPDATE", ""), function(response) {
					self.loadPage();
				}); 
			});
			self.windowResize();


			$('#featureTest').children('tbody').children('tr').children('td').each(function() {
				counter++;
			});
			if(counter==1)
				$('.features_box').parent('td').addClass('onefeature');
			else if(counter==2)
				$('.features_box').parent('td').addClass('twofeatures');
			else if(counter==3)
				$('.features_box').parent('td').addClass('threefeatures');
		},
		
		showSelected : function() {
			var self = this;
			$("#moduleContent li").hide();
			$("#jsibrariesContent li").hide();
			$("#componentsContent li").hide();
			$("ul li fieldset").each(function() {
				if($(this).attr("class") == "switch switchOn"){
					$(this).parent().show();
					self.featuresListener.scrollbarUpdate();
				}     			
			});
		},

		bcheck : function(obj){
			var button = $(obj).attr("value");
			$(obj).closest('fieldset').removeClass('switchOn'); 
			$(obj).closest('fieldset').removeClass('switchOff');			
			if(button == 'false'){ 
				$(obj).closest('fieldset').addClass('switchOff');
				$(obj).closest('fieldset').attr('value', "false"); 
			}else if(button == 'true'){ 
				$(obj).closest('fieldset').addClass('switchOn');
				$(obj).closest('fieldset').attr('value', "true");
			}
		}
	});

	return Clazz.com.components.features.js.Features;
});