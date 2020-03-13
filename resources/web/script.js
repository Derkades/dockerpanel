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

    $('.navbar-brand').click(function() {
        $('main').css('display', 'none');
        $('#information').css('display', 'initial');
        window.selectedContainerId = undefined;
    });

    loadConsoleText();
});

function loadConsoleText() {
    if (window.selectedContainerId) {
        $('#terminal-logs').load('/api/get_container_logs?id=' + window.selectedContainerId);
    } else {
        $('#terminal-logs').text('Loading..');
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
