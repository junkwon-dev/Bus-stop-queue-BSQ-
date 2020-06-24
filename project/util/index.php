<?php
header('Content-type: text/html');
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
$stop_id=$_GET['stop_id'];
$username=$_GET['username'];
$ko_name=$_GET['ko_name'];
$do_what = $_GET['do_what'];
$with_wheel = $_GET['with_wheel'];
if($do_what==1){
	/*echo $stop_id, $do_what, $username, $ko_name, $with_wheel;*/
	$sql="INSERT INTO utilizing(stop_id,user_id,ko_name,with_wheel) VALUES ($stop_id,'$username','$ko_name',$with_wheel);";
	
}
else if($do_what==0){
	$sql="DELETE FROM utilizing WHERE user_id=\"".$username."\";";
}

$result = mysqli_query($conn, $sql);
if($result){
	echo "정상 처리되었습니다.";
}
else{
	echo "실패되었습니다.";
}
/*header('Content-Type: application/json; charset=utf8');
$json = json_encode(array("distance"=>$list_data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
echo $json;
}
else{
	echo "SQL문 처리중 에러 발생 : ";
}
 */
?>
