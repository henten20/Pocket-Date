<?php
	// update php script

	$inData = json_decode(file_get_contents('php://input'), true);

	$servername = "localhost";
	$username = "root";
	$password = "";
	$db = "pocketdate";
	
	$user = $inData["username"];
	$field = $inData["field"];
	switch($field)
	{
		case "email":
			$newInfo = $inData["email"];
			break;
		case "firstname":
			$newInfo = $inData["firstname"];
			break;
		case "lastname":
			$newInfo = $inData["lastname"];
			break;
		case "zipcode":
			$newInfo = $inData["zipcode"];
			break;	
		case "preference":
			$newInfo = $inData["preference"];
			break;
		case "about":
			$newInfo = $inData["about"];
			break;
			
	}
	
	$conn = new mysqli($servername, $username, $password, $db);
	if (!$conn) 
	{
		die('Error, could not connect:');
	} 
	else
	{
		// use prepared statements to defend against sql injection attacks
		$sql = $conn->prepare("UPDATE user SET $field = ? WHERE username = ?");
		$sql->bind_param("ss", $newInfo, $user);
		$sql->execute();
		$result = $sql->get_result();
		
		$my_arr[] = array
		(
			'user' => $user
		);
		
		$json = json_encode($my_arr);
		echo($json);
		
		$conn->close();
	}
	
	
?>