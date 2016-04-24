<?php
	require_once('GCMPushMessage.php');

	define("HOST", "en605.woobi.co.kr");
	define("USER","en605");
	define("PASSWD","1230");
	define("DB_NAME","en605");
	
	//define("API_KEY", "AIzaSyBUztbJyZDYpz-Bovv1uk1vtJ2wm18QnVg");
	define("API_KEY", "AIzaSyDs2eA0LMDaHZU-If2B2qvex6Tew8lJDHI");
	
 class gcm_message{
	//Push the changed API Information to all users by using Google Cloud Messaging for Android
	function sendMessage($paramMessage = 'Test message for GCM by woobi server'){	   
		// the string connection to  database. (location of Database, username, password)
		$connect=mysql_connect(HOST, USER, PASSWD) or  
		die( "Fail to connect to SQL Server");
	 
		mysql_query("SET NAMES UTF8");
		// choose a database
		mysql_select_db(DB_NAME, $connect);
	 
		// Start Session
		//session_start();
		 
		//  Create a query sentence.
		$sql = "select id from gcmtest";
	 
		// store the query execution result into the variable $result.
		$result = mysql_query($sql, $connect);

		//The variable '$rows' means GCM RegId list.
		$rows = array();	

		//Put the list of the regId to array '$rows'
		while($row = mysql_fetch_array($result)){
			array_push($rows, $row["id"]);
		}

		//The Process of Google Cloud Push Messaging PHP Server Class
		$apiKey = API_KEY;
		$devices = $rows;
		
		$gcpm = new GCMPushMessage($apiKey);
		$gcpm-> setDevices($rows);
	
		$response = $gcpm->send($paramMessage) or die("Fail to send GCM") ;
	
		mysql_free_result($result);
		mysql_close($connect);

		echo "The length of the message is ".strlen($paramMessage)."\n";
	}
	
	//When every user turn on the application, they are given the API Information.
	function sendInitialPushMessage($regId, $paramMessage = 'Default Message'){
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
	
	function sendSuddenStopMessage($myRegId, $paramMessage = 'Test message for GCM by woobi server'){	   
		// the string connection to  database. (location of Database, username, password)
		$connect=mysql_connect(HOST, USER, PASSWD) or  
		die( "Fail to connect to SQL Server");
	 
		mysql_query("SET NAMES UTF8");
		// choose a database
		mysql_select_db(DB_NAME, $connect);
	 
		// Start Session
		//session_start();
		 
		//  Create a query sentence.
		$sql = "select id from gcmtest WHERE id != '$myRegId'";
	 
		// store the query execution result into the variable $result.
		$result = mysql_query($sql, $connect);

		//The variable '$rows' means GCM RegId list.
		$rows = array();	

		//Put the list of the regId to array '$rows'
		while($row = mysql_fetch_array($result)){
			array_push($rows, $row["id"]);
		}

		//The Process of Google Cloud Push Messaging PHP Server Class
		$apiKey = API_KEY;
		$devices = $rows;
		
		$gcpm = new GCMPushMessage($apiKey);
		$gcpm-> setDevices($rows);
	
		$response = $gcpm->send($paramMessage) or die("Fail to send GCM") ;
	
		mysql_free_result($result);
		mysql_close($connect);

		echo "The length of the message is ".strlen($paramMessage)."\n";
	}
 }
?>
