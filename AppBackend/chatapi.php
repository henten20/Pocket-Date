<?php

	# php file wasn't displaying errors properly and this was the required fix
	ini_set('display_errors', 1);

	$servername = "stupidcupid.mysql.database.azure.com";
	$username = "stupidcupid@stupidcupid";
	$password = "Cop4331poop!";
	$dbname = "androidlogin";

	$conn = new mysqli($servername, $username, $password, $dbname);

	if ($conn->connect_error) 
	{
		returnWithError($fail);
	}
	else if(isset($_POST['action']) && !empty($_POST['action'])) {

		$action = $_POST['action'];
		
		switch($action) {
			case 'load' : updateMessages(False);break;
			case 'send' : updateMessages(True);break;
			case 'profile' : getMatchID();break;
		}
	}
	

	# takes in a connection stream and a chat id and will return all messages from this chat in order, according to the timestamp
	function grabMessages($chatID)
	{
		$conn = $GLOBALS["conn"];
		$sql = $conn->prepare("SELECT senderID, messageContents, timeStamp FROM messages WHERE chatID = ? ORDER BY timestamp ASC");
		$sql->bind_param("s", $chatID);
		$sql->execute();
		$result = $sql->get_result();	

		while ($row = $result->fetch_assoc())
		{
    		$my_arr[] = array(
					'senderID' => $row['senderID'],
					'messageContents' => $row['messageContents'],
					'timeStamp' => $row['timeStamp']
				);
		}
		# encapsulates our messages into a json object and echos that back to the app
		echo(json_encode($my_arr));
		$conn->close();	
	}

	function getMatchID()
	{
		$userID = $_POST["userID"];
		$conn = $GLOBALS["conn"];

		$sql = $conn->prepare("SELECT chatID, user_one, user_two FROM chat WHERE user_one = ? or user_two = ?");
		$sql->bind_param("ss", $userID, $userID);
		$sql->execute();
		$result = $sql->get_result();	

		while ($row = $result->fetch_assoc())
		{
			if($userID == $row['user_one'])
			{
				grabProfile($row['user_two']);
			}
			else
			{
				grabProfile($row['user_one']);
			}
		}		
	}

	function grabProfile($matchID)
	{
		$conn = $GLOBALS["conn"];

		$sql = $conn->prepare("SELECT profileLocation, firstName, lastName FROM users WHERE userID = ?");
		$sql->bind_param("s", $matchID);
		$sql->execute();
		$result = $sql->get_result();	

		while ($row = $result->fetch_assoc())
		{
			$my_arr[] = array(
					'profileLocation' => $row['profileLocation'],
					'firstName' => $row['firstName'],
					'lastName' => $row['lastName']
				);
		}
		echo(json_encode($my_arr));		
	}

	# sample send message function
	# INPUT - String containing message
	# RETURN - JSON encoded set of updated messages, including the most recently sent one
	function sendMessage($chatID, $userID)
	{
		$conn = $GLOBALS["conn"];
		$message = $_POST["messageContents"];

		$sql = $conn->prepare("INSERT into messages (chatID, senderID, messageContents) VALUES (?, ?, ?)");
		$sql->bind_param("sss", $chatID, $userID, $message);
		$sql->execute();
		echo("did it blend?");
		//grabMessages($chatID);
	}

	function updateMessages($sendingMessage)
	{
		$userID = $_POST["userID"];
		$conn = $GLOBALS["conn"];

		$sql = $conn->prepare("SELECT chatID, user_one, user_two FROM chat WHERE user_one = ? or user_two = ?");
		$sql->bind_param("ss", $userID, $userID);
		$sql->execute();
		$result = $sql->get_result();	

		while ($row = $result->fetch_assoc())
		{
			if($sendingMessage == False)
			{
				grabMessages($row['chatID']);
			}
			else
			{
				sendMessage($row['chatID'], $userID);
			}
			// will be used for updating new messages -- need some kind of conditional statement to differentiate between modes
    		//sendMessage($row['chatID'], $userID, $conn);
		}		
	}

	# given a user's zip code, we use the external API, ZipCodeAPI to grab all zip codes within a 100 mile radius.
	function grabZips()
	{
		$xml = file_get_contents("https://www.zipcodeapi.com/rest/kH3Mgcqs0a33oWGZJ58mnsYP5zNVBZ1AdC95KeshCfOQC3aoZBS9g78HlldPcpMm/radius.json/32311/100/mile");
	
		// currently commented out, but we could utilize an R script to extract this data for us instead of vanilla php
		//echo(exec("Rscript test.r", $xml));

		$jsonArray = json_decode($xml, True);
		echo($xml);
		$myArray = $jsonArray['zip_codes'];
		$zipArray = array();
		$zipIndex = 0;

		// loops through the entire json array and extracts the zipcode.
		foreach($myArray as $key => $values)
		{
			$zipArray[$zipIndex] = $myArray[$key]['zip_code'];
			$zipIndex++;
		}

		$zipJson = json_encode($zipArray);

		// will return a json file with zip codes to the android application
		#echo($zipJson);
	}

	// TODO - check for preferences and also whether users have matched before
	function grabUnmatchedUsers( $currentUser, $conn )
	{
		// grabs list of users who are not currently in a chat with anyone
		$boolParam = False;

		// use prepared statements to defend against sql injection attacks
		$sql = $conn->prepare("SELECT userID, email, password FROM users WHERE inChat = ? AND userID != ?");
		$sql->bind_param("ss", $boolParam, $currentUser);
		$sql->execute();
		$result = $sql->get_result();

		// calls the function that will prepare the match
		makeMatch($conn, $result, $currentUser);
		
	}

	// TODO - add location checking
	function makeMatch( $conn, $potentialMatches, $currentUser)
	{
		$listSutors = array();
		$index = 0;

		// adds all of the available users to an array for easy access
		while ($row = $potentialMatches->fetch_assoc())
		{
    		$listSutors[$index] = $row;
    		$index++;
		}

		#echo($index);
		// generates a random index that will be used to grab a member from our sutorList
		$matchIndex = rand(0, $index-1);

		// your lovely new match
		newMatchUpdate($conn, $listSutors[$matchIndex], $currentUser);
	}

	function newMatchUpdate($conn, $userOne, $userTwo)
	{
		$userOne = $userOne['userID'];
		$newStatus = True;

		$updateOne = $conn->prepare("UPDATE users SET inChat = ? WHERE userID = ?");
		$updateOne->bind_param("ss", $newStatus, $userOne);
		$updateOne->execute();

		$updateTwo = $conn->prepare("UPDATE users SET inChat = ? WHERE userID = ?");
		$updateTwo->bind_param("ss", $newStatus, $userTwo);
		$updateTwo->execute();

		$sql = $conn->prepare("INSERT into chat (user_one, user_two) VALUES (?, ?)");
		$sql->bind_param("ss", $userOne, $userTwo);
		$sql->execute();

		echo("finished");
	}

?>
