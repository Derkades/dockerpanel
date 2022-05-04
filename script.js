$(document).ready(function() {
    toastr.options.progressBar = true;
    toastr.options.newestOnTop = false;
    toastr.options.positionClass = "toast-bottom-right";

    /*
     * window.pauseTerminal keeps track of whether the terminal should temporarily
     * not refresh. This is set to true when a user makes a selection. By default,
     * terminal output should refresh automatically.
     */
    window.pauseTerminal = false;

    $('.navbar-brand').click(function() {
        $('.list-group button').removeClass("active");
        $('main').css('display', 'none');
        $('#information').css('display', 'initial');
        window.selectedContainerId = undefined;
        $('#serverTitle').text("");
    });

    $('#button-start').click(function() {
        var params = {
            id: window.selectedContainerId
        };

        // toastr.info("Starting&mldr;");

        $.get('/api/start_container', params, function(text) {
            if (text == "ok") {
                toastr.success("Container started");
                loadConsoleText();
            } else if (text == "already started"){
                toastr.warning("This container is already started");
            } else if (text == "disabled") {
                toastr.warning("Buttons are disabled");
            } else {
                toastr.error("An error occured while starting the container");
                console.log("Response: '" + text + "'")
            }
        }, "text");
    });

    $('#button-stop').click(function() {
        var params = {
            id: window.selectedContainerId
        };

        toastr.info("Stopping&mldr;");

        $.get('/api/stop_container', params, function(text) {
            if (text == "ok") {
                toastr.success("Container stopped");
                loadConsoleText();
            } else if (text == "already stopped"){
                toastr.warning("This container is already stopped");
            } else if (text == "disabled") {
                toastr.warning("Buttons are disabled");
            } else {
                toastr.error("An error occured while stopping the container");
                console.log("Response: '" + text + "'")
            }
        }, "text");
    });

    $('#button-restart').click(function() {
        var params = {
            id: window.selectedContainerId
        };

        toastr.info("Restarting&mldr;");

        $.get('/api/restart_container', params, function(text) {
            if (text == "ok") {
                toastr.success("Container restarted");
                loadConsoleText();
            } else if (text == "disabled") {
                toastr.warning("Buttons are disabled");
            } else {
                toastr.error("An error occured while restarting the container");
                console.log("Response: '" + text + "'")
            }
        }, "text");
    });

    $('.terminal-input-form').keypress(function(e) {
        if (e.keyCode == 13) {
            if (!$('.terminal-input-form').val()){
                toastr.warning("Cannot send empty command");
                return;
            }
            sendConsoleCommand($('.terminal-input-form').val());
            $('.terminal-input-form').val('');
        }
    });

    $('#command-send-button').click(function(e) {
        if (!$('.terminal-input-form').val()){
            toastr.warning("Cannot send empty command");
            return;
        }
        sendConsoleCommand($('.terminal-input-form').val());
        $('.terminal-input-form').val('');
    });

    $('.terminal-logs').click(function(){
        if (!window.pauseTerminal){
            window.pauseTerminal = true;
            toastr.info("Clicked in the terminal, log paused.");
        }
    });

    setInterval(loadConsoleText, 1000);
    loadConsoleText();

    setInterval(loadNav, 3000);
    loadNav();
});

