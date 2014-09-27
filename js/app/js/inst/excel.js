flexdms.excelWorksheet=function(worksheet, sheetname){
	this.name=sheetname; //worksheet name.
	this.skipped=0; //how many instances are skipped.
	this.type= null;  //type for this worksheet.
	this.datarowStart=2; // data row starts from. First row is 1. 
	this.headersPropMap=null; // what property each column is mapped to.
	this.sheet=worksheet; //worksheet. 
	
	//a list of available properties
	this.getProps=function(){
		if (this.type==null){
			return new Array();
		}
		return this.type.getProps();
	};
	
	//not used, 
	this.cvsdata=function(){
		return XLSX.utils.sheet_to_csv(this.sheet);
	};
	
	/*
	 * Best effort to map each column to a property.
	 */
	this.initializeHeaderMap=function(){
		if (this.type==null){
			return ;
		}
		for (var i=0; i<this.maxCol; i++){
			this.headersPropMap[i].prop=null;
			this.headersPropMap[i].skip=true;
		}
		
		var props=this.getProps();
		//use first row as header. 
		//If it is not, user has chance to adjust.
		var headerRowData=this.json[0];
		for (var i=0; i<headerRowData.length; i++){
			var v=headerRowData[i];
			if (v){
				v=v.replace(/ /g, ""); //remove wspace
			}
			if (!v){
				this.headersPropMap[i].skip=true;
			} else {
				this.headersPropMap[i].skip=false;
			}
			
			if (v){
				for (var j=0; j<props.length; j++){
					//case insensitive
					if (props[j].getName().toLowerCase()===v.toLowerCase()){
						this.headersPropMap[i].prop=props[j];
						break;
					}
				}
			}
		}
	};
	
	this.unmapProp=function(propMap){
		this.insts=null;
		this.skip=0;
		delete(this.valid);
	};
	
	//did we do a initial data conversion from string to typed value.
	this.convertedData= function(){
		return this.insts?true:false;
	};
	
	//convert a row record to an instance.
	this.rowDataToInst=function(rowindex, inst, Inst){
		var rowdata=this.json[rowindex];
		var inputobj={};
		for (var i=0; i<this.headersPropMap.length; i++){
			var propMap=this.headersPropMap[i];
			if (propMap.skip){
				continue;
			}
			inputobj[propMap.prop.getName()]=rowdata[i];
		}
		var stringInst=flexdms.unflatObject(inputobj);
		flexdms.copyFromStringValued(inst, stringInst, Inst);
		return inst;
	};
	
	/*
	 * convert data from string to expected type
	 * Each instanace has the value
	 * $valid: whether it is valid or not.
	 * $skip: should it be skipped from upload
	 * $validState: an array to show each column is valid or not.
	 *
	 *After this function, sheetstate has two extra properties.
	 *invalidInsts: an array containing all invalid instances.
	 *invalidInstsFirst10: first 10 invalid instances.
	 * 
	 */

	this.convertData=function(Inst){
		
		//if not exists, intialize
		if (!this.insts){
			this.insts=new Array();
			for (var i=this.datarowStart-1; i<this.json.length; i++){
				var newinst=Inst.newInst(this.type.getName(), {}); //do not use default value
				newinst.$validState=new Array();
				newinst.$skip=false;
				this.insts.push(newinst);
			}
		}
		
		var invalidInsts=[];
		
		for (var j=this.datarowStart-1; j<this.json.length; j++){
			var inst=this.insts[j-(this.datarowStart-1)];
			inst.$rowindex=j;
			this.rowDataToInst(j, inst, Inst);
			this.validateInst(inst);
			if (!inst.$valid){
				invalidInsts.push(inst);
			}
		}
		
		this.invalidInsts=invalidInsts;
	};
	
	
	
	//check whether an instance is valid or not.
	this.validateInst=function(inst){
		//clean valid state
		inst.$valid=true;
		inst.$validState=[];
		
		for (var i=0; i<this.headersPropMap.length; i++){
			var propMap=this.headersPropMap[i];
			if (propMap.skip){
				continue;
			}	
			var propobj=propMap.prop;
			var propvalue=inst[propobj.getName()];
			if (propobj.isRequired() && (!angular.isDefined(propvalue) || propvalue==null)){
				//required property has to have value.
				inst.$valid=false;
				inst.$validState[i]=false;
				continue;
			}
			var rawvalue=this.json[inst.$rowindex][i];
			if (angular.isDefined(rawvalue)){										
				
				if (propvalue==null){
					inst.$valid=false;
					inst.$validState[i]=false;
				} else {
					inst.$validState[i]=true;
				}
			} else {
				inst.$validState[i]=true;
			}
		}
		return inst;
	};
	//whether an instance is valid or not.
	this.isInstValid=function(rowindex){
		if (!this.insts){
			return true;
		}
		var inst=this.insts[rowindex-(this.datarowStart-1)];
		return inst.$valid;
	};
	
	
	
	//is the sheet valid or not
	this.isSheetValid=function(){
		if (angular.isDefined(this.valid)){
			return this.valid;
		}
		if (this.json.length==0){
			//no data
			return true;
		}
		if (!this.insts){
			return false;
		}
		if (!angular.isDefined(this.invalidInsts)){
			return true;
		}
		return this.invalidInsts.length==0?true:false;
	};
	
	
	//initiailization code
	this.json=XLSX.utils.sheet_to_json(this.sheet, {header:1});
	var maxCol=0;
	for (var i=0; i<this.json.length; i++){
		if (this.json[i].length>maxCol){
			maxCol=this.json[i].length;
		}
	}		
	this.maxCol=maxCol;
	
	this.headersPropMap=new Array();
	for (var i=0; i<maxCol; i++){
		this.headersPropMap.push({
			prop: null,
			skip:true
		});
	}
	
};


