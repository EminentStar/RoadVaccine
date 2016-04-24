<?php

define("HOST","en605.woobi.co.kr");
define("USER","en605");
define("PASSWD","1230");
define("DB_NAME","en605");

class IncidentServerService{
	public function getLastIncident(&$incident){
		$connect=mysql_connect(HOST, USER, PASSWD) or die( "Fail to connect to SQL Server");

		mysql_query("SET NAMES UTF8");
		// choose a database
		mysql_select_db(DB_NAME, $connect);

		// Start Session
		//session_start();

		//  Create a query sentence.

		//$sql = "select analdtmc, startx, starty from incident";
		$sql = "select analdtmc, count from incident";

		// store the query execution result into the variable $result.
		$result = mysql_query($sql, $connect);

		//Save the the number of the records returned.
		$total_record = mysql_num_rows($result);

		//The last incident exists in DB.
		if($total_record == 1 ){
			//move the pointer which will bring the record
			mysql_data_seek($result, $i);

			$row = mysql_fetch_array($result);
			$incident->setAnaldtmc($row[analdtmc]);
			$incident->setCount($row[count]);
		}

		mysql_close($connect);
	}
	

	public function deleteIncident(){
		$connect = mysql_connect(HOST, USER, PASSWD) or die("Fail to connect to SQL Server");

		mysql_select_db(DB_NAME, $connect);

		$sql = "DELETE FROM incident";

		$result = mysql_query( $sql, $connect);
		mysql_close($connect);

		return $result;

	}

	public function insertIncident(&$incident){
		
		$this->deleteIncident();

		$analdtmc = $incident->getAnaldtmc();
		$count = $incident->getCount();		
	
		$connect = mysql_connect(HOST, USER, PASSWD) or die("Fail to connect to SQL Server");

		mysql_select_db(DB_NAME, $connect);

		$sql =  "INSERT INTO incident(analdtmc, count) VALUES('$analdtmc', '$count')";

		$result = mysql_query( $sql, $connect);
		mysql_close($connect);
		
		echo"result of inserting incident : $result \n";

		return $result;
	}

	public function getLastConstruction(&$construction){
		$connect=mysql_connect(HOST, USER, PASSWD) or die( "Fail to connect to SQL Server");

		mysql_query("SET NAMES UTF8");
		// choose a database
		mysql_select_db(DB_NAME, $connect);

		// Start Session
		//session_start();

		//  Create a query sentence.

		//$sql = "select analdtmc, startx, starty from incident";
		$sql = "SELECT analdtmc, count FROM construction";

		// store the query execution result into the variable $result.
		$result = mysql_query($sql, $connect);

		//Save the the number of the records returned.
		$total_record = mysql_num_rows($result);

		//The last incident exists in DB.
		if($total_record == 1 ){
			//move the pointer which will bring the record
			mysql_data_seek($result, $i);

			$row = mysql_fetch_array($result);
			$construction->setAnaldtmc($row[analdtmc]);
			$construction->setCount($row[count]);
		}

		mysql_close($connect);
	}

	public function deleteConstruction(){
		$connect = mysql_connect(HOST, USER, PASSWD) or die("Fail to connect to SQL Server");

		mysql_select_db(DB_NAME, $connect);

		$sql = "DELETE FROM construction";

		$result = mysql_query( $sql, $connect);
		mysql_close($connect);

		return $result;

	}

	public function insertConstruction(&$construction){
		$this->deleteConstruction();

		print("insertConstruction function called \n ");
		$analdtmc = $construction->getAnaldtmc();
		$count = $construction->getCount();

		$connect = mysql_connect(HOST, USER, PASSWD) or die("Fail to connect to SQL Server");

		mysql_select_db(DB_NAME, $connect);

		$sql =  "INSERT INTO construction(analdtmc, count) VALUES('$analdtmc', '$count')";

		$result = mysql_query( $sql, $connect);
		mysql_close($connect);
		
		echo "result of inserting construction DB: $result \n";
		
		return $result;
	}

