const version = "1.0.1";
const cacheName = `smart-lockers-${version}`;
self.addEventListener('install', function (e) {
    e.waitUntil(
        caches.open(cacheName).then(function (cache) {
            return cache.addAll([
                //HTML
                'add-clothes.html',
                'add-employee.html',
                'add-lockers.html',
                'add-position.html',
                'articles.html',
                'clothing-acceptance.html',
                'create-client.html',
                'create-client-from-file.html',
                'edit-departments.html',
                'edit-employee.html',
                'employee-relocate.html',
                'empty.html',
                'index.html',
                'kls_logo.jpg',
                'manage-lockers.html',
                'manage-plant.html',
                'management-list.html',
                'measurement-list.html',
                'navbar.html',
                'qr-reader.html',
                'orders.html',
                'view-boxes.html',
                'view-boxes-new-window.html',
                'view-employee.html',
                'view-locker.html',
                'view-lockers.html',
                'view-positions.html',
                //CSS
                '/styles/load-client.css',
                '/styles/measurement-list.css',
                '/styles/style.css',
                '/styles/style-add-client.css',
                '/styles/style-add-clothes.css',
                '/styles/style-add-position.css',
                //JS
                '/js/libs/html5-qrcode.min.js',
                '/js/libs/jquery-3.3.1.min.js',
                '/js/add-clothes.js',
                '/js/add-employee.js',
                '/js/add-lockers.js',
                '/js/add-position.js',
                '/js/article-manager.js',
                '/js/articles.js',
                '/js/boxes-manager.js',
                '/js/cloth-manager.js',
                '/js/clothing-acceptance.js',
                '/js/create-client.js',
                '/js/create-client-from-file.js',
                '/js/edit-departments.js',
                '/js/edit-employee.js',
                '/js/employee-relocate.js',

                '/js/employees-manager.js',
                '/js/end-points.js',
                '/js/load-client.js',
                '/js/manage-lockers.js',
                '/js/manage-plant.js',
                '/js/management-list.js',
                '/js/measurement-list.js',
                '/js/order-manager.js',
                '/js/qr-code-reader.js',
                '/js/orders.js',
                '/js/row-writer.js',
                '/js/smart-lockers.js',
                '/js/table-manager.js',
                '/js/utils.js',
                '/js/view-boxes.js',
                '/js/view-boxes-methods.js',
                '/js/view-employee.js',
                '/js/view-locker.js',
                '/js/view-lockers.js',
                '/js/view-positions.js',
                //BOOTSTRAP
                '/bootstrap/css/bootstrap.min.css',
                '/bootstrap/js/popper.min.js',
                '/bootstrap/js/bootstrap.min.js',
            ])
        })
    );
});

self.addEventListener('fetch', function (event) {
    // console.log(event.request.url);

    if(navigator.onLine) {
        caches.open('v1').then(function (cache) {
            // console.log("internet");
            return cache.add(event.request);
        })
    } else {
        event.respondWith(
            caches.match(event.request).then(function (response) {
                // console.log("kesz");
                return response;
            })
        );
    }
});


