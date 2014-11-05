var xmlHttp;
function GetZhwikiIMGInfo(pageid){
	xmlHttp = GetXmlHttpObject()
	if(xmlHttp == null)
	{
		alert("browser does not suppert http request");
		return;
	}
	var url = "GetZhwikiInfo.php";
	url = url + "?id=" + pageid + "&res=img";
	url = url+"&sid="+Math.random();
	xmlHttp.onreadystatechange= IMGChanged;
	xmlHttp.open("GET", url, true);
	xmlHttp.send(null);
}

function IMGChanged() { 
	if (xmlHttp.readyState==4 || xmlHttp.readyState=="complete"){
		document.getElementById("zhwiki_Img").innerHTML=xmlHttp.responseText;
	} 
}

var xmlHttp1;
function GetZhwikiFirParaInfo(pageid){
		alert( "para");
	xmlHttp1 = GetXmlHttpObject()
	if(xmlHttp1 == null)
	{
		alert("browser does not suppert http request");
		return;
	}
	var url = "GetZhwikiInfo.php";
	url = url + "?id=" + pageid + "&res=firpara";
	url = url+"&sid="+Math.random();
	xmlHttp1.onreadystatechange= FirParaChanged;
	xmlHttp1.open("GET", url, true);
	xmlHttp1.send(null);
}

function FirParaChanged() { 
	if (xmlHttp1.readyState==4 || xmlHttp1.readyState=="complete"){
		alert( xmlHttp1.responseText);
		document.getElementById("zhwiki_Summary").innerHTML=xmlHttp1.responseText;
	} 
}
function GetXmlHttpObject()
{
	var xmlHttp=null;
	try {
		// Firefox, Opera 8.0+, Safari
		xmlHttp=new XMLHttpRequest();
	}
	catch (e)
	{
		//Internet Explorer
		try {
			xmlHttp=new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e){
			xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
		}
	}
	return xmlHttp;
}

