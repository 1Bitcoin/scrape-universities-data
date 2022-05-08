function showAlert() {
    alert("The button was clicked!");
}

function showName(name) {
    alert("Here's the name: " + name);
}
var source = new EventSource('sse-endpoint-address');
source.onmessage = function (event) {
    console.log(event.data);
};
