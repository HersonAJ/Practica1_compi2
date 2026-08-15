import { AfterViewInit, Component, signal, ViewChild } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { EditorComponent } from './components/editor-component/editor-component';
import { CommonModule } from '@angular/common';
import { TraduccionComponent } from './components/traduccion/traduccion';
import { HeaderComponent } from './components/header/header';

@Component({
  selector: 'app-root',
  imports: [EditorComponent, CommonModule,TraduccionComponent, HeaderComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements AfterViewInit {

  @ViewChild('editor') editor!: EditorComponent;
  @ViewChild('header') header!: HeaderComponent;

  protected readonly title = signal('pig-latin');

  ngAfterViewInit() {
    this.header.setEditor(this.editor);
  }
}
