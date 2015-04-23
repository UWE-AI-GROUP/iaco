/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var req;
var isIE;

function init() {
    form = document.getElementById("form");
}

function initRequest() {
    if (window.XMLHttpRequest) {
        if (navigator.userAgent.indexOf('MSIE') != -1) {
            isIE = true;
        }
        return new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        isIE = true;
        return new ActiveXObject("Microsoft.XMLHTTP");
    }
}


$(document).ready(function () {                  // When the HTML DOM is ready loading, then execute the following function...
    $('#SubmitButton').click(function () {     // Locate HTML DOM element with ID "somebutton" and assign the following function to its "click" event...    
        $.post('do', {
            Designer:                   $("#Designer")[0].selectedIndex,
            DesignProblem:        $("#DesignProblem")[0].selectedIndex,
            EpisodeNumber:       $("#EpisodeNumber")[0].selectedIndex,
            Hints:                          $(".Hints:checked").val(),
            ColourMetaphor:      $(".ColourMetaphor:checked").val(),
            Filename:                   $("input#Filename").val()},
        function (responseJson) {                 // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
            var $select = $('#someselect');       // Locate HTML DOM element with ID "someselect".
            $select.find('option').remove();      // Find all child elements with tag name "option" and remove them (just to prevent duplicate options when button is pressed again).
            $.each(responseJson, function (key, value) {                 // Iterate over the JSON object.
                $('<option>').val(key).text(value).appendTo($select); // Create HTML <option> element, set its value with currently iterated key and its text content with currently iterated item and finally append it to the <select>.
            });
        });
    });
});
