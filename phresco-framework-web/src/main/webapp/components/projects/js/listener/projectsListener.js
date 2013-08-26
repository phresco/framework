define([], function() {

	Clazz.createPackage("com.components.projects.js.listener");

	Clazz.com.components.projects.js.listener.projectsListener = Clazz.extend(Clazz.WidgetWithTemplate, {
		
		basePlaceholder : commonVariables.basePlaceholder,
		//loadingScreen : null,
		applicationlayerData : null,
		weblayerData : null,
		mobilelayerData : null,
		projectInfo : {},
		customerIds : [],
		appInfos : [],
		appInfosweb : [],
		appInfosmobile : [],
		projectRequestBody : {},
		hasError: false,
		contentContainer : commonVariables.contentPlaceholder,
		projectlistContent : null,
		counter:null,
		appDepsArray : [],
		appCodeArray : [],
		selectedVal : [],
		count : 3,
		
		initialize : function(config) {
			var self = this;
		},
		
		cancelCreateproject : function() {
			var self = this;
			Clazz.navigationController.jQueryContainer = commonVariables.contentPlaceholder;
			commonVariables.navListener.getMyObj(commonVariables.projectlist, function(projectlistObj){
				Clazz.navigationController.push(projectlistObj, true, true);
			});
		},
		
		removelayer : function(object) {
			var self=this;
		 	if(self.counter !== 2){
		 	var layerId = object.attr('id');
		 	/* var findclass = object.closest('tr').next().attr('class'); */
		 		object.closest('tr').next().hide('slow');
		 		object.closest('tr').next().attr('key', 'hidden');
		 		/* $("tr[class="+findclass+"]").each(function() {
		 			$(this).attr('key', 'hidden');
		 			$(this).hide('slow');
		 		}); */
		 		//object.closest('tr').attr('name', layerId);
		 		object.closest('tr').hide('slow');
		 		$("input[name="+layerId+"]").show();
		 		self.counter=self.counter+1;
				if(self.counter === 2) {
					$('img[name="close"]').hide();
				}
		 	}
		},
		
		addlayer : function(object) {
			var self=this;
		 	$('img[name="close"]').show();
			var layerType = object.attr('name');
		 	$("input[name="+layerType+"]").hide();
		 	$("tr[name="+ layerType +"]").show('slow');
			$("tr[name="+ layerType +"]").closest('tr').next().show('slow');
			$("tr[name="+ layerType +"]").closest('tr').next().attr('key', 'displayed');
		 	/* var clasname = $("tr[name="+ layerType +"]").closest('tr').next().attr('class');
		 	$("tr[class="+clasname+"]").each(function() {
		 		$(this).show('slow');
		 		$(this).attr('key', 'displayed');
		 	}); */
		 	self.counter--;
		},
		
		addlayeredit:function(object) {
			var self=this;
		 	var layerType = object.attr('name');
			$("img[id="+layerType+"]").show();
		 	$("input[name="+layerType+"]").hide();
			$("tr[id="+ layerType +"]").show('slow');
			$("tr[id="+ layerType +"]").next().show('slow');
			$("tr[id="+ layerType +"]").next().attr('key', 'displayed');
			var clasname = $("tr[id="+ layerType +"]").closest('tr').next().attr('id');
			$("tr[id="+ layerType +"]").next().find("tbody."+ clasname).children().each(function() {
		 		$(this).show('slow');
		 		$(this).attr('key', 'displayed');
		 	}); 
		 	self.counter--;
		},
		
		removelayeredit : function(object) {
			var self=this;
		 	if(self.counter !== 2){
				var layerId = object.attr('id');
				$("tr[id="+ layerId +"]").hide('slow');
				$("tr[id="+ layerId +"]").next().hide('slow');
				$("tr[id="+ layerId +"]").next().attr('key', 'hidden');
				$("input[name="+layerId+"]").show();
				self.counter=self.counter+1;
				if(self.counter === 2) {
					$('img[name="close"]').hide();
				}
			}	
		},
		
		
		getEditProject : function(header, callback) {
			var self = this;
			try {
				//commonVariables.loadingScreen.showLoading();
				commonVariables.api.ajaxRequest(header,
					function(response) {
						if (response !== null && response.status !== "error" && response.status !== "failure") {
							if(response.responseCode !== null && response.responseCode !== 'PHR200005' && response.responseCode !== 'PHR200001') {
								$(".msgdisplay").removeClass("error").addClass("success");
								$(".success").attr('data-i18n', 'successCodes.' + response.responseCode);
								self.renderlocales(commonVariables.contentPlaceholder);	
								$(".success").show();
								$(".success").fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(5);
							}
							
							if(response.responseCode === "PHR200004" || response.responseCode === "PHR200006"){
								setTimeout(function() {
									$(".success").hide();
									callback(response);
								},1200);
							} else {
								callback(response);
							}	
							//commonVariables.loadingScreen.removeLoading();
						} else {
							$(".msgdisplay").removeClass("success").addClass("error");
							$(".error").attr('data-i18n', 'errorCodes.' + response.responseCode);
							self.renderlocales(commonVariables.contentPlaceholder);	
							$(".error").show();
							$(".error").fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(5);
							setTimeout(function() {
								$(".error").hide();
							},2500);
						}

					},

					function(textStatus) {
						$(".msgdisplay").removeClass("success").addClass("error");
						$(".error").attr('data-i18n', 'commonlabel.errormessage.serviceerror');
						self.renderlocales(commonVariables.contentPlaceholder);	
						$(".error").show();
						setTimeout(function() {
							$(".error").hide();
						},2500);
						//commonVariables.loadingScreen.removeLoading();
					}
				);
			} catch(exception) {
				//commonVariables.loadingScreen.removeLoading();
			}

		},
		
		getRequestHeader : function(projectRequestBody, id, action) {
			var self=this, header, data = {}, userId;
			self.projectId = id;
			data = JSON.parse(commonVariables.api.localVal.getSession('userInfo'));
			var userId = (data !== null) ? data.id : "";
		
				header = {
					contentType: "application/json",
					requestMethod: "GET",
					dataType: "json",
					webserviceurl: commonVariables.webserviceurl + "project/edit?userId="+userId+"&customerId="+self.getCustomer()+"&projectId="+id
				};

				if(action === "projectlist"){
					header.requestMethod = "GET";
					header.webserviceurl = commonVariables.webserviceurl + commonVariables.projectlistContext +"/list?customerId="+ self.getCustomer();
				}

				if(action === "update"){
					header.requestMethod = "PUT";
					header.requestPostBody = JSON.stringify(projectRequestBody);
					header.webserviceurl = commonVariables.webserviceurl + "project/updateproject?userId="+userId;
				}
				
				if(action === "create"){
					
					header.requestMethod = "POST";
					header.requestPostBody = JSON.stringify(projectRequestBody);
					header.webserviceurl = commonVariables.webserviceurl + "project/create?userId="+userId;
				}
				
				if(action === "apptypes"){
					header.requestMethod = "GET";
					header.webserviceurl = commonVariables.webserviceurl + "technology/apptypes?userId="+userId+"&customerId="+self.getCustomer();
				}
				
				if(action === "pilotlist"){
					header.requestMethod = "GET";
					header.webserviceurl = commonVariables.webserviceurl + "pilot/prebuilt?userId="+userId+"&customerId="+self.getCustomer();
				}

				return header;
		},
		
		valid : function(item, msg) {
			$(item).focus();
			$(item).attr('placeholder', msg);
			$(item).addClass("errormessage");
			$(item).bind('keypress', function() {
				$(this).removeClass("errormessage");
			});
		},

		appvalid : function() {
			var textboxval,techval,verval,count1 = 0,flagnew1 = 0,arr1 = [],flag = 0;
			$('.applnlayercontent').each(function(index,value) {
				if(($(value).css('display') !== 'none') && ($(value).attr('name') === 'staticApplnLayer')){
					textboxval = $(this).children('td.applnappcode').children('input.appln-appcode').val();
					techval = $(this).children('td.technology').children('select.appln_technology').val();
					techvalObj = $(this).children('td.technology').children('select.appln_technology');
					verval = $(this).children('td:eq(2)').children().val();
					if($("tr[class='applnLayer']").attr('key') === 'displayed'){
						if(textboxval === '') {
							var t = $(this).children('td:eq(0)').children();
							t.addClass("errormessage");
						 	t.focus();
							t.attr('placeholder','Enter AppCode');
							flag = 1;
							t.bind('keypress', function() {
								$(this).removeClass("errormessage");
								$(this).removeAttr("placeholder");
							});
						}
						if(flag !== 1) {
							if(techval !== undefined || techval !== 'undefined') { 
								if(techval === 'Select Technology' || techval === null) {
									flagnew1 = 1;
									$(techvalObj).focus();
									$(".errmsg1").show();
									$(".errmsg1").text("Select Technology.");
									setTimeout(function() {
										$(".errmsg1").hide();
									}, 5000);
								} 
							}
						}
					}
					arr1[count1]=textboxval;
					$(this).next();
					count1++;
				}	
			});
			if(flag === 1) {
				return true;
			}	
			if(flagnew1 === 1) {
				return true;
			}	
		},

		webvalid : function() {
			$(".errmsg1").hide();
			var textbox2val,tech2val,widgetval,webverval,count2 = 0,flagged = 0,flagg = 0,flagnew2 = 0,arr2 = [];
			$('.weblayercontent').each(function(index,value) {
				if(($(value).css('display') !== 'none') && ($(value).attr('name') === 'staticWebLayer')){
					textbox2val = $(this).children('td.webappcode').children('input.web-appcode').val();
					tech2val = $(this).children('td.web').children('select.weblayer').val();
					widgetval = $(this).children('td.widget').children('select.web_widget').val();
					webverval = $(this).children('td.widgetversion').children('select.web_version').val();
					tech2valobj = $(this).children('td.web').children('select.weblayer');
					widgetvalobj = $(this).children('td.widget').children('select.web_widget');
					if($("tr[class='webLayer']").attr('key') === 'displayed'){
						if(textbox2val ==='') {
							var t = $(this).children('td:eq(0)').children();
							t.addClass("errormessage");
						 	t.focus();
							t.attr('placeholder', 'Enter AppCode');
							flagg = 1;
							t.bind('keypress', function() {
								$(this).removeClass("errormessage");
								$(this).removeAttr("placeholder");
							});
						}
						if(flagg !== 1) {
							if(tech2val !== undefined || tech2val !== 'undefined') { 
								if(tech2val === 'Select Layer' || tech2val === null) {
									flagged=1;
									flagnew2 = 1;
									$(".errmsg2").show();
									$(".errmsg2").text("Select Layer.");
									$(tech2valobj).focus();	
								}
							}

							if(flagged !== 1) {
								if(widgetval !== undefined || widgetval !== 'undefined' ) {
									if(widgetval === 'Select Widget' || widgetval ===null) {
										flagnew2 = 1;
										$(".errmsg2").show();
										$(".errmsg2").text("Select Widget.");
										$(widgetvalobj).focus();
										setTimeout(function() {
											$(".errmsg2").hide();
										}, 5000);	
									}
								}
							}
						}
					}	
					arr2[count2]=textbox2val;
					$(this).next();
					count2++;
				}
			});
			if(flagg === 1) {
				return true;
			}	
			if(flagnew2 === 1) {
				return true;
			}		
			$(".errmsg2").hide();
		},

		mobvalid : function() {
			$(".errmsg2").hide();
			var textbox3val,layerval,typeval,mobverval,count3 = 0,flagger = 0,flaggg = 0,flagnew3 = 0,arr3 = [];
			$('.mobilelayercontent').each(function(index,value) {
				if(($(value).css('display') !== 'none') && ($(value).attr('name') === 'staticMobileLayer')){
					textbox3val = $(this).children('td.mobileappcode').children('input.mobile-appcode').val();
					layerval = $(this).children('td.mobile').children('select.mobile_layer').val();
					typeval = $(this).children('td.types').children('select.mobile_types').val();
					mobverval = $(this).children('td.mobileversion').children('select.mobile_version').val();
					layervalobj = $(this).children('td.mobile').children('select.mobile_layer');
					typevalobj = $(this).children('td.types').children('select.mobile_types'); 
					if($("tr[class='mobLayer']").attr('key') === 'displayed'){
						if(textbox3val === '') {
							var t = $(this).children('td:eq(0)').children();
							t.addClass("errormessage");
						 	t.focus();
							t.attr('placeholder','Enter AppCode');
							flaggg = 1;
							t.bind('keypress', function() {
								$(this).removeClass("errormessage");
								$(this).removeAttr("placeholder");
							});
						}
						if(flaggg !== 1) {
							if(layerval !== undefined || layerval !== 'undefined') { 
								if(layerval === 'Select Technology' ||layerval === null) {
									flagger=1;
									flagnew3=1;
									$(".errmsg3").show();
									$(".errmsg3").text("Select Technology.");
									$(layervalobj).focus();
								}
							}
							if(flagger !== 1) {
								if(typeval !== undefined || typeval !=='undefined') {
									if(typeval === 'Select Platform' || typeval === null) {
										flagnew3=1;
										$(".errmsg3").show();
										$(".errmsg3").text("Select Platform.");
										$(typevalobj).focus();
										setTimeout(function() {
											$(".errmsg2").hide();
										}, 5000);
									}
								}
							}
						}	
					}
					arr3[count3]=textbox3val;
					$(this).next();
					count3++;
				}	
			});
			if(flaggg === 1) {
				return true;
			}	
			if(flagnew3 === 1) {
				return true;	
			}
			$(".errmsg3").hide();	
		},
		
		validation : function() {
			 var self = this;
			 var name = $("input[name='projectname']").val();
			 var code = $("input[name='projectcode']").val();
			 var labelversion = $("input[name='projectversion']").val();
			 var appcode = $("#appcode").val();
			 var webappcode = $("#webappcode").val();
			 var mobileappcode = $("#mobileappcode").val();

			    if(name === ""){
					self.valid("input[name='projectname']", "Enter Name");
					self.hasError = true;
			    }else if(code === ""){
					self.valid("input[name='projectcode']", "Enter Code");
					self.hasError = true;
			    }else if(self.appvalid()) {
					self.hasError = true;
			    }else if(self.webvalid()) {
			    	$(".errmsg1").hide();
					self.hasError = true;
			    }else if(self.mobvalid()) {
			    	$(".errmsg2").hide();
					self.hasError = true;		
			    }else {		
			    	$(".errmsg3").hide();
					self.hasError=false;
					return self.hasError;
				}		
			
			return self.hasError;	
		},

		getCustomer : function() {
			var selectedcustomer = $("#selectedCustomer").text();
			var customerId = "";
			
			$.each($("#customer").children(), function(index, value){
				var customerText = $(value).children().text();
				if(customerText === selectedcustomer){
					customerId = $(value).children().attr('id');
				}
			});
			
			return customerId;
		},
		
		addLayers :function(layerType, whereToAppend) {
			var self=this, minusIcon = '<img src="themes/default/images/helios/minus_icon.png" border="0" alt="">',dynamicValue;
			
			if (layerType === "addApplnLayer") {
				self.count++;
				var applicationlayer = '<tr class="applnlayercontent" name="staticApplnLayer"><td class="applnappcode"><input type="text" id="appcode" maxlength="30" title="30 Characters only" class="appln-appcode appCodeText" count='+ self.count +'></td><td name="technology" class="technology"><select name="appln_technology" class="appln_technology selectpicker"><option>Select Technology</option>'+ self.getTechnology() +'</select></td><td colspan="3" name="version" class="version"><select name="appln_version" class="appln_version selectpicker"><option>Select Version</option></select></td><td class="appdependencyTd" colspan="2"><select class="selectpicker appdependencySelect" name="appdependencySelect" style="display:none" title="Select Dependency" multiple><option value="0" disabled>Select Dependency</option></select></td><td><div class="flt_right icon_center"><a name="addApplnLayer"><img src="themes/default/images/helios/plus_icon.png" border="0" alt=""></a> <a href="javascript:;" name="removeApplnLayer"><img src="themes/default/images/helios/minus_icon.png" border="0" alt=""></a></div></td></tr>';
				
				dynamicValue = $(applicationlayer).insertAfter(whereToAppend);
				self.multiselect();
				if (dynamicValue.prev('tr').attr("name") !== "dynamicAppLayer") {
					dynamicValue.prev('tr').find('a[name="addApplnLayer"]').html('');
					dynamicValue.prev('tr').find('a[name="removeApplnLayer"]').html(minusIcon);
				} else {
					dynamicValue.prev('tr').find('a[name="addApplnLayer"]').html('');
				}

			} else if (layerType === "addWebLayer") {
				self.count++;
				var weblayer ='<tr class="weblayercontent" name="staticWebLayer"><td class="webappcode"><input type="text" id="webappcode" maxlength="30" title="30 Characters only" class="web-appcode appCodeText" count='+ self.count +'></td><td name="web" class="web"><select name="weblayer" class="weblayer selectpicker"><option>Select Layer</option>'+self.getWidget() +'</select></td><td name="widget" class="widget"><select name="web_widget" class="web_widget selectpicker"><option>Select Widget</option> </select></td><td name="widgetversion" class="widgetversion"><select name="web_version" class="web_version selectpicker"><option>Select Version</option></select></td><td class="webdependencyTd"><select class="selectpicker webdependencySelect" title="Select Dependency" name="webdependencySelect" style="display:none" multiple><option disabled>Select Dependency</option></select></td><td> <div class="flt_right icon_center"><a href="javascript:;" name="addWebLayer"><img src="themes/default/images/helios/plus_icon.png" border="0" alt=""></a> <a href="javascript:;" name="removeWebLayer"><img src="themes/default/images/helios/minus_icon.png" border="0" alt=""></a></div></td></tr>';
				
				dynamicValue = $(weblayer).insertAfter(whereToAppend);
				self.multiselect();
				if (dynamicValue.prev('tr').attr("name") !== "dynamicWebLayer") {
					dynamicValue.prev('tr').find('a[name="addWebLayer"]').html('');
					dynamicValue.prev('tr').find('a[name="removeWebLayer"]').html(minusIcon);
				} else {
					dynamicValue.prev('tr').find('a[name="addWebLayer"]').html('');
				}
			} else {
				self.count++;
				var mobilelayer = '<tr class="mobilelayercontent" name="staticMobileLayer"><td class="mobileappcode"><input type="text" id="mobileappcode" maxlength="30" title="30 Characters only" class="mobile-appcode appCodeText" count='+ self.count +'></td><td name="mobile" class="mobile"><select name="mobile_layer" class="mobile_layer selectpicker"><option>Select Model</option>'+self.getMobile() +'</select></td><td name="types" class="types"><select name="mobile_types" class="mobile_types selectpicker"><option>Select Type</option></select></td><td colspan="2" name="mobileversion" class="mobileversion selectpicker"><select name="mobile_version" class="mobile_version selectpicker"><option>Select Version</option></select></td><td><div class="flt_right icon_center"><a href="javascript:;" name="addMobileLayer"><img src="themes/default/images/helios/plus_icon.png" border="0" alt=""></a> <a href="javascript:;" name="removeMobileLayer"><img src="themes/default/images/helios/minus_icon.png" border="0" alt=""></a></div></td></tr>';
				
				$(mobilelayer).find('input.appCodeText').attr('count', self.count);
				dynamicValue = $(mobilelayer).insertAfter(whereToAppend);
				self.multiselect();
				if (dynamicValue.prev('tr').attr("name") !== "dynamicMobileLayer") {
					dynamicValue.prev('tr').find('a[name="addMobileLayer"]').html('');
					dynamicValue.prev('tr').find('a[name="removeMobileLayer"]').html(minusIcon);
				} else {
					dynamicValue.prev('tr').find('a[name="addMobileLayer"]').html('');
				}
			}

			$("a[name=addApplnLayer]").unbind("click");
			$("a[name=addWebLayer]").unbind("click");
			$("a[name=addMobileLayer]").unbind("click");
			self.addLayersEvent();
			$("a[name=removeApplnLayer]").unbind("click");
			$("a[name=removeWebLayer]").unbind("click");
			$("a[name=removeMobileLayer]").unbind("click");
			self.removeLayersEvent();
			$("select[name='appln_technology']").unbind('click');
			$("select[name='weblayer']").unbind('click');
			$("select[name='mobile_layer']").unbind('click');
			$("select[name='appln_technology']").unbind('change');
			$("select[name='web_widget']").unbind('change');
			$("select[name='mobile_layer']").unbind('change');
			$("select[name='weblayer']").unbind('change');
			self.technologyAndVersionChangeEvent();
		},
		
		addLayersEvent : function() {
			var self=this, whereToAppend = '';
			
			$(".appln-appcode, .web-appcode, .mobile-appcode").unbind('input');
			$(".appln-appcode, .web-appcode, .mobile-appcode").bind('input', function(){
				$(this).val(self.specialCharValidation($(this).val().replace(/\s/g, "")));
			});

			$("a[name=addApplnLayer]").click(function(){
				whereToAppend = $(this).parents('tr.applnlayercontent:last');
				self.addLayers($(this).attr('name'), whereToAppend);
			});
			
			$("a[name=addWebLayer]").click(function(){
				whereToAppend = $("a[name=addWebLayer]").parents('tr.weblayercontent:last');
				self.addLayers($(this).attr('name'), whereToAppend);
			});
			
			$("a[name=addMobileLayer]").click(function(){
				whereToAppend = $("a[name=addMobileLayer]").parents('tr.mobilelayercontent:last');
				self.addLayers($(this).attr('name'), whereToAppend);
			});
		},
		
		removeLayersEvent : function() {
			var self=this, addIcon = '<img src="themes/default/images/helios/plus_icon.png" border="0" alt="">';
			$("a[name=removeApplnLayer]").click(function() {
				$("a[name=addApplnLayer]").html('');
				$(this).parent().parent().parent().remove();
				$("a[name=removeApplnLayer]").parent().parent().parent('tr:last').find('a[name="addApplnLayer"]').html(addIcon);
				if (($("a[name=removeApplnLayer]").parent().parent().parent('tr[name=staticApplnLayer]').length) === 1) {
					$('tr[name=staticApplnLayer]').find('a[name="addApplnLayer"]').html(addIcon);
					$("a[name=removeApplnLayer]").html('');
				}
			});

			$("a[name=removeWebLayer]").click(function(){
				$("a[name=addWebLayer]").html('');
				$(this).parent().parent().parent().remove();
				$("a[name=removeWebLayer]").parent().parent().parent('tr:last').find('a[name="addWebLayer"]').html(addIcon);
				if (($("a[name=removeWebLayer]").parent().parent().parent('tr[name=staticWebLayer]').length) === 1) {
					$('tr[name=staticWebLayer]').find('a[name="addWebLayer"]').html(addIcon);
					$("a[name=removeWebLayer]").html('');
				}
			});

			$("a[name=removeMobileLayer]").click(function(){
				$("a[name=addMobileLayer]").html('');
				$(this).parent().parent().parent().remove();
				$("a[name=removeMobileLayer]").parent().parent().parent('tr:last').find('a[name="addMobileLayer"]').html(addIcon);
				if (($("a[name=removeMobileLayer]").parent().parent().parent('tr[name=staticMobileLayer]').length) === 1) {
					$('tr[name=staticMobileLayer]').find('a[name="addMobileLayer"]').html(addIcon);
					$("a[name=removeMobileLayer]").html('');
				}
			});
		},
		
		technologyAndVersionChangeEvent : function() {
		
			var self=this;
			
			$("input.appCodeText").blur(function(){
				var currentCount = $(this).attr('count');
				var currentVal =  $(this).val();
				var appCodeTextObj = $(this);
				if(currentVal === '' || currentVal === null) {
					$(appCodeTextObj).focus();
					$(appCodeTextObj).addClass('errormessage');
					$(appCodeTextObj).attr('placeholder', 'Enter Appcode');
				} else {
					$(".appCodeText").each(function(index, value){
						var keyAttr = $(value).parents('tr[name=appvalidation]').attr('key');
						if(keyAttr === 'displayed'){
							var valueCount = $(value).attr('count');
							var appcodeVal = $(value).val();
							if(currentCount !== valueCount && currentVal === appcodeVal){
								$(appCodeTextObj).val("");
								$(appCodeTextObj).focus();
								$(appCodeTextObj).addClass('errormessage');
								$(appCodeTextObj).attr('placeholder', 'Appcode Already Exists');
								return false;
							}
						}
					});
				}	
				$(appCodeTextObj).bind('keypress', function() {
					$(this).removeClass("errormessage");
					$(this).removeAttr("placeholder");
				});
			});
			
			/***
			 ** Disabling select technology option
			 */
			$("select[name='appln_technology']").bind('click', function(){
				$("select[name='appln_technology'] option").each(function(index, value) {
					if($(value).text() === "Select Technology"){
						$(this).attr('disabled','disabled');
					}	
				});
			});
			
			/***
			 ** Disabling select Web Layer option
			 */
			$("select[name='weblayer']").bind('click', function(){
				$("select[name='weblayer'] option").each(function(index, value) {
					if($(value).text() === "Select Web Layer"){
						$(this).attr('disabled','disabled');
					}	
				});
			});
			
			/***
			 ** Disabling select model option
			 */
			$("select[name='mobile_layer']").bind('click', function(){
				$("select[name='mobile_layer'] option").each(function(index, value) {
					if($(value).text() === "Select Model"){
						$(this).attr('disabled','disabled');
					}	
				});
			});
			
			/***
			 ** Application Layer - Technology Change Event For Technology version
			 */
			$("select[name='appln_technology']").bind('change', function(){
				var techId = $(this).val();
				var versionplaceholder = $(this).parents("td[name='technology']").siblings("td[name='version']").children("select[name='appln_version']");
				self.gettechnologyversion(techId, versionplaceholder);
			});
			
			/***
			 ** Web Layer - Web Layer Change Event For Widget Type
			 */
			$("select[name='weblayer']").bind('change', function(){
				var type = $(this).val();
				var widgetTypePlaceholder = $(this).parents("td[name='web']").siblings("td[name='widget']").children("select[name='web_widget']");
				self.getwidgettype(type, widgetTypePlaceholder);
			});
			
			/***
			 ** Web Layer - Type Change Event For Widget Version
			 */
			$("select[name='web_widget']").bind('change', function(){
				var widgetType = $(this).val();
				var widgetTypePlaceholder = $(this).parents("td[name='widget']").siblings("td[name='widgetversion']").children("select[name='web_version']");
				self.getwidgetversion(widgetType, widgetTypePlaceholder);
			});

			/***
			 ** Mobile Layer - Mobile Change Event For Mobile Type
			 */
			$("select[name='mobile_layer']").bind('change', function(){
				var mobile = $(this).val();
				var mobileTypePlaceholder = $(this).parents("td[name='mobile']").siblings("td[name='types']").children("select[name='mobile_types']");
				self.getmobiletype(mobile, mobileTypePlaceholder);
			});
			
			/***
			 **	Mobile Layer - Type Change Event For Mobile Version
			 */
			$("select[name='mobile_types']").unbind('change');
			$("select[name='mobile_types']").bind('change', function(){
				var mobileType = $(this).val();
				var mobileTypePlaceholder = $(this).parents("td[name='types']").siblings("td[name='mobileversion']").children("select[name='mobile_version']");
				self.getmobileversion(mobileType, mobileTypePlaceholder);
			});
			
		},
		
		getTechnology : function(id) {
			var self=this, option;
			self.applicationlayerData = commonVariables.api.localVal.getJson("Application Layer");
			option = '';
			$.each(self.applicationlayerData.techGroups, function(index, value){
				$.each(value.techInfos, function(index, value){
					if (id === value.id) {
						option += '<option value='+ value.id +' selected=selected>'+ value.name +'</option>';
					} else {
						option += '<option value='+ value.id +'>'+ value.name +'</option>';
					}
				});
			});
			
			return option;
		},
		
		getWidget : function() {
			var self=this, option;
			self.weblayerData = commonVariables.api.localVal.getJson("Web Layer");
			option = '';
			$.each(self.weblayerData.techGroups, function(index, value){
				option += '<option value='+ value.id +'>'+ value.name +'</option>';
			});
			
			return option;
		},
		
		getMobile : function(id) {
			var self=this, option;
			self.mobilelayerData = commonVariables.api.localVal.getJson("Mobile Layer");
			option = '';
			$.each(self.mobilelayerData.techGroups, function(index, value){
				if (id === value.id) {
					option += '<option value='+ value.id +' selected=selected>'+ value.name +'</option>';
				} else {
					if(value !== '' && value !== undefined) {
						option += '<option value='+ value.id +'>'+ value.name +'</option>';
					}
				}
			});
			
			return option;
		},
		
		gettechnologyversion : function(technologyId, versionplaceholder) {
			var self=this, option;
			self.applicationlayerData = commonVariables.api.localVal.getJson("Application Layer");
			$.each(self.applicationlayerData.techGroups, function(index, value){
				$.each(value.techInfos, function(index, value){
				    if(value.id === technologyId){
						option = '';
						$.each(value.techVersions, function(index, value){
							option += '<option>'+ value +'</option>';
						});
						
						$(versionplaceholder).html(option);
						$(versionplaceholder).selectpicker('refresh');
					}
				});
			});
		},
		
		getwidgettype : function(type, widgetTypePlaceholder) {
			var self=this, option;
			self.weblayerData = commonVariables.api.localVal.getJson("Web Layer");
			$.each(self.weblayerData.techGroups, function(index, value){
				if(value.id === type){
					option = '';
					option += '<option disabled selected>Select Widget</option>';
					$.each(value.techInfos, function(index, value){
						option += '<option value='+ value.id +'>'+ value.name +'</option>';
					});
					
					$(widgetTypePlaceholder).html(option);
					$(widgetTypePlaceholder).selectpicker('refresh');
				}
			});
		},
		
		editgetwidgettype : function(id) {
			var self=this, option;
			self.weblayerData = commonVariables.api.localVal.getJson("Web Layer");
			$.each(self.weblayerData.techGroups, function(index, value){
				$.each(value.techInfos, function(index, value){
					if (id === value.id) {
						option += '<option value='+ value.id +' selected=selected>'+ value.name +'</option>';
					} else {
						option += '<option value='+ value.id +'>'+ value.name +'</option>';
					}
				});
			});
			
			return option;
		},
		
		getwidgetversion : function(widgettype, widgetTypePlaceholder) {
			var self=this, option;
			self.weblayerData = commonVariables.api.localVal.getJson("Web Layer");
			$.each(self.weblayerData.techGroups, function(index, value){
				$.each(value.techInfos, function(index, value){
					if(value.id === widgettype){
						option = '';
						$.each(value.techVersions, function(index, value){
							option += '<option>'+ value +'</option>';
						});
						
						$(widgetTypePlaceholder).html(option);
						$(widgetTypePlaceholder).selectpicker('refresh');
					}
				});
			});
		},
		
		getmobiletype : function(mobile, mobileTypePlaceholder) {
			var self=this, option;
			self.mobilelayerData = commonVariables.api.localVal.getJson("Mobile Layer");
			$.each(self.mobilelayerData.techGroups, function(index, value){
				if(value.id === mobile){
					option = '';
					option += '<option disabled selected>Select Type</option>';
					$.each(value.techInfos, function(index, value){
						option += '<option value='+ value.id +'>'+ value.name +'</option>';
					});
					
					$(mobileTypePlaceholder).html(option);
					$(mobileTypePlaceholder).selectpicker('refresh');
				}
			});
		},
		
		editgetmobiletype : function(id) {
			var self=this, option;
			self.mobilelayerData = commonVariables.api.localVal.getJson("Mobile Layer");
			$.each(self.mobilelayerData.techGroups, function(index, value){
				$.each(value.techInfos, function(index, value){
					if (id === value.id) {
						option += '<option value='+ value.id +' selected=selected>'+ value.name +'</option>';
					} else {
						option += '<option value='+ value.id +'>'+ value.name +'</option>';
					}
				});
			});
			
			return option;
		},
		
		getmobileversion : function(mobileType, mobileTypePlaceholder) {
			var self=this, option;
			self.mobilelayerData = commonVariables.api.localVal.getJson("Mobile Layer");
			$.each(self.mobilelayerData.techGroups, function(index, value){
				$.each(value.techInfos, function(index, value){
					if(value.id === mobileType){
						option = '';
						if(value.techVersions !== undefined && value.techVersions !== null) {
							if(value !== '' && value !== null) {
								$.each(value.techVersions, function(index, value){
									if(value !== '' && value !== null) {
										option += '<option>'+ value +'</option>';
									} else {
										option += '<option>No Versions available</option>';
									}
								});
							} else {
								option += '<option>No Versions available</option>';
							}
						} else {
							option += '<option>No Versions available</option>';
						}	
						
						$(mobileTypePlaceholder).html(option);
						$(mobileTypePlaceholder).selectpicker('refresh');
					}
				});
			});
		},
		
		pilotprojectsEvent : function() {
			var self=this;
			$("select[name='prebuiltapps']").hide();
			$("select[name='builtmyself']").bind('change', function(){
				$("input.appln-appcode").val('');
				$("input.web-appcode").val('');
				$("input.mobile-appcode").val('');
				$("input[name=applicationlayer]").hide();
				$("input[name=weblayer]").hide();
				$("input[name=mobilelayer]").hide();
				self.counter = null;
				var selectedText = $(this).find(':selected').val();
				if(selectedText === "prebuilt"){
					 $("tr[name=applicationlayer]").hide();
					 $("tr.applnLayer").hide();
					 $("tr.applnLayer").attr('key','hidden');	
					 $("tr[name=weblayer]").hide();
					 $("tr.webLayer").hide();
					 $("tr.webLayer").attr('key','hidden');	
					 $("tr[name=mobilelayer]").hide();
					 $("tr.mobLayer").hide();
					 $("tr.mobLayer").attr('key','hidden');	
					 $("input[name='startdate']").attr("disabled", true);
					 $("input[name='enddate']").attr("disabled", true);
					 $("select[name='builtmyselfapps']").hide();
					 $("#applicationlayer").hide();
					 $("#weblayer").hide();
					 $("#mobilelayer").hide();
					 self.setPilotData(function(Option){
						$("select[name='prebuiltapps']").css('display' , 'inline-block');
						$("select[name='prebuiltapps']").html(Option);
						var selectedPilot = $("select[name='prebuiltapps']").find(':selected').text();
						self.layerRender(selectedPilot);
					 });
				} else {
					 $("tr[name=applicationlayer]").show();
					 $("tr.applnLayer").show();
					 $("tr.applnLayer").attr('key','displayed');	
					 $("tr[name=weblayer]").show();
					 $("tr.webLayer").show();
					 $("tr.webLayer").attr('key','displayed');	
					 $("tr[name=mobilelayer]").show();
					 $("tr.mobLayer").show();
					 $("tr.mobLayer").attr('key','displayed');	
					 $("select[name='builtmyselfapps']").css('display' , 'inline-block');
					 $("input[name='startdate']").attr("disabled", false);
					 $("input[name='enddate']").attr("disabled", false);
					 $("select[name='appln_technology']").attr('disabled', false);
					 $("select[name='appln_version']").attr('disabled', false);
					 $("select[name='weblayer']").attr('disabled', false);
					 $("select[name='web_widget']").attr('disabled', false);
					 $("select[name='web_version']").attr('disabled', false);
					 $("select[name='mobile_layer']").attr('disabled', false);
					 $("select[name='mobile_types']").attr('disabled', false);
					 $("select[name='mobile_version']").attr('disabled', false);
					 $("select[name='prebuiltapps']").hide();
					 $("#applicationlayer").show();
					 $("#weblayer").show();
					 $("#mobilelayer").show();
					 self.revertSelectValues($("select[name='appln_technology']"),"Select Technology");
					 $("select[name=appln_version]").html('<option>Select Version</option>');
					 $("select[name=appln_version]").selectpicker('refresh');
					 self.revertSelectValues($("select[name='weblayer']"),"Select Layer");
					 self.revertSelectValues($("select[name='mobile_layer']"),"Select Model");
					 $("select[name=web_widget]").html('<option>Select Widget</option>');
					 $("select[name=web_widget]").selectpicker('refresh');
					 $("select[name=web_version]").html('<option>Select Version</option>');
					 $("select[name=web_version]").selectpicker('refresh');
					 $("select[name=mobile_types]").html('<option>Select Type</option>');
					 $("select[name=mobile_types]").selectpicker('refresh');
					 $("select[name=mobile_version]").html('<option>Select Version</option>');
					 $("select[name=mobile_version]").selectpicker('refresh');	
				}
			});
			
			$("select[name='prebuiltapps']").change(function() {
				self.enablebuttonAdd();
				$("input.appln-appcode").val('');
				$("input.web-appcode").val('');
				$("input.mobile-appcode").val('');
				var selectedPilot = $(this).find(':selected').text();
				self.layerRender(selectedPilot);
			});
		},
		
		revertSelectValues : function(obj, OptionText) {
			$(obj).removeAttr('appInfoId');
			$(obj).find('option').each(function(index, value) {
				$(value).removeAttr('selected');
				$(obj).selectpicker('refresh');
			});
			if($(obj).find('option').val() === OptionText) {
				//$(obj).find('option').attr('selected', 'selected');
				$(obj).val(OptionText);
				$(obj).selectpicker('refresh');
			}
		},
		
		setPilotData : function(callback) {
			var self=this, option = '';
			self.getEditProject(self.getRequestHeader(self.projectRequestBody, "", "pilotlist"), function(response) {
				if (response !== null && (response.status !== "error" || response.status !== "failure")){
					$.each(response.data, function(index, value){
						commonVariables.api.localVal.setJson(value.name, value);
						if(value.displayName !== undefined && value.displayName !== null && value.displayName !== '') {
							option += '<option>'+ value.displayName +'</option>';
						}
						if(response.data.length === (index + 1)){
							callback(option);
						} 
					});
				} else {
					$(".msgdisplay").removeClass("success").addClass("error");
					$(".error").attr('data-i18n', 'errorCodes.' + response.responseCode);
					self.renderlocales(commonVariables.contentPlaceholder);	
					$(".error").show();
					$(".error").fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(500).fadeIn(500).fadeOut(5);
					setTimeout(function() {
						$(".error").hide();
					},2500);
				}
			});
		},
		
		layerRender : function(selectedPilot) {
			var self=this;
			var selectedPilotData = commonVariables.api.localVal.getJson(selectedPilot);
			if(selectedPilotData !== undefined && selectedPilotData !== null) {
				$("tr[name=applicationlayer]").hide();
				$("tr.applnLayer").hide();
				$("tr.applnLayer").attr('key','hidden');	
				$("tr[name=weblayer]").hide();
				$("tr.webLayer").hide();
				$("tr.webLayer").attr('key','hidden');	
				$("tr[name=mobilelayer]").hide();
				$("tr.mobLayer").hide();
				$("tr.mobLayer").attr('key','hidden');
				$.each(selectedPilotData.appInfos, function(index, appInfo){
					if(appInfo.techInfo.appTypeId === "app-layer"){
						$("select[name='appln_technology'] option").each(function(index, value) {
							$(value).removeAttr('selected');	
							if($(value).val() === appInfo.techInfo.id.toLowerCase()) {
								$(value).attr('selected', 'selected');
								$("select[name='appln_technology']").attr('appInfoId', appInfo.id);
								$("select[name='appln_technology']").val($(value).val());
								if (appInfo.techInfo.id === 'tech-java-webservice'){
									$("select[name='appln_technology']").val('tech-java-webservice');
								} else if(appInfo.techInfo.id === 'tech-nodejs-webservice'){
									$("select[name='appln_technology']").val('tech-nodejs-webservice');
								}	
								$("select[name='appln_technology']").attr('disabled', 'disabled');
								$("select[name='appln_technology']").selectpicker('refresh');
							}
						});
						var techId = $("select[name='appln_technology']").val();
						var versionplaceholder = $("select[name='appln_technology']").parents("td[name='technology']").siblings("td[name='version']").children("select[name='appln_version']");
						self.gettechnologyversion(techId, versionplaceholder);
						$("select[name='appln_version']").attr('disabled', 'disabled');
						$("select[name='appln_version']").selectpicker('refresh');
						$("tr[name=applicationlayer]").show();
						$("tr.applnLayer").show();
						$("tr.applnLayer").attr('key','displayed');
					} else if (appInfo.techInfo.appTypeId === "web-layer") {
						$("select[name='weblayer'] option").each(function(index, value) {
							$(value).removeAttr('selected');
							var techGroupId = appInfo.techInfo.techGroupId;	
							if($(value).val() === techGroupId.toLowerCase()) {
								$(value).attr('selected', 'selected');
								$("select[name='weblayer']").val($(value).val());
								$("select[name='weblayer']").attr('appInfoId', appInfo.id);
								$("select[name='weblayer']").attr('disabled', 'disabled');
								$("select[name='weblayer']").selectpicker('refresh');
							}
						});
						var type = $("select[name='weblayer']").val();
						var widgetTypePlaceholder = $("select[name='weblayer']").parents("td[name='web']").siblings("td[name='widget']").children("select[name='web_widget']");
						self.getwidgettype(type, widgetTypePlaceholder);
						$("select[name='web_widget'] option").each(function(index, value) {
							$(value).removeAttr('selected');	
							if($(value).val() === appInfo.techInfo.id) {
								$(value).attr('selected', 'selected');
								$("select[name='web_widget']").val($(value).val());
								$("select[name='web_widget']").attr('disabled', 'disabled');
								$("select[name='web_widget']").selectpicker('refresh');
							}
						});
						var widgetType = $("select[name='web_widget']").val();
						var widgetTypePlaceholder = $("select[name='web_widget']").parents("td[name='widget']").siblings("td[name='widgetversion']").children("select[name='web_version']");
						self.getwidgetversion(widgetType, widgetTypePlaceholder);
						$("select[name='web_version']").attr('disabled', 'disabled');
						$("select[name='web_version']").selectpicker('refresh');
						$("tr[name=weblayer]").show();
						$("tr.webLayer").show();
						$("tr.webLayer").attr('key','displayed');
					} else if(appInfo.techInfo.appTypeId === "mob-layer") {
						$("select[name='mobile_layer'] option").each(function(index, value){
							$(value).removeAttr('selected');	
							if($(value).val() === appInfo.techInfo.techGroupId) {
								$(value).attr('selected', 'selected');
								$("select[name='mobile_layer']").val($(value).val());
								$("select[name='mobile_layer']").attr('appInfoId', appInfo.id);
								$("select[name='mobile_layer']").attr('disabled', 'disabled');
								$("select[name='mobile_layer']").selectpicker('refresh');
							}
						});
						var mobile = $("select[name='mobile_layer']").val();
						var mobileTypePlaceholder = $("select[name='mobile_layer']").parents("td[name='mobile']").siblings("td[name='types']").children("select[name='mobile_types']");
						self.getmobiletype(mobile, mobileTypePlaceholder);
						$("select[name='mobile_types'] option").each(function(index, value){
							$(value).removeAttr('selected');	
							if($(value).val() === appInfo.techInfo.id) {
								$(value).attr('selected', 'selected');
								$("select[name='mobile_types']").val($(value).val());
								$("select[name='mobile_types']").attr('disabled', 'disabled');
								$("select[name='mobile_types']").selectpicker('refresh');
							}
						});
						var mobileType = $("select[name='mobile_types']").val();
						var mobileTypePlaceholder = $("select[name='mobile_types']").parents("td[name='types']").siblings("td[name='mobileversion']").children("select[name='mobile_version']");
						self.getmobileversion(mobileType, mobileTypePlaceholder);
						$("select[name='mobile_version']").attr('disabled', 'disabled');
						$("select[name='mobile_version']").selectpicker('refresh');
						$("tr[name=mobilelayer]").show();	
						$("tr.mobLayer").show();
						$("tr.mobLayer").attr('key','displayed');
					}
					self.enablebuttonAdd();
				});
			}		
		}, 
		
		enablebuttonAdd : function() {
			if($('tr.applnLayer').attr('key') === "hidden") {
				$("input[name='applicationlayer']").show();
			} else {
				$("input[name='applicationlayer']").hide();
			}	
			if($('tr.webLayer').attr('key') === "hidden") {
				$("input[name='weblayer']").show();
			} else {
				$("input[name='weblayer']").hide();
			}	
			if($('tr.mobLayer').attr('key') === "hidden") {
				$("input[name='mobilelayer']").show();
			} else {
				$("input[name='mobilelayer']").hide();
			}
		},
		
		enablebuttonEdit : function() {
			if($('#appLayaer').css('display') === "none") {
					$("input[name='appLayaer']").show();
			}	
			if($('#webLayers').css('display') === "none") {
					$("input[name='webLayers']").show();
			}	
			if($('#mobLayers').css('display') === "none") {
					$("input[name='mobLayers']").show();
			}
		},
		
		editSeriveTechnolyEvent : function(getData) {
			var self = this, addIcon = '<img src="themes/default/images/helios/plus_icon.png" border="0" alt="">';
			$("#appLayaer").hide();
			$("tr.applnLayer").hide();
			$("#webLayers").hide();
			$("tr.webLayer").hide();
			$("#mobLayers").hide();
			$("tr.mobLayer").hide();

			$.each(getData, function(index, value) {
				if (value.techInfo.appTypeId === "app-layer") {
					$("#appLayaer").show();
					$("tr.applnLayer").show();
					$('img[name="close"]').hide();
					var option = '';
					if(value.dependentModules !== null) {
						$.each(value.dependentModules, function(index, value){
							option += '<option selected>'+ value +'</option>';
						});
					} else {
						option = '';
					}	
					var appendData = '<tr class="applnlayercontent" name="dynamicAppLayer"><td><input type="text" value="'+value.code+'" disabled></td><td><select disabled>'+ self.getTechnology(value.techInfo.id) +'</select></td><td colspan="3"><select disabled><option>'+value.techInfo.version+'</option></select></td><td colspan="2" class="appdependencyTd"><select class="selectpicker appdependencySelect" multiple title="Select Dependency" data-selected-text-format="count>2"><option value="0" disabled>Select Dependency</option>'+ option +'</select></td><td><div class="flt_right icon_center"><a name="addApplnLayer"></a><a href="javascript:;" name="removeApplnLayer"></a></div></td></tr>';
					$("tbody.applnLayer").append(appendData);
					self.multiselect();
				} else if (value.techInfo.appTypeId === "web-layer") {
					$("#webLayers").show();
					$("tr.webLayer").show();
					$('img[name="close"]').hide();
					var appendData = '<tr class="weblayercontent" name="dynamicWebLayer"><td><input type="text" value="'+value.code+'" disabled></td><td><select name="weblayer" disabled><option>'+value.techInfo.techGroupId+'</option></select></td><td name="widget"><select name="web_widget" disabled> '+ self.editgetwidgettype(value.techInfo.id) +'</select></td> <td name="widgetversion"><select name="web_version" disabled><option>'+value.techInfo.version+'</option></select></td><td class="webdependencyTd"><select class="selectpicker webdependencySelect" multiple data-selected-text-format="count>2" title="Select Dependency"><option value="0" disabled>Select Dependency</option>'+ option +'</select></td><td><div class="flt_right icon_center"><a href="javascript:;" name="addWebLayer"></a><a href="javascript:;" name="removeWebLayer"></a></div></td></tr>';
					$("tbody.WebLayer").append(appendData);
					self.multiselect();
				} else if (value.techInfo.appTypeId === "mobile-layer" || value.techInfo.appTypeId === "mob-layer") {
					$("#mobLayers").show();
					$("tr.mobLayer").show();
					$('img[name="close"]').hide();
					var versionMsg = value.techInfo.version;
					if(versionMsg === ""){
						versionMsg = "No Version Available";
					}
					var appendData = '<tr class="mobilelayercontent" name="dynamicMobileLayer"><td><input type="text" value="'+value.code+'" disabled></td><td><select disabled><option>'+value.techInfo.techGroupId+'</option></select></td><td><select name="mobile_types" disabled>'+self.editgetmobiletype(value.techInfo.id)+'</select></td><td colspan="2"><select disabled><option>'+versionMsg +'</option></select></td><td><div class="flt_right icon_center"><a href="javascript:;" name="addMobileLayer"></a><a href="javascript:;" name="removeMobileLayer"></a> </div></td></tr>';
					$("tbody.MobLayer").append(appendData);
					self.multiselect();
				}
			});
			$("tr[name=dynamicAppLayer]:last").find("a[name=addApplnLayer]").html(addIcon);
			$("tr[name=dynamicWebLayer]:last").find("a[name=addWebLayer]").html(addIcon);
			$("tr[name=dynamicMobileLayer]:last").find("a[name=addMobileLayer]").html(addIcon);
			self.addLayersEvent();
		},

		
		 /**
         * Called during the page refresh, displays the message, and total number of records
         * @response: response from the service
         */
        pageRefresh : function(response) {
			commonVariables.navListener.getMyObj(commonVariables.projectlist, function(projectlistObj){
				projectlistObj.loadPage(true);
			});
		},
		
		createproject : function(projectId, action) {
			
			var self = this;
			if(!self.validation()) {
				var projectname = $("input[name='projectname']").val();
				var projectcode = $("input[name='projectcode']").val();
				var projectversion = $("input[name='projectversion']").val();
				var projectdescription = $("textarea[name='projectdescription']").val();
				var startdate = $("input[name='startdate']").val();
				var enddate = $("input[name='enddate']").val();
				//To convert dd/mm//yyyy to (month date,year) format
				if((startdate.length !== 0) && (enddate.length !== 0)){
					var myStartDate = new Date(startdate);
					var myEndDate = new Date(enddate);
				} else {
					var myStartDate = null;
					var myEndDate = null;
				}	
				var multimodule = $("input[name='multimodule']").val() === "true"? true:false;
				if($("select[name=builtmyself]").find(":selected").val() !== undefined) {
					var	preBuilt = $("select[name=builtmyself]").find(":selected").val() === "custom"? false:true;
				}	
				var count = 0;
				self.customerIds = [];
				self.appInfos = [];
				self.appInfosweb = [];
				self.appInfosmobile = [];
				self.customerIds.push(self.getCustomer());
				self.projectInfo.version = projectversion;
				self.projectInfo.name = projectname;
				self.projectInfo.projectCode = projectcode;
				self.projectInfo.description = projectdescription;
				self.projectInfo.startDate = myStartDate;
				self.projectInfo.endDate = myEndDate;
				self.projectInfo.preBuilt = preBuilt;
				self.projectInfo.multiModule = multimodule;
				self.projectInfo.customerIds = self.customerIds;
							
				$.each($("tbody[name='layercontents']").children(), function(index, value){
				
					var techInfo = {};
					var tech;
					var appInfoId = "";
					var techName = "";
					var code = "";
					var dependency = "";
					var appInfo = {};
					var mobdata = {};
					var versionText = "";
					
					if($(value).attr('class') === "applnLayer" && $(value).attr('key') === "displayed") {
						var applnlayerDiv = $(value).children('td.appln').children('table.applnlayer').children('tbody.applnlayer');
						$.each($(applnlayerDiv).children(), function(index, value){
							if(($(value).css('display') !== "none") && ($(value).attr('name') === "staticApplnLayer")) {
								var appInfo = {};
								var techInfo = {};
								tech = $(value).children("td.technology").children("select.appln_technology");
								appInfoId = $(value).children("td.technology").children("select.appln_technology").attr('appInfoId');
								techName = $(tech).find(":selected").text();
								code = $(value).children("td.applnappcode").children("input.appln-appcode").val();
								dependency = $(value).children('td.appdependencyTd').children("select.appdependencySelect").val();
								if(appInfoId !== undefined && appInfoId !== null) {
									appInfo.id = appInfoId;
								}
								appInfo.code = code;
								appInfo.appDirName = code;
								appInfo.version = projectversion;
								appInfo.name = code;
								if(dependency !== null){
									appInfo.dependentModules = dependency;
								}	
								techInfo.id = $(value).children("td.technology").children("select.appln_technology").val();
								techInfo.appTypeId = "app-layer";
								techInfo.name = $(value).children("td.technology").children("select.appln_technology").find(":selected").text();
								techInfo.version = $(value).children("td.version").children("select.appln_version").val();
								if (appInfo.code !== undefined && appInfo.code !== null) {
									appInfo.techInfo = techInfo;
									if(preBuilt){
										var selectedPilot = $("select[name='prebuiltapps']").find(':selected').text();
										var selectedPilotData = commonVariables.api.localVal.getJson(selectedPilot);
										$.each(selectedPilotData.appInfos, function(index, appInfo){
											if(appInfo.techInfo.appTypeId === "app-layer"){
												//console.info('appInfo.techInfo.appTypeId = '  , appInfo.techInfo.appTypeId);
												appInfo.appDirName = code;
												appInfo.code = code;
												appInfo.name = code;
												appInfo.version = projectversion;
												self.appInfos.push(appInfo);
											}
										});
									}else{
										self.appInfos.push(appInfo);
									}
									count++;
								}
							}	
						});	
					} 
					
					if(($(value).attr('class') === "webLayer") && ($(value).attr('key') === "displayed")) {
						var weblayerDiv = $(value).children('td.WebLayer').children('table.WebLayer').children('tbody.WebLayer');
						$.each($(weblayerDiv).children(), function(index, value){
							if($(value).css('display') !== "none" && $(value).attr('name') === "staticWebLayer") {
								var appInfo = {};
								var techInfo = {};
								tech = $(value).children("td.widget").children("select.web_widget");
								techName = $(tech).find(":selected").text();
								code = $(value).children("td.webappcode").children("input.web-appcode").val();
								dependency = $(value).children('td.webdependencyTd').children("select.webdependencySelect").val();
								appInfoId = $(value).children("td.web").children("select.weblayer").attr('appInfoId');
								if(appInfoId !== undefined && appInfoId !== null){
									appInfo.id = appInfoId;
								}
								appInfo.code = code;
								appInfo.appDirName = code;
								appInfo.version = projectversion;
								appInfo.name = code;
								if(dependency !== null){
									appInfo.dependentModules = dependency;
								}		
								techInfo.id = $(value).children("td.widget").children("select.web_widget").val();
								techInfo.appTypeId = "web-layer";
								techInfo.techGroupId = $(value).children("td.web").children("select.weblayer").find(":selected").text();
								techInfo.version = $(value).children("td.widgetversion").children("select.web_version").find(":selected").text();
								techInfo.name = $(value).children("td.widget").children("select.web_widget").find(":selected").text();
								if (appInfo.code !== undefined && appInfo.code !== null) {
									appInfo.techInfo = techInfo;
									if(preBuilt){
										var selectedPilot = $("select[name='prebuiltapps']").find(':selected').text();
										var selectedPilotData = commonVariables.api.localVal.getJson(selectedPilot);
										//console.info('selectedPilotData  web layer = ' , selectedPilotData.appInfos);
										$.each(selectedPilotData.appInfos, function(index, appInfo){
											if(appInfo.techInfo.appTypeId === "web-layer"){
												//console.info('appdirname = '  , appInfo.appDirName)
												//console.info('appInfo.techInfo.appTypeId = '  , appInfo.techInfo.appTypeId);
												appInfo.appDirName = code;
												appInfo.code = code;
												appInfo.name = code;
												appInfo.version = projectversion;
												self.appInfos.push(appInfo);
											}
										});
									}else{
										self.appInfos.push(appInfo);
									}	
									count++;
								}
							}	
						});	
					}  

					if(($(value).attr('class') === "mobLayer") && ($(value).attr('key') === "displayed")) {
						var mobilelayerDiv = $(value).children('td.mob').children('table.mob-table').children('tbody');
						$.each($(mobilelayerDiv).children(), function(index, value){
							if($(value).css('display') !== "none" && $(value).attr('name') === "staticMobileLayer") {
								var appInfo = {};
								var techInfo = {};
								versionText = $(value).children("td.mobileversion").children("select.mobile_version").find(":selected").text();
								tech = $(value).children("td.types").children("select.mobile_types");
								techName = $(tech).find(":selected").text();
								code = $(value).children("td.mobileappcode").children("input.mobile-appcode").val();
								appInfoId = $(value).children("td.mobile").children("select.mobile_layer").attr('appInfoId');
								if(appInfoId !== undefined && appInfoId !== null){
									appInfo.id = appInfoId;
								}
								appInfo.code = code;
								appInfo.appDirName = code; 
								appInfo.version = projectversion;
								appInfo.name = code; 
								techInfo.id = $(value).children("td.types").children("select.mobile_types").find(':selected').val();
								techInfo.appTypeId = "mobile-layer";
								techInfo.techGroupId = $(value).children("td.mobile").children("select.mobile_layer").find(':selected').text();
								techInfo.name = $(value).children("td.types").children("select.mobile_types").find(':selected').text();
								if(versionText === "No Versions available") {
									techInfo.version = "";
								} else {
									techInfo.version = $(value).children("td.mobileversion").children("select.mobile_version").find(":selected").text();
								}
								if (appInfo.code !== undefined && appInfo.code !== null) {
									appInfo.techInfo = techInfo;
									if(preBuilt){
										var selectedPilot = $("select[name='prebuiltapps']").find(':selected').text();
										var selectedPilotData = commonVariables.api.localVal.getJson(selectedPilot);
										$.each(selectedPilotData.appInfos, function(index, appInfo){
											if(appInfo.techInfo.appTypeId === "mob-layer"){
												//console.info('appInfo.techInfo.appTypeId = '  , appInfo.techInfo.appTypeId);
												appInfo.appDirName = code;
												appInfo.code = code;
												appInfo.name = code;
												appInfo.version = projectversion;
												self.appInfos.push(appInfo);
											}
										});
									}else{
										self.appInfos.push(appInfo);
									}
									count++;
								}
							}
						});
					}    	
					
				});
				var appInfos = $.merge($.merge($.merge([],self.appInfos), self.appInfosweb), self.appInfosmobile);
				self.projectInfo.noOfApps = count;
				self.projectInfo.appInfos = appInfos;
				self.projectRequestBody = self.projectInfo;
				
				if(projectId !== '') {
					self.projectInfo.id = projectId;
				}
				
				self.appDepsArray = [];
				self.getEditProject(self.getRequestHeader(self.projectRequestBody, "", action), function(response) {
					self.counter = null;
					self.projectRequestBody = {};
					commonVariables.api.localVal.deleteSession("projectId");
					self.getEditProject(self.getRequestHeader(self.projectRequestBody, "", "projectlist"), function(response) {
						self.pageRefresh(response);
					});
				});
			}
		}
	});

	return Clazz.com.components.projects.js.listener.projectsListener;
});