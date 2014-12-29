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
<span>
<a style="float: right" href="upload.php">BACK</a>
<a style="float: right" href="logout.php">Logout</a>
</span>
<br><br>
<br><br><br>
<b>
<center>

<?php

$uname = $_SESSION['s_uname'];

$out = shell_exec("./file_view.sh $uname");
echo "<pre>".$out."</pre>";

?>
<br><br>
<form method = "post" action = "file_download.php">
<label> File Name: </label>
<input type = "text" name = "filename">
<br><br>
<label> File Password: </label>
<input type = "password" name = "filepass">
<br><br>
<input type = "submit" value = "Download">
</form>
</center>
</p>
</body>
</html>
