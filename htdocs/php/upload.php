<?php


$target_dir = "../img/";
$user_id = $_GET["user_id"];
$target_file = $target_dir . "profile_" . $user_id . ".jpg";
$uploadOk = 1;

// db deets
$servername = "localhost";
$username = "root";
$password = "";
$db = "pocketdate";

// please work
$conn = new mysqli($servername, $username, $password, $db);
$sql = $conn->prepare("UPDATE user SET profileLocation = ?");
$sql->bind_param("s", $target_file);
$sql->execute();
$result = $sql->get_result();

$imageFileType = strtolower(pathinfo($target_file,PATHINFO_EXTENSION));
// Check if image file is a actual image or fake image
if(isset($_POST["submit"])) {
    $check = getimagesize($_FILES["file-input"]["tmp_name"]);
    if($check !== false) {
        //echo "File is an image - " . $check["mime"] . ".";
        $uploadOk = 1;
    } else {
        //echo "File is not an image.";
        $uploadOk = 0;
    }
}

// Check file size
if ($_FILES["file-input"]["size"] > 500000) {
    //echo "Sorry, your file is too large.";
    $uploadOk = 0;
}
// Allow certain file formats
if($imageFileType != "jpg" && $imageFileType != "png" && $imageFileType != "jpeg"
&& $imageFileType != "gif" ) {
    //echo "Sorry, only JPG, JPEG, PNG & GIF files are allowed.";
    $uploadOk = 0;
}
// Check if $uploadOk is set to 0 by an error
if ($uploadOk == 0) {
    //echo "Sorry, your file was not uploaded.";
// if everything is ok, try to upload file
} else {
    if (move_uploaded_file($_FILES["file-input"]["tmp_name"], $target_file)) {
        //echo "The file ". basename( $_FILES["file-input"]["name"]). " has been uploaded.";
    } else {
        //echo "Sorry, there was an error uploading your file.";
    }
}

header("Location: http://localhost/profile.html");
exit;

?> 