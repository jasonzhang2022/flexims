 mvn -DskipTests install
 mvn license:aggregate-add-third-party 
 cat target/generated-sources/license/THIRD-PARTY.txt | sort >target/generated-sources/license/THIRD-PARTY_ALL_SORTED.txt
 

 
 #test libraries
  cd modeler
  mvn -Dlicense.thirdPartyFilename=THIRD-PARTY_TEST.txt -DincludeScopes=test license:add-third-party  
  sort target/generated-sources/license/THIRD-PARTY_TEST.txt > ../target/generated-sources/license/THIRD-PARTY_TEST_SORTED.txt
   
  cd form
  #dependency tree
  mvn dependency:tree -DoutputFile=target/dtree.txt
  cp target/dtree.txt ../target/generated-sources/license/dependency_tree.txt
  
   