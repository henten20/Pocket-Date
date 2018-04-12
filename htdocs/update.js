// Update

//var urlBase = 'http://cop4331groupeight.com';
var urlBase = '/php';
var extension = "php";

var userId = 0;
var firstName = "";
var lastName = "";
// These will be used for checking for correct characters
var specReg = /[^A-Za-z0-9 ]/;	
var specRegChar = /[^A-Za-z ]/;
var specRegNum = /[^0-9]/;
	

function updateField(field, updated)
{
	var username = document.cookie;

	var url = urlBase + '/update.' + extension;
	var xhr = new XMLHttpRequest();
	xhr.open("POST", url, true);
	//xhr.open("POST", url, false);
	
	// switch command for the type of field that is going to be updated
	switch(field)
	{
		case "email": 
			var jsonPayload = '{"email" : "' + updated + '", "username" : "' + username + '", "field" : "' + "email" + '"}';
			break;
		case "firstname":
			var jsonPayload = '{"firstname" : "' + updated + '", "username" : "' + username + '", "field" : "' + "firstname" + '"}';
			break;
		case "lastname":
			var jsonPayload = '{"lastname" : "' + updated + '", "username" : "' + username + '", "field" : "' + "lastname" + '"}';
			break;
		case "zipcode":
			var jsonPayload = '{"zipcode" : "' + updated + '", "username" : "' + username + '", "field" : "' + "zipcode" + '"}';
			break;
		case "preference":
			var jsonPayload = '{"preference" : "' + updated + '", "username" : "' + username + '", "field" : "' + "preference" + '"}';
			break;
		case "about":
			var jsonPayload = '{"about" : "' + updated + '", "username" : "' + username + '", "field" : "' + "about" + '"}';
			break;
	}
	alert(jsonPayload);
	xhr.setRequestHeader("Content-type", "application/json; charset=UTF-8");
	/*
	try
    {
        xhr.send(jsonPayload);
        var jsonObject = JSON.parse( xhr.responseText );
		user = jsonObject[0]["user"];
		alert(user);
    }
    catch(err)
    {
        alert(err.message);
    }
	*/
	
	xhr.onreadystatechange = function()
	{
		if(this.readyState == 4 && this.status == 200)
		{
			var jsonObject = JSON.parse( xhr.responseText );
			user = jsonObject[0]["user"];
			alert(user);
		}
	};
	
	
	xhr.send(jsonPayload);
}

// change the paragraph field to a textbox and update the information when pressing submit
function editEmail()
{
  document.getElementById("email").innerHTML = "<input type=\"text\" id=\"newemail\"><input type=\"submit\" onclick=\"updateEmail()\">";
}
// make sure to use a unique name for the temporary ID that you're creating.
function updateEmail()
{
  var updated = document.getElementById("newemail");

  // at this point, we need to take that information that was passed in and update the database with it
  // will figure this out later
  updateField("email", updated.value);
  document.getElementById("email").innerHTML = "<p id = \"email\">" + updated.value + "<span type=\"button\" id=\"setEmail\" onclick = \"editEmail()\" class=\"glyphicon glyphicon-pencil\"></span>";
}

function editFirst()
{
  document.getElementById("firstname").innerHTML = "<input type=\"text\" id=\"newfirst\"><input type=\"submit\" onclick=\"updateFirst()\">";
}
// make sure to use a unique name for the temporary ID that you're creating.
function updateFirst()
{
  var updated = document.getElementById("newfirst");

  // at this point, we need to take that information that was passed in and update the database with it
  // will figure this out later
  updateField("firstname", updated.value);
  document.getElementById("firstname").innerHTML = "<p id = \"firstname\">" + updated.value + "<span type=\"button\" id=\"setFirst\" onclick = \"editFirst()\" class=\"glyphicon glyphicon-pencil\"></span>";
}

