<?php 
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
$id = $_GET['id'];


$insert = "INSERT INTO user_table(user_id) VALUES($id);";

//mysqli_query($conn, $insert);

$sql_check = "SELECT * FROM user_table where user_id = $id;";
$sql = "SELECT * FROM user_table;";
$result = mysqli_query($conn, $sql_check);
$row = mysqli_fetch_array($result);
/*echo "row = ";
echo $row['user_id'];
echo '<br>';
echo "id = ";
echo $id;*/
$ch = "'".$row['user_id']."'";
/*echo '<br>';
echo $ch;
echo '<br>';*/
if($ch == $id){
	echo "중복된 id";
//	echo "
//";
}
else{
	echo"중복되지 않은 id";
mysqli_query($conn, $insert);
}
/*$result = mysqli_query($conn, $sql);
echo '
';
while($row = mysqli_fetch_array($result)) {
	echo $row['user_id'];
	echo '
';
 
 
}*/
mysqli_close($conn);
?>
