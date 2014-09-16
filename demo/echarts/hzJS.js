function hzUpdate() {
	// pageTitle is defined in main.html
	// get input pagetitle
	pageTitle = document.getElementById("pagetitle").value;
	var xmlHttp = GetXmlHttpObject();
	if(xmlHttp == null){
		alert("browser not suppport http request");
		return;
	}
	var url = "http://localhost/db.php?title=" + pageTitle;
	xmlHttp.onreadystatechange = UpdateDemo;
	xmlHttp.open("GET", url, true);
	xmlHttp.send();
}
function UpdateDemo() {
	var code = "";
}
function hzClearValue(target) {
	target.value = '';
}

function hzShowTitle(target) {
	target.value = pageTitle;
}

function GetXmlHttpObject() {
	var xmlHttp = null;
	try {
		// firefox, Opera 8.0 +, Safari
		xmlHttp = new XMLHttpRequest();
	}catch (e) {
		// Internet Explorer
		try {
			xmlHttp = new ActiveObject("Msxml2.XMLHTTP");
		}catch (e) {
			xmlHttp = new ActiveObject("Microsoft.XMLHTTP");
		}
	}
	return xmlHttp;
}
		
		
