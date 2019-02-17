console.log('Starting app.js ...');

const express = require('express');
const tf = require('@tensorflow/tfjs');
global.fetch = require('node-fetch');
const cors = require('cors');

const wrap = require('./helpers/AsyncMiddleware');

const PREDICTIONS = require('./tensorflow/predictions');

console.log('Hello world');

const app = express();
app.use(cors());

app.use('/', wrap( async () => {
    const pred = await PREDICTIONS.getPredictions();
    console.log(pred);
    const tensor = tf.tensor([2010, 1, 1, 0]).print();
    console.log(tensor);
    const result = pred.predict(tensor);
}));

app.get('/', function(req, res) {
    res.send('hello world');

});


app.get('/', function(req, res){
    res.send(example);
});
const PORT = process.env.PORT || 3000;
app.listen(PORT);

module.exports.app = app;