angular.module("flexdms.excelupload", ["flexdms.TypeResource","flexdms.InstResource", "ui.bootstrap", "ui.router"])
.config(function($stateProvider, $urlRouterProvider){
	
	//DO NOT support EMBEDDED PROPERTY.
	$stateProvider.state("upload", {
		url: '/upload',
		templateUrl: 'upload/upload.html',
		controller: function($scope, fxInstPopup, Inst, fxAlert, $timeout, $rootScope, $http){
			
			//no validate by default.
			$scope.validate=true;
			$scope.headerrow=1;
			$scope.typesrc="sheetname";
			$scope.errormsgs=[];
			$scope.$watch("typesrc", function(){
				if ($scope.typesrc!="sheetname"){
					$scope.headerrow=2;
				}
			});
			
			
			$scope.cleanErrors=function(){
				$scope.errormsgs.length=0;
			};
			$scope.addError=function(msg){
				$scope.errormsgs.push(msg);
			};
			
			var types=[];
			angular.forEach(flexdms.types, function(t){
				if (t.isEntity()){
					types.push(t);
				}
			});
			types.sort(function(a, b){
				return a.getName().localeCompare(b.getName());
			});
			
			$scope.types=types;
			
			
			function initSheets(){
				var sheets=[];
				var sheetnames=$scope.fileState.workbook.SheetNames;
				
				angular.forEach(sheetnames, function(sheetname){
					var sheetState=new flexdms.excelWorksheet($scope.fileState.workbook.Sheets[sheetname], sheetname);
					if (sheetState.json.length>0){
						sheets.push(sheetState);
					}
					
				});
				
				$scope.fileState.sheets=sheets;	
			}
			
			$scope.processUninvalidateExcel=function(){
				
				for (var i=0; i<$scope.fileState.sheets.length; i++){
					var sheetstate=$scope.fileState.sheets[i];
					sheetstate.datarowStart=$scope.headerrow+1;
					var typename=null;
					if ($scope.typesrc=='sheetname'){
						typename=sheetstate.name;
					} else {
						typename=sheetstate.json[0][0];
					}
					sheetstate.type=flexdms.findType(typename);
					if (sheetstate.type==null){
						$scope.addError("Could not find type for worksheet "+sheetstate.name+". The found type's name is "+typename+". It will be skipped");
						continue;
					}
					
					//process headerrow
					var headers=[];
					for (var j=0; j<sheetstate.json[$scope.headerrow-1].length; j++){
						var header=sheetstate.json[$scope.headerrow-1][j];
						if (angular.isDefined(header) && header!=null){
							header=header.replace(/ /g, "");
							headers.push(header);
						} else {
							headers.push(null);
						}
					}
					var insts=[];
					
					//process each data rows;
					for(var rowindex=$scope.headerrow; rowindex<sheetstate.json.length; rowindex++){
						var json=sheetstate.json[rowindex];
						var inputobj={};
						for (var j=0;j<headers.length; j++){
							var header=headers[j];
							if (header==null || !angular.isDefined(json[j]) || json[j]==null){
								continue;
							}
							inputobj[header]=json[j];
						}
						var newinst=Inst.newInst(sheetstate.type.getName()); //use default value
						newinst.$validState=new Array();
						newinst.$skip=false;
						
						insts.push(newinst);
						var stringInst=flexdms.unflatObject(inputobj);
						flexdms.copyFromStringValued(newinst, stringInst, Inst);
					}
					
					sheetstate.insts=insts;
				}
			};
			
			$scope.handleFile=function($event){
				//clean all state if file are reloaded.
				$scope.cleanErrors();
				$scope.saved=false;
				$scope.savemsg=null;
				$scope.saveerror=null;
				
				
				$scope.fileState={};
				$scope.fileState.loading=true;
				var files = $event.target.files;
				//put this in a timeout so that spinning icon is shown first.
				$timeout(function(){
					var i,f;
					//we only expects one file.
					for (i = 0, f = files[i]; i != files.length; ++i) {
						var reader = new FileReader();
						reader.onload = function(e) {
							var data = e.target.result;
							$scope.fileState.workbook = XLSX.read(data, {type: 'binary'});
							$scope.fileState.filename=f.name;  //file is ready.
							initSheets();
							if (!$scope.validate){
								$scope.processUninvalidateExcel();
							}
							$scope.fileState.loading=false;
							//update UI.
							$scope.$apply();
						};
						reader.readAsBinaryString(f);
					}
				}, 50);
				
			};
			
			$scope.correctInst=function(sheetstate, index){
				fxInstPopup.popupInstEditor(sheetstate.type.getName(), sheetstate.insts[index-(sheetstate.datarowStart-1)], true, false, true)
				.then(function(inst){
					sheetstate.validateInst(index);
				});
			};
			$scope.allsheetsValid=function(){
				for (var i=0; i<$scope.fileState.sheets.length; i++){
					if (!$scope.fileState.sheets[i].isSheetValid()){
						return false;
					}
				}
				return true;
			};
			$scope.upload=function(){
				var entities={};
				for (var i=0; i<$scope.fileState.sheets.length; i++){
					var sheetState=$scope.fileState.sheets[i];
					if (sheetState.json.length==0){
						continue;
					}
					if (!angular.isDefined(entities[sheetState.type.getName()])){
						entities[sheetState.type.getName()]=new Array();
					}
					var es=entities[sheetState.type.getName()];
					for(var j=0; j<sheetState.insts.length; j++) {
						if (sheetState.insts[j].$skip){
							continue;
						}
						//add default value and auto-generated value
						if ($scope.validate){
							sheetState.type.initInstance(sheetState.insts[j], false);
						}
						
						es.push(sheetState.insts[j]);
					}
					
				}
				
				$http.post(flexdms.instserviceurl+"/savebatch", {'entities': entities}, {fxAlertError: false}).
				then( function(ret){
					$scope.savemsg="Data is saved successfully.";
					$scope.saveerror=null;
					$scope.saved=true;
				}, function(error){
					$scope.saveerror=fxAlert.getErrorObject().msg;
					$scope.savemsg=null;
				});
			};
		}
	
	});
	
	
}).directive("fxExceluploadFile", function(){
	return {
		link: function($scope, $element, $attrs){
			$element.on("change", $scope.handleFile);
		}
	};
}).controller("fxExcelWorksheet", function($scope, Inst){
	$scope.showlabel=false;
	$scope.jsonFirst10=$scope.sheetstate.json.slice(0, 10);
	
	$scope.verifyRequired=function(){
		var props=$scope.sheetstate.getProps();
		$scope.requiredprop=null;
		for (var i=0; i<props.length; i++){
			var prop=props[i];
			if (prop.isRequired()){
				var found=false;
				for (var j=0; j<$scope.sheetstate.headersPropMap.length; j++){
					var propMap=$scope.sheetstate.headersPropMap[j];
					if (!propMap.skip && propMap.prop!=null && propMap.prop.getName()==prop.getName()){
						found=true;
						break;
					}
				}
				if (!found){
					$scope.requiredprop=prop;
					break;
				}
			}
		}
		
	};
	$scope.convertData=function(){
		
		$scope.verifyRequired();
		if ($scope.requiredprop!=null){
			return;
		}
		
		
		$scope.sheetstate.convertData(Inst);
		$scope.invalidInstsFirst10=$scope.sheetstate.invalidInsts.splice(0, 10);
		if ($scope.invalidInstsFirst10.length>0){
			$scope.sheetstate.valid=false;
		} 
	};
	
	
	
	//check the first 10 invalid instances to see whether they are valid or not.
	$scope.checkInvalidInsts=function(){
		
		for (var i=0; i<$scope.invalidInstsFirst10.length; i++){
			var inst=$scope.invalidInstsFirst10[i];
			if (inst.$skip){
				//do not check it.
				$scope.sheetstate.skipped++;
				continue;
			}
			$scope.sheetstate.validateInst(inst);
			if (!inst.$valid){
				$scope.sheetstate.invalidInsts.push(inst);
			}
		}
		if ($scope.sheetstate.invalidInsts.length>0){
			$scope.invalidInstsFirst10=$scope.sheetstate.invalidInsts.splice(0, 10);
		} else {
			$scope.invalidInstsFirst10.length=0;
			delete($scope.sheetstate.valid);
		}
	};
})
.controller("fxExceluploadRowScope", function($scope){
	$scope.rowindex=$scope.$index;
	
	$scope.showlabel=false;
	
}).service("instsToExcel", function(){
	
	function updateKey(srcObj, destObj){
		for(name in srcObj){
			destObj[name]=true;
		}
	}
	
	this.toExcel=function(insts, name){
		var objects=[];
		var keys={};
		for (var i=0; i<insts.length; i++){
			var outputobj={};
			flexdms.instToFlatObject(insts[i], outputobj);
			updateKey(outputobj, keys);
			objects.push(outputobj);
		}
		var ws = {};
		//cell(0, 0) is the type name.
		ws[XLSX.utils.encode_cell({c:0,r:0})]={v:insts[0][flexdms.insttype], t:'s'};
		var props=[];
		for(var prop in keys){
			if (prop.charAt(0)!=='$'){
				props.push(prop);
			}
		}
		//first row: prop name;
		for (var C=0; C<props.length; C++){
			ws[XLSX.utils.encode_cell({c:C,r:1})]={v:props[C], t:'s'};
		}
		//loop to add data.
		for(var R=0; R != objects.length; ++R) {
			for(var C = 0; C != props.length; ++C) {
				
				var cell = {v: objects[R][props[C]] };
				if(!angular.isDefined(cell.v)|| cell.v == null) {
					continue;
				}
				var cell_ref = XLSX.utils.encode_cell({c:C,r:R+2});
				
				if(typeof cell.v === 'number') {
					cell.t = 'n';
				} else if(typeof cell.v === 'boolean'){
					cell.t = 'b';
				} else {
					cell.t = 's';
				}
				ws[cell_ref] = cell;
			}
		}
		
		var range = {e: {c:props.length-1, r:objects.length+1}, s: {c:0, r:0 }};
		ws['!ref'] = XLSX.utils.encode_range(range);
		
		function Workbook() {
			if(!(this instanceof Workbook)) {
				return new Workbook();
			}
			this.SheetNames = [];
			this.Sheets = {};
		}
		
		var wb = new Workbook();
		
		/* add worksheet to workbook */
		wb.SheetNames.push(name);
		wb.Sheets[name] = ws;
		var wbout = XLSX.write(wb, {bookType:'xlsx', bookSST:false, type: 'binary'});
		
		function s2ab(s) {
			var buf = new ArrayBuffer(s.length);
			var view = new Uint8Array(buf);
			for (var i=0; i!=s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF;
			return buf;
		}
		saveAs(new Blob([s2ab(wbout)],{type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}), name+".xlsx");
		
	};
});
