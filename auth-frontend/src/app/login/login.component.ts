import { Component } from '@angular/core';
import {MatButton} from '@angular/material/button';
import {FormsModule, ReactiveFormsModule, FormGroup, FormBuilder} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {MatIcon} from '@angular/material/icon';


@Component({
  selector: 'app-login',
  imports: [
    MatButton,
    FormsModule,
    ReactiveFormsModule,
    RouterLink,
    MatFormField,
    MatLabel,
    MatFormField,
    MatInput,
    MatFormField,
    MatIcon,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  loginForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.loginForm = this.fb.group({
      email: [''],
      password: [''],
    });
  }

  onSubmit() {
    const formData = this.loginForm.value;
    console.log('Form data:', formData);
  }
}
