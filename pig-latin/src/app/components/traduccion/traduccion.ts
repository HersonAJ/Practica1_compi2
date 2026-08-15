import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-traduccion',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './traduccion.html',
  styleUrls: ['./traduccion.scss']
})
export class TraduccionComponent {

  translatedCode: string = `// La traducción aparecerá aquí
// cuando el código compile correctamente

// Ejemplo:
// esto x : numerus 10;
// → esto xay : umerusnay 10;`;

  hasTranslation: boolean = false;
}