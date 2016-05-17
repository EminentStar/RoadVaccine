<?php
	//suddenStopAlarm.php
	//급정지 정보 전송을 위한 php 파일
	require_once("gcm_message.php");

	$myRegId = "".$_GET[regId];	
	
	$stoppedPoint = "(".$_GET[yPoint].",".$_GET[xPoint].",".$_GET[regId].",".$_GET[updownDiv].")";

	/*
	 * (127.3434,32.34552,Aj323dsFER234wesfsdFEWRfdfdf....,,S)
	 * "http://en605.woobi.co.kr/incident_alarm/sudenStopAlarm.php?xPoint=" + xPoint + "&yPoint=" + yPoint + "&updownDiv=" + updownDiv+"&regId="+regId;
	 */

	//급정지가 된 위치를 모든 사용자에게 보낸다.
	$gcmMessage = new gcm_message;
        $gcmMessage->sendSuddenStopMessage($myRegId, $stoppedPoint);

?>
