var express  = require('express');
var app      = express();
var httpProxy = require('http-proxy');
var apiProxy = httpProxy.createProxyServer();

const API_PORT = process.env.API_PORT || 800;

var api_target = 'http://csp-misp:' + API_PORT;

app.all("/*", function(req, res) {

    if (req.headers['accept'] === 'application/json') {

        console.log('redirecting to: ' + api_target);

        apiProxy.web(req, res, {
            target: api_target,
            secure: false,
            changeOrigin: true,
        }, function (e) {
            console.log(e.message);
            return res.status(500).send({
                error: true,
                message: e.message
            });
        });
    } else {
        console.log('Operation not permitted for ' + req.headers['accept']);
        return res.status(400).send({
            error: true,
            message: 'cannot serve request'
        });
    }

});

app.listen(3000);