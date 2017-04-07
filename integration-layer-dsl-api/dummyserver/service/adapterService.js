'user strict';

exports.create = function (req, res) {
    //res.send(err);
    //res.json({message:'Successful creation'});
    res.send('Successful creation');
};

exports.update = function (req, res) {
    //res.json({message:'Successful update'});
    res.send('Successful update');
};

exports.delete = function (req, res) {
    res.json({message:'Successful delete'});
    res.send('Successful delete');
};