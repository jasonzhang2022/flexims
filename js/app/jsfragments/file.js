flexdms.fileserviceUrl=appctx.modelerprefix+"/rs/file/";
angular.module("instDirective").directive("fxFileUploader",function(){
	return { 
		restrict: 'A',
		priority: 500,
		controller : function($scope, $element, $attrs, $fileUploader, $http){
			
			var fileSchema=flexdms.config.fileSchema;
			if ($attrs.fxFileUploader){
				fileSchema=$attrs.fxFileUploader;
			}
			if ($scope.type.getExtraProp("FILESCHEMA")!=null){
				fileSchema=$scope.type.getExtraProp("FILESCHEMA");
			}
			if ($scope.propobj.getExtraProp("FILESCHEMA")!=null){
				fileSchema=$scope.propobj.getExtraProp("FILESCHEMA");
			}
			var multiple=$scope.propobj.isElementCollection();
			
			
			
			var uploader = $scope.uploader = $fileUploader.create({
				scope: $scope,   
				// url:flexdms.fileserviceUrl+"upload/"+fileSchema,
				url:appctx.modelerprefix+"/fileuploader/"+fileSchema,
				autoUpload: true,
			});
			
			function syncPropValueToQueue(){
				if (!angular.isDefined( $scope.inst[$scope.propobj.getName()]) ||  $scope.inst[$scope.propobj.getName()]==null){
					return;
				}
				var fileIDs=new Array();
				if (multiple){
					angular.forEach($scope.inst[$scope.propobj.getName()], function(propvalue){
						var parts=propvalue.split(":");
						fileIDs.push({
							schema:parts[0],
							id:parts[1],
						});
					});
				} else {
					var propvalue=$scope.inst[$scope.propobj.getName()];
					var parts=propvalue.split(":");
					fileIDs.push({
						schema:parts[0],
						id:parts[1],
					});
				}
				$http.post(flexdms.fileserviceUrl+"fileinfos", {
					"fileIDs": {
						"fileIDs":fileIDs
					}
				}).success(function(data){
					angular.forEach(data.fileInfos.fileInfos, function(f){
						uploader.queue.push({
							'file':f,
							progress: 100,
							isUploaded: true,
							isSuccess: true,
							status:"success",
						}); 
					});
				});
			}
			$scope.$watch("inst", function(){
				//embedded inst could be changed
				$scope.inst.$promise.then(syncPropValueToQueue);
			});
			
			function syncQueueToPropValue(){
				if (multiple){
					$scope.inst[$scope.propobj.getName()]=[];
					angular.forEach(uploader.queue, function(item){
						if (angular.isDefined(item.file.fileID)){
							$scope.inst[$scope.propobj.getName()].push(item.file.fileID.schema+":"+item.file.fileID.id);
						}
					});
				} else {
					if (uploader.queue.length>0 && angular.isDefined(uploader.queue[0].file.fileID)){
						$scope.inst[$scope.propobj.getName()]=uploader.queue[0].file.fileID.schema+":"+uploader.queue[0].file.fileID.id;
					} else {
						$scope.inst[$scope.propobj.getName()]=null;
					}
					
				}
			}
			uploader.bind('afteraddingfile', function (event, item) {
				console.info('After adding file', item);
				if (!multiple &&  uploader.queue.length>1){
					$scope.deleteFile(0);
				}
			});
			uploader.bind('beforeupload', function (event, item) {
				console.info('Before upload', item);
				item.status="loading";
			});
			
			uploader.bind('success', function (event, xhr, item, response) {
				console.info('Success', xhr, item, response);
				if (angular.isDefined(response.fileInfo)){
					item.file=response.fileInfo;
					syncQueueToPropValue();
				} else {
					item.status="error";
				}
				
			});
			uploader.bind('error', function (event, xhr, item, response) {
				console.info('Error', xhr, item, response);
				item.status="error";
			});
			
			$scope.deleteFile=function($index){
				return  $http.get(flexdms.fileserviceUrl+"totemp/"+uploader.queue[$index].file.fileID.schema+"/"+uploader.queue[$index].file.fileID.id)
				.success(function(data){
					if (data.appMsg.statuscode==0){
						uploader.removeFromQueue($index);
						syncQueueToPropValue();
					}
				});
			};
		}
	};
}).directive("fxClientFile",function(){
	return { 
		restrict: 'A',
		priority: 601,
		controller : function($scope, $element, $attrs, $fileUploader, $http){
			var multiple=$scope.propobj.isElementCollection();
			$scope.multiple=multiple;
			
			function retrievFileInfos(pvs){
				$scope.fileInfos=new Array();
				if (!angular.isDefined( pvs) ||  pvs==null){
					return;
				}
				var fileIDs=new Array();
				if (multiple){
					angular.forEach(pvs, function(propvalue){
						var parts=propvalue.split(":");
						fileIDs.push({
							schema:parts[0],
							id:parts[1],
						});
					});
				} else {
					var propvalue=pvs;
					var parts=propvalue.split(":");
					fileIDs.push({
						schema:parts[0],
						id:parts[1],
					});
				}
				$http.post(flexdms.fileserviceUrl+"fileinfos", {
					"fileIDs": {
						"fileIDs":fileIDs
					}
				}).success(function(data){
					angular.forEach(data.fileInfos.fileInfos, function(f){
						f.display=f.name+"("+(Math.round(f.size/1000))+"K)";
						f.href=flexdms.fileserviceUrl+"clientfile/"+f.fileID.schema+"/"+f.fileID.id;
						$scope.fileInfos.push(f);
					});
					
				});
			}
			$scope.$watch("propvalue", retrievFileInfos);
			if ($scope.type.isEntity()){
				$scope.downloadUrl=flexdms.fileserviceUrl+"clientfiles/"+$scope.type.getName()+"/"+$scope.inst.id+"/"+$scope.propobj.getName();
			} else {
				
				var pinst=$scope.inst[flexdms.parentinst];
				
				$scope.downloadUrl=flexdms.fileserviceUrl+"clientfiles/"+pinst[flexdms.insttype]+"/"+pinst.id+"/"+$scope.pscope.propobj.getName()+"."+$scope.propobj.getName();
			}
			
			
		}
	};
}).directive("fxServerFile",function(){
	return { 
		restrict: 'A',
		priority: 601,
		controller : function($scope, $element, $attrs,  $http, $modal){
			var multiple=$scope.propobj.isElementCollection();
			$scope.multiple=multiple;
			$scope.directory=$scope.propobj.getTypeObject().idx==16;
			$scope.sfile={
					
			};
			$scope.$watch("inst", function(){
				
				if ($scope.type.isEntity()){
					$scope.sfile.type=$scope.type.getName();
					$scope.sfile.instid=$scope.inst.id;
					$scope.sfile.propname=$scope.propobj.getName();
				} else {
					var pinst=$scope.inst[flexdms.parentinst];
					var pscope=$scope.instpscope;
					$scope.sfile.type=pinst[flexdms.insttype];
					$scope.sfile.instid=pinst.id;
					$scope.sfile.propname=pscope.propobj.getName()+"."+$scope.propobj.getName();
				}
				
			});
			function getPropUrl(){
				return $scope.sfile.type+"/"+$scope.sfile.instid+"/"+$scope.sfile.propname;
			}
			function getPropName(){
				if ($scope.type.isEntity()){
					return $scope.propobj.getName();
				} 
				var pscope=$scope.instpscope;
				return pscope.propobj.getName()+"."+$scope.propobj.getName();
			}
			function retrievFileInfos(pvs){
				$scope.fileInfos=new Array();
				if (!angular.isDefined( pvs) ||  pvs==null){
					return;
				}
				
				$http.get(flexdms.fileserviceUrl+"listserverfiles/"+getPropUrl()).success(function(data){
					angular.forEach(data.fileInfos.fileInfos, function(f){
						f.display=f.name+"("+(Math.round(f.size/1000))+"K)";
						if ($scope.directory){
							f.display=f.name;
						}
						f.href=flexdms.fileserviceUrl+"serverfiles/"+getPropUrl()+"/"+f.name;
						f.browserObject={
								typename: $scope.sfile.type,
								id:$scope.sfile.instid,
								propname: $scope.sfile.propname,
								dirname: f.name,
						
						};
						$scope.fileInfos.push(f);
					});
					
				});
			}
			$scope.$watch("propvalue", retrievFileInfos);
			$scope.downloadUrl=flexdms.fileserviceUrl+"serverfiles/"+getPropUrl();
			
			$scope.openFileBrowser=function(fileInfo){
				 $modal.open({
				      templateUrl: 'template/props/view/serverfile_modal.html',
				      controller: function($scope, type, instid, prop, dir, $modalInstance){
				    	$scope.type=type;
				    	$scope.instid=instid;
				    	$scope.prop=prop;
				    	$scope.dir=dir;
				    	$scope.close=function(){
				    		$modalInstance.dismiss("ok");
				    	};
				      },
				      resolve:{
				    	  type: function( ){
				    		  return $scope.sfile.type;
				    	  },
				    	  instid: function(){
				    		 return $scope.sfile.instid;
				    	  },
				    	  prop: function(){
				    		  return $scope.sfile.propname;
				    	  },
				    	  dir: function() {
				    		  return fileInfo.name;
				    	  }
				      }
				    });
			};
		}
	};
}).directive("fxServerFileBrowser",function(){
	return { 
		restrict: 'E',
		templateUrl:'template/props/view/serverfile_tree.html',
		controller : function($scope, $element, $attrs,  $http, instCache){
			var type=$scope.type;
			var instid=$scope.instid;
			var prop=$scope.prop;
			$scope.fileInfos=[];
			var baseurl=flexdms.fileserviceUrl+"listserverfiles/"+type+"/"+instid+"/"+prop;
			var starturl=$scope.dir?baseurl+"/"+$scope.dir:baseurl;
			$scope.options = {
					'data-drag-enabled': false,
					collapsed:true,
		    };
			function processFileInfo(f){
				f.href=flexdms.fileserviceUrl+"serverfiles/"+type+"/"+instid+"/"+prop+"/"+f.name;
				
				var index=f.name.lastIndexOf("/");
				if (index==-1){
					index=f.name.lastIndexOf("\\");
				}
				
				if (index==-1){
					f.shortname=f.name;
				} else {
					f.shortname=f.name.substring(index+1);
				}
				f.items=[];
				if (f.isDir){
					f.loaded=false;
				} else{
					f.loaded=true;
				}
			}
			
			instCache.getInst(type, instid).$promise.then(function(){
				$http.get(starturl).success(function(data){
					angular.forEach(data.fileInfos.fileInfos, function(f){
						processFileInfo(f);
						$scope.fileInfos.push(f);
					});
					
				});
			});
			
			
			$scope.mytoggle = function(scope) {
				
				if (!scope.fileInfo.loaded) {
					
					$http.get(baseurl+"/"+scope.fileInfo.name).success(function(data){
						angular.forEach(data.fileInfos.fileInfos, function(f){
							processFileInfo(f);
							scope.fileInfo.items.push(f);
							//DO we need apply?
						
						});
						scope.fileInfo.loaded=true;
						scope.toggle();
					});
				} else {
					scope.toggle();
				}
			};
			
			
		}
	};
});

