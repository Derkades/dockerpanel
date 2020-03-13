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
        $('#serverTitle').text("");
    });

    $('#buttonStart').click(function() {
        var params = {
            id: window.selectedContainerId
        };
        alert('start..');
        $.post('start_container', params, function(){
            alert('done!');
        });
    });

    $('#buttonKill').click(function() {
        var params = {
            id: window.selectedContainerId
        };
        alert('stop..');
        $.post('stop_container', params, function(){
            alert('done!');
        });
    });

    loadConsoleText();
});

function loadConsoleText() {
    if (window.selectedContainerId) {
        console.log($('#terminal-logs').text());
        var scroll = $('#terminal-logs').text() == "Loading..";
        $('#terminal-logs').load('/api/get_container_logs?id=' + window.selectedContainerId, null, function(){
            if (scroll) {
                $('#terminal-logs').scrollTop($('#terminal-logs')[0].scrollHeight);
            }
        });
    } else {
        $('#terminal-logs').text('Loading..');
        setTimeout(loadConsoleText, 100);
    }
}

function setSelectedContainer(id, name) {
    window.selectedContainerId = id;
    $('#nav-container-' + id).addClass("active");
    $('main').css('display', 'initial');
    $('#information').css('display', 'none');
    $('#serverTitle').text(" - " + name);
}
