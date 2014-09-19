//transfer server configuration under client.
angular.module("flexdms.serverConfig", []).run(function(){
	if (angular.isDefined(appctx) && angular.isDefined(appctx.config) ){
		for (var name in appctx.config){
			var value=appctx.config[name];
			if (value.toUpperCase() == 'TRUE'
				|| value.toUpperCase() == 'YES' || value.toUpperCase() == 'ON') {
				value=true;
			} else if (value.toUpperCase() == 'FALSE'
				|| value.toUpperCase() == 'NO' || value.toUpperCase() == 'OFF') {
				value=false;
			}
			appctx.config[name]=value;
			
		}
		
		var configObj=flexdms.unflatObject(appctx.config);
		for (var name in configObj){
			flexdms.config[name]=configObj[name];
		}
	}
});
