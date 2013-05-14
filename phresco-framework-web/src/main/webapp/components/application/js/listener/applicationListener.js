define(["framework/widget", "framework/widgetWithTemplate", "application/api/applicationAPI"], function() {

	Clazz.createPackage("com.components.application.js.listener");

	Clazz.com.components.application.js.listener.ApplicationListener = Clazz.extend(Clazz.WidgetWithTemplate, {
		
		basePlaceholder :  window.commonVariables.basePlaceholder,
		applicationAPI : null,
		projectlistContent : null,

		/***
		 * Called in initialization time of this class 
		 *
		 * @config: configurations for this listener
		 */
		initialize : function(config) {
				this.applicationAPI = new Clazz.com.components.application.js.api.ApplicationAPI();
		},
		
		onCancelUpdate : function() {
			var self = this;
			Clazz.navigationController.jQueryContainer = commonVariables.contentPlaceholder;
			self.projectlistContent = commonVariables.navListener.getMyObj(commonVariables.projectlist);
			Clazz.navigationController.push(self.projectlistContent, true);
		},
		
		addServerDatabase : function(appType, whereToAppend) {
			var self = this, dynamicValue, server = '<tr class="servers"> <td data-i18n="application.edit.servers"></td><td><select><option>Select Servers</option></select></td><td data-i18n="application.edit.versions"></td><td colspan="3"><select><option>Select Version</option></select> <div class="flt_right"><a href="javascript:;"><img name="addServer" src="../themes/default/images/helios/plus_icon.png" width="25" height="20" border="0" alt=""></a> <a href="javascript:;"><img name="removeServer" src="../themes/default/images/helios/minus_icon.png"  width="25" height="20" border="0" alt=""></a></div></td></tr>',
			
			database ='<tr class="database"><td data-i18n="application.edit.database"></td><td><select><option>Select Database</option></select></td><td data-i18n="application.edit.versions"></td> <td colspan="3"><select> <option>Select Version</option></select><div class="flt_right"><a href="javascript:;"><img src="../themes/default/images/helios/plus_icon.png" name="addDatabase" width="25" height="20" border="0" alt=""></a> <a href="javascript:;"><img src="../themes/default/images/helios/minus_icon.png" name="removeDatabase" width="25" height="20" border="0" alt=""></a></div></td></tr>';
			if (appType === "addServer") {
				dynamicValue = $(server).insertAfter(whereToAppend);
				dynamicValue.prev('tr').find('img[name="addServer"]').removeAttr("src");
				dynamicValue.prev('tr').find('img[name="removeServer"]').attr("src","../themes/default/images/helios/minus_icon.png");
			} else {
				dynamicValue = $(database).insertAfter(whereToAppend);
				dynamicValue.prev('tr').find('img[name="addDatabase"]').removeAttr("src");
				dynamicValue.prev('tr').find('img[name="removeDatabase"]').attr("src","../themes/default/images/helios/minus_icon.png");
			}
			$("img[name=addServer]").unbind("click");
			$("img[name=addDatabase]").unbind("click");
			self.addServerDatabaseEvent();
			$("img[name=removeServer]").unbind("click");
			$("img[name=removeDatabase]").unbind("click");
			self.removeServerDatabaseEvent();
			
		},

		addServerDatabaseEvent : function(){
			var self=this, whereToAppend = '';
			$("img[name=addServer]").click(function(){
				whereToAppend = $("img[name=addServer]").parents('tr.servers:last');
				self.dynamicRenderLocales(commonVariables.contentPlaceholder);
				self.addServerDatabase($(this).attr('name'), whereToAppend);
			});
			
			$("img[name=addDatabase]").click(function(){
				whereToAppend = $("img[name=addDatabase]").parents('tr.database:last');
				self.dynamicRenderLocales(commonVariables.contentPlaceholder);
				self.addServerDatabase($(this).attr('name'), whereToAppend);
			});
		},
		
		removeServerDatabaseEvent : function() {
			var self=this;
			$("img[name=removeServer]").click(function(){
				$("img[name=addServer]").removeAttr('src');
				$(this).parent().parent().parent().parent().remove();
				$("img[name=removeServer]").parents('tr:last').find('img[name="addServer"]').attr("src", "../themes/default/images/helios/plus_icon.png");
				if (($("img[name=removeServer]").parents('tr.servers').length) === 1) {
					$("img[name=addServer]").attr("src", "../themes/default/images/helios/plus_icon.png");
					$("img[name=removeServer]").removeAttr('src');
				}
			});
			
			$("img[name=removeDatabase]").click(function(){
				$("img[name=addDatabase]").removeAttr('src');
				$(this).parent().parent().parent().parent().remove();
				$("img[name=removeDatabase]").parents('tr:last').find('img[name="addDatabase"]').attr("src", "../themes/default/images/helios/plus_icon.png");
				if (($("img[name=removeDatabase]").parents('tr.database').length) === 1) {
					$("img[name=addDatabase]").attr("src", "../themes/default/images/helios/plus_icon.png");
					$("img[name=removeDatabase]").removeAttr('src');
				}
			});
		},
		
		dynamicRenderLocales : function(contentPlaceholder) {
			var self=this;
			self.renderlocales(contentPlaceholder);
		}
		
	});

	return Clazz.com.components.application.js.listener.ApplicationListener;
});