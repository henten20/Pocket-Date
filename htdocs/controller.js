//var urlBase = 'http://cop4331groupeight.com';
var urlBase = '/php';
var extension = "php";

var userId = 0;
var firstName = "";
var lastName = "";


// when the document initially opens
function profileLoad() 
{
	alert("Trying to load");
    user_id = document.cookie;

    // updates the form submit of the profile update input so that the php file will have access to the user's id
    var frm = document.getElementById('form-submit');
    frm.action = "/php/upload.php/?user_id=" + user_id;

    // json string that will contain the user's id
    var jsonPayload = '{"user_id" : "' + user_id + '"}';
    var url = urlBase + '/profileupdate.' + extension;
    var xhr = new XMLHttpRequest();
    xhr.open("POST", url, false);
    xhr.setRequestHeader("Content-type", "application/json; charset=UTF-8");

    try
    {
        xhr.send(jsonPayload);

        var jsonObject = JSON.parse( xhr.responseText );

        if(jsonObject["error"] != null)
        {
            alert("Couldn't grab profile image.");
            return;
        }

        // checks to see if the profileLocation field is there or not
        if(jsonObject[0]["profileLocation"] != null)
        {
        	profileLocation = jsonObject[0]["profileLocation"];
        	
        	// makes sure that the user has selected a profile image
        	if(profileLocation != "empty")
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
      
        // updates the source of the profile image	
        document.getElementById("username").innerHTML = username;
        document.getElementById("firstname").innerHTML = "<p id = \"firstname\">" + firstname + "<span type=\"button\" id=\"setFirst\" onclick = \"editFirst()\" class=\"glyphicon glyphicon-pencil\"></span>"
        document.getElementById("lastname").innerHTML = "<p id = \"lastname\">" + lastname + "<span type=\"button\" id=\"setLast\" onclick = \"editLast()\" class=\"glyphicon glyphicon-pencil\"></span>"
        document.getElementById("age").innerHTML = "<p id=\"age\">" + age + "</p>"
        document.getElementById("email").innerHTML = "<p id = \"email\">" + email + "<span type=\"button\" id=\"setEmail\" onclick = \"editEmail()\" class=\"glyphicon glyphicon-pencil\"></span>"
        document.getElementById("preference").innerHTML = "<p id = \"preference\">" + preference + "<span type=\"button\" id=\"setPreference\" onclick = \"editPreference()\" class=\"glyphicon glyphicon-pencil\"></span>"
        document.getElementById("zipcode").innerHTML = "<p id = \"zipcode\">" + zipcode + "<span type=\"button\" id=\"setZip\" onclick = \"editZip()\" class=\"glyphicon glyphicon-pencil\"></span>"
        document.getElementById("about").innerHTML = "<p id = \"about\">" + about + "<span type=\"button\" id=\"setAbout\" onclick = \"editAbout()\" class=\"glyphicon glyphicon-pencil\"></span>"
        
    }
    catch(err)
    {
        alert(xhr.responseText);
    }  
}

// uploads the profile image once a file is selected.
function uploadFile()
{
    document.getElementById("form-submit").submit();
}


function signOut(){
    window.location.href = "./index.html";
}

function processLogin()
{
    userId = 0;
    firstName = "";
    lastName = "";
    
    //alert("Attempting to login");
    
    var username = document.getElementById("inputUser").value;
    var pass = document.getElementById("inputPassword").value;
	
	//alert(username);
   
    var specReg = /[^A-Za-z0-9]/;

    // checks for invalid characters in login attempt
    if(specReg.test(username) || specReg.test(pass))
    {
        alert("Invalid characters found. Please try again.");
        return;
    }

    var jsonPayload = '{"username" : "' + username + '", "password" : "' + pass + '"}';
    var url = urlBase + '/auth.' + extension;
    var xhr = new XMLHttpRequest();
    xhr.open("POST", url, false);
    xhr.setRequestHeader("Content-type", "application/json; charset=UTF-8");

    try
    {
        xhr.send(jsonPayload);
		alert(xhr.responseText);
        var jsonObject = JSON.parse( xhr.responseText );

        if(jsonObject["error"] != null)
        {
            alert("User/Password combination incorrect");
            return;
        }
        user = jsonObject[0]["user_id"];
        document.cookie = user;
        window.location.href = "/profile.html";
    }
    catch(err)
    {
		alert(xhr.responseText);
        alert(err.message);
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
		//var birthDay = document.getElementById("birthdate").value;
        var DOB = new Date(birthDay);
        var today = new Date();
        var age = today.getTime() - DOB.getTime();
        age = Math.floor(age / (1000 * 60 * 60 * 24 * 365.25));
        return age;
}

// handles the creating of a new account -------------------------------------------------------------------------------------------------------
function createAccount() {
    
    var username = document.getElementById("username");
    var firstname = document.getElementById("firstname");
    var lastname = document.getElementById("lastname");
    var email = document.getElementById("email");
    var phone = document.getElementById("phone");
    var pass = document.getElementById("password");
    var confirmpass = document.getElementById("confirmpass");
    var zipcode = document.getElementById("zipcode");
	//var age = getAge();

	var birthdate = document.getElementById("birthdate");

	var about = document.getElementById("about");
    //var fieldArray = [username, pass, confirmpass, phone, email, firstname, lastname, age, zipcode, about];
    var validate = true;
	var filled = true;
    var gender = document.getElementsByName("gender");
    var preference = document.getElementsByName("preference");
    var specReg = /[^A-Za-z0-9 ]/;
	
	var specRegChar = /[^A-Za-z ]/;
	var specRegNum = /[^0-9]/;
	
    var selectGen, selectPref;
	
	if(age < 18)
	{		
		alert("Required age is 18 years or older");
		return;
	}
	
	/*
    // for loop will iterate through all input fields and check to make sure that they are filled out
    for(var i = 0; i < 10; i++)
	{
		//if(i == 4 || i == 7) continue;
		
        var curVal = fieldArray[i].value;
	

		// checks to ensure that the fields are populated
		if(curVal.length <= 0){
			alert("Please fill out all fields before submitting.");
			validate = false;
			break;
		}

		
		// checking for empty inputs
		if(curVal == "")
		{
			alert("Please fill out all fields before submitting.");
			validate == false;
			return;
		}
	
		

		else if(specReg.test(fieldArray[i].value))
		{
			alert("Invalid character(s) found. Please try again.");
			validate = false;
		}
	
    }
	*/
	
	
	// Run though every field to make sure it's not empty. Invalid entries will be labeled in red
	if(username.value == "")
	{
		filled = false;
		document.getElementById("usernameLabel").style.color = "red";
	}
	else
	{
		document.getElementById("usernameLabel").style.color = "black";
	}
	if(pass.value == "")
	{
		filled = false;
		document.getElementById("passwordLabel").style.color = "red";
	}
	else
	{
		document.getElementById("passwordLabel").style.color = "black";
	}
	if(confirmpass.value == "")
	{
		filled = false;
		document.getElementById("confirmLabel").style.color = "red";
	}
	else
	{
		document.getElementById("confirmLabel").style.color = "black";
	}
	if(phone.value == "")
	{
		filled = false;
		document.getElementById("phoneLabel").style.color = "red";
	}
	else
	{
		document.getElementById("phoneLabel").style.color = "black";
	}
	if(email.value == "")
	{
		filled = false;
		document.getElementById("emailLabel").style.color = "red";
	}
	else 
	{
		document.getElementById("emailLabel").style.color = "black";
	}
	if(firstname.value == "")
	{
		filled = false;
		document.getElementById("firstnameLabel").style.color = "red";
	}
	else
	{
		document.getElementById("firstnameLabel").style.color = "black";
	}
	if(lastname.value == "")
	{
		filled = false;
		document.getElementById("lastnameLabel").style.color = "red";
	}
	else
	{
		document.getElementById("lastnameLabel").style.color = "black";
	}
	if(birthdate.value == "")
	{
		filled = false;
		document.getElementById("birthdateLabel").style.color = "red";
	}
	else
	{
		document.getElementById("birthdateLabel").style.color = "black";
	}
	if(zipcode.value == "")
	{
		filled = false;
		document.getElementById("zipcodeLabel").style.color = "red";
	}
	else
	{
		document.getElementById("zipcodeLabel").style.color = "black";
	}
	
	
	if(filled == false)
	{
		alert("Please fill in all required fields before submitting.");
		return;
	}
	
	
	// At this point all fields are checked and not empty
	// Now we check for correct character and numeric inputs. Invalid entries will be labeled in red
	if(specReg.test(username.value))
	{
		alert("Incorrect format for username");
		document.getElementById("usernameLabel").style.color = "red";
		validate = false;
	}
	else
	{
		document.getElementById("usernameLabel").style.color = "black";
	}
	if(specRegChar.test(firstname.value))
	{
		alert("Invalid first name");
		document.getElementById("firstnameLabel").style.color = "red";
		validate = false;
	}
	else
	{
		document.getElementById("firstnameLabel").style.color = "black";
	}
	if(specRegChar.test(lastname.value))
	{
		alert("Invalid last name");
		document.getElementById("lastnameLabel").style.color = "red";
		validate = false;
	}
	else
	{
		document.getElementById("lastnameLabel").style.color = "black";
	}
	if(specRegNum.test(phone.value))
	{
		alert("Only numeric values allowed for phone numbers");
		document.getElementById("phoneLabel").style.color = "red";
		validate = false;
	}
	else
	{
		document.getElementById("phoneLabel").style.color = "black";
	}
	if(specRegNum.test(zipcode.value))
	{
		alert("Zipcodes can only have number values");
		document.getElementById("zipcodeLabel").style.color = "red";
		validate = false;
	}
	else
	{
		document.getElementById("zipcodeLabel").style.color = "black";
	}
	if(!validateEmail(email.value))
	{
        alert("Invalid Email Address.");
		document.getElementById("emailLabel").style.color = "red";
        validate = false;
    }
	else
	{
		document.getElementById("emailLabel").style.color = "black";
	}
    if(/*validate && */(pass.value != confirmpass.value)){
        alert("Password fields do not match. Please try again.");
		document.getElementById("confirmLabel").style.color = "red";
        validate = false;
    }
	else
	{
		document.getElementById("confirmLabel").style.color = "black";
	}
	
	if(validate == false)
		return;
	
	// Gender and preference OPTIONAL 
	// DEFAULT GENDER AND PREFERENCE
	selectGen = "other";
	selectPref = "both";
	
    for (var j = 0; j < gender.length; j++)
    {
        if (gender[j].checked)
        {
            selectGen = gender[j].value;
            //alert(selectGen.value);
            break;
        }
    }

    for (var q = 0; q < preference.length; q++)
    {
        if (preference[q].checked)
        {
            selectPref = preference[q].value;
            //alert(selectPref.value);
            break;
        }
    }
  
	
    //alert("preference is " + selectPref.value);
    /* The following code performs a post request and attempts to send data to the "create.php" file in the form of a json string
    
        var JsonPayload is the json string that passes in the necessary parameters.
            - These can be accessed in the php by using $_POST["username"], or whatever field that you want to access.
        The rest of the code here looks strange, but it involves a series of checks that make sure the php file doesn't get stuck in that infinite
        pending state, which the other Ajax code does not check for.
        Once you want to access data returned from the php script, var jsonObject = JSON.parse( xhr.responseText ); is the code that will allow you to grab
        an encoded JSON object from the php script. You can access elements of this just like you would do with a standard JSON file.
    */ 
	/*
		$user = $inData['username'];
		$firstname = $inData['firstname'];
		$lastname = $inData['lastname'];
		$email = $inData['email'];
		$phone = $inData['phone'];
		$pass = $inData['pass'];
		$zipcode = $inData['zipcode'];
		$birthdate = $inData['birthdate'];
		$about = $inData['about'];
		$gender = $inData['selectGen'];
		$preference = $inData['selectPref'];
	*/
	
    // jsonPayload is the JSON string that we are sending to the php. Always double-check the syntax of this statement, because the php won't be able to read it
    // if it's incorrect.
    var jsonPayload = '{"username" : "' + username.value + '", "firstname" : "' + firstname.value + '", "lastname" : "' + lastname.value + '", "email" : "' + email.value + '", "phone" : "' + phone.value + '", "pass" : "' + pass.value
		+ '", "zipcode" : "' + zipcode.value + '", "birthdate" : "' + birthdate.value + '", "about" : "' + about.value + '", "gender" : "' + selectGen + '", "preference" : "' + selectPref + '"}';
	

    var url = urlBase + '/create.php';
    var xhr = new XMLHttpRequest();
    xhr.open("POST", url, false);
    xhr.setRequestHeader("Content-type", "application/json; charset=UTF-8");

    try
    {
        // sends the JSON string over to the php script
        xhr.send(jsonPayload);

        // this will create an alert with the response from the php -- this is useful in case an invalid json object is returned or an error is thrown in the php code
        //alert(xhr.responseText);

        // IF ANYTHING IS RETURNED OTHER THAN PROPER JSON, AN ERROR WILL BE THROWN!!! This is why we have the alert statement above ^^^
        // returns a JSON string from the php script and converts it to a JSON object for easy access
        var jsonObject = JSON.parse( xhr.responseText );

        // example of how to access json object from php
        //alert(jsonObject[0]["firstname"]);
   
        // here's an example of how you would access a "user" string returned from the JSON. The "user" field is a key in the JSON object.
        // if you try to access an element that isn't in the json object, this statement will fail.
        //user = jsonObject[0]["user"];
        
        // redirects the window to the home.html page
		alert(xhr.responseText);
		var user_id = jsonObject[0]["user_id"];
        document.cookie = user_id;
        window.location.href = "/profile.html";
    }
    // if there's an error, we'll be able to see it in the form of an alert
    catch(err)
    {
		alert(xhr.responseText);
        alert("Username already exists");
    }  
	
    /*
        SUPER IMPORTANT - READ THIS
        - For one reason or another, when trying to use the Ajax call before the php file enters an endless "pending" state. To avoid this,
        use the post method listed directly above. 
        - I'm leaving the code below in case someone needs to reference it to fill the parameters in above, but it should be deleted afterwards.
    */

    /*if(validate)
	{
        $.ajax({
            type: 'POST',
            url: urlBase + '/create.php',
            data: {
                // action: 'create'
				username: username.value,
				firstname: firstname.value,
                lastname: lastname.value,
				email: email.value,
				phone: phone.value,
				pass: pass.value,
				zipcode: zipcode.value,
				birthdate: birthdate.value,
				about: about.value,
				selectGen: selectGen.value,
				selectPref: selectPref.value
            },
            success: function (data) {
                // checks to see if the password was valid or not
                if (data == "Verified")
                    window.location.href = "./home.html";
                else
                    alert(data);
            }
            
        });
    }
	else
	{
		alert("Please check your form before submission");
	}*/

    // clears all forms
    //for(var j = 0; j < 7; j++)
      //  fieldArray[j].value = '';
}

/*
function fillProfile(data)
{
    $.ajax
    ({
        type: 'POST',
        url: profile.php',
        data: {
            current_user: current_user
    },
    
    success: function (data)
    {
        var json = data;
        var obj = JSON.parse(json);
        var length = obj.length;
        
        document.getELementById("profileName").innerHTML = 
    }
    
    
    });
}
*/