<?php
	define("HOST","en605.woobi.co.kr");
	define("USER","en605");
	define("PASSWD","1230");
	define("DB_NAME","en605");

	/* 
	 * 안드로이드 애플리케이션의 GCM에 등록된 ID가 GCM Push Server에 등록되어있는지 확인 후,
	 * 갱신한다.
 	 */	

	$connect = mysql_connect(HOST, USER, PASSWD) or die("Fail to connect to SQL Server");	
	
	$delete_result = '';
	$insert_result = '';
	
	$regId = $_GET[id] ;
	$serialNumber = $_GET[serialNumber];
	mysql_select_db(DB_NAME, $connect) ;

	/* _GET[fIELD1] is the first parameter that you have get from Adnroid Applcation */
	$delete_regId_sql = "DELETE FROM gcmtest WHERE serialNumber = '$_GET[serialNumber]'";

	$exist_regId_sql = "SELECT * FROM gcmtest WHERE id = '$_GET[id]' AND serialNumber = '$_GET[serialNumber]'";
	$insert_new_regId_sql = "INSERT INTO gcmtest(id, serialNumber) VALUES('$_GET[id]', '$_GET[serialNumber]')";
	
	session_start();
		
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
