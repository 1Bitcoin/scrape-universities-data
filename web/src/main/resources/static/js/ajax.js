$(document).ready(function () {

    $("#search-form").submit(function (event) {

        //stop submit the form, we will post it manually.
        event.preventDefault();

        ajax_submit_modelling();

    });

    $("#generate-form").submit(function (event) {

        //stop submit the form, we will post it manually.
        event.preventDefault();

        ajax_submit_generate();

    });

});

function ajax_submit_modelling() {

    var search = {}
    search["username"] = 'test';

    $("#btn-search").prop("disabled", true);

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/modelling",
        data: JSON.stringify(search),
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (data) {

            var json = "<h4>Ajax Response</h4>&lt;pre&gt;"
                + JSON.stringify(data, null, 4) + "&lt;/pre&gt;";
            $('#feedback').html(json);

            console.log("SUCCESS : ", data);
            $("#btn-search").prop("disabled", false);

        },
        error: function (e) {

            var json = "<h4>Ajax Response</h4>&lt;pre&gt;"
                + e.responseText + "&lt;/pre&gt;";
            $('#feedback').html(json);

            console.log("ERROR : ", e);
            $("#btn-search").prop("disabled", false);

        }
    });

}

function ajax_submit_generate() {

    var search = {}
    search["username"] = 'test';

    $("#btn-generate").prop("disabled", true);

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/generating",
        data: JSON.stringify(search),
        dataType: 'json',
        cache: false,
        timeout: 600000
    });

}