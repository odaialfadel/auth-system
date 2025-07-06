import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {environment} from '../../../environments/environment';

interface RegisterRequest {
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

interface RegisterResponse {
  userId: number;
  keycloakId: string;
  username: string; // Fixed typo: String -> string
  email: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class RegisterService {
  private apiUrl = `${environment.apiUrl}/api/users`; // Use environment configuration

  constructor(private http: HttpClient) {}

  /**
   * Registers a new user by sending a POST request to the backend API.
   * @param username The username chosen by the user
   * @param firstName The user's first name
   * @param lastName The user's last name
   * @param email The user's email address
   * @param password The user's password
   * @returns An Observable of RegisterResponse containing user details and a success message
   */
  register(username: string, firstName: string, lastName: string, email: string, password: string): Observable<RegisterResponse> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    const body: RegisterRequest = { username, firstName, lastName, email, password };

    return this.http.post<RegisterResponse>(`${this.apiUrl}/register`, body, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Handles HTTP errors and transforms them into user-friendly messages.
   * @param error The HTTP error response
   * @returns An Observable with a user-friendly error message
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An error occurred during registration. Please try again later.';

    if (error.status === 400) {
      errorMessage = 'A user with this email already exists.';
    } else if (error.status === 500) {
      errorMessage = 'Registration failed due to a server error. Please contact support.';
    }

    console.error(`Registration error: ${errorMessage}`, error);
    return throwError(() => new Error(errorMessage));
  }
}
