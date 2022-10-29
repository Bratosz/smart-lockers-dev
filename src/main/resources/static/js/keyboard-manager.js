let backSlash = 191,
    ctrl = 17,
    enter = 13,
    keyCodesOfNumberKeys = [48, 49, 50, 51, 52, 53, 54, 55, 56, 57];


function loadGeneralKeyBehaviour() {
    ctrlIsPressed();
}

function loadNavKeysBehaviour() {
    pressedCtrlBackSlashGoToSearchWindow();
    enterKeyBehaviour();
}

function pressedCtrlBackSlashGoToSearchWindow() {
    ifKeyClickedWithCtrlAtThenGoTo(
        backSlash,
        $(document),
        '#nav-input-last-name');
}

function loadBoxByCtrlPlusNumber() {
    $(document).bind('keydown', function(e) {
        let $table = $('#table-of-boxes');
        if($table.length > 0) {
            let keyIndex = $.inArray(e.keyCode, keyCodesOfNumberKeys);
            if(e.ctrlKey && keyIndex > 0) {
                let counter = 0;
                $table.find('tbody > tr').each(function () {
                    if(counter == keyIndex)
                        $(this).find('td > .button-view-employee').trigger('click');
                    counter++;
                });
            }
        }
    });
}

function enterKeyBehaviour() {
    ifKeyClickedAtThenDo(enter, $('#nav-input-last-name'), function () {
        $('#nav-button-search-by-last-name').trigger('click');
    });
}

function ifKeyClickedWithCtrlAtThenGoTo(keyCode, $clickedArea, elementToFocus) {
    $clickedArea.keydown(function (e) {
        if (e.keyCode == keyCode && ctrlPressed) {
            $(elementToFocus).select();
        }
    });
}

function ifKeyClickedAtThenDo(keyCode, $where, desiredAction) {
    $where.keydown(function (e) {
        if (e.keyCode == keyCode && !ctrlPressed) {
            e.preventDefault();
            desiredAction();
        }
    });
}

function ifKeyClickedWithCtrlThenDo(keyCode, $where, action) {
    $where.keydown(function (e) {
        if (e.keyCode == keyCode && ctrlIsPressed) {
            e.preventDefault();
            action();
        }
    })
}

function ifOneOfKeysClickedWithCtrlThenDo(keyCodes, $where, action) {
    $where.keydown(function (e) {
        console.log(e.keyCode);
        let keyIndex = $.inArray(e.keyCode, keyCodes);
        if(keyIndex > 0 && ctrlIsPressed()) {
            e.preventDefault();
            action(keyIndex);
        }
    })
}

function ctrlIsPressed() {
    $(document).keydown(function (e) {
        if (e.ctrlKey) ctrlPressed = true;
    }).keyup(function (e) {
        if (e.keyCode == ctrl) {
            ctrlPressed = false;
        }
    })
}