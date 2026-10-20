# SmartMov local development and Vercel deployment

## Local application

SmartMov uses a local MySQL database and stores uploaded files in the backend
`uploads` directory.

1. Create a MySQL database named `SmartMov`.
2. Copy `SmartMov-backend/.env.example` to `SmartMov-backend/.env`.
3. Set `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET` for the local machine.
4. Start the backend:

   ```powershell
   cd SmartMov-backend
   .\mvnw.cmd spring-boot:run
   ```

5. Serve `SmartMov-frontend` with a local static file server on port `5500`.
   The frontend uses `/api` by default, so use a reverse proxy or set
   `window.SMARTMOV_CONFIG.apiBaseUrl` to
   `http://localhost:8080/api` when serving the frontend separately.

Flyway creates the schema from
`SmartMov-backend/src/main/resources/db/migration/V1__initial_schema.sql`.

## Vercel

Vercel can host the static files in `SmartMov-frontend`, but it cannot run the
Spring Boot backend or connect to a MySQL server running on your laptop.

To deploy the frontend:

1. Import this repository into Vercel.
2. Set the project root to `SmartMov-frontend`.
3. Leave the framework preset as **Other** and leave the build command empty.
4. Deploy.

For a hosted frontend to work, the backend must be deployed separately to a
Java-capable host with a reachable MySQL database. Before deploying the
frontend, copy `config.production.example.js` to `config.js` and replace the
placeholder backend URL. Also add the Vercel site URL to
`CORS_ALLOWED_ORIGINS` on the backend.

The local MySQL and local filesystem upload configuration is intentionally not
used as a Vercel production database or file store.
