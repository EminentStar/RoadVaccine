<?php
	define("INCIDENT_API_URL","http://data.ex.co.kr/openapi/safeDriving/safeDrivingSupport?key=5460526240&type=json&incTpCd=00");
	define("CONSTRUCTION_API_URL","http://data.ex.co.kr/openapi/safeDriving/safeDrivingSupport?key=5460526240&type=json&incTpCd=03");	
	
	define("INCIDENT_ID","00");
	define("CONSTRUCT_ID","03");
	
	define("HOST","en605.woobi.co.kr");
	define("USER","en605");	
	define("PASSWD","1230");	
	define("DB_NAME","en605");	
	define("SEND_ONETIME",40);	

	require_once("gcm_message.php");
	require_once("IncidentServerService.php");

	$serverMethods = new IncidentServerService;
	
	$regId = $_GET[regId];

	/*
		In Android Application,
		"http://en605.woobi.co.kr/incident_alarm/incident_api_init.php?regId=" + regId;
		
		the LAST "regId" is variable gotten from GCM Server.
	*/	


	/* The variable $incident is going to be used for store the last incident of incident table in DB  */	

	/*Testing variable */

	/*Request The Incident API and get the information as the JSON format*/
	$incResult = $serverMethods->getApiResult(INCIDENT_API_URL);
	try{
		$incResult_array = json_decode($incResult);
	}catch(Exception $ex){
		echo 'Caught exception: ', $e->getMessage(), "\n";
	}
	$consResult = $serverMethods->getApiResult(CONSTRUCTION_API_URL);
	
	try{
		$consResult_array = json_decode($consResult);
	}catch(Exception $ex){
		echo 'Caught exception: ', $e->getMessage(), "\n";
	}

	/* Seperate JSON Array to the each JSON Object  */
	$incList = $incResult_array->{"list"};
	$incCount = $incResult_array->{"count"};
	
	$consList = $consResult_array->{"list"};
	$consCount = $consResult_array->{"count"};
	
	/* get the last index of the incident list  */
	$incCntNum = $incCount - 1;
	$consCntNum = $consCount - 1;
	
	//Create the array which is going to be basis of the entire information string.
	$wholeArray = array();

	if($incCount > 0){
		$serverMethods->apiJsonToArray($incCount, $incList, $wholeArray);
	}
	
	/*  Check the Consctruction state   */	
	if($consCount > 0){
		$serverMethods->apiJsonToArray($consCount, $consList, $wholeArray);
	}
	
	if(count($wholeArray) > 0){	
		$serverMethods->fastRemoveSameElement($wholeArray);
		$serverMethods->removeSameElement($wholeArray);
		$serverMethods->removeLastAttribute($wholeArray);
	}
	
	
	$wholeCnt = count($wholeArray);
	$quotient = $wholeCnt/SEND_ONETIME ;
	$remainder = $wholeCnt%SEND_ONETIME ;

	print("the whole count is $wholeCnt \n");	
	
	//$subArray is array of the subarrays separated from $wholeArray
	//$subArray = $serverMethods->arangeArray($wholeCnt, $quotient, $remainder);
	$subArray = array_chunk($wholeArray, SEND_ONETIME );

	if(count($wholeArr != 0))
	{
		if( $quotient < 1 ){
			$wholeStr = $serverMethods->multipleArrayToString($subArray[0]);
			
			$wholeStr = "{".$wholeStr."}";

			echo $wholeStr."\n";		
			$gcmMessage = new gcm_message;
			$gcmMessage->sendMessage($wholeStr);	
		}else{	
			for($i = 0; $i <= $quotient ; $i++){
				if( ($i == $quotient) && ($remainder == 0) ){
					break;
				}
				$wholeStr = $serverMethods->multipleArrayToString($subArray[$i]);
				$wholeStr = "{".$wholeStr."}";		

				echo $wholeStr."\n";
				$gcmMessage = new gcm_message;
				$gcmMessage->sendMessage($wholeStr);
				for($sec = 0 ; $sec < 5 ; $sec++){
					print(".");
					sleep(1);
				}
				print("\n");
			}
		}
		
	}
?>
