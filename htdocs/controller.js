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
	
    var firstname = document.getElementById("firstname");
    var lastname = document.getElementById("lastname");
	var email = document.getElementById("email");
    var phone = document.getElementById("phone");
	var username = document.getElementById("username");
    var pass = document.getElementById("password");
    var confirmpass = document.getElementById("confirm");
    var fieldArray = [firstname, lastname, email, phone, username, pass, confirmpass];
    var validate = true;
	
    var specReg = /[^A-Za-z0-9 ]/;
	
    // for loop will iterate through all input fields and check to make sure that they are filled out
    for(var i = 0; i < 7; i++){
        
        var curVal = fieldArray[i].value;

        // checks to ensure that the fields are populated
        if(curVal.length <= 0){
            alert("Please fill out all fields before submitting.");
            validate = false;
            break;
        }

        // checks against email addresses
        if(i == 2){
            if(!validateEmail(fieldArray[i].value)){
                alert("Invalid Email Address.");
                validate = false;
                break;
            }
        }
        else if(specReg.test(fieldArray[i].value))
        {
            alert(fieldArray[i].value);
            alert("Invalid character(s) found. Please try again.");
            validate = false;
            break;
        }
    }

    // checks to see if the two password fields match.
    if(validate && (fieldArray[5].value != fieldArray[6].value)){
        alert("Password fields do not match. Please try again.");
        validate = false;
    }

    // only run the jquery 
    if(validate){
        $.ajax({
            type: 'POST',
            url: urlBase + '/create.php',
            data: {
               // action: 'create',
                firstname: firstname.value,
                lastname: lastname.value,
                email: email.value,
                username: username.value,
                pass: pass.value,
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