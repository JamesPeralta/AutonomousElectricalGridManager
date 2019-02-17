import { TestBed } from '@angular/core/testing';

import { HttpHelpersService } from './http-helpers.service';

describe('HttpHelpersService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: HttpHelpersService = TestBed.get(HttpHelpersService);
    expect(service).toBeTruthy();
  });
});
