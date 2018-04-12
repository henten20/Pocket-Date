//var urlBase = 'http://cop4331groupeight.com';
var urlBase = '/php';
var extension = "php";

var userId = 0;
var firstName = "";
var lastName = "";

// Function called when loading the profile page
function profileLoad()
{
    alert("Trying to load");
    user_id = document.cookie;

    // Updates the form submit of the profile update input so that the php file will have access to the user's id
    var frm = document.getElementById('form-submit');
    frm.action = "/php/upload.php/?user_id=" + user_id;

    // json string that will contain the user's id
    var jsonPayload = '{"user_id" : "' + user_id + '"}';
    var url = urlBase + '/profileupdate.' + extension;
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function()
    {
        if (this.readyState == 4)
        {
            if (this.status == 200)
            {
                var jsonObject = JSON.parse(xhr.responseText);

                if (jsonObject["error"] != null)
                {
                    alert("Couldn't grab profile image.");
                    return;
                }

                // Checks to see if the profileLocation field is there or not
                if (jsonObject[0]["profileLocation"] != null)
                {
                    profileLocation = jsonObject[0]["profileLocation"];

                    // Makes sure that the user has selected a profile image
                    if (profileLocation != "empty")
                    {
                        document.getElementById("profile_img").src = profileLocation;
                    }
                }

                var username = jsonObject[0]["username"];
                var firstname = jsonObject[0]["firstname"];
                var lastname = jsonObject[0]["lastname"];
                var birthdate = jsonObject[0]["birthdate"];
                var age = getAge(birthdate);
                var email = jsonObject[0]["email"];
                var preference = jsonObject[0]["preference"];
                var zipcode = jsonObject[0]["zipcode"];
                var about = jsonObject[0]["about"];

                // Updates the source of the profile image	
                document.getElementById("username").innerHTML = username;
                document.getElementById("firstname").innerHTML = "<p id = \"firstname\">" + firstname + "<span type=\"button\" id=\"setFirst\" onclick = \"editFirst()\" class=\"glyphicon glyphicon-pencil\"></span>"
                document.getElementById("lastname").innerHTML = "<p id = \"lastname\">" + lastname + "<span type=\"button\" id=\"setLast\" onclick = \"editLast()\" class=\"glyphicon glyphicon-pencil\"></span>"
                document.getElementById("age").innerHTML = "<p id=\"age\">" + age + "</p>"
                document.getElementById("email").innerHTML = "<p id = \"email\">" + email + "<span type=\"button\" id=\"setEmail\" onclick = \"editEmail()\" class=\"glyphicon glyphicon-pencil\"></span>"
                document.getElementById("preference").innerHTML = "<p id = \"preference\">" + preference + "<span type=\"button\" id=\"setPreference\" onclick = \"editPreference()\" class=\"glyphicon glyphicon-pencil\"></span>"
                document.getElementById("zipcode").innerHTML = "<p id = \"zipcode\">" + zipcode + "<span type=\"button\" id=\"setZip\" onclick = \"editZip()\" class=\"glyphicon glyphicon-pencil\"></span>"
                document.getElementById("about").innerHTML = "<p id = \"about\">" + about + "<span type=\"button\" id=\"setAbout\" onclick = \"editAbout()\" class=\"glyphicon glyphicon-pencil\"></span>"
            }
        }
    };

    xhr.open("POST", url, true);
    xhr.send(jsonPayload);
}

// Uploads the profile image once a file is selected.
function uploadFile()
{
    document.getElementById("form-submit").submit();
}

// Signs the user out and drects them to the index page
function signOut()
{
    window.location.href = "./index.html";
}

