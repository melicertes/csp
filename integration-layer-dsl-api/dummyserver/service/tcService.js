'user strict';

exports.getCsps = function (req, res) {
    console.log("tc post got it!");
    //res.send(err);
    res.json({cspId:['http://ex.csp1.com']});
    //res.send('Successful creation');
};
