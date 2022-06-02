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

    $("#delete-form").submit(function (event) {

        //stop submit the form, we will post it manually.
        event.preventDefault();

        ajax_submit_delete();

    });

});

function ajax_submit_modelling() {

    var search = {}
    search["countVUZ"] = document.getElementById('countVUZ').value;
    search["year"] = document.getElementById('year').value;
    search["durationOriginal"] = document.getElementById('durationOriginal').value;
    search["durationCopy"] = document.getElementById('durationCopy').value;
    search["countStudent"] = document.getElementById('countStudent').value;

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
    search["procentFail"] = document.getElementById('procentFail').value;
    search["procentMiddle"] = document.getElementById('procentMiddle').value;
    search["procentSenior"] = document.getElementById('procentSenior').value;
    search["procentGenius"] = document.getElementById('procentGenius').value;

    search["procentMiddleChange"] = document.getElementById('procentMiddleChange').value;
    search["procentSeniorChange"] = document.getElementById('procentSeniorChange').value;
    search["procentGeniusChange"] = document.getElementById('procentGeniusChange').value;

    search["countYGSN"] = document.getElementById('countYGSN').value;

    search["minScoreRus"] = document.getElementById('minScoreRus').value;
    search["minScoreMath"] = document.getElementById('minScoreMath').value;
    search["minScorePhys"] = document.getElementById('minScorePhys').value;
    search["minScoreChem"] = document.getElementById('minScoreChem').value;
    search["minScoreBio"] = document.getElementById('minScoreBio').value;
    search["minScoreSoc"] = document.getElementById('minScoreSoc').value;
    search["minScoreInf"] = document.getElementById('minScoreInf').value;
    search["minScoreHis"] = document.getElementById('minScoreHis').value;
    search["minScoreGeo"] = document.getElementById('minScoreGeo').value;
    search["minScoreEn"] = document.getElementById('minScoreEn').value;
    search["minScoreLit"] = document.getElementById('minScoreLit').value;

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

function ajax_submit_delete() {

    $("#btn-delete").prop("disabled", true);

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/delete",
        cache: false,
        timeout: 600000,
        success: function (data) {

            var json = "<h4>Ajax Response</h4>&lt;pre&gt;"
                + JSON.stringify(data, null, 4) + "&lt;/pre&gt;";
            $('#feedback').html(json);

            console.log("SUCCESS : ", data);
            $("#btn-delete").prop("disabled", false);

        },
        error: function (e) {

            var json = "<h4>Ajax Response</h4>&lt;pre&gt;"
                + e.responseText + "&lt;/pre&gt;";
            $('#feedback').html(json);

            console.log("ERROR : ", e);
            $("#btn-delete").prop("disabled", false);

        }
    });

}
