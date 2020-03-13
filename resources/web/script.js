$(document).ready(function(){
    $.get('/api/get_available_containers', function(data){
        var text = "";
        $.each(data, function(id, name){
            text += '<button type="button" class="list-group-item list-group-item-action" id="' + id + '">' + name + '</button>\n'
        })
        $('#collapseContainerSpoiler > .list-group').html(text);
    });
});

function loadConsoleText(id) {
    $.get('/api/get_console_log?id=' + id, function(data) {
        alert(data);
    });
    // $('#consoleViewPort').text()
}
