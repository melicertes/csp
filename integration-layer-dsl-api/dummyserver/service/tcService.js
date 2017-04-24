'user strict';

exports.getCsps = function (req, res) {
    console.log("tc post got it!");
    //res.send(err);
    //res.json({cspId:['http://ex.csp1.com']});
    res.json({id:1,teams:[1,2]});
    //res.send('Successful creation');
};
