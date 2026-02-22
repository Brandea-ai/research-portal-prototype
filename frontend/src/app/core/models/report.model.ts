export interface Report {
  id: number;
  analystId: number;
  securityId: number;
  publishedAt: string;
  reportType: string;
  title: string;
  executiveSummary: string;
  fullText: string;
  rating: string;
  previousRating: string | null;
  ratingChanged: boolean;
  targetPrice: number;
  previousTarget: number | null;
  currentPrice: number;
  impliedUpside: number;
  riskLevel: string | null;
  investmentCatalysts: string[];
  keyRisks: string[];
  tags: string[];
}

export interface CreateReportRequest {
  analystId: number;
  securityId: number;
  reportType: string;
  title: string;
  executiveSummary: string;
  fullText?: string;
  rating: string;
  previousRating?: string;
  targetPrice: number;
  previousTarget?: number;
  currentPrice?: number;
  riskLevel?: string;
  investmentCatalysts?: string[];
  keyRisks?: string[];
  tags?: string[];
}
