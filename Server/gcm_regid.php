<?php
	define("HOST","en605.woobi.co.kr");
	define("USER","en605");
	define("PASSWD","1230");
	define("DB_NAME","en605");

	/* Android users request this PHP file for two cases
	   case 1: The users run the android application as the first time.
		   parameters: id=regislation id, before_regId='null' (Just string 'null')
	   case 2: The regislation id of the users has changed.
		   parameters: id=new regislation id, before_regId=previous regislation id
	*/
	

	$connect = mysql_connect(HOST, USER, PASSWD) or die("Fail to connect to SQL Server");	
	
	$delete_result = '';
	$insert_result = '';
	
	//$before_regId = $_GET[before_regId] ;
	$regId = $_GET[id] ;
	$serialNumber = $_GET[serialNumber];
	mysql_select_db(DB_NAME, $connect) ;

	/* _GET[fIELD1] is the first parameter that you have get from Adnroid Applcation */
	//$delete_before_regId_sql = "DELETE FROM gcmtest WHERE id = '$_GET[before_regId]'";
	$delete_regId_sql = "DELETE FROM gcmtest WHERE serialNumber = '$_GET[serialNumber]'";

	$exist_regId_sql = "SELECT * FROM gcmtest WHERE id = '$_GET[id]' AND serialNumber = '$_GET[serialNumber]'";
	$insert_new_regId_sql = "INSERT INTO gcmtest(id, serialNumber) VALUES('$_GET[id]', '$_GET[serialNumber]')";
	
	session_start();
		
	/*
	if( strcmp( $before_regId, "null" ) != 0  ) //two strings are NOT equal
	{
		$delete_result = mysql_query( $delete_before_regId_sql, $connect );
	}
	*/
	
	//get the count of the row that has the phone's serial number and regislation id.
	$count_result = mysql_query($exist_regId_sql, $connect );
	
	$num_rows = mysql_num_rows($count_result);
	
	if($num_rows == 0 )//if there is no such record that contains the serialNumber and regid, 
	{
		$delete_result = mysql_query($delete_regId_sql, $connect );
		$insert_result = mysql_query( $insert_new_regId_sql, $connect);
	}

	mysql_close($connect);
	
	$total_result = 'delete result: '.$delete_result.'\ncount result: '.$count_result.'\ninsert result: '.$insert_result;
	
	return $total_result;	
?>
