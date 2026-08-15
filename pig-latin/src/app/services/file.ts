import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class FileService {

    // Abrir archivo .lat
  openFile(): Promise<string> {
    return new Promise((resolve, reject) => {
      const input = document.createElement('input');
      input.type = 'file';
      input.accept = '.lat';

      input.onchange = (event: Event) => {
        const target = event.target as HTMLInputElement;
        const file = target.files?.[0];

        if (!file) {
          reject('No se seleccionó ningún archivo');
          return;
        }

        const reader = new FileReader();
        reader.onload = (e) => {
          const content = e.target?.result as string;
          resolve(content);
        };
        reader.onerror = () => {
          reject('Error al leer el archivo');
        };
        reader.readAsText(file);
      };

      input.click();
    });
  }

  // Guardar archivo .lat
  saveFile(content: string, filename: string = 'codigo.lat') {
    const blob = new Blob([content], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    window.URL.revokeObjectURL(url);
  }

  // Descargar archivo .pig
  downloadPig(content: string, filename: string = 'codigo.pig') {
    const blob = new Blob([content], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    window.URL.revokeObjectURL(url);
  }
  
}
