function showAlert() {
    alert("The button was clicked!");
}

function showName(name) {
    alert("Here's the name: " + name);
}

var eventSource = new EventSource('sse-endpoint-address');
eventSource.addEventListener("message", function (event) {
    // const textarea = document.getElementById('one');
    // textarea.value = event.data;

    var x = document.getElementById('one');
    x.value += event.data;

    console.log(event.data);

})

function addBlock(text) {
    var a = document.createElement("article");

    var para = document.createElement("P");
    para.innerHTML = text;
    a.appendChild(para);

    document.getElementById("pack").appendChild(a);
}

