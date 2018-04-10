<?php

    // db deets
    $servername = "localhost";
    $username = "root";
    $password = "";
    $db = "pocketdate";

    // decodes the json object that is sent over from the js file
    $inData = json_decode(file_get_contents('php://input'), true);
       
    $conn = new mysqli($servername, $username, $password, $db);
    $user_id = $inData['user_id'];

    // use prepared statements to defend against sql injection attacks
    $sql = $conn->prepare("SELECT profileLocation, username, email, firstname, lastname, birthdate, zipcode, gender, preference, about FROM user WHERE user_id = ?");
    $sql->bind_param("s", $user_id);
    $sql->execute();
    $result = $sql->get_result();
       
    while ($row = $result->fetch_assoc())
    {
        $my_arr[] = array(
            'profileLocation' => $row['profileLocation'],
            'username' => $row['username'],
            'email' => $row['email'],
            'firstname' => $row['firstname'],
            'lastname' => $row['lastname'],
            'birthdate' => $row['birthdate'],
            'zipcode' => $row['zipcode'],
            'gender' => $row['gender'],
            'preference' => $row['preference'],
            'about' => $row['about']);

    }

    // checks to see if no results were returned
    if(mysqli_num_rows($result) == 0)
    {
        // use prepared statements to defend against sql injection attacks
        $sql = $conn->prepare("SELECT username, email, firstname, lastname, birthdate, zipcode, gender, preference, about FROM user WHERE user_id = ?");
        $sql->bind_param("s", $user_id);
        $sql->execute();
        $result = $sql->get_result();

        while ($row = $result->fetch_assoc())
        {
            $my_arr[] = array(
                'username' => $row['username'],
                'email' => $row['email'],
                'firstname' => $row['firstname'],
                'lastname' => $row['lastname'],
                'birthdate' => $row['birthdate'],
                'zipcode' => $row['zipcode'],
                'gender' => $row['gender'],
                'preference' => $row['preference'],
                'about' => $row['about']);
        }
    }
    // encodes the array in a json readable format
    echo(json_encode($my_arr));


?> 