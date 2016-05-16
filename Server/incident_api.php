<?php
/* 한국도로공사의 교통데이터 API의 주소를 저장한다. */
	define("INCIDENT_API_URL","http://data.ex.co.kr/openapi/safeDriving/safeDrivingSupport?key=5460526240&type=json&incTpCd=00");
	define("CONSTRUCTION_API_URL","http://data.ex.co.kr/openapi/safeDriving/safeDrivingSupport?key=5460526240&type=json&incTpCd=03");	
	
	//API의ID번호가 00이면 사고, 03이면 공사정보임을 나타낸다.
	define("INCIDENT_ID","00");
	define("CONSTRUCT_ID","03");
	
	//DB 접속정보를 저장한다.
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

	$isChanged = false;

	/* The variable $incident is going to be used for store the last incident of incident table in DB  */	
	$incident = new Incident;	
	$construction = new Construction;

	$serverMethods->getLastIncident($incident);
	$serverMethods->getLastConstruction($construction);

	// 공사/사고 API를 계속적으로 체크하는 루프
	while(true){
		//공사/사고 API를 요청하고 JSON형식으로 변환한다.
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

		//JSON array로부터 각 JSON object를 뽑아낸다.
		$incList = $incResult_array->{"list"};
		$incCount = $incResult_array->{"count"};
		$incCode = $incResult_array->{"code"};
		
		$consList = $consResult_array->{"list"};
		$consCount = $consResult_array->{"count"};
		$consCode = $consResult_array->{"code"};
		
		//공사/사고의 마지막 index를 구한다.
		$incCntNum = $incCount - 1;
		$consCntNum = $consCount - 1;
	
		//Temporary arrays.
		$incArray = array();
		$consArray = array();
	
		//JSON 형태의 사건 API를 특정 포맷의 Array로 변경한다.
		$serverMethods->apiJsonToArray($incCount, $incList, $incArray);
		$serverMethods->apiJsonToArray($consCount, $consList, $consArray);
	
		//사건에서 중복 사건들을 제거한다.
		$serverMethods->fastRemoveSameElement($incArray);
		$serverMethods->fastRemoveSameElement($consArray);

		$incArrayCnt = count($incArray);
		$consArrayCnt = count($consArray);		

		$LastIncArrIndex = $incArray[$incArrayCnt - 1];
		$LastConsArrIndex = $consArray[$consArrayCnt - 1];

		//전체 정보 문자열의 기본이 될 $wholeArray 어레이를 생성한다.
		$wholeArray = array();
		$wholeStr = "";	

		/* Compare whether the last incident of Table is the same as the last incident element of array */
		//사고 현황의 변화가 있을때
		if( ($LastIncArrIndex[ANALDTMC] != $incident->getAnaldtmc() ) || ($incArrayCnt != $incident->getCount()) )
		{
			//Not the same, So you gotta put the last incident element to the Out table.	
			echo "The LAST accident HAS changed. \n";
		
			$incident->setAnaldtmc($LastIncArrIndex[ANALDTMC]);
			$incident->setCount($incArrayCnt);

			$serverMethods->insertIncident($incident);

			/* The status has changed. */
			if($incArrayCnt > 0)
				$wholeArray = array_merge($wholeArray, $incArray);
			
			$isChanged = true;
		}else{
			echo "The LAST accident has NOT changed.\n";
		}
			
	    	$consAnaldtmc = $construction->getAnaldtmc();
		$consCnt = $construction->getCount();
		
		//공사 현황의 변화가 있을때
		if(($LastConsArrIndex[ANALDTMC] != $construction->getAnaldtmc()) || ($consArrayCnt != $construction->getCount()) ){
			echo "The construction status HAS changed. \n";
					
			$construction->setAnaldtmc($LastConsArrIndex[ANALDTMC]);	
			$construction->setCount($consArrayCnt);			

			$serverMethods->insertConstruction($construction);
			
			if( ($isChanged == false) && ($incArrCnt > 0) )
				$wholeArray = array_merge($wholeArray, $incArray);

			/* The status has changed. */
			if($consArrayCnt > 0)
				$wholeArray = array_merge($wholeArray, $consArray);
			
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
		
		//결과 문자열을 GCM을 이용하여 안드로이드 애플리케이션으로 푸시한다.
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
					if( ($i == $quotient) && ($remainder == 0) )
						break;
					
					$wholeStr = $serverMethods->multipleArrayToString($subArray[$i]);
					$wholeStr = "{".$wholeStr."}";		

					echo $wholeStr."\n";
					$gcmMessage = new gcm_message;
					$gcmMessage->sendMessage($wholeStr);

					if( ($i == $quotient) && ($remainder == 0) )
						break;
					
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
