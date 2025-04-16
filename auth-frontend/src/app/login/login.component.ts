import { Component } from '@angular/core';
import {MatButton} from '@angular/material/button';
import {FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';


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
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  loginForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      console.log('Form data:', this.loginForm.value);
    }
  }
}
