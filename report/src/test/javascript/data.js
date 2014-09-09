var alltracedown={
		   "entities" : {
			      "DefaultTypedQuery" : [ {
			         "fxversion" : "2014-04-25T23:51:57.228331-07:00",
			         "Name" : "testpage",
			         "id" : 10350,
			         "Description" : "page",
			         "TargetedType" : "Basictype",
			         "Conditions" : [ {
			            "Description" : "prop integer bigger than 50",
			            "CollectionMode" : "all",
			            "Operator" : "eq",
			            "FirstValue" : "50",
			            "WholeTime" : false,
			            "IgnoreCase" : false,
			            "Property" : "propint"
			         } ]
			      } ]
			   }
			};
var fxReport={
		   "FxReport" : {
			      "fxversion" : "2014-05-02T10:39:17.037579-07:00",
			      "ParamValues" : {
			         "paramValues" : [ {
			            "index" : 0
			         }, {
			            "index" : 1,
			            "values" : [ "2014-05-02T17:38:05.000Z" ]
			         }, {
			            "index" : 2,
			            "values" : [ "2014-05-02T16:40:05.000Z" ]
			         } ]
			      },
			      "Name" : "testfxreport",
			      "id" : 10300,
			      "Query" : 10350,
			      "OrderBy" : {
			      },
			      "Properties" : {
			      }
			   }
			}
var timeunits={
		   "nameValueList" : {
			      "entry" : [ {
			         "key" : "WEEK",
			         "value" : "WEEK"
			      }, {
			         "key" : "MONTH",
			         "value" : "MONTH"
			      }, {
			         "key" : "YEAR",
			         "value" : "YEAR"
			      }, {
			         "key" : "DAY",
			         "value" : "DAY"
			      }, {
			         "key" : "HOUR",
			         "value" : "HOUR"
			      }, {
			         "key" : "MINUTE",
			         "value" : "MINUTE"
			      } ]
			   }
			};
var operators={
		   "nameValueList" : {
			      "entry" : [ {
			         "key" : "lt",
			         "value" : "<"
			      }, {
			         "key" : "le",
			         "value" : "<="
			      }, {
			         "key" : "eq",
			         "value" : "="
			      }, {
			         "key" : "gt",
			         "value" : ">"
			      }, {
			         "key" : "ge",
			         "value" : ">="
			      }, {
			         "key" : "ne",
			         "value" : "!="
			      }, {
			         "key" : "oneof",
			         "value" : "contains"
			      }, {
			         "key" : "notoneof",
			         "value" : "not contain"
			      }, {
			         "key" : "between",
			         "value" : "between"
			      }, {
			         "key" : "notbetween",
			         "value" : "not between"
			      }, {
			         "key" : "checked",
			         "value" : "checked"
			      }, {
			         "key" : "unchecked",
			         "value" : "unchecked"
			      }, {
			         "key" : "like",
			         "value" : "like"
			      }, {
			         "key" : "notlike",
			         "value" : "not like"
			      }, {
			         "key" : "notnull",
			         "value" : "has value"
			      }, {
			         "key" : "isnull",
			         "value" : "no value"
			      }, {
			         "key" : "size",
			         "value" : "number of elements"
			      }, {
			         "key" : "sizeGt",
			         "value" : "number of elements more than"
			      }, {
			         "key" : "sizeLt",
			         "value" : "number of elements less than"
			      }, {
			         "key" : "empty",
			         "value" : "empty"
			      }, {
			         "key" : "notempty",
			         "value" : "not empty"
			      }, {
			         "key" : "tracedown",
			         "value" : "trace down"
			      } ]
			   }
			};

var buildTraceDown={
		   "entities" : {
			      "DefaultTypedQuery" : [ {
			         "fxversion" : "2014-05-02T10:45:09.471027-07:00",
			         "Name" : "build",
			         "id" : 10553,
			         "TargetedType" : "Mdoombuild",
			         "Conditions" : [ {
			            "TraceDown" : 10552,
			            "CollectionMode" : "all",
			            "Operator" : "tracedown",
			            "WholeTime" : false,
			            "IgnoreCase" : false,
			            "Property" : "students"
			         }, {
			            "CollectionMode" : "all",
			            "Operator" : "like",
			            "FirstValue" : "build%",
			            "WholeTime" : false,
			            "IgnoreCase" : false,
			            "Property" : "name"
			         } ]
			      }, {
			         "fxversion" : "2014-05-02T10:42:35.487679-07:00",
			         "Name" : "student",
			         "id" : 10552,
			         "TargetedType" : "Mstudent",
			         "Conditions" : [ {
			            "CollectionMode" : "all",
			            "Operator" : "like",
			            "FirstValue" : "jason%",
			            "WholeTime" : false,
			            "IgnoreCase" : false,
			            "Property" : "Name"
			         } ]
			      } ]
			   }
			};


