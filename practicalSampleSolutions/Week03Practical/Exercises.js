navigator.getUserMedia  = navigator.getUserMedia ||
navigator.webkitGetUserMedia ||
navigator.mozGetUserMedia ||
navigator.msGetUserMedia;

function takeSnapshot(){
	  var video = document.querySelector('video');
	  var canvas = document.querySelector('canvas');
	  var ctx = canvas.getContext("2d");
	  canvas.width = video.videoWidth;
	  canvas.height = video.videoHeight;
	  
	  ctx.drawImage(video,0,0);
	  
	  //document.querySelector("img").src = canvas.toDataURL("image/webp");
}

function saveSnapshot(){
	var mycanvas = document.getElementById("canvas_snapshot");
	var myimage = mycanvas.toDataURL("image/png");
	var myannotation = document.getElementById("img_annotation").value;
	localStorage.setItem("img" , myimage);
	localStorage.setItem("annotation" , myannotation);
}

function loadSnapshot(){
	var myimage = localStorage.getItem('img');
	var myannotation = localStorage.getItem('annotation');
	document.getElementById("loaded_snapshot").src = myimage;
	document.getElementById("loaded_annotation").innerHTML = myannotation;
}

function getVideo(){
	var constraint = {video : true};
	
	navigator.getUserMedia(constraint, successCallback, errorCallback);
}

function successCallback(stream){
	var video = document.getElementById("webcam_video");
	video.src = window.URL.createObjectURL(stream);
}

function errorCallback(error){
	alert("something went wrong for getting user media");
}