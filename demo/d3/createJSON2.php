<?php
echo "<meta http-equiv='Content-Type'' content='text/html; charset=utf-8'>";
$error_level = error_reporting(0);
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
$value;
$edgeNr = 0;
$entityList;
$entityPair = array();

$tripleNr = 0;

function addEntity($pageid) {
	global $entityList;
	if($pageid <= 0) 
		return;
	$x;
	for($x = 0; $x < count($entityList); $x ++){			
		if($entityList[$x] == $pageid)
			break;
	}
	if($x >= count($entityList)){
		$entityList[count($entityList)] = $pageid;
	}
}
function SolveOneRecord($row, $hasThres) {
	global $tripleNr, $output, $node, $nodeId, $nodeGroup, 
		$src, $tar, $edgeNr, $id, $idNr, $value, $group,
		$entityPair;
		$tSid = $row["SubId"];
		$tOid = $row["ObjId"];
		$tSnode = $row["Subject"];
		$tOnode = $row["Object"];
		$tGroup = 10;
		
		$tVal = 2;
		$tVal += ($tOid > 0) ? 3:0;
		for($x = 0; $x < count($id); $x ++){
			if($id[$x] == $tOid){
				$tVal += 3;
				break;
			}
		}
		if($tOid > 0){
			$pair = $tSid . $tOid;
			$pair2 = $tOid . $tSid;
			for($x = 0; $x < count($entityPair); $x ++) {
				if($pair == $entityPair[$x] || $pair2 == $entityPair) 
					return;
			}
			for($x = 0; $x < count($id); $x ++){
				if($id[$x] == $tOid) {
					$tVal = 30;
					break;
				}
			}
			array_push($entityPair, $pair);
			array_push($entityPair, $pair2);
		}	
		if(//$hasThres == true && 
		$tripleNr > 6* $idNr && $tripleNr > 15)
			return;
		$tripleNr ++;
		
			addEntity($tSid);
			addEntity($tOid);
		$Sid;
		$Oid;
		//update object
		for($Oid = 0; $Oid < $nodeId; $Oid ++){
			if($node[$Oid] == $tOnode)
				break;
		}
		if($Oid == $nodeId){
			$node[$nodeId] = $tOnode;
			// find group id
			$nodeGroup[$nodeId] = $tVal;
			$nodeId ++;
		}
		// get subject
		$tVal = 10;
			for($x = 0; $x < count($id); $x ++){
				if($id[$x] == $tSid) {
					$tVal = 30;
					break;
				}
			}
		for($Sid = 0; $Sid < $nodeId; $Sid ++){
			if($node[$Sid] == $tSnode)
				break;
		}
		if($Sid == $nodeId){
			$node[$Sid] = $tSnode;
			$nodeGroup[$Sid] = $tVal;
			$nodeId ++;
		}
		$src[$edgeNr] = $Sid;
		$tar[$edgeNr] = $Oid;
		$value[$edgeNr] = $tVal/2;
		$edgeNr ++;
		$info = $tSid . "\t" . $tOid . "\t" . 
			$tSnode . "\t" . $row["Predicate"] . "\t" . $tOnode . "\n";
		$output .= $info;
		//echo $tripleNr . " " . $info . "<br/>";
}
function updateNodeAndEdge($link, $query) {
	global $tripleNr, $idNr, $entityList;
	$result = mysqli_query($link, $query);
	if(! $result)
	{
		printf("Error:%s;%s;\n", $result, $query);
		exit();
	}
	
	while($row = mysqli_fetch_array($result)) 
	{		
		$tOid = $row["ObjId"];
		$tSid = $row["SubId"];
		if($tOid > 0){
			SolveOneRecord($row, true);
		}
	}
	echo "part1:" . $tripleNr . "<br/>";
	$tids = $entityList[0];
	for($x = 1; $x < count($entityList); $x ++) {
		$tids .= "," . $entityList[$x];
	}
	$sql2 = "select * from hzTriple where SubId in (" . $tids . ") and " . "ObjId in (" . $tids . "); ";
	echo $sql2 . "<br>";
	$result2 = mysqli_query($link, $sql2);
	while($row = mysqli_fetch_array($result2)) 
	{
		$tOid = $row["ObjId"];			
		if($tOid >= 0)
			SolveOneRecord($row, false);
	}
	echo "part2:" . $tripleNr . "<br/>";
	$result2 = mysqli_query($link, $query);
	if($tripleNr < 6* $idNr || $tripleNr < 30){
		while($row = mysqli_fetch_array($result2)) 
		{
			$tOid = $row["ObjId"];			
			if($tOid == 0)
			SolveOneRecord($row, true);
		}
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
			//echo "id:" . $id[$x] . "\n";
			$group[$x] = $x + 1;
		}
		$sql .= "SubId in (" . $pageids . ") and " . "ObjId in (" . $pageids . "); ";
		$sql2 .= "SubId in (" . $pageids . ") or " . "ObjId in (" . $pageids . "); ";
	}
	elseif(isset($_GET["title"])) {
		$pagetitle = iconv("gb2312","utf-8", $_GET["title"]);
		$pagetitle = str_replace(";", ",", $pagetitle);
		$pagetitles = explode(",", $pagetitle);
		$idNr = count($id);
		for($x = 0; $x < $idNr; $x ++){
			//echo "id:" . $id[$x] . "\n";
			$group[$x] = $x + 1;
		}
		$sql .= "Subject = " . $pagetitle . "; ";
	}
	else {
		$sql .= "SubId = 130; ";
	}
}

