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
		case "password":
			$oldPass = $inData["oldPass"];
			$newPass = $inData["newPass"];
			break;
	}
	
	$conn = new mysqli($servername, $username, $password, $db);
	if (!$conn) 
	{
		die('Error, could not connect:');
	} 
	else if($field == "password")
	{
		// hashing the password and inserting into the db
		$hashed_pass = crypt($oldPass, 'CRYPT_BLOWFISH');
		
		// Select the pass and check if the old password matches 
		$sql = $conn->prepare("SELECT pass FROM user WHERE pass = ? AND user_id = ?");
		$sql->bind_param("ss", $hashed_pass, $user);
		$sql->execute();
		$result = $sql->get_result();
		
		if ($result->num_rows > 0)
		{
			// The password matches if the result returns a value
			// use prepared statements to defend against sql injection attacks
			// hashing the password and inserting into the db
			$new_hashed_pass = crypt($newPass, 'CRYPT_BLOWFISH');
			
			$sql = $conn->prepare("UPDATE user SET pass = ? WHERE user_id = ?");
			$sql->bind_param("ss", $new_hashed_pass, $user);
			$sql->execute();
			$result = $sql->get_result();
			$msg = "success";
			$my_arr[] = array
			(
				'user' => $msg
			);
		}
		else
		{
			// return an error stating that the password didn't match
			$msg = "error";
			$my_arr[] = array
			(
				'user' => $msg
			);
			
			return;
		}
	}
	// For all other fields other than password
	else
	{
		$sql = $conn->prepare("UPDATE user SET $field = ? WHERE user_id = ?");
		$sql->bind_param("ss", $newInfo, $user);
		$sql->execute();
		$result = $sql->get_result();
		
		$my_arr[] = array
		(
			'user' => $user
		);
	}
	
	$json = json_encode($my_arr);
	echo($json);
		
	$conn->close();
	
?>