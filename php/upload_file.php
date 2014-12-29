<html>
<head>
<title> Secure Cloud </title>
</head>
<body background="images/cloud.jpg">
<p>

<?php
include "config.php";
session_start();
echo "Hello, ".$_SESSION['s_uname'];
?>

<a style="float: right" href="upload.php">BACK</a>
<a style="float: right" href="logout.php">Logout</a>
<br><br>
<br><br><br><br><br><br><br><br><br><br>
<b>
<center>

<?php 

$target = "upload/"; 
$target = $target . basename( $_FILES['uploaded']['name']); 
$ok=1; 
$file_name = basename( $_FILES['uploaded']['name']);

if(move_uploaded_file($_FILES['uploaded']['tmp_name'], $target)) 
{
	echo "YOUR FILE HAS ". basename( $_FILES['uploadedfile']['name']). " BEEN SECURELY UPLOADED";
} 
else 
{
	echo "Sorry, there was a problem uploading your file.";
}

$filepass = $_POST['filepass'];
$passha = sha1($filepass);
$uname = $_SESSION['s_uname'];

$query2= "insert into user_file values('$uname','$file_name')";
$query3= "insert into file_details values('$file_name','$passha')";

$res3 = mysql_query($query2);
$res4 = mysql_query($query3);

$uname = substr($uname,0,9);
$key = crypt($passha,$uname);

while (strlen($key) <= 15)
{
	$key = $key."x";
}

$myfile ="key.txt";
$fh = fopen($myfile,'w') or die("cannot open file");
fwrite($fh,$key);
fclose($fh);

shell_exec("./enc.sh $uname $myfile $file_name");
?>

</center>
</b>
</p>
</body>
</html>
