function showAlert() {
    alert("The button was clicked!");
}

function showName(name) {
    alert("Here's the name: " + name);
}

var eventSource = new EventSource('sse-endpoint-address');
eventSource.addEventListener("message", function (event) {

    var x = document.getElementById('one');
    x.value += event.data + "\n";

    console.log(event.data);

})

