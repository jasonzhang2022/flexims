
typeApp.controller("typeListCtrl", function($scope, Type){
	
	function init(){
		var fixeds=[];
		var systems=[];
		var dynamics=[];
		var modules={};
		
		angular.forEach(flexdms.types, function(t){
			var module=t.getExtraProp("module");
			if (module){
				modules[module]=true;
			}
		});
		
		
		angular.forEach(flexdms.types, function(t){
			if (t.isSystem()){
				systems.push(t);
				return;
			}
			if (t.isExtraProp("fixed", false)){
				fixeds.push(t);
			} else {
				dynamics.push(t);
			}
			
		});
		
		$scope.tabs = [
		               { title:'Self', types: dynamics, active:true},
		               { title:'System', types: systems, active:false},
		               { title:'All From Modules', types: fixeds, active:false},
		               ];
		for (var module in modules){
			var types=[];
			angular.forEach(flexdms.types, function(t){
				var module1=t.getExtraProp("module");
				if (module1 && module===module1){
					types.push(t);
				}
			});
			$scope.tabs.push({title:module, 'types': types, active:false});
		}
	}
	
	init();
	$scope.deleteType=function(type){
		Type.deletetype({typename:type.getName()}, function(data, headers){
			Type.assignnewmeta(data);
			init();
		});
	};
});
