$(document).ready(function() {
    $.get('/api/get_available_containers', function(data) {
        var text = "";
        $.each(data, function(id, name) {
            text += '<button type="button" class="list-group-item list-group-item-action" ';
            text += 'id="nav-container-' + id + '" ';
            text += 'onclick="setSelectedContainer(&quot;' + id + '&quot;, &quot;' + name + '&quot;)">' + name;
            text += '<span style="text-align: right; width: 100%;"><div class="circle" ';
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
    console.log("test0");
    if (window.selectedContainerId) {
        $('#terminal-logs').load('/api/get_container_logs?id=' + window.selectedContainerId, null, function(){
            if (window.containerScroll) {
                $('#terminal-logs').scrollTop($('#terminal-logs')[0].scrollHeight);
                window.containerScroll = false;
            }

            console.log("test1");

            var params = {
                id: window.selectedContainerId
            };

            console.log("test12");

            $.get('/api/get_container_status', params, function(data) {
                console.log("test2");
                var response = data.responseText;
                console.log("response");
                if (response == "running\n") {
                    console.log("yes");
                    $('#active-status-indicator').removeClass('status-offline').addClass('status-online');
                } else {
                    $('#active-status-indicator').removeClass('status-online').addClass('status-offline');
                }

                setTimeout(loadConsoleText, 300);
            });
        });
    } else {
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
