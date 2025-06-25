import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

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
  username: String;
  email: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  private apiUrl = 'http://localhost:8080/api/users'; // adjust if needed

  constructor(private http: HttpClient) {}

  register(username: string, firstName: string, lastName: string, email: string, password: string): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.apiUrl}/register`, { username, email, firstName, lastName, password });
  }
}
