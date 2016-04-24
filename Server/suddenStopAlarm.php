<?php
	//suddenStopAlarm.php

	require_once("gcm_message.php");

	$myRegId = "".$_GET[regId];	
	
	$stoppedPoint = "(".$_GET[yPoint].",".$_GET[xPoint].",".$_GET[regId].",".$_GET[updownDiv].")";

	/* (127.3434,32.34552,Aj323dsFER234wesfsdFEWRfdfdf....,,S)  */	
	/*
	 * "http://en605.woobi.co.kr/incident_alarm/sudenStopAlarm.php?xPoint=" + xPoint + "&yPoint=" + yPoint + "&updownDiv=" + updownDiv+"&regId="+regId;
	 */

	//Push all users the suddenly stopped point.		
	$gcmMessage = new gcm_message;
        $gcmMessage->sendSuddenStopMessage($myRegId, $stoppedPoint);
        //$gcmMessage->sendMessage($stoppedPoint);

?>
