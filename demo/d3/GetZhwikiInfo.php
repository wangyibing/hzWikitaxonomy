<?php
$error_level = error_reporting(0);
$sql = ""; 
$res = "";
if(is_array($_GET) && count($_GET) > 0)
{
	$res = strtolower($_GET["res"]);
	if(isset($_GET["id"])){
		$pageid = ($_GET["id"]);
		if($res == "img") {
			$sql = "select IMGUrl, IMGWidth, IMGHeight from EntityInfo where EntityId = " . $pageid . ";";
		}elseif($res == "firpara") {
			$sql = "select FirPara from EntityInfo where EntityId = " . $pageid . ";";
		}
	}
	elseif(isset($_GET["title"])) {
		$pagetitle = iconv("gb2312","utf-8", $_GET["title"]);
		if($res == "img") {
			$sql = "select IMGUrl, IMGWidth, IMGHeight from EntityInfo where title = " . $pagetitle . ";";
		}elseif($res == "firpara") {
			$sql = "select FirPara from EntityInfo where title = " . $pagetitle . ";";
		}
	}
	else {
		//echo "not exist in db";
		return;
	}
}
else {
	return;
}

$link = mysqli_connect("172.31.19.9", "root", "", "hzWikiCount2");
mysqli_query($link, "SET NAMES 'utf8'");
$result = mysqli_query($link, $sql);
if(mysqli_connect_error($link))
{
	//echo "connection failed:%s\n" . mysqli_connect_error();
	exit();
}

if($res == "img"){
	while($row = mysqli_fetch_array($result)) {
	$IMGUrl = $row["IMGUrl"] == null ? "": $row["IMGUrl"];
	$IMGWidth = $row["IMGWidth"] == null ? "": $row["IMGWidth"];
	$IMGHeight = $row["IMGHeight"] == null ? "": $row["IMGHeight"];
		echo "<img src=\"http://" . $IMGUrl . "\" width=\"" . $IMGWidth .
			"\" height=\"" . $IMGHeight . "\" />";
	} 
}
elseif($res == "firpara") {
	while($row = mysqli_fetch_array($result)) {
		echo $row["FirPara"];
	}
}
?>