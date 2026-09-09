# 프리인보이스 (FreeInvoice)

프리랜서를 위한 견적서·인보이스 발행 관리 서비스 — SKALA AI 웹 서비스 설계 Mini-project

## 산출물

| 구분 | 경로 |
|---|---|
| 프로젝트 기술서 (PDF) | [`docs/2반_이름_프리인보이스-개요.pdf`](docs/2반_이름_프리인보이스-개요.pdf) |
| API 명세서 (OpenAPI YAML) | [`api/2반_이름_프리인보이스-API.yml`](api/2반_이름_프리인보이스-API.yml) |
| DB 다이어그램 (DBML) | [`db/2반_이름_프리인보이스-DB.dbml`](db/2반_이름_프리인보이스-DB.dbml) |

> 파일명의 `2반_이름`은 제출 규칙에 맞게 실제 반/이름으로 변경해서 제출하세요.

## 서비스 개요

프리랜서가 거래처(클라이언트)별로 견적서를 작성·발송하면, 클라이언트가 직접 로그인해
승인/거절하고, 승인된 건에 대해 인보이스를 발행 및 입금 확인까지 관리하는 B2B2C 서비스입니다.

- **프리랜서**: 거래처 관리, 견적서 작성/발송, 인보이스 발행, 입금 확인
- **클라이언트**: 견적서 확인/승인/거절, 인보이스 조회

## 폴더 구조

```
miniproject1/
├── docs/       # 프로젝트 기술서(PDF), UI 흐름 스크린샷
├── db/         # ERD (DBML)
├── api/        # API 명세서 (OpenAPI YAML)
├── backend/    # Spring Boot (Java 17) REST API
└── frontend/   # React (Vite) SPA
```

## 실행 방법

### 백엔드 (Spring Boot, 포트 8080)

```bash
cd backend
mvn spring-boot:run
```

- H2 인메모리 DB 사용 (별도 설치 불필요)
- H2 콘솔: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:freeinvoice`)

### 프론트엔드 (React + Vite, 포트 5173)

```bash
cd frontend
npm install
npm run dev
```

`frontend/.env`의 `VITE_API_BASE`가 백엔드 주소(`http://localhost:8080/api`)를 가리킵니다.

## 기술 스택

- Backend: Java 17, Spring Boot 3.3 (Web, Data JPA, Security, Validation), H2, JWT(jjwt)
- Frontend: React 19, Vite, React Router, Axios
