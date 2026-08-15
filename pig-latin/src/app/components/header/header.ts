import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EditorComponent } from '../editor-component/editor-component';
import { FileService } from '../../services/file';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.html',
  styleUrls: ['./header.scss']
})
export class HeaderComponent {

  private editorComponent!: EditorComponent;

  constructor(private fileService: FileService) {}

    setEditor(editor: EditorComponent) {
    this.editorComponent = editor;
  }

    onNew() {
      if (this.editorComponent) {
        this.editorComponent.loadContent(`// Nuevo archivo Latinus
      // Escribe tu código aquí

      `);
      }
    }

      async onOpen() {
    try {
      const content = await this.fileService.openFile();
      if (this.editorComponent) {
        this.editorComponent.loadContent(content);
      }
    } catch (error) {
      console.error('Error al abrir archivo:', error);
    }
  }

    onSave() {
    if (this.editorComponent) {
      const content = this.editorComponent.getCode();
      this.fileService.saveFile(content);
    }
  }

    onDownload() {
    if (this.editorComponent) {
      const content = this.editorComponent.getCode();
      this.fileService.saveFile(content);
    }
  }


  // Analizar
  onAnalyze() {
    console.log('Analizando código...');
    if (this.editorComponent) {
      const code = this.editorComponent.getCode();
      console.log('Código a analizar:', code);
    }
  }

  
    // Traducir
  onTranslate() {
    console.log('Traduciendo a PigLatin...');
    if (this.editorComponent) {
      const code = this.editorComponent.getCode();
      console.log('Código a traducir:', code);
    }
  }

  // Modales
  onOpenAST() {
    console.log('Abrir AST');
  }

  onOpenSymbolTable() {
    console.log('Abrir Tabla de Símbolos');
  }

  onOpenStack() {
    console.log('Abrir Pila de Llamadas');
  }

  onOpenErrors() {
    console.log('Abrir Errores');
  }
}