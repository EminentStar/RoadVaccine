<?php
class Construction{
	private $analdtmc; //가장 마지막 공사의 발생 시간
	private $count;//현재 공사상황의 개수

	public function getAnaldtmc(){
		return $this->analdtmc;
	}
	public function getCount(){
		return $this->count;
	}

	public function setAnaldtmc($param){
		$this->analdtmc = $param;
	}
	public function setCount($param){
		$this->count = $param;
	}	
}
?>
