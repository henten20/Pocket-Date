<?php
	$inData = getRequestInfo();
	
	$id = 0;
	$firstName = "";
	$lastName = "";
	$working = "None";
	$servername = "localhost";
	$username = "root";
	$password = "";
	$db = "pocketdate";
	
	// hashes the password using php's built-in crypt function
	$hashed_unver_pass = crypt($inData["password"], 'CRYPT_BLOWFISH');
	$conn = new mysqli($servername, $username, $password, $db);
	if ($conn->connect_error) 
	{
		returnWithError( $conn->connect_error );
	} 
	else
	{
		// use prepared statements to defend against sql injection attacks
		$sql = $conn->prepare("SELECT username, pass, user_id FROM user where username = ? and pass = ?");
		$sql->bind_param("ss", $inData["username"], $hashed_unver_pass);
		$sql->execute();
		$result = $sql->get_result();
		if ($result->num_rows > 0)
		{
			$row = $result->fetch_assoc();
			$user= $row["username"];
			$pass = $row["pass"];
			$user_id = $row["user_id"];
			returnWithInfo($user, $pass, $user_id);
		}
		else
		{
			returnWithError( "No Records Found" );
		}
		$conn->close();
	}
	
	function getRequestInfo()
	{
		return json_decode(file_get_contents('php://input'), true);
	}
	function sendResultInfoAsJson( $obj )
	{
		header('Content-type: application/json');
		echo $obj;
	}
	
	function returnWithError( $err )
	{
		$retValue = '{"user":"","pass":"", "user_id":"", error":"' . $err . '"}';
		sendResultInfoAsJson( $retValue );
	}
	
	function returnWithInfo( $user, $pass, $user_id)
	{
		$my_arr[] = array(
					'user' => $user,
					'pass' => $pass,
					'user_id' => $user_id,
					'error' => "None"
				);
		$json = json_encode($my_arr);
		sendResultInfoAsJson( $json );
	}
	
?>