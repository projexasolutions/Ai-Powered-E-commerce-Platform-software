# NEXTGEN Store

A production-oriented fashion and accessories commerce platform for Gen Z, with AI-assisted discovery and styling planned as a separate intelligence service.

## Phase 1 — Commerce Core

Implemented foundations:
- React + TypeScript + Vite storefront
- Spring Boot REST API
- PostgreSQL persistence
- Flyway schema migrations and seed catalog
- Product listing, search, category filter, sorting and pagination
- Product detail API
- Wishlist and cart interaction at the storefront layer
- Responsive white / black / purple NEXTGEN UI

## Run locally

### Database

```bash
docker compose up -d postgres
```

### API

```bash
cd backend
mvn spring-boot:run
```

API: `http://localhost:8080`

### Web

```bash
npm install
npm run dev
```

Web: `http://localhost:5173`

Set `VITE_API_URL` when the API is not running on `http://localhost:8080`.

## Architecture direction

The commerce core is intentionally separate from the future Python AI service. AI will consume stable catalog APIs rather than being embedded into the storefront.