$JSONfile = "EGraph/zhwiki/";
sort($id);
for($x = 0; $x < $idNr; $x ++){
	$JSONfile .= $id[$x] . "_";
}
$JSONfile .= ".json";
if(file_exists($JSONfile)) {
	echo $JSONfile;
	return;
}
// this enables Chinese char
$link = mysqli_connect("localhost", "root", "19920326", "hzwikitaxonomy");
mysqli_query($link, "SET NAMES 'utf8'");
if(mysqli_connect_error($link))
{
	echo "connection failed:%s\n" . mysqli_connect_error();
	exit();
}

updateNodeAndEdge($link, $sql2);
if($tripleNr < 20) {
}
// 'w' mode will delete the exist file
$file = "/home/hanzhe/Public/result_hz/Apache/triple.data";
file_put_contents($file, $output);

$ne = array('name', 'group');
$nps = array();
for($x = 0 ; $x < $nodeId; $x ++){
	$insp = array(($node[$x]), $nodeGroup[$x]);
	$np = array_combine($ne, $insp);
	array_push($nps, $np);
}
$ee = array('source', 'target', 'value');
$eps = array();
for($x = 0 ; $x < $edgeNr; $x ++){
	$inse = array(($src[$x]), ($tar[$x]), $value[$x]);
	$ep = array_combine($ee, ($inse));
	array_push($eps, $ep);
}
$jsonCode = array('nodes'=>$nps, 'links'=>$eps);
//echo (json_encode($jsonCode, JSON_UNESCAPED_UNICODE));
//echo $output;
echo "<script type='text/javascript'>DrawEntityMap(". $JSONfile . ");</script>";
file_put_contents($JSONfile, json_encode($jsonCode, JSON_UNESCAPED_UNICODE));
?>

<script type="text/javascript">
function DrawEntityMap(jsonPath) {
    var height = 600;
	
	var color = d3.scale.category20();
	
	var svg = d3.select("body").append("svg")
		.attr("width", width)
		.attr("height", height);
	
	var force = d3.layout.force()
		.charge(-150)
		.linkDistance(function(d) { 
return 200;
     })
		.size([width, height]);	
	
	d3.json(jsonPath, function(error, graph) {
	  force
		  .nodes(graph.nodes)
		  .links(graph.links)
		  .start();
	
	var link = svg.selectAll(".link")
	  .data(graph.links)
	  .enter()
	  .append("line")
	  .attr("class", "link")
	  .attr("stroke","#09F")
	  .attr("stroke-opacity","0.4")
                .attr('cursor','pointer')
	  .style("stroke-width",1)
                .attr("marker-end",function(d){  
                    return "url(#marker-" + (d.target.r) + ")";
                });;
	  
	var node = svg.selectAll(".node")
	  .data(graph.nodes)
	  .enter()
	  .append("g")
	  .call(force.drag);
	
	node.append("circle")
	  .attr("class", "node")
	  .attr("r",function(d){return 10+d.group;})
	  .style("fill", function(d) { return color(d.group); });
	
	node.append("title")
	  .text(function(d) { return d.name; });
	
	node.append("text")
	  .attr("dy", ".3em")
	  .attr("class","nodetext")
	  .style("text-anchor", "middle")
	  .text(function(d) { return d.name; });
	
	force.on("tick", function() {
		link.attr("x1", function(d) { return d.source.x; })
			.attr("y1", function(d) { return d.source.y; })
			.attr("x2", function(d) { return d.target.x; })
			.attr("y2", function(d) { return d.target.y; });
		
		node.attr("transform", function(d){ return "translate("+d.x+"," + d.y + ")";});
	});
	});
}
</script>