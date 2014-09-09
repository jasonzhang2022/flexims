angular.module("typeER", []).controller("typeERCtrl", function($scope, $stateParams, $modal){
	
	$scope.type=$stateParams.type;
	
	
	
	
	/**
	 * Separate types into two elements: related and orphaned.
	 */
	function relatedtypes(){
		var relatedtypes={};
		var orphantypes=[];
		for (var i=0; i<flexdms.types.length; i++){
			var type=flexdms.types[i];
			var related=false;
			if (!type.isEntity()){
				related=true;
			}
			
			var props=type.getSelfProps();
			for (var j=0; j<props.length; j++){
				var propobj=props[j];
				var proptype=propobj.getTypeObject();
				if (proptype.isEmbedded() || proptype.isRelation()){
					related=true;
					relatedtypes[proptype.value]=flexdms.findType(proptype.value);
				}
			}
			
			var updown=type.upanddown();
			for (var tname in updown){
				related=true;
				relatedtypes[tname]=updown[tname];
			}
			
			if (related){
				relatedtypes[type.getName()]=type;
			} 
		}
		for (var i=0; i<flexdms.types.length; i++){
			var type=flexdms.types[i];
			if (!relatedtypes[type.getName()]){
				orphantypes.push(type);
			}
		}
		var rs=[];
		for (var tname in relatedtypes){
			rs.push(relatedtypes[tname]);
		}
		return [rs, orphantypes];
	}
	
	function pickroots(types){
		
		//roots
		var roots=[];
		for (var i=0; i<types.length; i++){
			var t=types[i];
			if (t.getParentTypeName()==null){
				roots.push(t);
			}
		}
		return roots;
	}
	
	
	
	function clusters(){
		var ro=relatedtypes();
		var cs=[];
		
		var roots=pickroots(ro[0]);
		
		function processed(type){
			for (var i=0; i<cs.length; i++){
				for (var tname in cs[i]){
					if (tname===type.getName()){
						return true;
					}
				}
			}
			return false;
		}
	
		for (var i=0; i<roots.length; i++){
			if (! processed(roots[i])){
				var c=roots[i].cluster();
				cs.push(c);
			}
		}
		
		var c=[];
		for (var i=0; i<ro[1].length; i++){
			var t=ro[1][i];
			c[t.getName()]=t;
		}
		cs.push(c);
		return cs;
	}
	
	/**
	 * If a coordinate is occupied, move it a little bit.
	 */
	function avoidConflict(coords, xy){
		var x=xy[0];
		var y=xy[1];
		var key=x+"."+y;	
		while(coords[key]) {
			x=x+100;
			key=x+"."+y;
		}
		return [x, y];
	}
	
	/**
	 * put the relation cell in a position to minimize conflict.
	 */
	function calculateXY(fromcell,tocell){
		var topcell;
		var bottomcell;
		if (fromcell.attributes.position.y<tocell.attributes.position.y){
			topcell=fromcell;
			bottomcell=tocell;
		} else {
			topcell=tocell;
			bottomcell=fromcell;
		}
		var topx=topcell.attributes.position.x;
		var topy=topcell.attributes.position.y;
		var bottomx=bottomcell.attributes.position.x;
		var bottomy=bottomcell.attributes.position.y;
		var x;
		var y;
		if (topy==bottomy){
			//in same row;
			x=450; //in the middle
			y=topy-100;
		} else if (topx==bottomx) {
			//in the same column.
			if (topx==300){
				x=150;
			} else {
				x=750;
			}
			y=(bottomy+topy)/2;
		} else {
			//corss row and column
			x=450;
			y=(bottomy+topy)/2;
		}
		return [x, y];
	}
	
	$scope._draw=function(){
		var graph = new joint.dia.Graph;

		
		var paper = new joint.dia.Paper({

		    el: $('.panel-body .typeser'),
		    width: 900,
		    height: Math.ceil(flexdms.types.length/2)*300,
		    gridSize: 1,
		    model: graph
		});
		
		var erd = joint.shapes.erd;
		var coords={};
		var element = function(elm, x, y, label) {
		   var cell = new elm({ position: { x: x, y: y }, attrs: { text: { text: label }}});
		    graph.addCell(cell);
		    coords[x+"."+y]=true;
		    return cell;
		};

		var link = function(elm1, elm2) {
		    var myLink = new erd.Line({ source: { id: elm1.id }, target: { id: elm2.id }});
		    graph.addCell(myLink);
		    return myLink;
		};

		var cs=null;
		if ($stateParams.type==='_ALL'){
			cs=clusters();	
		} else {
			cs=[flexdms.findType($stateParams.type).cluster(), {}];
		}
		
		
		
		var cells={};
		
		var line=0;
		
		var spacelen=300;
		for (var i=0; i<cs.length-1; i++){
			var cluster=cs[i];
			var count=0;
			
			//lay the node itself.
			for (var tname in cluster){
				var type=cluster[tname];
				var x=spacelen+count%2*spacelen;
				var y=150+Math.floor(line+count/2)*spacelen;
				
				var cell;
				if (type.isEntity()){
					cell=element(erd.Entity, x, y, tname);
				} else {
					cell=element(erd.WeakEntity, x, y, tname);
				}
				
				cells[tname]=cell;
				count++;
			}
			line+=Math.ceil(count/2);
			
			//----------------------construct inheritance
			for (var tname in cluster){
				var type=cluster[tname];
				var ptype=type.getParentType();
				if (ptype==null){
					continue;
				}
				var pcell=cells[ptype.getName()];
				var subcell=cells[type.getName()];
				var xy=calculateXY(pcell, subcell);
				xy=avoidConflict(coords, xy);
				var relcell= element(erd.ISA, xy[0], xy[1],"Inherit");
				var leftlink=link(pcell, relcell);
				leftlink.set('router', { name: 'metro' });
				var rightlink=link(relcell, subcell);
				rightlink.set('router', { name: 'metro' });
			}
			
			//-----------------------construct relationship
			for (var tname in cluster){
				var type=cluster[tname];
				var props=type.getSelfProps();
				for (var j=0; j<props.length; j++){
					var propobj=props[j];
					var proptype=propobj.getTypeObject();
					if (!proptype.isEmbedded() && !proptype.isRelation()){
						continue;
					}
					if (propobj.getMappedBy()){
						//skip birection properties
						continue;
					}
					var fromcell=cells[type.getName()];
					var tocell=cells[proptype.value];
					
					var xy=calculateXY(fromcell, tocell);
					xy=avoidConflict(coords, xy);
					var relcell= element(erd.Relationship, xy[0], xy[1], propobj.getName());
					var leftlink=link(fromcell, relcell);
					var rightlink=link(relcell, tocell);
					if (type.getName()===proptype.value){
						leftlink.set('router', { name: 'manhattan' });
						//rightlink.set('router', { name: 'metro' });
					}
					
					
					
					if (propobj.isOneToOne()){
						leftlink.cardinality('1');
						rightlink.cardinality('1');
					} else if (propobj.isOneToMany()){
						leftlink.cardinality('1');
						rightlink.cardinality('N');
					}else if (propobj.isManyToMany()){
						leftlink.cardinality('N');
						rightlink.cardinality('N');
					}else if (propobj.isManyToOne()){
						leftlink.cardinality('N');
						rightlink.cardinality('1');
					} else if (propobj.isEmbeddedElementCollection()){
						leftlink.cardinality('1');
						rightlink.cardinality('N');
					}else if (propobj.isEmbedded()){
						leftlink.cardinality('1');
						rightlink.cardinality('1');
					}
				}
			}
			
		}
		
		var starty=line*spacelen;
		//orphaned types
		spacelen=150;
		var cluster=cs[cs.length-1];
		var count=0;
		for (var tname in cluster){	
			var x=spacelen+count%4*spacelen;
			var y=starty+spacelen+Math.floor(count/4)*spacelen;
			var cell=element(erd.Entity, x, y, tname);
			cells[tname]=cell;
			count++;
		}
		
		return graph;
		
	};
	
	//not working correctly.
	$scope.save=function(){
		var svgdata=$('.panel-body .typeser').html();
		open("data:image/svg+xml," + encodeURIComponent(svgdata));
	};
	
	$scope._draw();
}).config(function($stateProvider, $urlRouterProvider) {
	
	  $urlRouterProvider.when("/erdiagram", "/erdiagram/_ALL");
	  //
	  // Now set up the states
	  $stateProvider.state('erdiagram', {
		      url: "/erdiagram/:type",
		      templateUrl: "type/diagram.html",
		      controller:"typeERCtrl"
		})
		;
	    
	    
});

