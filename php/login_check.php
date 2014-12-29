<?php
	session_start();
	include "config.php";

	$username = $_POST['user'];
	$passwd_user = $_POST['pass'];

	$_SESSION['s_uname'] = $username;
//	echo $_SESSION['uname']=0;

	$query = "select * from login_details where username = '$username'" ;
	$result = mysql_query($query);
	$row = mysql_fetch_array($result);
	$passwd_db = $row['password'];

	if($passwd_user == $passwd_db && $passwd_user != "" )		
	{
		$_SESSION['s_auth'] = 1;
		header('Location: upload.php'); 
	}
	else
	{
		$_SESSION['s_auth'] = 0;
		header('Location: index.php'); 
	}
?>
