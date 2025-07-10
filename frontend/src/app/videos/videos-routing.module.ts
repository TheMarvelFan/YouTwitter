import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { VideosComponent } from './videos.component';
import {WatchVideoComponent} from './watch-video/watch-video.component';

const routes: Routes = [
  { path: '', component: VideosComponent },
  { path: ':id', component: WatchVideoComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class VideosRoutingModule { }
