<?php
//header('Content-type: text/html');
//	header('Content-Type: applcation/json; charset=utf8');
$conn = mysqli_connect(
	'localhost',
	'test',
	'capstone123!',
	'bus_stop_db',
	'3306');
if(mysqli_connect_errno())
{
	echo "Falied to connect to MySQL: " . mysqli_connect_error();
}
$stop_id = $_GET['stop_id'];
$sql = "SELECT count(*) as cnt, count(with_wheel) as wheel FROM utilizing WHERE stop_id=".$stop_id.";";
$sql_check = "SELECT * FROM bus_stop WHERE stop_id=".$stop_id.";";
$get_ko = "SELECT ko_name FROM bus_stop WHERE stop_id=".$stop_id.";";
$ko_name = mysqli_fetch_array(mysqli_query($conn, $get_ko));

$result = mysqli_query($conn, $sql);
$result_check = mysqli_query($conn, $sql_check);
$list_data=array();
if(mysqli_fetch_array($result_check)){
while($row = mysqli_fetch_array($result)) {
	array_push($list_data, array("stop_id"=>$stop_id,"ko_name"=>$ko_name['ko_name'], "people"=>$row['cnt'], "wheel"=>$row['wheel']));
		
}
}
else{
	
	array_push($list_data, array("stop_id"=>$stop_id, "ko_name"=>$row['ko_name'], "people"=>"stop_id 오류", "wheel"=>"stop_id 오류"));


}
header('Content-Type: application/json; charset=utf8');
$json = json_encode(array("next_stop"=>$list_data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
echo $json;

?>
