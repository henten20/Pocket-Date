<?php
	
		// Grabs the indata from the post request sent fro the createAccount() function
		$inData = json_decode(file_get_contents('php://input'), true);
		
		// Ajax post data containing containing the passed in fields which are parsed into separate variables
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
		
		// Default profile location (for the profile pic) is set to an empty string. 
		// This is filled out when a user uploads a profile picture
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
		
	
		$sql = $conn->prepare("SELECT username FROM user WHERE username = ?");
		$sql->bind_param("s", $user);
		$sql->execute();
		$result = $sql->get_result();

		// Fetch the array that's returned from the results
		$row = $result->fetch_assoc();
		$contains_username = $row["username"];
			
		if($contains_username == $user)
		{
			echo "Username already exists";
			return;
		}
		
		// hashing the password and inserting into the db
		$hashed_pass = crypt($pass, 'CRYPT_BLOWFISH');
	
		// Use prepared statements to guard against sql injections
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
 			$my_arr[] = array
			(
 				'user_id' => $row["user_id"]
			);
	    }
		
		// Return the json object containing the array we created and close off all connections
		$json = json_encode($my_arr);
		echo($json);
		
		$sql->close();
		$conn->close();

?>