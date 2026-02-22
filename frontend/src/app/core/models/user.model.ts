export interface User {
  username: string;
  displayName: string;
  role: 'ANALYST' | 'SENIOR_ANALYST' | 'ADMIN';
  department: string;
}