	public function getApiResult($url){
		//First,
		$curl = curl_init();
		
		//Second,
		curl_setopt_array($curl, array(
			CURLOPT_RETURNTRANSFER => 1,
			CURLOPT_URL => $url
		));
		//Third,
		$result = curl_exec($curl);
	
		if(substr($url, -2) == "00"){ //If the last two characters were "00", this URL might mean that we are requesting Incident API.
			$apiKind = "Incident";
		}else if(substr($url, -2) == "03"){
			$apiKind = "Construction";
		}
		echo "[  Requesting the ".$apiKind." API, Time: ". date('Y-m-d h:i:sa',time()) . "  ]\n";
		
		//Lastly,
		curl_close($curl);

		return $result;
	}
	
	public function apiJsonToString($paramInt, $paramJsonList, &$wholeList){
                for($i = 0 ; $i < $paramInt; $i++){
                        $element = $paramJsonList[$i];
                        $wholeList .= "{".$element->{"incTpCd"}."/".$element->{"contentMsg"}."/".$element->{"analDtmc"}."/".$element->{"startY"}."/".$element->{"startX"}."/".$element->{"updownDiv"}."}";
                        if($i == ($paramInt - 1)){
                                break;
                        }
                        $wholeList .= ",";
                }
        }

        public function apiJsonToArray($paramInt, $paramJsonList, &$wholeArray){
                for($i = 0; $i < $paramInt ; $i++){
                        $element = $paramJsonList[$i];

                        $eArray =  array( $element->{"incTpCd"}, $element->{"contentMsg"}, $element->{"analDtmc"}, $element->{"startY"}, $element->{"startX"}, $element->{"updownDiv"}, $element->{"incId"} );

                        array_push($wholeArray,$eArray);
                }
        }

        public function multipleArrayToString(&$wholeArray){
                $wholeStr = "";

                $lastIndex = count($wholeArray) - 1;
		
                for($i = 0; $i < $lastIndex + 1 ; $i++){
			$element = $wholeArray[$i];
                        $str = "{".implode("/",$element)."}";
                       
			 if($i != $lastIndex ){
                        	$str .= ",";
			}
			
			$wholeStr .= $str;
                }
                return $wholeStr;
        }

	public function removeLastAttribute(&$wholeArray){
		define("INCID", "6");
		foreach($wholeArray as &$element){
			unset($element[INCID]);
		}
	}
	
        //Eliminate the element which is the same incident as later element. So we gatta campare to each other's incId.
        public function removeSameElement(&$wholeArray){
                $isRemoved = false;
		define("INCID","6");
		$delCnt = 0;
                for($i = 0; $i< count($wholeArray) ; $i++){
                        for($j = $i + 1; $j < count($wholeArray) ; $j++){
                                if(($wholeArray[$i][INCID] == $wholeArray[$j][INCID]) ){
                                        unset($wholeArray[$i]);
                                        $wholeArray = array_values($wholeArray);
                                        $i = -1; // When facing continue statement, you need to plus $i once, so you gotta intend $i to be 0.
                                        $isRemoved = true;
				//	print("$delCnt times deleted\n");
					$delCnt++;
                                        break;
                                }
                        }
                        if($isRemoved = true){
                         //go back to the index 0 so that you can check the whole array.
                                $isRemoved = false;
                                continue;
                        }
                }
                $wholeArray = array_values($wholeArray);
        }
	
	public function fastRemoveSameElement(&$wholeArray){
		define("INCID", "6");
	
		$firstIndex = 0;
		$firstIncid = 0;
		$lastIndex = 0;
		$lastIncid = 0;
		
		$arrCnt = count($wholeArray);			
		$delCnt = 0;
		
		for($currIndex = 0 ; $currIndex < ($arrCnt - 1) ; $currIndex++ ){
			if($wholeArray[$lastIndex][INCID] == $wholeArray[$currIndex][INCID] ){
			//if lastIndex is the same as currIndex,
				$lastIndex = $currIndex ;
				continue;
			}else{
			//if lastIndex is not the same as currIndex,
				for($j = 0 ; $j < ($lastIndex - $firstIndex) ; $j++){
					unset($wholeArray[$firstIndex + $j]);
				//	print("$delCnt times deleted \n");
					$delCnt++;
				}
				$firstIndex = $currIndex ; 
				$lastIndex = $currIndex ;
			}
		}
		$wholeArray = array_values($wholeArray);
	}

}

?>
