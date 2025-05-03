import { Component } from '@angular/core';
import {MatButton} from '@angular/material/button';
import {FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {AuthLayoutComponent} from '../../shared/auth-layout/auth-layout.component';
import {LoginService} from '../service/login.service';


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
    AuthLayoutComponent,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private authService: LoginService) {
    this.loginForm = this.fb.group({
      identifier: ['', [Validators.required]],
      password: ['', [Validators.required]],
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const { identifier, password } = this.loginForm.value;

      this.authService.login(identifier, password).subscribe({
        next: token => {
          console.log('login success', token);
        },
        error: error => {
          console.log('login failed', error);
        }
      })
    }
  }
}
