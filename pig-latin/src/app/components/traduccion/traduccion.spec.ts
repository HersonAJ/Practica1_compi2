import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Traduccion } from './traduccion';

describe('Traduccion', () => {
  let component: Traduccion;
  let fixture: ComponentFixture<Traduccion>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Traduccion]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Traduccion);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
