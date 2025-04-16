import {Component, OnInit} from '@angular/core';
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton} from '@angular/material/button';
import {RouterLink} from '@angular/router';
import {MatCheckbox} from '@angular/material/checkbox';
import {MatIcon} from '@angular/material/icon';
import {AuthLayoutComponent} from '../shared/auth-layout/auth-layout.component';

@Component({
  selector: 'app-register',
  imports: [
    MatButton,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    RouterLink,
    MatIcon,
    MatCheckbox,
    MatIcon,
    AuthLayoutComponent
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {

  registerForm!: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required],
      termsAccepted: [false, Validators.requiredTrue]
    });
  }

  onRegister(): void {
    if (this.registerForm.valid) {
      // handle registration logic here
      console.log(this.registerForm.value);
    }
  }
}
