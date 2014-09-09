
typeApp.controller("regexpCtrl", function ($scope, $modalInstance) {

	 
		$scope.positivematch=true;
		$scope.negativematch=false;
	  $scope.showverify=false;
	  $scope.verifyExpexp=function(){
		
		  var regex=null;
		  if ($scope.extraprops.ignorepatterncase){
			  regex=new RegExp($scope.extraprops.pattern, "i");
		  } else{
			  regex=new RegExp($scope.extraprops.pattern);
		  }
			 //use this.postivestr
		  if (regex.test(this.postivestr)){
			  this.positivematch=true;
		  } else{
			  this.positivematch=false;
		  }
			  
		  if (regex.test(this.negativestr)){
			  this.negativematch=true;
		  } else {
			  this.negativematch=false;
		  }
		  $scope.showverify=true;
	  };

	

	  $scope.close = function () {
	    $modalInstance.dismiss('cancel');
	  };
	});