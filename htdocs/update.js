// Update

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
  document.getElementById("zipcode").innerHTML = "<p id = \"zipcode\">" + updated.value + "<span type=\"button\" id=\"setZip\" onclick = \"editZip()\" class=\"glyphicon glyphicon-pencil\"></span>";
}

function editPreference()
{
  document.getElementById("preference").innerHTML = "<select name = \"preference\" id=\"newpreference\" onchange=\"updatePreference()\"> <option value=\"Male\">Male</option> <option value=\"Female\">Female</option>>";
}
// make sure to use a unique name for the temporary ID that you're creating.
function updatePreference()
{
  var updated = document.getElementById("newpreference");

  // at this point, we need to take that information that was passed in and update the database with it
  // will figure this out later
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
  document.getElementById("about").innerHTML = "<p id = \"about\">" + updated.value + "<span type=\"button\" id=\"setAbout\" onclick = \"editAbout()\" class=\"glyphicon glyphicon-pencil\"></span>";
}