var reportbytypForMdoomroom={
		   "entities" : {
			      "FxReport" : [ {
			         "fxversion" : "2014-05-16T13:34:37.275398-07:00",
			         "Name" : "ALL Mdoomroom",
			         "id" : 10055,
			         "Query" : 10057,
			         "Properties" : {
			            "entry" : [ {
			               "key" : "0",
			               "value" : "number"
			            }, {
			               "key" : "1",
			               "value" : "name"
			            } ]
			         }
			      }, {
			         "fxversion" : "2014-05-19T14:13:17.760342-07:00",
			         "ParamValues" : {
			            "paramValues" : [ {
			               "index" : 0,
			               "values" : [ "30" ]
			            } ]
			         },
			         "Name" : "A doom report",
			         "id" : 10350,
			         "Query" : 10400,
			         "gridOptions" : {
			         },
			         "OrderBy" : {
			         },
			         "Properties" : {
			            "entry" : [ {
			               "key" : "0",
			               "value" : "number"
			            } ]
			         }
			      } ],
			      "DefaultTypedQuery" : [ {
			         "fxversion" : "2014-05-16T13:34:37.275398-07:00",
			         "Name" : "ALL Mdoomroom",
			         "id" : 10057,
			         "Reports" : [ 10055 ],
			         "TargetedType" : "Mdoomroom",
			         "Conditions" : [ ]
			      }, {
			         "fxversion" : "2014-05-19T14:11:58.561142-07:00",
			         "Name" : "A doom report",
			         "id" : 10400,
			         "Reports" : [ 10350 ],
			         "TargetedType" : "Mdoomroom",
			         "Conditions" : [ {
			            "CollectionMode" : "all",
			            "Operator" : "ge",
			            "WholeTime" : true,
			            "Property" : "number",
			            "IgnoreCase" : false
			         } ]
			      } ]
			   }
			};
var allreportforMdoomroom={
		   "entities" : {
			      "FxReport" : [ reportbytypForMdoomroom.entities.FxReport[0]],
			      "DefaultTypedQuery" : [reportbytypForMdoomroom.entities.DefaultTypedQuery[0] ]
			   }
			};
var rooms={
		   "entities" : {
			      "Mdoomroom" : [ {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 794,
			         "number" : 393
			      }, {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 800,
			         "number" : 399
			      }, {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 795,
			         "number" : 394
			      }, {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 797,
			         "number" : 396
			      }, {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 793,
			         "number" : 392
			      }, {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 791,
			         "number" : 390
			      }, {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 792,
			         "number" : 391
			      }, {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 799,
			         "number" : 398
			      }, {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 798,
			         "number" : 397
			      }, {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 796,
			         "number" : 395
			      } ]
			   }
			};

var onemanyreport={
		   "entities" : {
			      "FxReport" : [ {
			         "fxversion" : "2014-05-20T11:39:23.72482-07:00",
			         "Name" : "ALL MOneMany",
			         "id" : 10400,
			         "Query" : 10450,
			         "Properties" : {
			            "entry" : [ {
			               "key" : "0",
			               "value" : "name"
			            } ]
			         }
			      } ],
			      "DefaultTypedQuery" : [ {
			         "fxversion" : "2014-05-20T11:39:23.72482-07:00",
			         "Name" : "ALL MOneMany",
			         "id" : 10450,
			         "Reports" : [ 10400 ],
			         "TargetedType" : "MOneMany",
			         "Conditions" : [ ]
			      } ]
			   }
			};
var onemanys={
		   "entities" : {
			      "MOneMany" : [ {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 10277,
			         "name" : "onemany77"
			      }, {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 10361,
			         "name" : "onemany161"
			      }, {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 10229,
			         "name" : "onemany29"
			      }, {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 10276,
			         "name" : "onemany76"
			      }, {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 10292,
			         "name" : "onemany92"
			      }, {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 10202,
			         "name" : "onemany2"
			      }, {
			         "fxversion" : "2014-05-19T17:23:51.342789-07:00",
			         "id" : 10397,
			         "name" : "onemany197"
			      }
			      ]
}
};


			      