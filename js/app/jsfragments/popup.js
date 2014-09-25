angular.module("instDirective").service("fxInstPopup", function($modal, Inst, $rootScope){
	
	this.popupInstEditor=function(typename, inst, showlabel, showsave, showok){
		var $scope1=$rootScope.$new();
		$scope1.typename=typename;
		$scope1.showlabel=angular.isDefined(showlabel)?showlabel:true;
		
		if (angular.isDefined(inst)){
			$scope1.edit=true;
			if (angular.isDefined(inst.$promise)){
				$scope1.inst=inst;
			} else {
				$scope1.inst=Inst.newInst(typename, inst);
			}
		} else {
			$scope1.inst=Inst.newInst(typename);
			$scope1.edit=false;
		}
		
		
		
		var modalInstance = $modal.open({
			templateUrl: 'template/props/edit/popupeditor.html',
			controller: function($scope, $modalInstance ){
				$scope.showsave=angular.isDefined(showsave)?showsave:true;
				$scope.showok=angular.isDefined(showok)?showok:false;
				
				$scope.save=function(){
					$scope.inst.$save(null, function(inst1){
						$modalInstance.close(inst1);
					});
				};
				$scope.ok=function(){
					$modalInstance.close($scope.inst);
				};
				$scope.cancel=function(){
					$modalInstance.dismiss('cancel');
				};
			},
			size: "lg",
			scope:$scope1
		});
		
		return modalInstance.result;
		
		
		
	};
	
	this.popupInstViewer=function(typename, inst, showlabel){
		var $scope1=$rootScope.$new();
		$scope1.typename=typename;
		$scope1.showlabel=angular.isDefined(showlabel)?showlable:true;
		if (angular.isDefined(inst.$promise)){
			$scope1.inst=inst;
		} else {
			$scope1.inst=Inst.newInst(typename, inst);
		}
		
		var modalInstance = $modal.open({
			templateUrl: 'template/props/view/popupviewer.html',
			controller: function($scope, $modalInstance){
				$scope.cancel=function(){
					$modalInstance.dismiss('cancel');
				};
			},
			size: "lg",
			scope:$scope1
		});
		
		return modalInstance.result;
		
		
	};
});
