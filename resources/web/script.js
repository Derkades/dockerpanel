$(document).ready(function() {
    $.get('/api/get_available_containers', function(data) {
        // console.log("test");
        var text = "";
        $.each(data, function(id, name) {
            text += '<button type="button" class="list-group-item list-group-item-action" ';
            text += 'id="nav-container-' + id + '" ';
            text += 'onclick="setSelectedContainer(&quot;' + id + '&quot;, &quot;' + name + '&quot;)">' + name;
            text += '<span style="float: right;"><div class="circle" ';
            text += 'id="nav-container-' + id + '-button"></div></span>';
            text += '</button>\n'
        });
        $('#collapseContainerSpoiler > .list-group').html(text);
    });

    $('.navbar-brand').click(function() {
        $('.list-group button').removeClass("active");
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
        $.get('/api/start_container', params, function(text) {
            if (text == "ok") {
                alert("success");
            } else if (text == "already started"){
                alert("already started");
            } else {
                alert("unknown error");
            }
        }, "text");
    });

    $('#buttonKill').click(function() {
        var params = {
            id: window.selectedContainerId
        };
        alert('stop..');
        $.get('/api/stop_container', params, function(text) {
            if (text == "ok") {
                alert("success");
            } else if (text == "already stopped"){
                alert("already stopped");
            } else {
                alert("unknown error");
            }
        }, "text");
    });

    loadConsoleText();
});

function loadConsoleText() {
    if (window.selectedContainerId) {
        $('#terminal-logs').load('/api/get_container_logs?id=' + window.selectedContainerId, null, function(){
            if (window.containerScroll) {
                $('#terminal-logs').scrollTop($('#terminal-logs')[0].scrollHeight);
                window.containerScroll = false;
            }

            var params = {
                id: window.selectedContainerId
            };

            $.get('/api/get_container_status', params, function(text) {
                if (text == "running") {
                    $('#active-status-indicator').removeClass('status-offline').addClass('status-online');
                } else {
                    $('#active-status-indicator').removeClass('status-online').addClass('status-offline');
                }

                setTimeout(loadConsoleText, 500);
            }, "text");
        });
    } else {
        $('#active-status-indicator').removeClass('status-online').addClass('status-offline');
        $('#terminal-logs').text('');
        setTimeout(loadConsoleText, 100);
    }
}

function setSelectedContainer(id, name) {
    window.containerScroll = true;
    window.selectedContainerId = id;
    $('.list-group button').removeClass("active");
    $('#nav-container-' + id).addClass("active");
    $('main').css('display', 'initial');
    $('#information').css('display', 'none');
    $('#serverTitle').text(" - " + name);
}