function editLast()
{
  document.getElementById("lastname").innerHTML = "<input type=\"text\" id=\"newlast\"><input type=\"submit\" onclick=\"updateLast()\">";
}
// make sure to use a unique name for the temporary ID that you're creating.
function updateLast()
{
  var updated = document.getElementById("newlast");

  // at this point, we need to take that information that was passed in and update the database with it
  // will figure this out later
  updateField("lastname", updated.value);
  document.getElementById("lastname").innerHTML = "<p id = \"lastname\">" + updated.value + "<span type=\"button\" id=\"setLast\" onclick = \"editLast()\" class=\"glyphicon glyphicon-pencil\"></span>";
}


function editZip()
{
  document.getElementById("zipcode").innerHTML = "<input type=\"text\" id=\"newzip\"><input type=\"submit\" onclick=\"updateZip()\">";
}
// make sure to use a unique name for the temporary ID that you're creating.
function updateZip()
{
  var updated = document.getElementById("newzip");

  // at this point, we need to take that information that was passed in and update the database with it
  // will figure this out later
  updateField("zipcode", updated.value);
  document.getElementById("zipcode").innerHTML = "<p id = \"zipcode\">" + updated.value + "<span type=\"button\" id=\"setZip\" onclick = \"editZip()\" class=\"glyphicon glyphicon-pencil\"></span>";
}

function editPreference()
{
  document.getElementById("preference").innerHTML = "<select name = \"preference\" id=\"newpreference\" onchange=\"updatePreference()\"> <option value=\"Select\">Select</option> <option value=\"Male\">Male</option> <option value=\"Female\">Female</option> <option value=\"Both\">Both</option>>";
}
// make sure to use a unique name for the temporary ID that you're creating.
function updatePreference()
{
  var updated = document.getElementById("newpreference");

  // at this point, we need to take that information that was passed in and update the database with it
  // will figure this out later
  updateField("preference", updated.value);
  document.getElementById("preference").innerHTML = "<p id = \"preference\">" + updated.value + "<span type=\"button\" id=\"setPreference\" onclick = \"editPreference()\" class=\"glyphicon glyphicon-pencil\"></span>";
}

function editAbout()
{
  document.getElementById("about").innerHTML = "<input type=\"text\" id=\"newabout\"><input type=\"submit\" onclick=\"updateAbout()\">";
}
// make sure to use a unique name for the temporary ID that you're creating.
function updateAbout()
{
  var updated = document.getElementById("newabout");

  // at this point, we need to take that information that was passed in and update the database with it
  // will figure this out later
  updateField("about", updated.value);
  document.getElementById("about").innerHTML = "<p id = \"about\">" + updated.value + "<span type=\"button\" id=\"setAbout\" onclick = \"editAbout()\" class=\"glyphicon glyphicon-pencil\"></span>";
}

function editPass()
{
	var oldPass = document.getElementById("oldPass");
	var newPass = document.getElementById("newPass");
	var confirmPass = document.getElementById("confirmPass");
	alert("Here");
	if(oldPass.value == null || newPass.value == null || confirmPass.value == null)
		alert("Please fill in all password fields before submitting");
	
	if(newPass.value != confirmPass.value)
		alert("New passwords do not match");
	
	var username = document.cookie;

	var url = urlBase + '/update.' + extension;
	var xhr = new XMLHttpRequest();
	xhr.open("POST", url, true)
	
	
	// create json payload with the password fields
	var jsonPayload = '{"username" : "' + username + '", "oldPass" : "' + oldPass.value + '", "newPass" : "' + newPass.value + '", "field" : "' + "password" + '"}';
	alert(jsonPayload);
	
	xhr.onreadystatechange = function()
	{
		if(this.readyState == 4 && this.status == 200)
		{
			alert(xhr.responseText);
			
			var jsonObject = JSON.parse( xhr.responseText );
			user = jsonObject[0]["user"];
			
			if(user == "error")
				alert("Old password was incorrect");
			else alert(user);
		}
	};
	
	
	xhr.send(jsonPayload);
	//alert(xhr.responseText)
}