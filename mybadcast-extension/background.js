document.addEventListener('DOMContentLoaded', function () {
    console.log('background.js');

    function uuid(length) {
        let result = '';
        const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        const charactersLength = characters.length;
        for (let i = 0; i < length; i++) {
            result += characters.charAt(Math.floor(Math.random() * charactersLength));
        }
        return result;
    }

    function createUniqueIdIfNecessary() {
        chrome.storage.local.get('mybadcast_userid', function (result) {
            let myBadCastUserid = result.mybadcast_userid;
            if (myBadCastUserid === undefined) {
                myBadCastUserid = uuid(16);
                chrome.storage.local.set({'mybadcast_userid': myBadCastUserid});
                console.log('mybadcast_userid set to: ' + myBadCastUserid);
            }
            console.log('mybadcast_userid: ' + myBadCastUserid);
        });
    }

    createUniqueIdIfNecessary();

    //listOpenTabsPeriodically();

    function listOpenTabsPeriodically() {
        listOpenTabs();
        setTimeout(listOpenTabsPeriodically, 3000);
    }

    function listOpenTabs() {
        chrome.windows.getAll({populate: true}, getAllOpenWindows);
    }

    function getAllOpenWindows(windows) {
        let youtubeTab;
        for (let i = 0; i < windows.length; ++i) {
            let tabs = windows[i].tabs;
            for (let j = 0; j < tabs.length; ++j) {
                let tab = tabs[j];
                let url = tab.url;
                if (url.startsWith('https://www.youtube.com/watch?v=')) {
                    youtubeTab = tab;
                    break;
                }
            }
        }

        console.log('Current yt video: ' + youtubeTab.url);
    }
}, false);