function loadNav() {
    // Don't waste resources if tab is not in view
    if (document.visibilityState === "hidden") {
        return;
    }

    $.getJSON('/api/get_containers', function(containers) {
        window.containerData = containers;

        var text = "";
        /*
         * Generate a string of HTML buttons for each container. Each button has a
         * click event that triggers setSelectedContainer. This function is called
         * on an interval, to periodically refresh the navbar if new containers are
         * created. Where possible, the color of the status dot is re-used so that
         * the status color dots don't flicker. After adding all the buttons, the
         * status dots are updated asynchronously.
         */
        // $.each(containers, function(containerId, containerInfo) {
        for (var containerId in containers) {
            var containerName = containers[containerId].name;
            var containerState = containers[containerId].state;
            var statusClass = containerState == "running" ? "status-online" : "status-offline";
            text += '<button type="button" class="list-group-item list-group-item-action"';
            text += 'id="nav-container-' + containerId + '" ';
            text += 'onclick="setSelectedContainer(&quot;' + containerId + '&quot;, &quot;' + containerName + '&quot;)">' + containerName;
            text += '<span style="float: right;"><div class="circle ' + statusClass + '" ';
            text += 'id="nav-container-' + containerId + '-status"></div></span>';
            text += '</button>\n'
        };

        $('#collapseContainerSpoiler > .list-group').html(text);
        setNavActive();
    });
}

function loadConsoleText() {
    // Don't waste resources if tab is not in view
    if (document.visibilityState === "hidden") {
        return;
    }

    /*
     * If no container is selected, clear the terminal and set the container status
     * dot to offline. This means that if a user switches to a different page and
     * then decides to view a different container, they won't briefly see the
     * terminal output of the first container.
     */
    if (!window.selectedContainerId) {
        $('#active-status-indicator').removeClass('status-online').addClass('status-offline');
        $('.terminal-logs').text('');
        return;
    }

    var statusClass = window.containerData[window.selectedContainerId].state == "running" ? "status-online" : "status-offline";
    $('#active-status-indicator').removeClass('status-offline status-online').addClass(statusClass);

    /*
     * If terminal output is paused, send a message. This message is shown
     * indefinitely. When clicked, terminal output will resume.
     */
    if (window.pauseTerminal){
        toastr.warning('Console output paused. Click to resume', '',
            {
                onclick: function() {
                    toastr.success('Console output resumed');
                    window.pauseTerminal = false;

                },
                preventDuplicates: true,
                timeOut: 0,
            }
        );
        return;
    }

    /*
    * Before loading new text into the terminal, check if the user is scrolled
    * to the bottom. If they are, after the text is loaded, scroll to
    * the bottom again so div stays scrolled to the bottom like terminals
    * should. If the user has intentionally scrolled up to have a look
    * at something, the scroll position is not modified.
    */
    const term = document.getElementsByClassName("terminal-logs")[0];
    const isScrolledToBottom = term.scrollHeight - term.clientHeight <= term.scrollTop + 1;
    $('.terminal-logs').load('/api/get_container_logs?id=' + window.selectedContainerId, null, function(){
        if (isScrolledToBottom) {
            term.scrollTop = term.scrollHeight;
        }
    });
}

function setSelectedContainer(id, name) {
    /*
     * Change the selected container. This function is called when a user clicks on
     * an entry in the container nav. Sets window.selectedContainerId, updates
     * 'active' classes for navbar entries and unpauses the terminal if it was
     * paused.
     */
    window.selectedContainerId = id;
    $('main').css('display', 'initial');
    $('#information').css('display', 'none');
    $('#serverTitle').text(" - " + name);
    if (window.pauseTerminal){
        window.pauseTerminal = false;
        toastr.clear();
    }
    setNavActive();
    loadConsoleText();
}

function setNavActive(){
    $('.list-group button').removeClass("active");
    if (window.selectedContainerId){
        $('#nav-container-' + window.selectedContainerId).addClass("active");
    }
}

function sendConsoleCommand(command){
    var params = {
        id: window.selectedContainerId,
        command: command
    };

    $.get('/api/send_command', params, function(text) {
        if (text == "ok") {
            toastr.success("Command sent");
        } else if (text == "offline") {
            toastr.warning("Cannot send command, the container is offline.");
        } else if (text == "disabled") {
            toastr.warning("Command sending is disabled");
        } else if (text == "timeout") {
            toastr.error("Request timed out: did not receive a response from docker in time.");
        } else {
            toastr.error("Error occured while sending command");
            console.log("Response: '" + text + "'")
        }

        setTimeout(loadConsoleText, 100)
    }, "text");
}
