import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'user', loadChildren: () =>
      import('./user/user.module').then(m => m.UserModule)
  },
  {
    path: 'videos', loadChildren: () =>
      import('./videos/videos.module').then(m => m.VideosModule)
  },
  {
    path: 'tweets', loadChildren: () =>
      import('./tweets/tweets.module').then(m => m.TweetsModule)
  },
  {
    path: '',
    redirectTo: '/videos',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/videos' // create a 404 component later
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
