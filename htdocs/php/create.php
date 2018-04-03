<?php

	
		// grabs the indata from the post request
		$inData = json_decode(file_get_contents('php://input'), true);

		// ajax post data containing user and pass input
		$firstname = $inData['firstname'];
		$lastname = $inData['lastname'];
		$user = $inData['username'];
		$pass = $inData['pass'];
		
		// db deets
		$servername = "localhost";
		$username = "root";
		$password = "";
		$db = "pocketdate";
	   

 		// example of returning json back to js
 		$my_arr[] = array(
						'firstname' => $firstname,
						'lastname' => $lastname
					);
		$json = json_encode($my_arr);

		echo($json);

		// Establishing the connection
		/*$conn = mysqli_connect($servername, $username, $password, $db); 
		
		 // terminates if the connection fails
		if(!$conn)
			die('Error, could not connect:');
		// using prepared statements to guard against sql injection attacks
		$sql = $conn->prepare("SELECT username, password FROM users WHERE username = ?");
		$sql->bind_param("s", $user);
		$sql->execute();
		$result = $sql->get_result();
		
		if(!$result)
			//echo "Error";		
			$row = $result->fetch_assoc();
			$contains_username = $row["username"];
			
			if($contains_username == $user)
				echo "Username already exists";
			
			// hashing the password and inserting into the db
			$hashed_pass = crypt($pass, 'CRYPT_BLOWFISH');
			
			$sql = $conn->prepare("INSERT into users (username, password, phone, email, firstname, lastname, age, zipcode, gender, preference, about) VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?)");
			$sql->bind_param("sssss", $user, $hashed_pass, $firstname, $lastname, $email, $firstname, $lastname, $age, $zipcode, $gender, $preference, $zipcode, $about);
			$sql->execute();
			
			// test to see if insertion was successful
			//if($sql)
				//echo "Verified";

		$sql->close();
		$conn->close();
		*/
	
	
?>