// Process the login function when a user enters a username and password
function processLogin()
{
    var username = document.getElementById("inputUser").value;
    var pass = document.getElementById("inputPassword").value;

    // Regex that will define the set character values that will be accepted from the textfields
    var specReg = /[^A-Za-z0-9]/;

    // Checks for invalid characters in login attempt using the specified regex
    if (specReg.test(username) || specReg.test(pass))
    {
        alert("Invalid characters found. Please try again.");
        return;
    }

    // Create a json payload using the username and password
    var jsonPayload = '{"username" : "' + username + '", "password" : "' + pass + '"}';
    var url = urlBase + '/auth.' + extension;
    var xhr = new XMLHttpRequest();

    // Since logging in will direct the user to another page, we will not need to worry about using an asynchronous call 
    xhr.open("POST", url, false);
    xhr.setRequestHeader("Content-type", "application/json; charset=UTF-8");

    try
    {
        xhr.send(jsonPayload);
        var jsonObject = JSON.parse(xhr.responseText);

        if (jsonObject["error"] != null)
        {
            alert("User/Password combination incorrect");
            return;
        }

        // Save the user's ID as a cookie and redirect to the profile page
        user = jsonObject[0]["user_id"];
        document.cookie = user;
        window.location.href = "/profile.html";
    }
    catch (err)
    {
        alert("Invalid login credentials");
    }
}

// checks for valid email using regex
function validateEmail(email)
{
    var re = /\S+@\S+\.\S+/;
    return re.test(email);
}

function getAge(birthDay)
{
    var DOB = new Date(birthDay);
    var today = new Date();
    var age = today.getTime() - DOB.getTime();
    age = Math.floor(age / (1000 * 60 * 60 * 24 * 365.25));
    return age;
}

