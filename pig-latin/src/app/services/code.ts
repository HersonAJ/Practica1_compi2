import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CodeService {
  private codeSubject = new BehaviorSubject<string>('');
  code$ = this.codeSubject.asObservable();

  private highlightedSubject = new BehaviorSubject<string>('');
  highlighted$ = this.highlightedSubject.asObservable();

  setCode(code: string) {
    this.codeSubject.next(code);
  }

  setHighlighted(html: string) {
    this.highlightedSubject.next(html);
  }

  getCode(): string {
    return this.codeSubject.value;
  }
}