<?php

 class Incident{
                private $incid; //가장 마지막 사고의 ID
                private $analdtmc; //가장 마지막 사고의 발생시간
                private $startx; //가장 마지막 사고의 경도
                private $starty; //가장 마지막 사고의 위도
		private $count; //현재 공사상황의 개수

                public function getIncid(){
                        return $this->incid;
                }
                public function getAnaldtmc(){
                        return $this->analdtmc;
                }
                public function getStartx(){
                        return $this->startx;
                }
                public function getStarty(){
                        return $this->starty;
                }
		public function getCount(){
			return $this->count;
		}

                public function setIncid($param){
                        $this->incid = $param;
                }
                public function setAnaldtmc($param){
                        $this->analdtmc = $param;
                }
                public function setStartx($param){
                        $this->startx = $param;
                }
                public function setStarty($param){
                        $this->starty = $param;
                }
		public function setCount($param){
			$this->count = $param;
		}
        }

?>
