import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import { FormControl } from '@angular/forms';
import * as moment from 'moment';
import {HttpHelpersService} from '../Services/http-helpers.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class HomeComponent implements OnInit {
  intervals: string[] = ['Day', 'Week', 'Month', 'Year'];
  minDate: Date = new Date('2016-01-02');
  maxDate: Date = new Date('2019-02-16');
  selectedInterval = '';
  selectedDate = new FormControl('');
  dataSource: Object;
  // graphData: Object[];
  dataset: Object[] = [];
  predictedData: Object[];
  predictedDataSet: Object;
  actualData: Object[];
  actualDataSet: Object;
  category: Object[];
  chartConfig: Object;

  showTrends = false;

  constructor(private _httpHelper: HttpHelpersService) {
    this.chartConfig = {
      width: '100%',
      height: '100%',
      type: 'msline',
      dataFormat: 'json',
    };
  }

  ngOnInit() {
  }

  onChange() {
    const date = moment(this.selectedDate.value).format( 'YYYY-MM-DD');
    // @ts-ignore
    const range = moment(date).add(1 , this.selectedInterval.toLowerCase()).format('YYYY');
    if (this.selectedInterval && this.selectedDate.value) {
      const reqType = range > 2016 ? 'predict' : 'historic';
      const reqBody = {
        date,
        interval: this.selectedInterval,
      };
      this._httpHelper.getData(reqType, reqBody).subscribe((res: any[]) => {
        console.log(res);
        this.category = res.map((el) => {
          return {
            'label': el.label,
          };
        });

        this.predictedData = res.map((el) => {
          return {
            'value': el.predict,
          };
        });

        if (reqType === 'historic') {
          this.actualData = res.map((el) => {
            return {
              'value': el.actual,
            };
          });
          this.actualDataSet = {
            'seriesname': 'Actual Values',
            'data': this.actualData
          };
        }

        this.predictedDataSet = {
          'seriesname': 'Predicted Values',
          'data': this.predictedData
        };

        this.dataset.push(this.predictedDataSet);
        if (this.actualDataSet) {
          this.dataset.push(this.actualDataSet);
        }

        this.drawChart();
        this.showTrends = true;
        setTimeout(() => {
          const htmlDoc = document.getElementById('trend');
          htmlDoc.scrollIntoView({behavior: 'smooth'});
        }, 100);
      });
    }
  }

  drawChart() {
    this.dataSource = {
      'chart': {
        'caption': 'Energy Usage VS Time',
        'yaxisname': 'Energy Usage',
        'numbersuffix':  'MWh',
        'rotatelabels': '1',
        'setadaptiveymin': '1',
        'theme': 'candy'
      },
      'categories': [{
        'category': this.category
      }],
      'dataset': this.dataset,
    };
    console.log(this.dataSource);
  }
}
