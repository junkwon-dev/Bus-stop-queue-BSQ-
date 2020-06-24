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
$now_lng = $_GET['lng'];
$now_lat = $_GET['lat'];
$sql = "SELECT *, ST_DISTANCE_SPHERE(POINT({$now_lng},{$now_lat}), coords) AS dist from bus_stop order by dist limit 5;";
$result = mysqli_query($conn, $sql);
$list_data=array();
if($result){
while($row = mysqli_fetch_array($result)) {
	array_push($list_data, array("ko_name"=>$row['ko_name'], "stop_id"=>$row['stop_id'], "latitude"=>$row['longitude'], "longitude"=>$row['latitude'], "dist"=>$row['dist']));

	
}
header('Content-Type: application/json; charset=utf8');
$json = json_encode(array("distance"=>$list_data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
echo $json;
}
else{
	echo "SQL문 처리중 에러 발생 : ";
}

?>
