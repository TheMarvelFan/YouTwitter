import { InjectionToken } from '@angular/core';
import { AppConfig } from '../models/app.config';
import { environment } from '../../environments/environment.development';

export const APP_SERVICE_CONFIG = new InjectionToken<AppConfig>('app.service.config');

export const APP_CONFIG: AppConfig = {
  apiUrl: environment.apiUrl
}
