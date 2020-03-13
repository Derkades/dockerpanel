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

    loadConsoleText();
});

function loadConsoleText() {
    if (window.selectedContainerId) {
        $('#consoleViewPort').load('/api/get_container_logs?id=' + window.selectedContainerId);
    } else {
        $('#consoleViewPort').text('Loading..');
        setTimeout(loadConsoleText, 500);
    }
}

function setSelectedContainer(id, name) {
    window.selectedContainerId = id;
    $('#nav-container-' + id).addClass("active");
    $('main').css('display', 'initial');
    $('#information').css('display', 'none');
    $('#serverTitle').text(" - " + name);
}
