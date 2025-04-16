import { Component } from '@angular/core';
import {MatButton} from '@angular/material/button';
import {faEnvelope} from '@fortawesome/free-solid-svg-icons';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {FormsModule} from '@angular/forms';


@Component({
  selector: 'app-login',
  imports: [
    MatButton,
    FaIconComponent,
    FormsModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  protected readonly faEnvelope = faEnvelope;
  protected password: string = '';
}
