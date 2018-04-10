<?php
	
		// grabs the indata from the post request
		$inData = json_decode(file_get_contents('php://input'), true);
		// ajax post data containing containing the passed in fields
		$user_id = 0;
		$user = $inData['username'];
		$firstname = $inData['firstname'];
		$lastname = $inData['lastname'];
		$email = $inData['email'];
		$phone = $inData['phone'];
		$pass = $inData['pass'];
		$zipcode = $inData['zipcode'];
		$birthdate = $inData['birthdate'];
		$about = $inData['about'];
		$gender = $inData['gender'];
		$preference = $inData['preference'];
		$profileLocation = "empty";
		
		// db deets
		$servername = "localhost";
		$username = "root";
		$password = "";
		$db = "pocketdate";
	   
		// Establishing the connection
		$conn = mysqli_connect($servername, $username, $password, $db); 
		
		 // terminates if the connection fails
		if(!$conn)
			die('Error, could not connect:');
		
		// using prepared statements to guard against sql injection attacks
		try
		{
			$sql = $conn->prepare("SELECT username FROM user WHERE username = ?");
			$sql->bind_param("s", $user);
			$sql->execute();
			$result = $sql->get_result();
		}
		catch(Exception $e)
		{
			//if(!$result)
				//echo "Error";		
			// If a user already exists, we want to return a json object saying that this "user" is taken
			$my_arr[] = array(
						'user' => $user
					);
			$json = json_encode($my_arr);
			echo($json);
		}
		
		$row = $result->fetch_assoc();
		$contains_username = $row["username"];
			
		if($contains_username == $user)
			echo "Username already exists";
			
		// hashing the password and inserting into the db
		$hashed_pass = crypt($pass, 'CRYPT_BLOWFISH');
		
		// Before inserting into the database, we will need to calculate the users age
		// via the birthdate that they provided. Currently using a placeholder
		
		$sql = $conn->prepare("INSERT into user (user_id, username, pass, phone, email, firstname, lastname, profileLocation, birthdate, zipcode, gender, preference, about) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?)");
		$sql->bind_param("sssssssssssss", $user_id, $user, $hashed_pass, $phone, $email, $firstname, $lastname, $profileLocation, $birthdate, $zipcode, $gender, $preference, $about);
		$sql->execute();
			
		$sql = $conn->prepare("SELECT user_id from user where username = ?");
		$sql->bind_param("s", $user);
		$sql->execute();
		$result = $sql->get_result();

		while ($row = $result->fetch_assoc())
	    {
	    	// example of returning json back to js
 			$my_arr[] = array(
 						'user_id' => $row["user_id"],
						'user' => $user,
						'pass' => $pass
					);
	    }
		
		$json = json_encode($my_arr);
		echo($json);
		
		$sql->close();
		$conn->close();

	
	
?>