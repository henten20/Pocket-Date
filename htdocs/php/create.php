<?php
	

		// ajax post data containing user and pass input
		$firstname = $_POST['firstname'];
		$lastname = $_POST['lastname'];
		$email = $_POST['email'];
		$user = $_POST['username'];
		$pass = $_POST['pass'];
		
		// db deets
		$servername = "localhost";
		$username = "root";
		$password = "";
		$db = "kouki";
	   
	    
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
			
			$sql = $conn->prepare("INSERT into users (username, password, first_name, last_name, email) VALUES (?, ?, ?, ?, ?)");
			$sql->bind_param("sssss", $user, $hashed_pass, $firstname, $lastname, $email);
			$sql->execute();
			
			// test to see if insertion was successful
			if($sql)
				echo "Verified";
		$sql->close();
		$conn->close();

	
	
?>