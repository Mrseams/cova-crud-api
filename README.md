# Task Manager

Mini application de gestion de tâches réalisée dans le cadre du test technique. Un utilisateur peut créer un compte, se connecter, et gérer ses tâches (créer, modifier, supprimer, filtrer par statut, rechercher par titre).

Le dossier `mobile/` prévu dans le sujet (Flutter) n'a pas été traité, le reste du périmètre (backend, frontend, CI/CD) est couvert.

## Stack technique

- **Backend** : Java 17, Spring Boot 4, Spring Data JPA, Spring Security, JWT (jjwt), MySQL
- **Frontend** : React 19 + Vite + TypeScript, Tailwind CSS v4, React Router, Axios, react-hot-toast
- **Infra** : Docker, docker-compose, GitHub Actions

## Architecture

```
cova-crud-api/
├── backend/    API REST Spring Boot (auth JWT + CRUD tâches)
├── frontend/   SPA React consommant l'API
├── docker-compose.yml
└── .github/workflows/ci-cd.yml
```

Le backend expose une API stateless protégée par JWT. Chaque utilisateur ne voit que ses propres tâches (la requête de récupération des tâches est toujours filtrée par l'id de l'utilisateur authentifié, jamais par un id passé côté client). Le frontend stocke le token dans le `localStorage` et l'attache automatiquement aux requêtes via un intercepteur Axios ; en cas de 401, l'utilisateur est redirigé vers la page de login.

## Endpoints API

| Méthode | Route | Description | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Créer un compte | non |
| POST | `/api/auth/login` | Se connecter, renvoie un JWT | non |
| GET | `/api/tasks?status=&search=` | Liste des tâches de l'utilisateur connecté | oui |
| POST | `/api/tasks` | Créer une tâche | oui |
| PUT | `/api/tasks/{id}` | Modifier une tâche | oui |
| DELETE | `/api/tasks/{id}` | Supprimer une tâche | oui |

## Lancer le projet avec Docker (recommandé)

Prérequis : Docker et Docker Compose.

```bash
docker compose up --build
```

- Frontend : http://localhost:5173
- Backend : http://localhost:8080
- MySQL : localhost:3306 (user `root`, password `root`)

## Lancer en local sans Docker

### Backend

Prérequis : Java 17, une base MySQL qui tourne en local (ou modifier les variables d'environnement pour pointer ailleurs).

```bash
cd backend
./mvnw spring-boot:run
```

Variables d'environnement (valeurs par défaut entre parenthèses) :

| Variable | Défaut |
|---|---|
| `DB_HOST` | `localhost` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `taskmanager` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | `root` |
| `JWT_SECRET` | valeur de dev, à changer en prod |
| `JWT_EXPIRATION_MS` | `86400000` (24h) |
| `SERVER_PORT` | `8080` |

### Frontend

Prérequis : Node 20+.

```bash
cd frontend
npm install
cp .env.example .env   # ajuster VITE_API_URL si besoin
npm run dev
```

L'app tourne sur http://localhost:5173.

## Tests

```bash
cd backend
./mvnw test
```

Les tests unitaires (`AuthServiceTest`, `TaskServiceTest`, `JwtServiceTest`) tournent avec des mocks, le test de contexte Spring (`TaskmanagerApplicationTests`) utilise une base H2 en mémoire (voir `src/test/resources/application.properties`), donc aucun besoin de MySQL pour lancer les tests.

## CI/CD (bonus)

Le workflow `.github/workflows/ci-cd.yml` :

1. build + tests du backend (Maven)
2. lint + build du frontend (npm)
3. build des images Docker backend/frontend
4. déploiement sur Cloud Run (job séparé, déclenché seulement sur `main`)

L'étape de déploiement nécessite les secrets GitHub suivants côté repo : `GCP_SA_KEY` (clé JSON d'un service account), `GCP_PROJECT_ID`, `GCP_REGION`. Sans ces secrets, la CI (build + tests) fonctionne quand même, seul le job de déploiement échouera.

## Choix techniques

- **JWT stateless** plutôt que sessions : plus simple à faire cohabiter avec un frontend SPA et un futur client mobile qui consommerait la même API.
- **Records Java** pour les DTO (request/response) : évite le boilerplate des getters/setters sur des objets qui ne portent pas de logique.
- **H2 en test** pour ne pas dépendre d'une vraie instance MySQL pendant les tests, tout en gardant MySQL en dev/prod.
- **Filtrage par statut/recherche fait côté base** (requête JPQL dans `TaskRepository`) plutôt que côté frontend, pour rester correct même si la liste de tâches grossit.
- **Tailwind v4** sans fichier de config séparé (le plugin Vite officiel détecte le contenu automatiquement), ça évite un fichier de configuration en plus pour un projet de cette taille.
