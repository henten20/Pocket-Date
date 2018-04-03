<?php

		// ajax post data containing user and pass input
		$user = $_POST['username'];
		$pass = $_POST['pass'];
		$phone = $_POST['phone'];
		$email = $_POST['email'];
		$firstname = $_POST['firstname'];
		$lastname = $_POST['lastname'];
		$birthdate = $_POST['birthdate'];
		$zipcode = $_POST['zipcode'];
		$gender = $_POST['selectGen'];
		$preference = $_POST['selectPref'];
		$about = $_POST['about'];
		
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
		$sql = $conn->prepare("SELECT username, password FROM users WHERE username = ?");
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
			
			$sql = $conn->prepare("INSERT into users (username, password, phone, email, firstname, lastname, age, zipcode, gender, preference, about) VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?)");
			$sql->bind_param("sssss", $user, $hashed_pass, $firstname, $lastname, $email, $firstname, $lastname, $age, $zipcode, $gender, $preference, $zipcode, $about);
			$sql->execute();
			
			// test to see if insertion was successful
			if($sql && $sql2)
				echo "Verified";
			else 
				echo "error";
			
		$sql->close();
		$conn->close();

	
	
?>