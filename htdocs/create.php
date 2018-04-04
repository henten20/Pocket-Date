<?php
	
		// grabs the indata from the post request
		$inData = json_decode(file_get_contents('php://input'), true);
		// ajax post data containing containing the passed in fields
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
		$sql = $conn->prepare("SELECT username, pass FROM users WHERE username = ?");
		$sql->bind_param("s", $user);
		$sql->execute();
		$result = $sql->get_result();
		
		if(!$result)
			echo "Error";		
		
		$row = $result->fetch_assoc();
		$contains_username = $row["username"];
			
		if($contains_username == $user)
			echo "Username already exists";
			
		// hashing the password and inserting into the db
		$hashed_pass = crypt($pass, 'CRYPT_BLOWFISH');
			
		$sql = $conn->prepare("INSERT into users (username, pass, phone, email, firstname, lastname, age, zipcode, gender, preference, about) VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?)");
		$sql->bind_param("sssssssssss", $user, $hashed_pass, $phone, $email, $firstname, $lastname, 21, $zipcode, $gender, $preference, $about);
		$sql->execute();
			
			// test to see if insertion was successful
			//if($sql)
				//echo "Verified";.
			
			
		// example of returning json back to js
 		$my_arr[] = array(
						'gender' => $gender,
						'preferencee' => $preference
					);
		$json = json_encode($my_arr);
		echo($json);
		
		$sql->close();
		$conn->close();
	
	
	
?>