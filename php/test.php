<?php
$passha = sha1("nextpage99");

$passha1 = sha1($passha);
//echo $passha1;
$key = crypt($passha1,"dadley115");
echo $key;
?>
