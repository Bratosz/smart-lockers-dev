$('#button-start-scan').click(function () {
    Html5Qrcode.getCameras().then(devices => {
        if(devices && devices.length) {
            cameraId = devices[1].id;
            console.log(cameraId);
        }
    }).catch(err => {
        console.log(err);
    }).then(() => {
        scanner = new Html5Qrcode(
            "reader");
        scanner.start(
            cameraId,
            config,
            qrCodeMessage => {
                alert(qrCodeMessage);
            },
            errorMessage => {
                // console.log("QR code no longer in front of camera.")
            })
            .catch(err => {
                console.log(err);
            });
    });
});

$('#button-stop-scan').click(function () {
    scanner.stop().then(ignore => {
        console.log("QR Code scanning stopped.");
    }).catch(err => {
        console.log("Unable to stop scanning.");
    })
});

