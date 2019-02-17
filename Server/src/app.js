require('@tensorflow/tfjs-node');
const express = require('express');
const bodyParser = require('body-parser');
const tf = require('@tensorflow/tfjs-node');
const cors = require('cors');
const { BigQuery } = require('@google-cloud/bigquery');
const moment = require('moment-timezone');

const wrap = require('./helpers/AsyncMiddleware');

const bigquery = new BigQuery({
    keyFilename: './bq-key.json',
    projectId: 'calgaryhacks',
});

const app = express();
app.use(cors());
app.use(bodyParser.json());

const STORAGE_PREFIX = 'https://storage.googleapis.com/calgaryhacks.appspot.com/keras/';
const STORAGE_POSTFIX = '-2016/model.json';

app.use('/predict', wrap( async (req, res, next) => {
    if (!req.body.date || !req.body.interval) {
        return res.status(400).send(`Missing params. Date: ${String(req.body.date)}, Interval: ${String(req.body.interval)}`);
    }

    let start = moment.tz(req.body.date, 'YYYY-MM-DD', 'America/Edmonton');
    const interval = req.body.interval.toLowerCase();
    const end = moment.tz(start, 'America/Edmonton').add(1, interval);

    console.debug('Predict Body', start.toString(), end.toString(), interval);

    const now = Date.now();
    const data = [];
    while (start.valueOf() <= end.valueOf()) {
        const monthString = start.format('MMM').toLowerCase();
        const dateString = start.format('YYYY-MM-DD');
        const hourNum = start.format('H');
        const monthNum = start.format('M');

        const pred = await tf.loadLayersModel(`${STORAGE_PREFIX}${monthString}${STORAGE_POSTFIX}`);
        const query = `SELECT temp FROM \`calgaryhacks.temps.temps\` WHERE date = \'${dateString}\' AND hour = ${hourNum} AND month = ${monthNum}`;
        console.debug('Predict Query:', query);

        const options = {
            query: query,
            location: 'US',
        };

        const [job] = await bigquery.createQueryJob(options);
        const [rows] = await job.getQueryResults();

        if (rows.length > 0) {
            const temp = rows[0].temp;
            const tensor = tf.tensor([[Number(hourNum), temp]]);
            const result = await pred.predict(tensor).data();

            if (result.length > 0) {
                data.push({
                    unix: start.valueOf(),
                    label: start.format('MMM D, YYYY ha'),
                    temp,
                    value: result[0],
                });
            }
        }
        start = moment.tz(start, 'America/Edmonton').add(1, 'hour');
    }

    console.debug('Predict time taken: ', (Date.now() - now)/1000, 'seconds', data.length);
    return res.status(200).send(data);
}));

app.use('/historic', wrap( async (req, res, next) => {
    if (!req.body.date || !req.body.interval) {
        return res.status(400).send(`Missing params. Date: ${String(req.body.date)}, Interval: ${String(req.body.interval)}`);
    }

    let start = moment.tz(req.body.date, 'YYYY-MM-DD', 'America/Edmonton');
    const interval = req.body.interval.toLowerCase();
    const end = moment.tz(start, 'America/Edmonton').add(1, interval);

    console.debug('Historic Body', start.toString(), end.toString(), interval);
    const now = Date.now();
    let dateString = '';

    while (start.valueOf() <= end.valueOf()) {
        if (dateString.length === 0) {
            dateString = `date like \'%${start.format('MM-DD')}%\'`;
        } else {
            dateString = `${dateString} OR date like \'%${start.format('MM-DD')}%\'`;
        }

        start = moment.tz(start, 'America/Edmonton').add(1, 'day');
    }

    const query = `SELECT * FROM \`calgaryhacks.2016_usage.2016_usage\` WHERE ${dateString}`;
    const options = {
        query: query,
        location: 'US',
    };
    console.debug('Historic Query:', query);

    const [job] = await bigquery.createQueryJob(options);
    const [rows] = await job.getQueryResults();

    const data = [];
    let monthString = '';
    let pred;
    for (let i = 0; i < rows.length; i++) {
        const row = rows[i];
        const newMonth = row.date.split('-')[1];
        const formattedMonth = moment.tz(newMonth, 'MM', 'America/Edmonton').format('MMM').toLowerCase();

        if ((monthString.length === 0 || newMonth !== monthString) && newMonth.length > 0) {
            monthString = newMonth;
            pred = await tf.loadLayersModel(`${STORAGE_PREFIX}${formattedMonth}${STORAGE_POSTFIX}`);
        }

        const tensor = tf.tensor([[Number(row.hour), row.temp]]);
        const result = await pred.predict(tensor).data();

        if (result.length > 0) {
            data.push({
                unix: start.valueOf(),
                label: start.format('MMM D, YYYY ha'),
                temp: row.temp,
                predict: result[0],
                actual: row.usage,
            });
        }
    }

    console.debug('Historic time taken: ', (Date.now() - now)/1000, 'seconds', data.length);
    return res.status(200).send(data);
}));

app.use((error, req, res, next) => {
    return res.status(500).send(JSON.stringify(error));
});

const PORT = process.env.PORT || 3000;
const server = app.listen(PORT);

server.setTimeout(1200000);

module.exports.app = app;
