<html>
<head>
<title> Secure Cloud </title>
</head>
<body background="images/cloud.jpg">
<p>
<?php
session_start();
echo "Hello, ".$_SESSION['s_uname'];
?>
<a style="float: right" href="upload.php">BACK</a>
<a style="float: right" href="logout.php">Logout</a>
<br><br>
<?php

$filename = $_POST['filename'];
$pass = $_POST['filepass'];
$uname = $_SESSION['s_uname'];

$passha = sha1($pass);
$key = crypt($passha,$uname);

while (strlen($key) <= 15)
{
	$key = $key."x";
}

$myfile ="key.txt";
$fh = fopen($myfile,'w') or die("cannot open file");
fwrite($fh,$key);
fclose($fh);

shell_exec("./dec.sh $uname $myfile $filename");

echo "Decrypted!";
?>
