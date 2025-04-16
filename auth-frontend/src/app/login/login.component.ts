import { Component } from '@angular/core';
import {MatAnchor, MatButton} from '@angular/material/button';
import {faEnvelope} from '@fortawesome/free-solid-svg-icons';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {FormsModule, ReactiveFormsModule, FormControl, FormGroup} from '@angular/forms';
import {MatCheckbox} from '@angular/material/checkbox';
import {RouterLink} from '@angular/router';


@Component({
  selector: 'app-login',
  imports: [
    MatButton,
    FaIconComponent,
    FormsModule,
    MatCheckbox,
    ReactiveFormsModule,
    RouterLink,
    MatAnchor,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  protected readonly faEnvelope = faEnvelope;
  protected password: string = '';
  protected rememberMe: boolean = false;

  form = new FormGroup({
    rememberMe: new FormControl(false)
  });

  get rememberMeControl(): FormControl {
    return this.form.get('rememberMe') as FormControl;
  }

}
