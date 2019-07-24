var express  = require('express');
var app      = express();
var httpProxy = require('http-proxy');
var apiProxy = httpProxy.createProxyServer();

const HTML_PORT = process.env.HTML_PORT || 3000;
const API_PORT = process.env.API_PORT || 3001;
const MISP_BASEURL = process.env.MISP_LOCAL_DOMAIN || 'localhost';

var api_target = 'https://' + MISP_BASEURL +':' + API_PORT;
var html_target = 'https://' + MISP_BASEURL +':' + HTML_PORT;


app.all("/*", function(req, res) {

    console.log(req.headers)

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

        console.log('redirecting to: ' + html_target);
        apiProxy.web(req, res, {
            target: html_target,
            secure: false,
            changeOrigin: true,
        }, function (e) {
            console.log(e.message);
            return res.status(500).send({
                error: true,
                message: e.message
            });
        });
    }

});

app.listen(3000);