// handles the creating of a new account -------------------------------------------------------------------------------------------------------
function createAccount()
{
    var username = document.getElementById("username");
    var firstname = document.getElementById("firstname");
    var lastname = document.getElementById("lastname");
    var email = document.getElementById("email");
    var phone = document.getElementById("phone");
    var pass = document.getElementById("password");
    var confirmpass = document.getElementById("confirmpass");
    var zipcode = document.getElementById("zipcode");
    var birthdate = document.getElementById("birthdate");
    var age = getAge(birthdate.value);
    var about = document.getElementById("about");
    var gender = document.getElementsByName("gender");
    var preference = document.getElementsByName("preference");
    var selectGen;
    var selectPref;

    // Regex
    var specReg = /[^A-Za-z0-9 ]/;
    var specRegChar = /[^A-Za-z ]/;
    var specRegNum = /[^0-9]/;

    // Boolean values used to verify form input
    var validate = true;
    var filled = true;

    if (age < 18)
    {
        alert("Required age is 18 years or older");
        return;
    }

    // Run though every field to make sure it's not empty. Invalid entries will be labeled in red
    if (username.value == "")
    {
        filled = false;
        document.getElementById("usernameLabel").style.color = "red";
    }
    else
    {
        document.getElementById("usernameLabel").style.color = "black";
    }
    if (pass.value == "")
    {
        filled = false;
        document.getElementById("passwordLabel").style.color = "red";
    }
    else
    {
        document.getElementById("passwordLabel").style.color = "black";
    }
    if (confirmpass.value == "")
    {
        filled = false;
        document.getElementById("confirmLabel").style.color = "red";
    }
    else
    {
        document.getElementById("confirmLabel").style.color = "black";
    }
    if (phone.value == "")
    {
        filled = false;
        document.getElementById("phoneLabel").style.color = "red";
    }
    else
    {
        document.getElementById("phoneLabel").style.color = "black";
    }
    if (email.value == "")
    {
        filled = false;
        document.getElementById("emailLabel").style.color = "red";
    }
    else
    {
        document.getElementById("emailLabel").style.color = "black";
    }
    if (firstname.value == "")
    {
        filled = false;
        document.getElementById("firstnameLabel").style.color = "red";
    }
    else
    {
        document.getElementById("firstnameLabel").style.color = "black";
    }
    if (lastname.value == "")
    {
        filled = false;
        document.getElementById("lastnameLabel").style.color = "red";
    }
    else
    {
        document.getElementById("lastnameLabel").style.color = "black";
    }
    if (birthdate.value == "")
    {
        filled = false;
        document.getElementById("birthdateLabel").style.color = "red";
    }
    else
    {
        document.getElementById("birthdateLabel").style.color = "black";
    }
    if (zipcode.value == "")
    {
        filled = false;
        document.getElementById("zipcodeLabel").style.color = "red";
    }
    else
    {
        document.getElementById("zipcodeLabel").style.color = "black";
    }

    if (filled == false)
    {
        alert("Please fill in all required fields before submitting.");
		return;
    }

    // At this point all fields are checked and not empty
    // Now we check for correct character and numeric inputs. Invalid entries will be labeled in red
    if (specReg.test(username.value))
    {
        alert("Incorrect format for username");
        document.getElementById("usernameLabel").style.color = "red";
        validate = false;
    }
    else
    {
        document.getElementById("usernameLabel").style.color = "black";
    }
    if (specRegChar.test(firstname.value))
    {
        alert("Invalid first name");
        document.getElementById("firstnameLabel").style.color = "red";
        validate = false;
    }
    else
    {
        document.getElementById("firstnameLabel").style.color = "black";
    }
    if (specRegChar.test(lastname.value))
    {
        alert("Invalid last name");
        document.getElementById("lastnameLabel").style.color = "red";
        validate = false;
    }
    else
    {
        document.getElementById("lastnameLabel").style.color = "black";
    }
    if (specRegNum.test(phone.value))
    {
        alert("Only numeric values allowed for phone numbers");
        document.getElementById("phoneLabel").style.color = "red";
        validate = false;
    }
    else
    {
        document.getElementById("phoneLabel").style.color = "black";
    }
    if (specRegNum.test(zipcode.value))
    {
        alert("Zipcodes can only have number values");
        document.getElementById("zipcodeLabel").style.color = "red";
        validate = false;
    }
    else
    {
        document.getElementById("zipcodeLabel").style.color = "black";
    }
    if (!validateEmail(email.value))
    {
        alert("Invalid Email Address.");
        document.getElementById("emailLabel").style.color = "red";
        validate = false;
    }
    else
    {
        document.getElementById("emailLabel").style.color = "black";
    }
    if ((pass.value != confirmpass.value))
    {
        alert("Password fields do not match. Please try again.");
        document.getElementById("confirmLabel").style.color = "red";
        validate = false;
    }
    else
    {
        document.getElementById("confirmLabel").style.color = "black";
    }

    // Gender and preference OPTIONAL 
    // DEFAULT GENDER AND PREFERENCE
    selectGen = "other";
    selectPref = "both";

    for (var j = 0; j < gender.length; j++)
    {
        if (gender[j].checked)
        {
            selectGen = gender[j].value;
            break;
        }
    }

    for (var q = 0; q < preference.length; q++)
    {
        if (preference[q].checked)
        {
            selectPref = preference[q].value;
            break;
        }
    }

	if(validate == false)
		return; 
	
	if(validate == true && filled == true)
	{
		// jsonPayload is the JSON string that we are sending to the php. Always double-check the syntax of this statement, 
		// because the php won't be able to read it if it's incorrect.
		var jsonPayload = '{"username" : "' + username.value + '", "firstname" : "' + firstname.value + '", "lastname" : "' + lastname.value + '", "email" : "' + email.value + '", "phone" : "' + phone.value + '", "pass" : "' + pass.value +
			'", "zipcode" : "' + zipcode.value + '", "birthdate" : "' + birthdate.value + '", "about" : "' + about.value + '", "gender" : "' + selectGen + '", "preference" : "' + selectPref + '"}';

		var url = urlBase + '/create.php';
		var xhr = new XMLHttpRequest();
		xhr.open("POST", url, false);
		xhr.setRequestHeader("Content-type", "application/json; charset=UTF-8");

		try
		{
			// Sends the JSON string over to the php script
			xhr.send(jsonPayload);

			// IF ANYTHING IS RETURNED OTHER THAN PROPER JSON, AN ERROR WILL BE THROWN!!! This is why we have the alert statement above ^^^
			// returns a JSON string from the php script and converts it to a JSON object for easy access
			var jsonObject = JSON.parse(xhr.responseText);

			// Here's an example of how you would access a "user" string returned from the JSON. The "user" field is a key in the JSON object.
			// if you try to access an element that isn't in the json object, this statement will fail.
			// user = jsonObject[0]["user"];

			// Redirects the window to the home.html page
			var user_id = jsonObject[0]["user_id"];
			document.cookie = user_id;
			window.location.href = "/profile.html";
		}
		catch (err)
		{
			alert(xhr.responseText);
		}
	}
}