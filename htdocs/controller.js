//var urlBase = 'http://cop4331groupeight.com';
var urlBase = '/php';
var extension = "php";

var userId = 0;
var firstName = "";
var lastName = "";


function signOut(){
    window.location.href = "./index.html";
}

function processLogin()
{
    userId = 0;
    firstName = "";
    lastName = "";
    
    alert("Attempting to login");
    
    var login = document.getElementById("inputEmail").value;
    var password = document.getElementById("inputPassword").value;
   
    var specReg = /[^A-Za-z0-9 ]/;

    // checks for invalid characters in login attempt
    if(specReg.test(login) || specReg.test(password))
    {
        alert("Invalid characters found. Please try again.");
        return;
    }

    var jsonPayload = '{"login" : "' + login + '", "password" : "' + password + '"}';
    var url = urlBase + '/auth.' + extension;
    var xhr = new XMLHttpRequest();
    xhr.open("POST", url, false);
    xhr.setRequestHeader("Content-type", "application/json; charset=UTF-8");

    try
    {
        xhr.send(jsonPayload);
        var jsonObject = JSON.parse( xhr.responseText );

        if(jsonObject["error"] != null)
        {
            alert("User/Password combination incorrect");
            return;
        }
        user = jsonObject[0]["user"];
        document.cookie = user;
        window.location.href = "./home.html";
    }
    catch(err)
    {
        alert(err.message);
    }  
}

// checks for valid email using regex
function validateEmail(email) 
{
    var re = /\S+@\S+\.\S+/;
    return re.test(email);
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
    var birthdate = document.getElementById("birthdate");
	var about = document.getElementById("about");
    var fieldArray = [username, pass, confirmpass, phone, email, firstname, lastname, birthdate, zipcode, about];
    var validate = true;
    var gender = document.getElementsByName("gender");
    var preference = document.getElementsByName("preference");
    var specReg = /[^A-Za-z0-9 ]/;
    var selectGen, selectPref;


    alert("Entering loop");
    
    // for loop will iterate through all input fields and check to make sure that they are filled out
    for(var i = 0; i < 10; i++)
	{
  
        var curVal = fieldArray[i].value;
		

		//alert("Checking " + fieldArray[i].value);

			
		// checks to ensure that the fields are populated
		if(curVal.length <= 0){
			alert("Please fill out all fields before submitting.");
			validate = false;
		}
		
		// bypass email
		if(i == 4) continue;
			
		else if(specReg.test(fieldArray[i].value))
		{
			alert("Invalid character(s) found. Please try again.");
			validate = false;
		}

    }
	
	//alert("Finished first loop");

	if(!validateEmail(fieldArray[4].value))
	{
        alert("Invalid Email Address.");
        validate = false;
    }
	
	//alert("Finished email");
    	
    // checks to see if the two password fields match.
    if(validate && (fieldArray[1].value != fieldArray[2].value)){
        alert("Password fields do not match. Please try again.");
        validate = false;
    }
	
	//alert("passwords match");
    
    for (var j = 0; j < gender.length; j++)
    {
        if (gender[j].checked)
        {
            selectGen = gender[j];
            //alert(selectGen.value);
            break;
        }
    }

    //alert("gender is " + selectGen.value);
	
    for (var q = 0; q < preference.length; q++)
    {
        if (preference[q].checked)
        {
            selectPref = preference[q];
            //alert(selectPref.value);
            break;
        }
    }
    
    //alert("preference is " + selectPref.value);
	//alert("passed through initial checks");


    /* The following code performs a post request and attempts to send data to the "create.php" file in the form of a json string
    
        var JsonPayload is the json string that passes in the necessary parameters.

            - These can be accessed in the php by using $_POST["username"], or whatever field that you want to access.

        The rest of the code here looks strange, but it involves a series of checks that make sure the php file doesn't get stuck in that infinite
        pending state, which the other Ajax code does not check for.

        Once you want to access data returned from the php script, var jsonObject = JSON.parse( xhr.responseText ); is the code that will allow you to grab
        an encoded JSON object from the php script. You can access elements of this just like you would do with a standard JSON file.

    */ 

    // jsonPayload is the JSON string that we are sending to the php. Always double-check the syntax of this statement, because the php won't be able to read it
    // if it's incorrect.
    var jsonPayload = '{"username" : "' + username.value + '", "pass" : "' + pass.value + '", "email" : "' + email.value + '", "firstname" : "' + firstname.value + '", "lastname" : "' + lastname.value +'"}';
    var url = urlBase + '/create.php';
    var xhr = new XMLHttpRequest();
    xhr.open("POST", url, false);
    xhr.setRequestHeader("Content-type", "application/json; charset=UTF-8");

    try
    {
        // sends the JSON string over to the php script
        xhr.send(jsonPayload);

        // this will create an alert with the response from the php -- this is useful in case an invalid json object is returned or an error is thrown in the php code
        alert(xhr.responseText);

        // IF ANYTHING IS RETURNED OTHER THAN PROPER JSON, AN ERROR WILL BE THROWN!!! This is why we have the alert statement above ^^^
        // returns a JSON string from the php script and converts it to a JSON object for easy access
        var jsonObject = JSON.parse( xhr.responseText );

        // example of how to access json object from php
        alert(jsonObject[0]["firstname"]);
   
        // here's an example of how you would access a "user" string returned from the JSON. The "user" field is a key in the JSON object.
        // if you try to access an element that isn't in the json object, this statement will fail.
        //user = jsonObject[0]["user"];
        
        // redirects the window to the home.html page
        window.location.href = "./home.html";
    }
    // if there's an error, we'll be able to see it in the form of an alert
    catch(err)
    {
        alert(err.message);
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