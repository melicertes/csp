var fs = require('fs');
var https = require('https');

var options = {
    hostname: 'localhost',
    port: 8081,
    path: '/tc/212',
    method: 'GET',
    key: fs.readFileSync('/data/common/sslcert2/client.key'),
    cert: fs.readFileSync('/data/common/sslcert2/client.crt'),
    ca: fs.readFileSync('/data/common/sslcert2/ca.crt') };

var req = https.request(options, function(res) {
    res.on('data', function(data) {
        process.stdout.write(data);
    });
});

req.end();

req.on('error', function(e) {
    console.error(e);
});