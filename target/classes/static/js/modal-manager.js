function focusOnInput($modalId, $inputId) {
    $modalId.on('shown.bs.modal', function () {
        $inputId.trigger('focus');
    });
}

function closeWhenConfirm($modalId, $confirmButtonId) {
    $confirmButtonId.click(function () {
        $modalId.modal('dispose');
    });
}

function loadDefaultBehaviour($modalId, $inputId, $confirmButtonId) {
    focusOnInput($modalId, $inputId);
    // closeWhenConfirm($modalId, $confirmButtonId);
}


