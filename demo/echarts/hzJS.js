$(document.ready(
function () {
	$("#inputEnter").click(function () {
		alert($("#pagetitle").val())
	});
})
);
function hzUpdate() {
	// pageTitle is defined in main.html
	// get input pagetitle
	pageTitle = document.getElementById("pagetitle").value;
	alert(pageTitle);
	var xmlHttp = GetXmlHttpObject();
	if(xmlHttp == null){
		alert("browser not suppport http request");
		return;
	}
	var url = "http://localhost/db.php?title=" + pageTitle;
	alert("url:" + url);
	xmlHttp.onreadystatechange = function () {
	alert(xmlHttp.readyState);
	var info = "readyState:" + xmlHttp.readyState + " status:" + xmlHttp.status + " ";
	alert(info);
	if (xmlHttp.readyState == 4 && (xmlHttp.status == 200 || xmlHttp.status == 0)) {
		alert("respbose:" + xmlHttp.responseText);
	}
	var code = "";
}
	xmlHttp.open("GET", url, false);
	xmlHttp.send();
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
		
		
