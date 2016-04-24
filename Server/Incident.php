<?php

 class Incident{
                private $incid;
                private $analdtmc;
                private $startx;
                private $starty;
		private $count;
		
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
