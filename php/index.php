<?php
session_start();
if($_SESSION['s_auth'] == 1)
{
	header('Location: upload.php'); 
}
else{
?>

<html>
<head>
<title> Secure Cloud </title>
</head>
<body background = "images/cloud.jpg">
<br><br>
<center><h1> Secure Cloud </h1></center>

<form action = "login_check.php" method = "post">
<br><br><br><br><br><br><br><br>
<center><label> Username: </label>
<input type = "text" name = "user">
<br><br>
<label> Password: </label>
<input type = "password" name = "pass">
<br><br>
<input type = "submit" value = "Login">
</center>
</form>
<?php
if($_SESSION['s_auth'] != 0)
{
?>
<a href = "logout.php"> Logout</a>
<?php }
?>

</body>
</html>

<?php }
?>
