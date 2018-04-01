<?php

	$servername = "stupidcupid.mysql.database.azure.com";
	$username = "stupidcupid@stupidcupid";
	$password = "Cop4331poop!";
	$dbname = "androidlogin";

	$inData = file_get_contents('php://input');
	$unverified_email = $_POST['email'];
	$hashed_unver_pass = $_POST['password'];
	$fail = "error";

	// hashes the password using php's built-in crypt function
	//$hashed_unver_pass = crypt($unverified_pass, 'CRYPT_BLOWFISH');

	$conn = new mysqli($servername, $username, $password, $dbname);


	if ($conn->connect_error) 
	{
		returnWithError($fail);
	}
	else
	{
		// use prepared statements to defend against sql injection attacks
		$sql = $conn->prepare("SELECT userID, email, password, profileLocation, firstName, lastName, inChat FROM users WHERE email = ? and password = ?");
		$sql->bind_param("ss", $unverified_email, $hashed_unver_pass);
		$sql->execute();
		$result = $sql->get_result();

		if ($result->num_rows > 0)
		{
			$row = $result->fetch_assoc();
			$userID = $row["userID"];
			$verified_email = $row["email"];
			$verfied_password = $row["password"];
			$profile_location = $row["profileLocation"];
			$first_name = $row["firstName"];
			$last_name = $row["lastName"];
			
			# deals with the lack of a boolean type in mysql
			if($row["inChat"] == 0)
			{
				$inChat = False;
			}
			else
			{
				$inChat = True;
			}

			$my_arr[] = array(
						'userID' => $userID,
						'email' => $verified_email,
						'pass' => $verfied_password,
						'profileLocation' => $profile_location,
						'firstName' => $first_name,
						'lastName' => $last_name,
						'inChat' => $inChat,
						'error' => "None"
					);
			$json = json_encode($my_arr);

			echo($json);
		}
		else
		{
			returnWithError($fail);
		}
	}

	function sendResultInfoAsJson( $obj )
	{
		header('Content-type: application/json');
		echo $obj;
	}
	
	function returnWithError( $err )
	{
		$retValue = '{"userID": "", "email":"","pass":"", "profileLocation": "", "firstName": "", "lastName": "", "inChat": "", "error":"' . $err . '"}';
		sendResultInfoAsJson( $retValue );
	}

?>