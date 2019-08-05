var express  = require('express');
var app      = express();
var httpProxy = require('http-proxy');
var apiProxy = httpProxy.createProxyServer();
var fs = require("fs");


const API_PORT = process.env.API_PORT || 800;

var api_target = 'http://csp-misp:' + API_PORT;

app.all("/*", function(req, res) {

    console.log(req.headers);

    if (req.headers['accept'] === 'application/json') {

        if (!req.headers['authorization']){
            return res.status(400).send({
                error: true,
                message: 'Authorization header missing'
            });
        } else {
            fs.readFile("/run/secrets/authkey", function(err, data) {

                if (data.toString().replace(/\n$/, '') != req.headers['authorization']){
                    return res.status(403).send({
                        error: true,
                        message: 'not authorized'
                    });
                }

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
            });
        }
    } else {
        console.log('Operation not permitted for ' + req.headers['accept']);
        return res.status(400).send({
            error: true,
            message: 'cannot serve request'
        });
    }
});

app.listen(3000);

