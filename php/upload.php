<?php
session_start();
echo "Hello, ".$_SESSION['s_uname'];
?>

<html>
<head>
<title> Secure Cloud </title>
</head>
<body background = "images/cloud.jpg">
<a style="float: right" href = "logout.php"> Logout</a>
<form enctype="multipart/form-data" action="upload_file.php" method="POST">
<br><br><br><br><br><br>
<center>
Please choose a file: <br><br>
<input name="uploaded" type="file" /><br /> 
<br><br>
<p> Enter a file password: </p>
<input type="password" name="filepass" />
<br><br>
<input type="submit" value="Upload" />
</center>
</form> 
<center><a href = "file_view.php"> View uploaded files </a></center>
</body>
</html>
