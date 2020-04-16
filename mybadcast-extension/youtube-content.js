console.log('youtube-content.js');

let previousPush = {videoid: undefined, currentTime: undefined};

function getUrlVars() {
    const vars = {};
    window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, (m, key, value) => vars[key] = value);
    return vars;
}

function listCurrentStatePeriodically() {
    setTimeout(listCurrentStatePeriodically, 1000);
    listCurrentState();
}

function pushEvent(type, videoid, currentTime) {
    chrome.storage.local.get('mybadcast_userid', function (result) {
        let userid = result.mybadcast_userid;
        console.log('>> userid: ' + userid + ', type: ' + type + ', videoid: ' + videoid + ', currentTime: ' + currentTime);

        let xhr = new XMLHttpRequest();
        xhr.open("POST", 'https://mybadcast-server-prod.scapp.io/api/extension/v1/push', true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE) {
                if (this.status === 200) {
                    console.debug('Ok');
                } else {
                    console.log('Failed to push message, status: ' + this.status);
                }
            }
        }
        let body = {
            userid: userid,
            type: type,
            videoid: videoid,
            currentTime: parseInt(currentTime)
        };

        let bodyString = JSON.stringify(body);
        xhr.send(bodyString);
    });
}

function listCurrentState() {
    let videoStream = document.getElementsByTagName('video')[0];
    if (videoStream === undefined) {
        console.debug('video is not initialized yet');
        return;
    }

    let videoid = getUrlVars()['v']
    let currentTime = videoStream.currentTime;

    let previousVideoid = previousPush.videoid;
    let previousCurrentTime = previousPush.currentTime;
    previousPush.videoid = videoid;
    previousPush.currentTime = currentTime;

    let newVideo = previousVideoid !== videoid;
    if (newVideo) {
        pushEvent('CHANGE', videoid, currentTime);
        return;
    }

    let paused = currentTime === previousCurrentTime;
    if (paused) {
        pushEvent('PAUSE', videoid, currentTime);
        return;
    }

    let rewind = currentTime < previousCurrentTime;
    if (rewind) {
        pushEvent('REWIND', videoid, currentTime);
        return;
    }

    let fastForward = currentTime - 5 > previousCurrentTime;
    if (fastForward) {
        pushEvent('FAST_FORWARD', videoid, currentTime);
        return;
    }

    pushEvent('PLAYING', videoid, currentTime);
}

listCurrentStatePeriodically();
