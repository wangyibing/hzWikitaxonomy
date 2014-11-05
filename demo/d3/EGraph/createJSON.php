<?php

$output = "";
// sql that find triples
$sql = "select * from hzTriple where ";
// sql that find triples
$sql2 = "select * from hzTriple where ";
$pagetitle = "";
// group of triple
$group;
// subject id of corresponding group
$id;
// node titles
$node;
// node id of each node
$nodeId = 0;
$nodeGroup;
// edges
$src;
$tar;
$val;
$edgeNr = 0;

$tripleNr = 0;
$link = mysqli_connect("localhost", "root", "19920326", "hzwikitaxonomy");

function updateNodeAndEdge($link, $query) {
	global $tripleNr, $output, $node, $nodeId, $nodeGroup, 
		$src, $tar;
	$result = mysqli_query($link, $query);
	if(! $result)
	{
		printf("Error:%s;%s;\n", mysqli_error($result), $query);
		exit();
	}
	while($row = mysqli_fetch_array($result)) 
	{
		$tripleNr ++;
		$tSid = $row["SubId"];
		$tOid = $row["ObjId"];
		$tPid = $row["PredId"];
		$tSnode = $row["Subject"];
		$tOnode = $row["Object"];
		$tGroup = 10;
		$Sid;
		$tVal = 1;
		$tVal += ($tOid > 0) ? 4:0;
		$tVal += ($tPid > 0) ? 2:0;
		for($x s= 0; $x < $nodeId; $x ++){
			if($id[$x] == $tOid){
				$tVal += 10;
				break;
			}
		}
		
			
		// update subject
		for($Sid s= 0; $Sid < $nodeId; $Sid ++){
			if($node[$Sid] == $tSnode)
				break;
		}
		if($Sid == $nodeId){
			$node[$Sid] = $tSnode;
			$nodeGroup[$Sid] = 0;
			$nodeId ++;
		}
		$Oid;
		//update object
		for($Oid s= 0; $Oid < $nodeId; $Oid ++){
			if($node[$Oid] == $tOnode)
				break;
		}
		if($Oid == $nodeId){
			$node[$nodeId] = $tOnode;
			// find group id
			for($x = 0; $x < count($group); $x ++){
				if($id[$x] == $tSid) {
					$tGroup = $x;
					break;
				}
			}
			$nodeGroup[$nodeId] = $tGroup;
			$nodeId ++;
		}
		$src[$edgeNr] = $tSnode;
		$tar[$edgeNr] = $tOnode;
		$edgeNr ++;
		$output .= $tSid . "\t" . $tOid . "\t" . 
			$tSnode . "\t" . $row["Predicate"] . "\t" . $tOnode . "\n";
	}
}

if(is_array($_GET) && count($_GET) > 0)
{
	if(isset($_GET["id"])){
		$pageids = $_GET["id"];
		$pageids = str_replace(";", ",", $pageids);
		$id = explode(",", $pageids);
		$idNr = count($id);
		for($x = 0; $x < $idNr; $x ++){
			echo $id[$x];
			$group[$x] = 1;
		}
		$sql .= "SubId in (" . $pageids . ") and " . "ObjId in (" . $pageids . "); ";
		$sql2 .= "SubId in (" . $pageids . ") or " . "ObjId in (" . $pageids . "); ";
	}
	elseif(isset($_GET["title"])) {
		$pagetitle = iconv("gb2312","utf-8", $_GET["title"]);
		$sql .= "Subject = " . $pagetitle . "; ";
	}
	else {
		$sql .= "SubId = 130; ";
	}
}
echo "php." . $sql;
// this enables Chinese char
mysqli_query($link, "SET NAMES 'utf8'");
if(mysqli_connect_error($link))
{
	echo "connection failed:%s\n" . mysqli_connect_error();
	exit();
}

updateNodeAndEdge($link, $sql);
echo "tripleNr:" . $tripleNr;
if($tripleNr < 20) {
	updateNodeAndEdge($link, $sql2);
	echo "tripleNr:" . $tripleNr;
}
// 'w' mode will delete the exist file
$file = "/home/hanzhe/Public/result_hz/Apache/triple.data";
file_put_contents($file, $output);


$JSONfile = "EGraph/zhwiki/";
sort($id);
for($x = 0; $x < $idNr; $x ++){
	$JSONfile .= $id[$x] . ";";
}
$JSONfile .= ".json";
$ne = array('name', 'group');
$nps = array();
for($x = 0 ; $x < $nodeId; $x ++){
	$insp = array($node[$x], $nodeGroup[$x]);
	$np = array_combine($ne, $insp);
	array_push($nps, $np);
}
$ee = array('source', 'target', 'value');
$eps = array();
for($x = 0 ; $x < $edgeNr; $x ++){
	$inse = array($src[$x], $tar[$x], $val[$x]);
	$ep = array_combine($ee, $inse);
	array_push($eps, $ep);
}
$jsonCode = array('nodes'=>$nps, 'links'=>$eps);
echo json_code($jsonCode);
file_put_contents($JSONfile, json_code($jsonCode));
?>