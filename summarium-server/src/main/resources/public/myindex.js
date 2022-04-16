var formName = "#audioform";

var idsToFileNames = {};
var idsToStatus = {};
var currentAnalytics = {};


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
          idsToFileNames[t] = files[0].name;
          idsToStatus[t] = false;
        });
    console.log("Hey");

}

document.getElementById("upload").addEventListener("change", handleFiles, false);
console.log('hellllo');


//run the below function in setInterval
function updateStatusForAll() {
  for(const prop in idsToStatus) {
    if(idsToStatus[prop] == false) {
      fetch("http://localhost:8080/checkstatus?id=" + prop, {
        method: "get"
      })
      .then(r => r.text())
      .then(y => {
        if(y == "true") {
          idsToStatus[prop] = true;
          window.localStorage.setItem(prop, idsToFileNames[prop]);
          triggerChange();
        }
      })
    }
  }
}

//run this function once on startup
function readFromLocalStorage() {
  var archive = {},
  keys = Object.keys(localStorage),
  i = keys.length;

  while ( i-- ) {
    idsToStatus[kes[i]] = true;
    idsToFileNames[keys[i]] = localStorage.getItem(keys[i]);
  }

  return archive;
}


//run the below function onClick
function getAnalysisFromId(id) {
  fetch("http://localhost:8080/gettopics?id=" + id, {
    method: "get"
  })
  .then(r => r.json())
  .then(y => {
    currentAnalytics = y;
    updateTimestamps();
  })
}

//call to update list about pending processes
function triggerChange() {

}

//call when new Data has arrived
function updateTimestamps() {

}

readFromLocalStorage();
setInterval(updateStatusForAll, 1000);
