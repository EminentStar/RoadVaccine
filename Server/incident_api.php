	<?php
		define("INCIDENT_API_URL","http://data.ex.co.kr/openapi/safeDriving/safeDrivingSupport?key=5460526240&type=json&incTpCd=00");
		define("CONSTRUCTION_API_URL","http://data.ex.co.kr/openapi/safeDriving/safeDrivingSupport?key=5460526240&type=json&incTpCd=03");	
		
		define("INCIDENT_ID","00");
		define("CONSTRUCT_ID","03");
		
		define("HOST","en605.woobi.co.kr");
		define("USER","en605");	
		define("PASSWD","1230");	
		define("DB_NAME","en605");	
		
		define("TIME_QUANTUM",60);

		define("ANALDTMC","2");
		define("SEND_ONETIME",40);	

		require_once("gcm_message.php");
		require_once("Incident.php");
		require_once("Construction.php");	
		require_once("IncidentServerService.php");
			

		$serverMethods = new IncidentServerService;
		
	//	$incidentUrl = API_URL.INCIDENT_ID;
	//	$constructUrl = API_URL.CONSTRUCT_ID;	

		$isChanged = false;

		/* The variable $incident is going to be used for store the last incident of incident table in DB  */	
		$incident = new Incident;	
		$construction = new Construction;

	$serverMethods->getLastIncident($incident);
	$serverMethods->getLastConstruction($construction);

	/*Testing variable */
	$testCnt = 0;

	/* infinite check Incidents and Construction API loop */
	while(true){
		/*Request The Incident API and get the information as the JSON format*/
		$incResult = $serverMethods->getApiResult(INCIDENT_API_URL);
		try{
			$incResult_array = json_decode($incResult);
		}catch(Exception $e){
			echo 'Caught exception: ', $e->getMessage(), "\n";
		}	
		$consResult = $serverMethods->getApiResult(CONSTRUCTION_API_URL);
		
		try{
			$consResult_array = json_decode($consResult);
		}catch(Exception $e){
			echo 'Caught exception: ', $e->getMessage(), "\n";
		}	

		/* Seperate JSON Array to the each JSON Object  */
		$incList = $incResult_array->{"list"};
		$incCount = $incResult_array->{"count"};
		$incCode = $incResult_array->{"code"};
		
		$consList = $consResult_array->{"list"};
		$consCount = $consResult_array->{"count"};
		$consCode = $consResult_array->{"code"};
		
		/* get the last index of the incident list  */
		$incCntNum = $incCount - 1;
		$consCntNum = $consCount - 1;
	
		/* get the last incident from the incident array which we got from API Server */
		//$incLastIndex = $incList[$incCntNum];
		//$consLastIndex = $consList[$consCntNum];		
		
		//Temporary arrays.
		$incArray = array();
		$consArray = array();
	
		//print('api to JSON');
		$serverMethods->apiJsonToArray($incCount, $incList, $incArray);
		$serverMethods->apiJsonToArray($consCount, $consList, $consArray);
	
		//print('remove same element(inc)\n');	
		$serverMethods->fastRemoveSameElement($incArray);
                $serverMethods->removeSameElement($incArray);

		//print('remove same element(cons)\n');	
		$serverMethods->fastRemoveSameElement($consArray);
                $serverMethods->removeSameElement($consArray);
		
		$serverMethods->removeLastAttribute($incArray);
		$serverMethods->removeLastAttribute($consArray);
		

		$incArrayCnt = count($incArray);
		$consArrayCnt = count($consArray);		

		//print(count($incArray));
		//print(count($consArray));

		$LastIncArrIndex = $incArray[$incArrayCnt - 1];
		$LastConsArrIndex = $consArray[$consArrayCnt - 1];

		//Create the array which is going to be basis of the entire information string.
		$wholeArray = array();
		$wholeStr = "";	
		/*
		if($incLastIndex->{"analDtmc"} == NULL){
			$lastIncIndexAnaldtmc = "xx";
		}else{
			$lastIncIndexAnaldtmc = $incLastIndex->{"analDtmc"};
		}			
		*/

		/* Compare whether the last incident of Table is the same as the last incident element of array */
		if( ($LastIncArrIndex[ANALDTMC] != $incident->getAnaldtmc() ) || ($incArrayCnt != $incident->getCount()) )
		{
			//Not the same, So you gotta put the last incident element to the Out table.	
			echo "The LAST accident HAS changed. \n";
		
			$incident->setAnaldtmc($LastIncArrIndex[ANALDTMC]);
			$incident->setCount($incArrayCnt);

			$serverMethods->insertIncident($incident);

			/* The status has changed. */
			if($incArrayCnt > 0){
				//$serverMethods->apiJsonToArray($incCount, $incList, $wholeArray);
				$wholeArray = array_merge($wholeArray, $incArray);
			}
			$isChanged = true;
		}else{
			echo "The LAST accident has NOT changed.\n";
		}
			
	    	$consAnaldtmc = $construction->getAnaldtmc();
		$consCnt = $construction->getCount();
	
		/*  Check the Consctruction state   */	
		if(($LastConsArrIndex[ANALDTMC] != $construction->getAnaldtmc()) || ($consArrayCnt != $construction->getCount()) ){
			echo "The construction status HAS changed. \n";
					
			$construction->setAnaldtmc($LastConsArrIndex[ANALDTMC]);	
			$construction->setCount($consArrayCnt);			

			$serverMethods->insertConstruction($construction);
			
			if( ($isChanged == false) && ($incArrCnt > 0) ){
				//$serverMethods->apiJsonToArray($incCount, $incList, $wholeArray); //you need to give the same status to the Drivers.
				$wholeArray = array_merge($wholeArray, $incArray);
			}

			/* The status has changed. */
			if($consArrayCnt > 0){
				//$serverMethods->apiJsonToArray($consCount, $consList, $wholeArray);
				$wholeArray = array_merge($wholeArray, $consArray);
			}
			$isChanged = true;

		}else{
			echo "The construction status has NOT changed. \n";
		}	
		
		//You need to change the array to String list	
		//But We're supposed to send 40 objects of incident and construction.

		//So, first, we gotta count how many elements are in the $wholeArray
		$wholeCnt = count($wholeArray);
		$quotient = $wholeCnt/SEND_ONETIME ;
		$remainder = $wholeCnt%SEND_ONETIME ;
		
		//$subArray is array of the subarrays separated from $wholeArray
		$subArray = array_chunk($wholeArray, SEND_ONETIME );
		
		//Push the Result String to Android Application by using GCM
		print(" wholeCnt is $wholeCnt \n");
		print("quotient is $quotient \n");	
		if($isChanged == true){
			if( $quotient < 1 ){
				print("quotient variable is 0 \n");
				$wholeStr = $serverMethods->multipleArrayToString($subArray[0]);
				
				$wholeStr = "{".$wholeStr."}";

				echo $wholeStr."\n";		
				$gcmMessage = new gcm_message;
				$gcmMessage->sendMessage($wholeStr);	
			}else{	
				print("quotient variable is not 0 \n");
				for($i = 0; $i <= $quotient ; $i++){
					if( ($i == $quotient) && ($remainder == 0) ){
						break;
					}
					$wholeStr = $serverMethods->multipleArrayToString($subArray[$i]);
					$wholeStr = "{".$wholeStr."}";		

					echo $wholeStr."\n";
					$gcmMessage = new gcm_message;
					$gcmMessage->sendMessage($wholeStr);

					if( ($i == $quotient) && ($remainder == 0) ){
						break;
					}
					for($sec = 0 ; $sec < 5 ; $sec++){
						echo ".";
						sleep(1);
					}
					echo "\n";
				}
			}
		}
		
		print("A circle finished \n");
	
		for($i = 0; $i < TIME_QUANTUM ; $i++){
			echo ".";
			sleep(1);
		}
		echo "\n";
	}
?>
