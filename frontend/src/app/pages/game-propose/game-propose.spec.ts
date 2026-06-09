import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { GameProposeComponent } from './game-propose';

describe('GamePropose', () => {
  let component: GameProposeComponent;
  let fixture: ComponentFixture<GameProposeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameProposeComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GameProposeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
