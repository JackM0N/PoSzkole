import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import 'moment/locale/pl'
import moment from 'moment';

moment.locale('pl')

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));
