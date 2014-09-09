var jsonHeaders={
		'Content-Type': 'application/json'
};
var meta={
   "entity-mappings" : {
      "persistence-unit-metadata" : {
         "xml-mapping-metadata-complete" : true,
         "exclude-default-mappings" : true,
         "persistence-unit-defaults" : {
            "access" : "VIRTUAL",
            "access-methods" : {
               "@get-method" : "get",
               "@set-method" : "set"
            },
            "tenant-discriminator-column" : [ ],
            "entity-listeners" : {
               "entity-listener" : [ ]
            }
         }
      },
      "package" : "com.flexdms.flexims.dynamic.model.generated",
      "tenant-discriminator-column" : [ ],
      "converter" : [ ],
      "type-converter" : [ ],
      "object-type-converter" : [ ],
      "struct-converter" : [ ],
      "sequence-generator" : [ ],
      "table-generator" : [ {
         "@name" : "Sallowesseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Sallowes",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Sautoseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Sauto",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Defaulttestseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Defaulttest",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Sreqseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Sreq",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Basictypeseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Basictype",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Mallowesseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Mallowes",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Mautoseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Mauto",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Mdefaultseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Mdefault",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Collection1seq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Collection1",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Mreqseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Mreq",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Embedmainseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Embedmainreq",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Mstudentseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Mstudent",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Mcourseseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Mcourse",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "MOneManyseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "MOneMany",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Mdoombuildseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Mdoombuild",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Mdoomroomseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Mdoomroom",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Mspecialseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Mspecial",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "Testseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "Test",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "TypedQueryseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "TypedQuery",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      }, {
         "@name" : "FxReportseq",
         "@table" : "seqs",
         "@pk-column-name" : "name",
         "@value-column-name" : "seq",
         "@pk-column-value" : "FxReport",
         "@initial-value" : 10000,
         "@allocation-size" : 50,
         "unique-constraint" : [ ],
         "index" : [ ]
      } ],
      "uuid-generator" : [ ],
      "partitioning" : [ ],
      "replication-partitioning" : [ ],
      "round-robin-partitioning" : [ ],
      "pinned-partitioning" : [ ],
      "range-partitioning" : [ ],
      "value-partitioning" : [ ],
      "hash-partitioning" : [ ],
      "union-partitioning" : [ ],
      "named-query" : [ ],
      "named-native-query" : [ ],
      "named-stored-procedure-query" : [ ],
      "named-stored-function-query" : [ ],
      "named-plsql-stored-procedure-query" : [ ],
      "named-plsql-stored-function-query" : [ ],
      "oracle-object" : [ ],
      "oracle-array" : [ ],
      "plsql-record" : [ ],
      "plsql-table" : [ ],
      "sql-result-set-mapping" : [ ],
      "mapped-superclass" : [ ],
      "entity" : [ {
         "@class" : "Sallowes",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Sallowesseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ {
               "@name" : "shortstring",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "displaytext",
                  "@value" : "Short string with some limitation"
               }, {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "tooltip",
                  "@value" : "Long description"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "minlen",
                  "@value" : "5"
               }, {
                  "@name" : "pattern",
                  "@value" : "^[a-z][a-z0-9]*$"
               }, {
                  "@name" : "ignorepatterncase",
                  "@value" : "true"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               }, {
                  "@name" : "summaryprop",
                  "@value" : "true"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "jason"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "jason"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "jason1"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "maria"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "propint",
               "@attribute-type" : "java.lang.Integer",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "3"
               }, {
                  "@name" : "minvalue",
                  "@value" : "5"
               }, {
                  "@name" : "maxvalue",
                  "@value" : "20"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "6"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "6"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "7"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "8"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "proplong",
               "@attribute-type" : "java.lang.Long",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "4"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "7"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "6"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "7"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "8"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "propfloat",
               "@attribute-type" : "java.lang.Float",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "5"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "8"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "6"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "7"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "8"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "propdouble",
               "@attribute-type" : "java.lang.Double",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "6"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "7"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "6"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "7"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "8"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "propcurrency",
               "@attribute-type" : "java.math.BigDecimal",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@precision" : 20,
                  "@scale" : 5
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "7"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "6"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "6"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "7"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "8"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "propdate",
               "@attribute-type" : "java.util.Calendar",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "temporal" : "DATE",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "11"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "2013-12-05"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "2013-12-05"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "2013-12-06"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "2013-12-07"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "proptimestamp",
               "@attribute-type" : "java.util.Calendar",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "temporal" : "TIMESTAMP",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "12"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "2013-12-05T05:06:07"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "2013-12-05T05:06:07"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "2013-12-05T05:06:08"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "2013-12-05T05:06:09"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "proptime",
               "@attribute-type" : "java.util.Calendar",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "temporal" : "TIME",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "13"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "2013-12-05T05:06:07"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "2013-12-05T05:06:07"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "2013-12-05T05:06:08"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "2013-12-05T05:06:09"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Sauto",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Sautoseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ {
               "@name" : "shortstring",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "displaytext",
                  "@value" : "Short string with some limitation"
               }, {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "tooltip",
                  "@value" : "Long description"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "minlen",
                  "@value" : "5"
               }, {
                  "@name" : "pattern",
                  "@value" : "^[a-z][a-z0-9]*$"
               }, {
                  "@name" : "ignorepatterncase",
                  "@value" : "true"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               }, {
                  "@name" : "summaryprop",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "mediumstring",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 4096
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "1"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "longstring",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "lob" : {
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "2"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propint",
               "@attribute-type" : "java.lang.Integer",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "3"
               }, {
                  "@name" : "minvalue",
                  "@value" : "5"
               }, {
                  "@name" : "maxvalue",
                  "@value" : "20"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "proplong",
               "@attribute-type" : "java.lang.Long",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "4"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propfloat",
               "@attribute-type" : "java.lang.Float",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "5"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propdouble",
               "@attribute-type" : "java.lang.Double",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "6"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propcurrency",
               "@attribute-type" : "java.math.BigDecimal",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@precision" : 20,
                  "@scale" : 5
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "7"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propboolean",
               "@attribute-type" : "boolean",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "9"
               }, {
                  "@name" : "tooltip",
                  "@value" : "This has a tooltip"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propdate",
               "@attribute-type" : "java.util.Calendar",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "temporal" : "DATE",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "11"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "proptimestamp",
               "@attribute-type" : "java.util.Calendar",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "temporal" : "TIMESTAMP",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "12"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "proptime",
               "@attribute-type" : "java.util.Calendar",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "temporal" : "TIME",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "13"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propemail",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "14"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propurl",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "15"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propdirectory",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "16"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propfile",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "17"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propuneditable",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "displaytext",
                  "@value" : "This property can not be editted"
               }, {
                  "@name" : "editable",
                  "@value" : "false"
               } ]
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Defaulttest",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Defaulttestseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ {
               "@name" : "shortstring",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "displaytext",
                  "@value" : "Short string with some limitation"
               }, {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "tooltip",
                  "@value" : "Long description"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "minlen",
                  "@value" : "5"
               }, {
                  "@name" : "pattern",
                  "@value" : "^[a-z][a-z0-9]*$"
               }, {
                  "@name" : "ignorepatterncase",
                  "@value" : "true"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               }, {
                  "@name" : "summaryprop",
                  "@value" : "true"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "short"
               } ]
            }, {
               "@name" : "mediumstring",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 4096
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "1"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "mediumstring"
               } ]
            }, {
               "@name" : "longstring",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "lob" : {
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "2"
               } ]
            }, {
               "@name" : "propint",
               "@attribute-type" : "java.lang.Integer",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "3"
               }, {
                  "@name" : "minvalue",
                  "@value" : "5"
               }, {
                  "@name" : "maxvalue",
                  "@value" : "20"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "6"
               } ]
            }, {
               "@name" : "proplong",
               "@attribute-type" : "java.lang.Long",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "4"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "7"
               } ]
            }, {
               "@name" : "propfloat",
               "@attribute-type" : "java.lang.Float",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "5"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "6.1"
               } ]
            }, {
               "@name" : "propdouble",
               "@attribute-type" : "java.lang.Double",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "6"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "7.1"
               } ]
            }, {
               "@name" : "propcurrency",
               "@attribute-type" : "java.math.BigDecimal",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@precision" : 20,
                  "@scale" : 5
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "7"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "8.1"
               } ]
            }, {
               "@name" : "propboolean",
               "@attribute-type" : "boolean",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "9"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "true"
               }, {
                  "@name" : "tooltip",
                  "@value" : "This has a tooltip"
               } ]
            }, {
               "@name" : "propdate",
               "@attribute-type" : "java.util.Calendar",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "temporal" : "DATE",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "11"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "2013-12-05"
               } ]
            }, {
               "@name" : "proptimestamp",
               "@attribute-type" : "java.util.Calendar",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "temporal" : "TIMESTAMP",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "12"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "2013-12-05T05:06:07"
               } ]
            }, {
               "@name" : "proptime",
               "@attribute-type" : "java.util.Calendar",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "temporal" : "TIME",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "13"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "T05:06:07"
               } ]
            }, {
               "@name" : "propemail",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "14"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "test@example.com"
               } ]
            }, {
               "@name" : "propurl",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "15"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "http://www.flexdms.com"
               } ]
            }, {
               "@name" : "propdirectory",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "16"
               } ]
            }, {
               "@name" : "propfile",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "17"
               } ]
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Sreq",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Sreqseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ {
               "@name" : "shortstring",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "displaytext",
                  "@value" : "Short string with some limitation"
               }, {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "tooltip",
                  "@value" : "Long description"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "minlen",
                  "@value" : "5"
               }, {
                  "@name" : "pattern",
                  "@value" : "^[a-z][a-z0-9]*$"
               }, {
                  "@name" : "ignorepatterncase",
                  "@value" : "true"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               }, {
                  "@name" : "summaryprop",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "mediumstring",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 4096
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "1"
               } ]
            }, {
               "@name" : "longstring",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "lob" : {
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "2"
               } ]
            }, {
               "@name" : "propint",
               "@attribute-type" : "java.lang.Integer",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "3"
               }, {
                  "@name" : "minvalue",
                  "@value" : "5"
               }, {
                  "@name" : "maxvalue",
                  "@value" : "20"
               } ]
            }, {
               "@name" : "proplong",
               "@attribute-type" : "java.lang.Long",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "4"
               } ]
            }, {
               "@name" : "propfloat",
               "@attribute-type" : "java.lang.Float",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "5"
               } ]
            }, {
               "@name" : "propdouble",
               "@attribute-type" : "java.lang.Double",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "6"
               } ]
            }, {
               "@name" : "propcurrency",
               "@attribute-type" : "java.math.BigDecimal",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@precision" : 20,
                  "@scale" : 5
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "7"
               } ]
            }, {
               "@name" : "propboolean",
               "@attribute-type" : "boolean",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "9"
               }, {
                  "@name" : "tooltip",
                  "@value" : "This has a tooltip"
               } ]
            }, {
               "@name" : "propdate",
               "@attribute-type" : "java.util.Calendar",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "temporal" : "DATE",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "11"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "proptimestamp",
               "@attribute-type" : "java.util.Calendar",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "temporal" : "TIMESTAMP",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "12"
               } ]
            }, {
               "@name" : "proptime",
               "@attribute-type" : "java.util.Calendar",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "temporal" : "TIME",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "13"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propemail",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "14"
               } ]
            }, {
               "@name" : "propurl",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "15"
               } ]
            }, {
               "@name" : "propdirectory",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "16"
               } ]
            }, {
               "@name" : "propfile",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "17"
               } ]
            }, {
               "@name" : "propuneditable",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "displaytext",
                  "@value" : "This property can not be editted"
               }, {
                  "@name" : "editable",
                  "@value" : "false"
               } ]
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Basictype",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Basictypeseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ {
               "@name" : "shortstring",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "displaytext",
                  "@value" : "Short string with some limitation"
               }, {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "tooltip",
                  "@value" : "Long description"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "minlen",
                  "@value" : "5"
               }, {
                  "@name" : "pattern",
                  "@value" : "^[a-z][a-z0-9]*$"
               }, {
                  "@name" : "ignorepatterncase",
                  "@value" : "true"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               }, {
                  "@name" : "summaryprop",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "mediumstring",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 4096
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "1"
               } ]
            }, {
               "@name" : "longstring",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 4096
               },
               "lob" : {
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "2"
               } ]
            }, {
               "@name" : "propint",
               "@attribute-type" : "java.lang.Integer",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "3"
               }, {
                  "@name" : "minvalue",
                  "@value" : "5"
               }, {
                  "@name" : "maxvalue",
                  "@value" : "20"
               } ]
            }, {
               "@name" : "proplong",
               "@attribute-type" : "java.lang.Long",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "4"
               } ]
            }, {
               "@name" : "propfloat",
               "@attribute-type" : "java.lang.Float",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "5"
               } ]
            }, {
               "@name" : "propdouble",
               "@attribute-type" : "java.lang.Double",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "6"
               } ]
            }, {
               "@name" : "propcurrency",
               "@attribute-type" : "java.math.BigDecimal",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@precision" : 20,
                  "@scale" : 5
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "7"
               } ]
            }, {
               "@name" : "propboolean",
               "@attribute-type" : "boolean",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "9"
               }, {
                  "@name" : "tooltip",
                  "@value" : "This has a tooltip"
               } ]
            }, {
               "@name" : "propdate",
               "@attribute-type" : "java.util.Calendar",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "temporal" : "DATE",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "11"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "proptimestamp",
               "@attribute-type" : "java.util.Calendar",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "temporal" : "TIMESTAMP",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "12"
               } ]
            }, {
               "@name" : "proptime",
               "@attribute-type" : "java.util.Calendar",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "temporal" : "TIME",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "13"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propemail",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "14"
               } ]
            }, {
               "@name" : "propurl",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "15"
               } ]
            }, {
               "@name" : "propdirectory",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "16"
               } ]
            }, {
               "@name" : "propfile",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "17"
               } ]
            }, {
               "@name" : "propuneditable",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "displaytext",
                  "@value" : "This property can not be editted"
               }, {
                  "@name" : "editable",
                  "@value" : "false"
               } ]
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Mallowes",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Mallowesseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ {
               "@name" : "shortstring",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "displaytext",
                  "@value" : "Short string with some limitation"
               }, {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "tooltip",
                  "@value" : "Long description"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "minlen",
                  "@value" : "5"
               }, {
                  "@name" : "pattern",
                  "@value" : "^[a-z][a-z0-9]*$"
               }, {
                  "@name" : "ignorepatterncase",
                  "@value" : "true"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               }, {
                  "@name" : "summaryprop",
                  "@value" : "true"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "jason,maria"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "jason"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "jason1"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "maria"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "propint",
               "@target-class" : "java.lang.Integer",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "3"
               }, {
                  "@name" : "minvalue",
                  "@value" : "5"
               }, {
                  "@name" : "maxvalue",
                  "@value" : "20"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "6,7,8"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "6"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "7"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "8"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "proplong",
               "@target-class" : "java.lang.Long",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "4"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "6,7"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "6"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "7"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "8"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "propfloat",
               "@target-class" : "java.lang.Float",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "5"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "6.2,7.3"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "6.2"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "7.3"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "8"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "propdouble",
               "@target-class" : "java.lang.Double",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "6"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "6.2,7.3"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "6.2"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "7.3"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "8"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "propcurrency",
               "@target-class" : "java.math.BigDecimal",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@precision" : 20,
                  "@scale" : 5
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "7"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "6.2,7.3"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "6.2"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "7.3"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "8"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "propdate",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "temporal" : "DATE",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "11"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "2013-12-05,2013-12-06"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "2013-12-05"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "2013-12-06"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "2013-12-07"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "proptimestamp",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "temporal" : "TIMESTAMP",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "12"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "2013-12-05T05:06:07,2013-12-05T05:06:08"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "2013-12-05T05:06:07"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "2013-12-05T05:06:08"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "2013-12-05T05:06:09"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "proptime",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "temporal" : "TIME",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "13"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "2013-12-05T05:06:07,2013-12-05T05:06:08"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "2013-12-05T05:06:07"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "2013-12-05T05:06:08"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "2013-12-05T05:06:09"
               }, {
                  "@name" : "allowedvalues.2.display"
               } ]
            }, {
               "@name" : "propemail",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "14"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "test@example.com,test1@example.com"
               } ]
            }, {
               "@name" : "propurl",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "15"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "http://www.google.com,http://www.yahoo.com"
               } ]
            } ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Mauto",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Mautoseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ {
               "@name" : "shortstring",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propint",
               "@target-class" : "java.lang.Integer",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "3"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "proplong",
               "@target-class" : "java.lang.Long",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "4"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propfloat",
               "@target-class" : "java.lang.Float",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "5"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propdouble",
               "@target-class" : "java.lang.Double",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "6"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propcurrency",
               "@target-class" : "java.math.BigDecimal",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@precision" : 20,
                  "@scale" : 5
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "7"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propdate",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "temporal" : "DATE",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "11"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "proptimestamp",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "temporal" : "TIMESTAMP",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "12"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "proptime",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "temporal" : "TIME",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "13"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propemail",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "14"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propurl",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "15"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propdirectory",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "16"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propfile",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "17"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ]
            } ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Mdefault",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Mdefaultseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ {
               "@name" : "shortstring",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "displaytext",
                  "@value" : "Short string with some limitation"
               }, {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "tooltip",
                  "@value" : "Long description"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "minlen",
                  "@value" : "5"
               }, {
                  "@name" : "pattern",
                  "@value" : "^[a-z][a-z0-9]*$"
               }, {
                  "@name" : "ignorepatterncase",
                  "@value" : "true"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               }, {
                  "@name" : "summaryprop",
                  "@value" : "true"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "jason,maria"
               } ]
            }, {
               "@name" : "propint",
               "@target-class" : "java.lang.Integer",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "3"
               }, {
                  "@name" : "minvalue",
                  "@value" : "5"
               }, {
                  "@name" : "maxvalue",
                  "@value" : "20"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "6,7,8"
               } ]
            }, {
               "@name" : "proplong",
               "@target-class" : "java.lang.Long",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "4"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "5,6,7"
               } ]
            }, {
               "@name" : "propfloat",
               "@target-class" : "java.lang.Float",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "5"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "5.1,6.2,7.3"
               } ]
            }, {
               "@name" : "propdouble",
               "@target-class" : "java.lang.Double",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "6"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "5.1,6.2,7.3"
               } ]
            }, {
               "@name" : "propcurrency",
               "@target-class" : "java.math.BigDecimal",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@precision" : 20,
                  "@scale" : 5
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "7"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "5.1,6.2,7.3"
               } ]
            }, {
               "@name" : "propdate",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "temporal" : "DATE",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "11"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "2013-12-05,2013-12-06"
               } ]
            }, {
               "@name" : "proptimestamp",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "temporal" : "TIMESTAMP",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "12"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "2013-12-05T05:06:07,2013-12-05T05:06:08"
               } ]
            }, {
               "@name" : "proptime",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "temporal" : "TIME",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "13"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "T05:06:07,T05:06:08"
               } ]
            }, {
               "@name" : "propemail",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "14"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "test@example.com,test1@example.com"
               } ]
            }, {
               "@name" : "propurl",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "15"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "http://www.google.com,http://www.yahoo.com"
               } ]
            } ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Collection1",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Collection1seq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ {
               "@name" : "shortstring",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "displaytext",
                  "@value" : "Short string with some limitation"
               }, {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "tooltip",
                  "@value" : "Long description"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "minlen",
                  "@value" : "5"
               }, {
                  "@name" : "pattern",
                  "@value" : "^[a-z][a-z0-9]*$"
               }, {
                  "@name" : "ignorepatterncase",
                  "@value" : "true"
               }, {
                  "@name" : "summaryprop",
                  "@value" : "true"
               } ]
            }, {
               "@name" : "propint",
               "@target-class" : "java.lang.Integer",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "3"
               }, {
                  "@name" : "minvalue",
                  "@value" : "5"
               }, {
                  "@name" : "maxvalue",
                  "@value" : "20"
               } ]
            }, {
               "@name" : "proplong",
               "@target-class" : "java.lang.Long",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "4"
               } ]
            }, {
               "@name" : "propfloat",
               "@target-class" : "java.lang.Float",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "5"
               } ]
            }, {
               "@name" : "propdouble",
               "@target-class" : "java.lang.Double",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "6"
               } ]
            }, {
               "@name" : "propcurrency",
               "@target-class" : "java.math.BigDecimal",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@precision" : 20,
                  "@scale" : 5
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "7"
               } ]
            }, {
               "@name" : "propdate",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true
               },
               "temporal" : "DATE",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "11"
               } ]
            }, {
               "@name" : "proptimestamp",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true
               },
               "temporal" : "TIMESTAMP",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "12"
               } ]
            }, {
               "@name" : "proptime",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true
               },
               "temporal" : "TIME",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "13"
               } ]
            }, {
               "@name" : "propemail",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "14"
               } ]
            }, {
               "@name" : "propurl",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "15"
               } ]
            }, {
               "@name" : "propdirectory",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "16"
               } ]
            }, {
               "@name" : "propfile",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "17"
               } ]
            } ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Mreq",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Mreqseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ {
               "@name" : "shortstring",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               } ]
            }, {
               "@name" : "propint",
               "@target-class" : "java.lang.Integer",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "3"
               } ]
            }, {
               "@name" : "proplong",
               "@target-class" : "java.lang.Long",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "4"
               } ]
            }, {
               "@name" : "propfloat",
               "@target-class" : "java.lang.Float",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "5"
               } ]
            }, {
               "@name" : "propdouble",
               "@target-class" : "java.lang.Double",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "6"
               } ]
            }, {
               "@name" : "propcurrency",
               "@target-class" : "java.math.BigDecimal",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@precision" : 20,
                  "@scale" : 5
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "7"
               } ]
            }, {
               "@name" : "propdate",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "temporal" : "DATE",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "11"
               } ]
            }, {
               "@name" : "proptimestamp",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "temporal" : "TIMESTAMP",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "12"
               } ]
            }, {
               "@name" : "proptime",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "temporal" : "TIME",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "13"
               } ]
            }, {
               "@name" : "propemail",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "14"
               } ]
            }, {
               "@name" : "propurl",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "15"
               } ]
            }, {
               "@name" : "propdirectory",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "16"
               } ]
            }, {
               "@name" : "propfile",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "17"
               } ]
            } ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@name" : "Embedmain",
         "@class" : "Embedmain",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Embedmainseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ {
               "@name" : "fname",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ {
               "@name" : "multiembed",
               "@target-class" : "Embed2",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "-1"
               } ]
            } ],
            "embedded" : [ {
               "@name" : "singleembed",
               "@attribute-type" : "Embed1",
               "attribute-override" : [ ],
               "association-override" : [ ],
               "convert" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "-1"
               } ]
            } ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Mstudent",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Mstudentseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ {
               "@name" : "Name",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 10
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            }, {
               "@name" : "number",
               "@attribute-type" : "java.lang.Integer",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ {
               "@name" : "doombuild",
               "@target-entity" : "Mdoombuild",
               "primary-key-join-column" : [ ],
               "join-column" : [ ],
               "join-field" : [ ],
               "property" : [ ]
            } ],
            "one-to-many" : [ {
               "@name" : "OneManys",
               "@target-entity" : "MOneMany",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "join-column" : [ ],
               "join-field" : [ ],
               "cascade" : {
                  "cascade-persist" : true
               },
               "property" : [ ]
            } ],
            "one-to-one" : [ {
               "@name" : "doomroom",
               "@target-entity" : "Mdoomroom",
               "primary-key-join-column" : [ ],
               "join-column" : [ ],
               "join-field" : [ ],
               "property" : [ ]
            } ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ {
               "@name" : "Courses",
               "@target-entity" : "Mcourse",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "join-field" : [ ],
               "property" : [ ]
            } ],
            "element-collection" : [ {
               "@name" : "propint",
               "@target-class" : "java.lang.Integer",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "3"
               } ]
            } ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Mcourse",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Mcourseseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ {
               "@name" : "name",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ {
               "@name" : "Students",
               "@target-entity" : "Mstudent",
               "@mapped-by" : "Courses",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "join-field" : [ ],
               "property" : [ ]
            } ],
            "element-collection" : [ ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "MOneMany",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "MOneManyseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ {
               "@name" : "name",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Mdoombuild",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Mdoombuildseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ {
               "@name" : "name",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ {
               "@name" : "students",
               "@target-entity" : "Mstudent",
               "@mapped-by" : "doombuild",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "join-column" : [ ],
               "join-field" : [ ],
               "property" : [ ]
            }, {
               "@name" : "rooms",
               "@target-entity" : "Mdoomroom",
               "@mapped-by" : "doombuild",
               "@orphan-removal" : true,
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "join-column" : [ ],
               "join-field" : [ ],
               "cascade" : {
                  "cascade-remove" : true
               },
               "private-owned" : true,
               "property" : [ ]
            } ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Mdoomroom",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Mdoomroomdseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ {
               "@name" : "number",
               "@attribute-type" : "java.lang.Integer",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            }, {
               "@name" : "name",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 10
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ {
               "@name" : "doombuild",
               "@target-entity" : "Mdoombuild",
               "primary-key-join-column" : [ ],
               "join-column" : [ ],
               "join-field" : [ ],
               "property" : [ ]
            } ],
            "one-to-many" : [ ],
            "one-to-one" : [ {
               "@name" : "student",
               "@target-entity" : "Mstudent",
               "@mapped-by" : "doomroom",
               "primary-key-join-column" : [ ],
               "join-column" : [ ],
               "join-field" : [ ],
               "property" : [ ]
            } ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Mspecial",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Mspecialseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ {
               "@name" : "propbyte",
               "@attribute-type" : "com.flexdms.flexims.jpa.helper.ByteArray",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true
               },
               "lob" : {
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "8"
               } ]
            }, {
               "@name" : "propobject",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 4096
               },
               "lob" : {
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "rootClass",
                  "@value" : "com.flexdms.flexims.jpa.helper.NameValueList"
               }, {
                  "@name" : "typeidx",
                  "@value" : "10"
               } ]
            }, {
               "@name" : "fxExtraProp",
               "@optional" : true,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 4096
               },
               "lob" : {
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "editable",
                  "@value" : "false"
               }, {
                  "@name" : "viewable",
                  "@value" : "false"
               }, {
                  "@name" : "displaytext'",
                  "@value" : "extensible properties"
               }, {
                  "@name" : "system'",
                  "@value" : "true"
               }, {
                  "@name" : "typeidx",
                  "@value" : "10"
               }, {
                  "@name" : "rootClass",
                  "@value" : "com.flexdms.flexims.jpa.helper.NameValueList"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ {
               "@name" : "propdates",
               "@target-class" : "java.util.Calendar",
               "@attribute-type" : "java.util.Set",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
               },
               "temporal" : "DATE",
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "11"
               } ]
            } ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Test",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "Testseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic" : [ {
               "@name" : "fname",
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ]
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@name" : "TypedQuery",
         "@class" : "TypedQuery",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "description" : "Query for a type",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "inheritance" : {
         },
         "discriminator-value" : "TypedQuery",
         "discriminator-column" : {
            "@discriminator-type" : "String",
            "@length" : 40
         },
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ {
            "@name" : "SYSTEM",
            "@value" : "True"
         } ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "column" : {
               },
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "TypedQueryseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            } ],
            "basic" : [ {
               "@name" : "Name",
               "@optional" : false,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : true,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "summaryprop",
                  "@value" : "true"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "Description",
               "@optional" : true,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "TargetedType",
               "@optional" : false,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "summaryprop",
                  "@value" : "true"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "column" : {
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            } ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@name" : "JPQL",
         "@class" : "JPQL",
         "@parent-class" : "TypedQuery",
         "@access" : "VIRTUAL",
         "description" : "Java Persistence Query",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "discriminator-value" : "JPQL",
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ {
            "@name" : "entity",
            "@value" : "true"
         } ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ ],
            "basic" : [ {
               "@name" : "JPQLText",
               "@optional" : true,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 4096
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "1"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "autogenerate",
                  "@value" : "true"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@name" : "DefaultTypedQuery",
         "@class" : "DefaultTypedQuery",
         "@parent-class" : "TypedQuery",
         "@access" : "VIRTUAL",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "discriminator-value" : "DefaultTypedQuery",
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ {
            "@name" : "entity",
            "@value" : "true"
         }, {
            "@name" : "hasjs",
            "@value" : "true"
         } ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ ],
            "basic" : [ ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ {
               "@name" : "Conditions",
               "@target-class" : "PropertyCondition",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "collection-table" : {
                  "join-column" : [ {
                  } ],
                  "primary-key-join-column" : [ ],
                  "unique-constraint" : [ ],
                  "index" : [ ]
               },
               "cascade-on-delete" : false,
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "-1"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            } ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@name" : "FxReport",
         "@class" : "FxReport",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "description" : "Report",
         "secondary-table" : [ ],
         "primary-key-join-column" : [ ],
         "index" : [ ],
         "cache-index" : [ ],
         "fetch-group" : [ ],
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "named-query" : [ ],
         "named-native-query" : [ ],
         "named-stored-procedure-query" : [ ],
         "named-stored-function-query" : [ ],
         "named-plsql-stored-procedure-query" : [ ],
         "named-plsql-stored-function-query" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "sql-result-set-mapping" : [ ],
         "entity-listeners" : {
            "entity-listener" : [ ]
         },
         "property" : [ {
            "@name" : "SYSTEM",
            "@value" : "True"
         } ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "convert" : [ ],
         "named-entity-graph" : [ ],
         "attributes" : {
            "id" : [ {
               "@name" : "id",
               "@attribute-type" : "Long",
               "column" : {
               },
               "generated-value" : {
                  "@strategy" : "TABLE",
                  "@generator" : "FxReportseq"
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "displaytext",
                  "@value" : "ID"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            } ],
            "basic" : [ {
               "@name" : "Name",
               "@optional" : false,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : true,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "summaryprop",
                  "@value" : "true"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "Description",
               "@optional" : true,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "Properties",
               "@optional" : true,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 1024
               },
               "lob" : {
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "displaytext",
                  "@value" : "Properties to Display"
               }, {
                  "@name" : "system",
                  "@value" : "true"
               }, {
                  "@name" : "typeidx",
                  "@value" : "10"
               }, {
                  "@name" : "rootClass",
                  "@value" : "com.flexdms.flexims.jpa.helper.NameValueList"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "OrderBy",
               "@optional" : true,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 512
               },
               "lob" : {
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "displaytext",
                  "@value" : "Order By"
               }, {
                  "@name" : "system",
                  "@value" : "true"
               }, {
                  "@name" : "typeidx",
                  "@value" : "10"
               }, {
                  "@name" : "rootClass",
                  "@value" : "com.flexdms.flexims.jpa.helper.NameValueList"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "ParamValues",
               "@optional" : true,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 4096
               },
               "lob" : {
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "displaytext",
                  "@value" : "Value for Query Parameters"
               }, {
                  "@name" : "system",
                  "@value" : "true"
               }, {
                  "@name" : "typeidx",
                  "@value" : "10"
               }, {
                  "@name" : "rootClass",
                  "@value" : "com.flexdms.flexims.report.rs.QueryParamValues"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ {
               "@name" : "fxversion",
               "@mutable" : true,
               "@attribute-type" : "java.sql.Timestamp",
               "column" : {
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "displaytext",
                  "@value" : "Version"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            } ],
            "many-to-one" : [ {
               "@name" : "Query",
               "@target-entity" : "TypedQuery",
               "@fetch" : "EAGER",
               "@optional" : false,
               "primary-key-join-column" : [ ],
               "join-column" : [ ],
               "join-field" : [ ],
               "property" : [ {
                  "@name" : "displaytext",
                  "@value" : "Query to run"
               } ]
            } ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      } ],
      "embeddable" : [ {
         "@class" : "Embed1",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "attributes" : {
            "id" : [ ],
            "basic" : [ {
               "@name" : "streetAddress",
               "@optional" : false,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ {
               "@name" : "mstr",
               "@target-class" : "java.lang.String",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 10
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "minlen",
                  "@value" : "5"
               }, {
                  "@name" : "displaytext",
                  "@value" : "Short string with some limitation"
               }, {
                  "@name" : "pattern",
                  "@value" : "^[a-z][a-z0-9]*$"
               }, {
                  "@name" : "ignorepatterncase",
                  "@value" : "true"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "mint",
               "@target-class" : "java.lang.Integer",
               "@attribute-type" : "java.util.List",
               "map-key-attribute-override" : [ ],
               "map-key-convert" : [ ],
               "map-key-association-override" : [ ],
               "map-key-join-column" : [ ],
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "attribute-override" : [ ],
               "association-override" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "3"
               }, {
                  "@name" : "minvalue",
                  "@value" : "5"
               }, {
                  "@name" : "maxvalue",
                  "@value" : "20"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            } ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "Embed2",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "attributes" : {
            "id" : [ ],
            "basic" : [ {
               "@name" : "streetAddress",
               "@optional" : false,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ ],
            "many-to-one" : [ ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      }, {
         "@class" : "PropertyCondition",
         "@parent-class" : "com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl",
         "@access" : "VIRTUAL",
         "description" : "A query condition on one property",
         "converter" : [ ],
         "type-converter" : [ ],
         "object-type-converter" : [ ],
         "struct-converter" : [ ],
         "oracle-object" : [ ],
         "oracle-array" : [ ],
         "plsql-record" : [ ],
         "plsql-table" : [ ],
         "property" : [ ],
         "attribute-override" : [ ],
         "association-override" : [ ],
         "attributes" : {
            "id" : [ ],
            "basic" : [ {
               "@name" : "Property",
               "@optional" : false,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "summaryprop",
                  "@value" : "true"
               }, {
                  "@name" : "displaytext",
                  "@value" : "Property"
               }, {
                  "@name" : "maxlen",
                  "@value" : "254"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "Operator",
               "@optional" : false,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : false,
                  "@length" : 20
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "summaryprop",
                  "@value" : "true"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "RelativeStartDate",
               "@optional" : true,
               "@attribute-type" : "java.lang.Integer",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "3"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "false"
               }, {
                  "@name" : "displaytext",
                  "@value" : "Relative to query execution time"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "RelativeStartUnit",
               "@optional" : true,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 10
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "false"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "FirstValue",
               "@optional" : true,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "displaytext",
                  "@value" : "Value. Multiple value separated by ,"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "RelativeEndDate",
               "@optional" : true,
               "@attribute-type" : "java.lang.Integer",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "3"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "false"
               }, {
                  "@name" : "displaytext",
                  "@value" : "Relative to query execution time"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "RelativeEndUnit",
               "@optional" : true,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 10
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "false"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "SecondValue",
               "@optional" : true,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "displaytext",
                  "@value" : "second value for between"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "Description",
               "@optional" : true,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "displaytext",
                  "@value" : "Description for this restriction"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "IgnoreCase",
               "@optional" : true,
               "@attribute-type" : "boolean",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "9"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "displaytext",
                  "@value" : "Case insensitive when comparing characters"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "WholeTime",
               "@optional" : true,
               "@attribute-type" : "boolean",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 254
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "9"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "false"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "true"
               }, {
                  "@name" : "displaytext",
                  "@value" : "ignore fractional when using relative time"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            }, {
               "@name" : "CollectionMode",
               "@optional" : true,
               "@attribute-type" : "java.lang.String",
               "column" : {
                  "@unique" : false,
                  "@nullable" : true,
                  "@length" : 10
               },
               "convert" : [ ],
               "converter" : [ ],
               "type-converter" : [ ],
               "object-type-converter" : [ ],
               "struct-converter" : [ ],
               "property" : [ {
                  "@name" : "typeidx",
                  "@value" : "0"
               }, {
                  "@name" : "viewable",
                  "@value" : "true"
               }, {
                  "@name" : "editable",
                  "@value" : "true"
               }, {
                  "@name" : "displaytext",
                  "@value" : "How to apply condition to elements in collection"
               }, {
                  "@name" : "allowedvalues.0.value",
                  "@value" : "some"
               }, {
                  "@name" : "allowedvalues.0.display"
               }, {
                  "@name" : "allowedvalues.1.value",
                  "@value" : "all"
               }, {
                  "@name" : "allowedvalues.1.display"
               }, {
                  "@name" : "allowedvalues.2.value",
                  "@value" : "none"
               }, {
                  "@name" : "allowedvalues.2.display"
               }, {
                  "@name" : "defaultvalue",
                  "@value" : "all"
               } ],
               "access-methods" : {
                  "@get-method" : "get",
                  "@set-method" : "set"
               }
            } ],
            "basic-collection" : [ ],
            "basic-map" : [ ],
            "version" : [ ],
            "many-to-one" : [ {
               "@name" : "TraceDown",
               "@target-entity" : "DefaultTypedQuery",
               "@optional" : true,
               "primary-key-join-column" : [ ],
               "join-column" : [ ],
               "join-field" : [ ],
               "property" : [ {
                  "@name" : "displaytext",
                  "@value" : "Apply drill down query to related entity"
               } ]
            } ],
            "one-to-many" : [ ],
            "one-to-one" : [ ],
            "variable-one-to-one" : [ ],
            "many-to-many" : [ ],
            "element-collection" : [ ],
            "embedded" : [ ],
            "transformation" : [ ],
            "transient" : [ ],
            "structure" : [ ],
            "array" : [ ]
         }
      } ]
   }
};