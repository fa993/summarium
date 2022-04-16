var formName = "#audioform";

var currentAnalytics = {};

var id;
var filename = undefined;
var status = "false"; // trivial comparison to get false
var timer_handle;
var startTime;

var dotCount = 0;
var dotHandler;

console.log(status);

var helper = document.getElementById("helptext");
var but = document.getElementById('buttonDiv');

function printError() {
  clearInterval(timer_handle);
  clearInterval(dotHandler);
  helper.innerHTML = "Some error occured";
}

//run the below function in setInterval
function updateStatus() {
  if(status === "false") {
    fetch("http://localhost:8080/checkstatus?id=" + id, {
      method: "get"
    })
    .then(response => response.text())
    .then(y => {
      console.log(y);
      if(y === "error") {
        printError();
      } else {
        status = y;
        if(y === "true") {
          clearInterval(timer_handle);
          clearInterval(dotHandler);
          getAnalysisFromId(id);
        }
      }
    }).catch(error => printError());
  }
}

function printDot() {
  switch (dotCount) {
    case 0:
      helper.innerHTML = "Processing";
      break;
    case 1:
      helper.innerHTML = "Processing.";
      break;
    case 2:
      helper.innerHTML = "Processing..";
      break;
    case 3:
      helper.innerHTML = "Processing...";
      break;
    default:
      dotCount = 0;
      break;
  }
  dotCount = (dotCount + 1) % 4;
}

function poll() {
  timer_handle = setInterval(updateStatus, 3000);
  but.innerHTML = "";
  dotHandler = setInterval(printDot, 300);
}

function handleFiles(e) {
    var files = e.target.files;
    $("#src").attr("src", URL.createObjectURL(files[0]));
    document.getElementById("audio").load();
    var documentData = new FormData();
    documentData.append('upload', files[0]);
    console.log('hellllo');

    fetch("http://localhost:8080/processaudio", {
            method: "post",
            body: documentData,
        }).then(r => r.text()).then(t => {
          console.log(t);
          filename = files[0].name;
          status = "false";
          id = t;
          startTime = Date.now();
          poll();
          console.log("Heeeeere")
        }).catch(y => printError());
    console.log("Hey");
}

document.getElementById("upload").addEventListener("change", handleFiles, false);
console.log('hellllo');

var aud = document.getElementById('audio');


// //run this function once on startup
// function readFromLocalStorage() {
//   var archive = {},
//   keys = Object.keys(localStorage),
//   i = keys.length;
//
//   while ( i-- ) {
//     idsToStatus[kes[i]] = true;
//     idsToFileNames[keys[i]] = localStorage.getItem(keys[i]);
//   }
//
//   return archive;
// }


//run the below function onClick
function getAnalysisFromId(id) {
  console.log("In Analytics");
  fetch("http://localhost:8080/gettopics?id=" + id, {
    method: "get"
  })
  .then(r => r.json())
  .then(y => {
    currentAnalytics = y;
    updateTimestamps();
  }).catch(y => printError());
}

function setCurTime(c) {
  return function() {
    aud.currentTime = c;
    aud.play();
  }
}

//call when new Data has arrived
function updateTimestamps() {
  console.log(currentAnalytics);
  console.log((Date.now() - startTime) / 1000);

  helper.innerHTML = "";

  but.innerHTML = "";

  //Now create the buttons
  for(const prop of currentAnalytics.topics) {
    if(prop.score < 0.2) {
      continue;
    }
    console.log("fuzzy");
    for(const tp of prop.timestamps) {
      var seeker = document.createElement("button");
      seeker.setAttribute(
        'style',
        'color:#66BB6A'
      );
      var text = document.createTextNode("Click here to navigate to The Topic: "+"\"" + prop.text + "\"which starts at time " + Math.floor(tp / 60) + ":" + tp % 60);
      seeker.appendChild(text);
      seeker.addEventListener('click', setCurTime(currentAnalytics.silenceTime + tp), false)
      but.appendChild(seeker);
      but.appendChild(document.createElement("br"));
    }
  }

}

// readFromLocalStorage();
