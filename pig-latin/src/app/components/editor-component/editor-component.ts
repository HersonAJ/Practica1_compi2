import { Component, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-editor',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './editor-component.html',
  styleUrls: ['./editor-component.scss']
})
export class EditorComponent implements AfterViewInit {

  @ViewChild('codeTextarea') textarea!: ElementRef<HTMLTextAreaElement>;
  @ViewChild('lineNumbersContainer') lineNumbersContainer!: ElementRef<HTMLDivElement>;

  code: string = '';
  lineNumbers: number[] = [1];

  ngAfterViewInit() {
    this.syncScroll();
  }

  onCodeChange(event: Event) {
    const textarea = event.target as HTMLTextAreaElement;
    this.code = textarea.value;
    this.updateLineNumbers();

    setTimeout(() => {
      this.syncScroll();
    }, 0);
  }

  updateLineNumbers() {
    const lines = this.code.split('\n').length;
    this.lineNumbers = Array.from({ length: lines }, (_, i) => i + 1);
  }

  loadContent(content: string) {
    this.code = content;
    this.updateLineNumbers();
    setTimeout(() => {
      if (this.textarea) {
        this.textarea.nativeElement.scrollTop = 0;
        this.syncScroll();
      }
    }, 0);
  }

  getCode(): string {
    return this.code;
  }

  onScroll() {
    this.syncScroll();
  }

  private syncScroll() {
    if (this.textarea && this.lineNumbersContainer) {
      this.lineNumbersContainer.nativeElement.scrollTop = this.textarea.nativeElement.scrollTop;
    }
  }
}