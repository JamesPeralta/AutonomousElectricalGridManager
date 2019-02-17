const tf = require('@tensorflow/tfjs');

exports.getPredictions = () => {
    console.log('called');
    return tf.loadModel('https://storage.googleapis.com/calgaryhacks.appspot.com/model.json');
};
