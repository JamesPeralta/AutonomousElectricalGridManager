console.log('Starting app.js ...');

const express = require('express');
const tsflow = require('@tensorflow/tfjs');
const cors = require('cors');

console.log('Hello world');

const app = express();
app.use(cors());

app.get('/', function(req, res){
    res.send('hello world');
});

const PORT = process.env.PORT || 3000;
app.listen(PORT);

module.exports.app = app;
