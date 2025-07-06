import {Component, OnInit} from '@angular/core';
import {MatError, MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton} from '@angular/material/button';
import {Router, RouterLink} from '@angular/router';
import {MatCheckbox} from '@angular/material/checkbox';
import {MatIcon} from '@angular/material/icon';
import {AuthLayoutComponent} from '../../shared/auth-layout/auth-layout.component';
import {NgIf} from '@angular/common';
import {RegisterService} from '../service/register.service';
import { MatDialog } from '@angular/material/dialog';
import {MessageDialogComponent} from '../../shared/message-dialog/message-dialog.component';

@Component({
  selector: 'app-register',
  imports: [
    MatButton,
    MatFormField,
    MatInput,
    MatLabel,
    MatError,
    ReactiveFormsModule,
    RouterLink,
    MatIcon,
    MatCheckbox,
    MatIcon,
    AuthLayoutComponent,
    NgIf
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit {

  registerForm!: FormGroup;

  constructor(private fb: FormBuilder,
              private registerService: RegisterService,
              private dialog: MatDialog,
              private router: Router) {
  }

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      username: ['', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(30),
        Validators.pattern('^[a-zA-Z][a-zA-Z0-9_.-]{4,29}$')
      ]],
      firstName: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(30),
        Validators.pattern("^[A-Za-z][A-Za-z_\\-\\s]{1,29}$")
      ]],
      lastName: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(30),
        Validators.pattern("^[A-Za-z][A-Za-z_\\-\\s]{1,29}$")
      ]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$')
      ]],
      termsAccepted: [false, Validators.requiredTrue]
    });
  }

  shouldShowError(controlName: string): boolean {
    const control = this.registerForm.get(controlName);
    return !!control && (control.touched || this.submitted) && control.invalid;
  }

  getUsernameError(): string | null {
    const control = this.registerForm.get('username');
    if (control?.hasError('required')) {
      return 'Username is required';
    }
    if (control?.hasError('minlength')) {
      return 'Username must be at least 5 characters';
    }
    if (control?.hasError('maxlength')) {
      return 'Username can\'t exceed 30 characters';
    }
    if (control?.hasError('pattern')) {
      return 'Must start with a letter and contain only letters, numbers, underscores (_), dots (.), or dashes (-)';
    }
    return null;
  }

  getFirstNameError(): string | null {
    const control = this.registerForm.get('firstName');
    if (control?.hasError('required')) {
      return 'First name is required';
    }
    if (control?.hasError('minlength')) {
      return 'First name must be at least 2 characters';
    }
    if (control?.hasError('maxlength')) {
      return 'First name can\'t exceed 30 characters';
    }
    if (control?.hasError('pattern')) {
      return 'Only contain letters, underscores (_), dashes (-), and spaces';
    }
    return null;
  }

  getLastNameError(): string | null {
    const control = this.registerForm.get('lastName');
    if (control?.hasError('required')) {
      return 'Last name is required';
    }
    if (control?.hasError('minlength')) {
      return 'Last name must be at least 2 characters';
    }
    if (control?.hasError('maxlength')) {
      return 'Last name can\'t exceed 30 characters';
    }
    if (control?.hasError('pattern')) {
      return 'Only contain letters, underscores (_), dashes (-), and spaces';
    }
    return null;
  }

  getEmailError(): string | null {
    const control = this.registerForm.get('email');
    if (control?.hasError('required')) {
      return 'Email is required';
    }
    if (control?.hasError('email')) {
      return 'Enter a valid email address';
    }
    return null;
  }

  getPasswordError(): string | null {
    const control = this.registerForm.get('password');
    if (control?.hasError('required')) {
      return 'Password is required';
    }
    if (control?.hasError('minlength')) {
      return 'Password must be at least 8 characters';
    }
    if (control?.hasError('pattern')) {
      return 'Password must include uppercase, lowercase, number, and special character';
    }
    return null;
  }

  getTermsError(): string | null {
    const control = this.registerForm.get('termsAccepted');
    if (control?.hasError('required')) {
      return 'You must agree to the Terms & Conditions';
    }
    if (control?.hasError('requiredTrue')) {
      return 'You must agree to the Terms & Conditions';
    }
    return null;
  }

  submitted = false;

  onRegister(): void {
    this.submitted = true;
    if (this.registerForm.valid) {
      const { username, firstName, lastName, email, password } = this.registerForm.value;

      this.registerService.register(username, firstName, lastName, email, password).subscribe({
        next: (data) => {
          console.log('Registration success', data);
          this.openDialog('Success', 'Account created successfully!', true);
        },
        error: (error) => {
          console.log('Registration error', error);
          this.openDialog('Failed', 'Failed to create an account!', false);
        }
      })
    }
  }

  openDialog(title: string, message: string, navigateOnClose: boolean = false): void {
    const dialogRef = this.dialog.open(MessageDialogComponent, {
      data: { title, message}
    });

    dialogRef.afterClosed().subscribe(() => {
      this.router.navigate(['/login']).then(success => {
        if (success) {
          console.log('Navigate to login successfully!');
        } else {
          console.log('Navigate to login failed.');
        }
      });
    });
  }
}
