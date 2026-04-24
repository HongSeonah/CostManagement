# Cost Management API

원가/관리회계 포트폴리오용 백엔드 프로젝트입니다.
5개 본부가 동시에 20여 개 프로젝트를 운영하는 환경을 기준으로 설계했습니다.

## 핵심 기능
- 본부/프로젝트 관리
- 프로젝트/원가 항목 관리
- 월별 원가 집계
- 프로젝트별 지출 현황 확인
- 본부별 프로젝트 현황 확인
- 샘플 데이터 자동 주입

## API
- `GET /api/health`
- `GET /api/dashboard/summary`
- `GET /api/business-units`
- `GET /api/business-units/{id}`
- `GET /api/projects`
- `GET /api/projects?businessUnitId=1`
- `POST /api/projects`
- `GET /api/cost-entries`
- `POST /api/cost-entries`

## 실행
- Java 17
- Spring Boot
- MySQL 로컬 DB

## 로컬 DB 설정
- DB명: `cost_management`
- 계정: `root`
- 비밀번호: `1234`
