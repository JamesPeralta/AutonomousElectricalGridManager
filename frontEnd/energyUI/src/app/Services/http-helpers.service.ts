import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HttpHelpersService {
  private readonly BASEURL = 'https://calgaryhacks.appspot.com/';
  constructor(private _http: HttpClient) { }

  getData(type: 'predict' | 'historic', body: Object): Observable<any> {
    const headers = {};
    headers['Content-Type'] = 'application/json';

    return this._http.post(`${this.BASEURL}${type}`, body, {headers});
  }
}
