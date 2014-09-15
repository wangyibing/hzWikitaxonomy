<?php
$PageTitle = "习近平";
$output = "";
echo $_SERVER['QUERY_STRING'] . "\r\n";
// $PageTitle = $_POST['title'];
$sql = "select * from hzTriple where ";
if(is_array($_GET) && count($_GET) > 0)
{
	if(isset($_GET["id"])){
		$sql .= "SubId = " . $_GET["id"] . "; ";
	}
	elseif(isset($_GET["title"])) {
		$sql .= "Subject = " . $_GET["title"] . "; ";
	}
	else {
		$sql .= "SubId = 130; ";
	}
}
echo $sql;
$link = mysqli_connect("localhost", "root", "19920326", "hzwikitaxonomy");
// this enables Chinese char
mysqli_query($link, "SET NAMES 'utf8'");
if(mysqli_connect_error($link))
{
	echo "connection failed:%s\n" . mysqli_connect_error();
	exit();
}
	
$result = mysqli_query($link, $sql);
if(! $result)
{
	printf("Error:%s;%s;\n", mysqli_error($result), $sql);
	exit();
}
$objIds = array();
while($row = mysqli_fetch_array($result)) 
{
	$output .= $row["SubId"] . "\t" . $row["ObjId"] . "\t" . 
		$row["Subject"] . "\t" . $row["Predicate"] . "\t" . $row["Object"] . "\n";
	if($row["ObjId"] != "0"){
		array_push($objIds, $row["ObjId"]);
		echo "objId: " . $row["ObjId"] . "<br>";
	}
}
$sql = "select * from hzTriple where ";
if(count($objIds) > 1){
	$condi = "(";
	for($i = 0; $i < count($objIds); $i ++){
		if($i == 0){
			$condi .= $objIds[$i];
		}
		else {
			$condi .= ", " . $objIds[$i];
		}
	}
	$condi .= ")";
	$sql .= " SubId IN " . $condi . " AND ObjId IN " . $condi . " AND SubId != ObjId";
	$result = mysqli_query($link, $sql);
	if(! $result || mysqli_num_rows($result) <= 0) {
		echo $sql . "<br>";
		continue;
	}
	while($row = mysqli_fetch_array($result)) {
		$output .= $row["SubId"] . "\t" . $row["ObjId"] . "\t" .
			$row["Subject"] . "\t" . $row["Predicate"] . "\t" . $row["Object"] . "\n";
	}
}

// 'w' mode will delete the exist file
$file = "/home/hanzhe/Public/result_hz/Apache/triple.data";
file_put_contents($file, $output);
?>