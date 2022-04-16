var formName = "#audioform";

var idsToFileNames = {};
var idsToStatus = {};
var currentAnalytics = {};

var id;
var filename = undefined;
var status = "false"; // trivial comparison to get false
var timer_handle;
var startTime;

console.log(status);

//run the below function in setInterval
function updateStatus() {
  if(status === "false") {
    fetch("http://localhost:8080/checkstatus?id=" + id, {
      method: "get"
    })
    .then(r => r.text())
    .then(y => {
      console.log(y);
      status = y;
      if(y === "true") {
        clearInterval(timer_handle);
        getAnalysisFromId(id);
      }
    })
  }
}

function poll() {
  timer_handle = setInterval(updateStatus, 10000);
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
        });
    console.log("Hey");

}

document.getElementById("upload").addEventListener("change", handleFiles, false);
console.log('hellllo');


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
  })
}

//call when new Data has arrived
function updateTimestamps() {
  console.log(currentAnalytics);
  console.log((Date.now() - startTime) / 1000);
}

// readFromLocalStorage();
