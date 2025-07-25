import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddTweetComponent } from './add-tweet.component';

describe('AddTweetComponent', () => {
  let component: AddTweetComponent;
  let fixture: ComponentFixture<AddTweetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddTweetComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddTweetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
