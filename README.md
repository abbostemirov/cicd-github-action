# cicd-demo

Docker va CI/CD ni o'rganish uchun minimal Spring Boot loyihasi.

**Stack:** Java 21, Spring Boot 4.0.6, Maven, PostgreSQL, Flyway, Testcontainers, Docker, GitHub Actions.

## Loyiha tuzilishi

```
src/main/java/.../controller   -> TestController (GET /api/items)
src/main/java/.../entity       -> Item (JPA entity)
src/main/java/.../repository   -> ItemRepository
src/main/java/.../config       -> DataSeeder (boshlang'ich data)
src/main/resources/db/migration -> Flyway migratsiyalari
Dockerfile                     -> multi-stage, non-root, layered jar
docker-compose.yml             -> app + postgres
.github/workflows/ci-cd.yml    -> test -> docker build/push -> deploy
```

## 1. Lokal, Docker'siz ishga tushirish

Postgres kerak bo'ladi (lokal yoki Docker orqali):

```bash
docker run --name local-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=cicd_demo -p 5432:5432 -d postgres:16-alpine
mvn spring-boot:run
```

## 2. Docker Compose bilan ishga tushirish (tavsiya etiladi)

```bash
cp .env.example .env      # kerak bo'lsa parollarni o'zgartiring
docker compose up --build
```

Tekshirish:

```bash
curl http://localhost:8080/api/items
curl http://localhost:8080/actuator/health
```

To'xtatish va tozalash:

```bash
docker compose down -v
```

## 3. Faqat Dockerfile bilan build/run

```bash
docker build -t cicd-demo:local .
docker run --rm -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PASSWORD=postgres \
  cicd-demo:local
```

## 4. Testlarni ishga tushirish

```bash
mvn clean verify
```

Integratsion test (`TestControllerIT`) Testcontainers orqali haqiqiy Postgres konteynerini
ko'taradi — shuning uchun mahalliy Docker daemon ishlab turishi kerak.

## 5. CI/CD pipeline (`.github/workflows/ci-cd.yml`)

| Job      | Qachon ishlaydi                         | Nima qiladi                                    |
|----------|------------------------------------------|-------------------------------------------------|
| test     | har bir push va pull request             | `mvn verify` (unit + integration testlar)       |
| docker   | faqat `main` ga push, `test` o'tgandan keyin | image build qiladi, GHCR ga push qiladi, Trivy bilan skan qiladi |
| deploy   | `docker` dan keyin                       | namuna bosqich — real serveringizga moslang     |

### GHCR (GitHub Container Registry) haqida

Workflow qo'shimcha secret talab qilmaydi — `GITHUB_TOKEN` avtomatik beriladi.
Repo **Settings -> Actions -> General -> Workflow permissions** bo'limida
"Read and write permissions" yoqilganiga ishonch hosil qiling, aks holda
`packages: write` ruxsati ishlamaydi.

## Keyingi qadamlar (o'rganish uchun)

- `docker-compose.yml` ga Nginx yoki healthcheck-based restart qo'shib ko'ring.
- CI ga `mvn verify` ustiga SonarQube/Checkstyle qo'shing.
- `deploy` bosqichini haqiqiy serverga (SSH) yoki Kubernetes'ga ulang.
- Image tag sifatida `latest` o'rniga faqat git SHA ishlatishni sinab ko'ring (immutable tag'lar - production best practice).
