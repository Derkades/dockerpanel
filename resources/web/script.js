$(document).ready(function() {
    toastr.options.progressBar = true;
    toastr.options.newestOnTop = false;
    toastr.options.positionClass = "toast-bottom-right";

    $.get('/api/get_containers', function(data) {
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

        toastr.info("Starting..");

        $.get('/api/start_container', params, function(text) {
            if (text == "ok") {
                toastr.success("Container started");
                loadConsoleText();
            } else if (text == "already started"){
                toastr.warning("This container is already started");
            } else {
                toastr.error("An error occured while starting the container");
            }
        }, "text");
    });

    $('#buttonKill').click(function() {
        var params = {
            id: window.selectedContainerId
        };

        toastr.info("Stopping..");

        $.get('/api/stop_container', params, function(text) {
            if (text == "ok") {
                toastr.success("Container stopped");
                loadConsoleText();
            } else if (text == "already stopped"){
                toastr.warning("This container is already stopped");
            } else {
                toastr.error("An error occured while stopping the container");
            }
        }, "text");
    });

    $('#terminal-input-form').keypress(function(e){
        if(e.keyCode == 13) {
            sendConsoleCommand($('#terminal-input-form').val());
            $('#terminal-input-form').val('');
        }
    });

    // $(document.body).bind('mouseup', function(e){
    //     var selection;

    //     if (window.getSelection) {
    //       selection = window.getSelection();
    //     } else if (document.selection) {
    //       selection = document.selection.createRange();
    //     }

    //     if (selection){
    //         toastr.options.onclick = function(){
    //             window.pauseConsole = false;
    //         };
    //         toastr.warning("Console output suspended, click to resume");
    //         window.pauseConsole = true;
    //     }
    // });

    setInterval(loadConsoleText, 2000)

    loadConsoleText();
});

function loadConsoleText() {
    if (window.selectedContainerId) {
        var params = {
            id: window.selectedContainerId
        };

        $.get('/api/get_container_status', params, function(text) {
            if (text == "running") {
                if ($('#freeze-console').prop("checked")) {
                    // setTimeout(loadConsoleText, 200);
                } else {
                    $('#active-status-indicator').removeClass('status-offline').addClass('status-online');
                    const out = document.getElementById("terminal-logs");
                    const isScrolledToBottom = out.scrollHeight - out.clientHeight <= out.scrollTop + 1;
                    $('#terminal-logs').load('/api/get_container_logs?id=' + window.selectedContainerId, null, function(){
                        if (isScrolledToBottom) {
                            $('#terminal-logs').scrollTop($('#terminal-logs')[0].scrollHeight);
                            // window.containerScroll = false;
                        }
                        // setTimeout(loadConsoleText, 1000);
                    });
                }
            } else {
                $('#active-status-indicator').removeClass('status-online').addClass('status-offline');
                // window.containerScroll = true;
                $('#terminal-logs').text("offline");
                // setTimeout(loadConsoleText, 1000);
            }
        }, "text");

    } else {
        $('#active-status-indicator').removeClass('status-online').addClass('status-offline');
        $('#terminal-logs').text('');
        // setTimeout(loadConsoleText, 100);
    }
}

function setSelectedContainer(id, name) {
    // window.containerScroll = true;
    window.selectedContainerId = id;
    $('.list-group button').removeClass("active");
    $('#nav-container-' + id).addClass("active");
    $('main').css('display', 'initial');
    $('#information').css('display', 'none');
    $('#serverTitle').text(" - " + name);
    loadConsoleText()
}

function sendConsoleCommand(command){
    var params = {
        id: window.selectedContainerId,
        command: command
    };

    $.get('/api/send_command', params, function(text) {
        if (text == "ok") {
            toastr.success("Command sent");
        } else {
            toastr.error("Error occured while sending command");
        }

        setTimeout(loadConsoleText, 100)
    }, "text");
}

function isTextSelected(input){
    var startPos = input.selectionStart;
    var endPos = input.selectionEnd;
    var doc = document.selection;

    if(doc && doc.createRange().text.length != 0){
       return true;
    }else if (!doc && input.text().substring(startPos,endPos).length != 0){
       return true;
    }
    return false;
 }
