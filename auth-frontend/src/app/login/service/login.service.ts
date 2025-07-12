import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {environment} from '../../../environments/environment';

interface LoginRequest {
  emailOrUsername: string;
  password: string;
}

interface LoginResponse {
  access_token: string;
  expires_in: number;
  token_type: string;
}

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  login(emailOrUsername: string, password: string): Observable<LoginResponse> {
    const headers = new HttpHeaders({'Content-Type': 'application/json'});
    const body: LoginRequest = { emailOrUsername, password };

    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, body, { headers })
      .pipe(
        catchError(this.handleError));
  }

  /**
   * Handles HTTP errors and transforms them into user-friendly messages.
   * @param error The HTTP error response
   * @returns An Observable with a user-friendly error message
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An error occurred during login. Please try again later.';

    if (error.status === 500) {
      errorMessage = 'Login failed due to a server error. Please contact support.';
    }

    console.error(`Login error: ${errorMessage}`, error);
    return throwError(() => new Error(errorMessage));
  }
}
