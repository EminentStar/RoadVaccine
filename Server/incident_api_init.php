<?php
	/* 한국도로공사의 교통데이터 API의 주소를 저장한다. */
	define("INCIDENT_API_URL","http://data.ex.co.kr/openapi/safeDriving/safeDrivingSupport?key=5460526240&type=json&incTpCd=00");
	define("CONSTRUCTION_API_URL","http://data.ex.co.kr/openapi/safeDriving/safeDrivingSupport?key=5460526240&type=json&incTpCd=03");	
	
	//API의 ID번호가 00이면 사고, 03이면 공사정보임을 나타낸다.
	define("INCIDENT_ID","00");
	define("CONSTRUCT_ID","03");
	
	/* DB접속정보를 저장한다.*/
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

	/* 사고 API를 받아오고 JSON 포맷으로 변환한다. */
	$incResult = $serverMethods->getApiResult(INCIDENT_API_URL);
	try{
		$incResult_array = json_decode($incResult);
	}catch(Exception $ex){
		echo 'Caught exception: ', $e->getMessage(), "\n";
	}

	/* 공사 API를 받아오고 JSON포멧으로 변환한다. */
	$consResult = $serverMethods->getApiResult(CONSTRUCTION_API_URL);
	
	try{
		$consResult_array = json_decode($consResult);
	}catch(Exception $ex){
		echo 'Caught exception: ', $e->getMessage(), "\n";
	}

	//사고 및 공사 JSON 어레이를 사고 list와 list요소의 개수로 나눈다.
	$incList = $incResult_array->{"list"};
	$incCount = $incResult_array->{"count"};
	
	$consList = $consResult_array->{"list"};
	$consCount = $consResult_array->{"count"};
	
	/* 사고 및 공사 리스트의 마지막 Index */
	$incCntNum = $incCount - 1;
	$consCntNum = $consCount - 1;
	
	/* 전체 공사/사고 정보 문자열의 기본이 될 어레이 $wholeArray를 만든다. */
	$wholeArray = array();

	/* 공사나 사고가 1개라도 존재하면 apiJsonToArray 메서드를 호출하고,
	공사와 사고 데이터를 하나의 $whileArray에 저장한다.   */
	if($incCount > 0){
		$serverMethods->apiJsonToArray($incCount, $incList, $wholeArray);
	}	
	if($consCount > 0){
		$serverMethods->apiJsonToArray($consCount, $consList, $wholeArray);
	}
	
	/*
	 * 어레이의 중복 사건들을 제거한다.
 	 */
	if(count($wholeArray) > 0){	
		$serverMethods->fastRemoveSameElement($wholeArray);
	}	

	/* $wholeArray가 일정길이이상이면 분할하기위한 준비를 한다.  */
	$wholeCnt = count($wholeArray); 	//57
	$quotient = $wholeCnt/SEND_ONETIME ; 	//57/40 = 1
	$remainder = $wholeCnt%SEND_ONETIME ;   // 57 % 40 = 17

	print("the whole count is $wholeCnt \n");	
	
	/* $wholeArray로부터 일정크기(SEND_ONETIME)만큼 분할한 $subArray를 만든다. */
	$subArray = array_chunk($wholeArray, SEND_ONETIME );
	if(count($wholeArr != 0))
	{
		if( $quotient < 1 ){ //sub chunk가 한개 일 때
			$wholeStr = $serverMethods->multipleArrayToString($subArray[0]);
			
			$wholeStr = "{".$wholeStr."}";

			echo $wholeStr."\n";		
			$gcmMessage = new gcm_message;
			$gcmMessage->sendMessage($wholeStr);	
		}else{	// sub chunk가 한 개 이상 OR  한개인데 최대길이 일때
			for($i = 0; $i <= $quotient ; $i++){
				if( ($i == $quotient) && ($remainder == 0) ){
					break;
				}
				$wholeStr = $serverMethods->multipleArrayToString($subArray[$i]);
				$wholeStr = "{".$wholeStr."}";		

				echo $wholeStr."\n";
				try{
					$gcmMessage = new gcm_message;
					$gcmMessage->sendMessage($wholeStr);
				}catch(Exception $ex){
					echo 'Caught exception: ', $e->getMessage(), "\n";
				}
				for($sec = 0 ; $sec < 5 ; $sec++){
					print(".");
					sleep(1);
				}
				print("\n");
			}
		}
		
	}
	print("End logic.\n");
?>
