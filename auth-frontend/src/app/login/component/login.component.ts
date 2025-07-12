import { Component } from '@angular/core';
import {MatButton} from '@angular/material/button';
import {FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {AuthLayoutComponent} from '../../shared/auth-layout/auth-layout.component';
import {LoginService} from '../service/login.service';
import {MessageDialogComponent} from '../../shared/message-dialog/message-dialog.component';
import {MatDialog} from '@angular/material/dialog';


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

  constructor(private fb: FormBuilder,
              private loginService: LoginService,
              private dialog: MatDialog,
              private router: Router) {
    this.loginForm = this.fb.group({
      identifier: ['', [Validators.required]],
      password: ['', [Validators.required]],
    });
  }

  onLogin() {
    if (this.loginForm.valid) {
      const { identifier: emailOrUsername, password } = this.loginForm.value;

      this.loginService.login(emailOrUsername, password).subscribe({
        next: token => {
          console.log('login success', token);
          this.router.navigate(['/dashboard']).then(success => {
            console.log(success ? 'Navigate to dashboard successfully!' : 'Navigate to dashboard failed.');
          });
        },
        error: error => {
          this.openDialog('Failed to login', 'Login failed please try again.');
          console.log('login failed', error);
        }
      })
    }
  }

  openDialog(title: string, message: string): void {
    this.dialog.open(MessageDialogComponent, {
      data: { title, message }
    });
  }
}
