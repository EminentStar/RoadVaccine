<?php

define("HOST","en605.woobi.co.kr");
define("USER","en605");
define("PASSWD","1230");
define("DB_NAME","en605");

/*IncidentServerService 클래스는 공사/사고 데이터를 가공하는데 필요한 메서드의 집합이다.*/
class IncidentServerService{
	public function getLastIncident(&$incident){
		$connect=mysql_connect(HOST, USER, PASSWD) or die( "Fail to connect to SQL Server");

		mysql_query("SET NAMES UTF8");
	
		mysql_select_db(DB_NAME, $connect);

		$sql = "select analdtmc, count from incident";

		$result = mysql_query($sql, $connect);

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

		mysql_select_db(DB_NAME, $connect);
	
		$sql = "SELECT analdtmc, count FROM construction";

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

	//교통데이터를 데이터센터로부터 받아온다.
	public function getApiResult($url){
		$curl = curl_init();
		
		//url로 부터 결과값을 받을 것이라는 설정
		curl_setopt_array($curl, array(
			CURLOPT_RETURNTRANSFER => 1,
			CURLOPT_URL => $url
		));
		//결과 반환
		$result = curl_exec($curl);
	
		if(substr($url, -2) == "00"){ //If the last two characters were "00", this URL might mean that we are requesting Incident API.
			$apiKind = "Incident";
		}else if(substr($url, -2) == "03"){
			$apiKind = "Construction";
		}
		echo "[  Requesting the ".$apiKind." API, Time: ". date('Y-m-d h:i:sa',time()) . "  ]\n";
		
		curl_close($curl);

		return $result;
	}

	//JSON 객체를 문자열로 변경한다.
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
	
	//API의 JSON 리스트를 다차원 어레이로 변형한다.
        public function apiJsonToArray($paramInt, $paramJsonList, &$wholeArray){
                for($i = 0; $i < $paramInt ; $i++){
                        $element = $paramJsonList[$i];

                        $eArray =  array( $element->{"incTpCd"}, $element->{"contentMsg"}, $element->{"analDtmc"}, $element->{"startY"}, $element->{"startX"}, $element->{"updownDiv"}, $element->{"incId"} );

                        array_push($wholeArray,$eArray);
                }
        }
	
	//정렬된 데이터 배열을 하나의 문자열로 변환한다.
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
	
	//문제가 있던 중복사건 제거 알고리즘
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

        //Eliminate the element which is the same incident as later element. So we gatta campare to each other's incId.
	/* 공사나 사고 정보의 경우는 같은 사건에 대해서 여러 데이터가 존재한다.
	 * 그래서 incId비교를 통해서 하나만 남겨두고 나머지 요소는 제거한다.(중복된 incId중 가장 뒤에 있는 요소만 남긴다.)
	 */	
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
				//lastIndex가 currIndex와 같을때 넘어간다.
				$lastIndex = $currIndex ;
				continue;
			}else{
				//lastIndex가 currIndex와 같지 않을때, 그이전까지의 중복요소들을 하나만 남기고 전부 제거한다.
				for($j = 0 ; $j < ($lastIndex - $firstIndex) ; $j++){
					unset($wholeArray[$firstIndex + $j]);
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
