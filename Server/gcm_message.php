<?php
	/*
	 * File Name:  gcm_message.php
	 * Purpose: GCM 메시지를 전송하는 로직
	 */
	require_once('GCMPushMessage.php');
	
	/* DB 접속정보를 저장한다. */
	define("HOST", "en605.woobi.co.kr");
	define("USER","en605");
	define("PASSWD","1230");
	define("DB_NAME","en605");

	//GCM Server key를 저장한다.
	define("API_KEY", "AIzaSyDs2eA0LMDaHZU-If2B2qvex6Tew8lJDHI");
	
 class gcm_message{
	//Push the changed API Information to all users by using Google Cloud Messaging for Android
	//변화된 API 데이터를 GCM 기능을 이용해서 Push한다.
	function sendMessage($paramMessage = 'Test message for GCM by woobi server'){	   
		//DB connection을 설정한다.
		$connect=mysql_connect(HOST, USER, PASSWD) or  
		die( "Fail to connect to SQL Server");
	 
		mysql_query("SET NAMES UTF8");
		
		mysql_select_db(DB_NAME, $connect);
		
		//쿼리문을 생성한다.
		$sql = "select id from gcmtest";
	 
		//쿼리문에 대한 결과를 저장한다.
		$result = mysql_query($sql, $connect);

		//GCM테이블에 등록된 ID 리스트를 위한 어레이를 만든다.
		$rows = array();	

		//GCM 등록 ID를 array에 담는다.
		while($row = mysql_fetch_array($result)){
			array_push($rows, $row["id"]);
		}

		//The Process of Google Cloud Push Messaging PHP Server Class
		$apiKey = API_KEY;
		$devices = $rows;
		
		//GCM Message를 Push한다.
		$gcpm = new GCMPushMessage($apiKey);
		$gcpm-> setDevices($rows);
		$response = $gcpm->send($paramMessage) or die("Fail to send GCM") ;
	
		//DB connection을 해제한다.
		mysql_free_result($result);
		mysql_close($connect);

		echo "The length of the message is ".strlen($paramMessage)."\n";
	}
	
	//사용자가 로드백신 앱을 킬때마다, API 데이터를 전송한다.
	function sendInitialPushMessage($regId, $paramMessage = 'Default Message'){
		//전달된 GCM ID로 API 데이터를 Push한다.

		$rows = array();
		array_push($rows, $regId);	

		//The Process of Google Cloud Push Messaging PHP Server Class
		$apiKey = API_KEY;
		$devices = $rows;
		
		$gcpm = new GCMPushMessage($apiKey);
		$gcpm-> setDevices($rows);
	
		$response = $gcpm->send($paramMessage) or die("Fail to send GCM") ;
		
		echo "The length of the message is ".strlen($paramMessage)."\n";
	}
	
	//로드백신 사용자의 자동차가 급정지를하면 해당 사용자의 위치를 모든 등록된 GCM  아이디로 Push한다.
	function sendSuddenStopMessage($myRegId, $paramMessage = 'Test message for GCM by woobi server'){	   
		//DB connection 설정	
		$connect=mysql_connect(HOST, USER, PASSWD) or  
		die( "Fail to connect to SQL Server");
	 
		mysql_query("SET NAMES UTF8");
		// database 선택
		mysql_select_db(DB_NAME, $connect);
	 	
		//쿼리문 생성. 급정지 운전자를 제외한 gcm registration id를 받아온다.
		$sql = "select id from gcmtest WHERE id != '$myRegId'";
		
		//쿼리문 결과를 저장한다.
		$result = mysql_query($sql, $connect);

		//'$rows'는 GCM RegId 리스트를 의미한다.
		$rows = array();	
 		
		//rows에 GCM RegId를 담는다.
		while($row = mysql_fetch_array($result)){
			array_push($rows, $row["id"]);
		}

		//The Process of Google Cloud Push Messaging PHP Server Class
		$apiKey = API_KEY;
		$devices = $rows;
		
		//GCM Push
		$gcpm = new GCMPushMessage($apiKey);
		$gcpm-> setDevices($rows);	
		$response = $gcpm->send($paramMessage) or die("Fail to send GCM") ;

		//DB connection 해제
		mysql_free_result($result);
		mysql_close($connect);

		echo "The length of the message is ".strlen($paramMessage)."\n";
	}
 }
?>
