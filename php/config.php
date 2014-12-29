<?php
//error_reporting(1);
// DB Connectivity
	$username = "root";
	$password = "sample";
	$database = "securetce";

	mysql_connect($localhost,$username,$password);
	@mysql_select_db($database) or die( "Unable to select database");
	// End of DB Connectivity
?>
