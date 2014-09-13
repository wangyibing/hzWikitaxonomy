<?php
$server = "localhost";
$username = "root";
$password = "19920326";
$database_name = "hzTriple";
$PageTitle = $_GET("title");
$output = "";

$con = mysql_connect($server, $username, $password);
if( ! $con )
{
	echo "<script>alert(\"database connect failed\")</script>";
	return;
}
$database = mysql_select_db($database_name , $con);
$result = mysql_query("select predicate, object from hzTriple where PageTitle = " + $PageTitle, $con);
mysql_data_seek($result, 0);
$row
while($row = mysql_fetch_row($result)) 
{
	$output += $row[0] + "\t" +$row[1] + "\n";
}
// 'w' mode will delete the exist file
$outputFile = fopen("triple.data", "w");
fwrite($outputFile, $output);
fclose($outputFile);
?>