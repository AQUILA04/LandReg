import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../core/models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {
  private readonly http = inject(HttpClient);

  private extractData<T>(res: ApiResponse<T>): T {
    return res.data;
  }

  // --- Users ---
  getUsers(page: number, size: number): Observable<any> {
    return this.http.get<ApiResponse<any>>(`/api/v1/users?page=${page}&size=${size}`)
      .pipe(map(this.extractData));
  }

  updateUser(id: number, userData: any): Observable<any> {
    return this.http.put<ApiResponse<any>>(`/api/v1/users/${id}`, userData)
      .pipe(map(this.extractData));
  }

  assignProfile(userId: number, profilId: number): Observable<any> {
    return this.http.patch<ApiResponse<any>>(`/api/v1/users/${userId}/assign-profile/${profilId}`, {})
      .pipe(map(this.extractData));
  }

  // --- Profiles ---
  getProfiles(page: number, size: number): Observable<any> {
    return this.http.get<ApiResponse<any>>(`/api/v1/profils?page=${page}&size=${size}`)
      .pipe(map(this.extractData));
  }

  getAllProfilesList(): Observable<any[]> {
    return this.http.get<ApiResponse<any[]>>(`/api/v1/profils/all`)
      .pipe(map(this.extractData));
  }

  createProfile(profileData: any): Observable<any> {
    return this.http.post<ApiResponse<any>>(`/api/v1/profils`, profileData)
      .pipe(map(this.extractData));
  }

  updateProfile(id: number, profileData: any): Observable<any> {
    return this.http.put<ApiResponse<any>>(`/api/v1/profils/${id}`, profileData)
      .pipe(map(this.extractData));
  }

  addPermissionsToProfile(profilId: number, permissions: string[]): Observable<any> {
    return this.http.patch<ApiResponse<any>>(`/api/v1/profils/add-permissions`, { profilId, permissions })
      .pipe(map(this.extractData));
  }

  // --- Permissions ---
  getAllPermissions(): Observable<string[]> {
    return this.http.get<ApiResponse<string[]>>(`/api/v1/users/permission/all`)
      .pipe(map(this.extractData));
  }
}
