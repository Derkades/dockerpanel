$(document).ready(function() {
    $.get('/api/get_available_containers', function(data) {
        var text = "";
        $.each(data, function(id, name) {
            text += '<button type="button" class="list-group-item list-group-item-action" ';
            text += 'id="nav-container-' + id + '" ';
            text += 'onclick="setSelectedContainer(&quot;' + id + '&quot;, &quot;' + name + '&quot;)">' + name + '</button>\n'
        });
        $('#collapseContainerSpoiler > .list-group').html(text);
    });
});

function loadConsoleText(id) {
    $.get('/api/get_console_log?id=' + id, function(data) {
        alert(data);
    });
}

function setSelectedContainer(id, name) {
    window.selectedContainerId = id;
    $('#nav-container-' + id).addClass("active");
    alert("selected container set to " + id + " " + name);
}
