Movie Ticket Booking System
A complete production-style Movie Ticket Booking System built with Java Spring Boot REST API.

Tech Stack
Java 21 + Spring Boot 3.2
Spring Security + JWT Authentication
Spring Data JPA + Hibernate
Microsoft SQL Server (JDBC)
Spring Mail (Gmail SMTP)
Lombok, Swagger/OpenAPI 3
JUnit 5 + Mockito
HTML5 / Bootstrap 5 / Vanilla JS (Frontend)
Maven (Build)
Quick Start
Prerequisites
Java 21+
Maven 3.8+
SQL Server running at LAPTOP-IHF4ITV1\MSSQLSERVER01
Database MovieTicketBookingDB created
Configure database password
Edit src/main/resources/application.properties: `properties spring.datasource.password=YOUR_SQL_SERVER_PASSWORD `

Build & Run
# Build
mvn clean package

# Run
mvn spring-boot:run

# Or run the JAR
java -jar target/booking-1.0.0.jar
The app will start at http://localhost:8000

Default Credentials (auto-seeded)
| Role | Email | Password | |-------|--------------------|-------------| | Admin | admin@gmail.com | Admin@123 | | User | alice@gmail.com | User@123 | | User | bob@gmail.com | User@123 |

Frontend Pages
| Path | Description | |-----------------------------|----------------------| | / | Home / Hero | | /pages/movies.html | Browse movies | | /pages/movie-detail.html | Movie details + shows| | /pages/booking.html | Seat selection | | /pages/login.html | Login | | /pages/register.html | Register | | /pages/dashboard.html | User dashboard | | /pages/admin-dashboard.html| Admin panel | | /pages/theaters.html | Theaters |

API Documentation
Swagger UI: http://localhost:8000/swagger-ui.html

API Endpoints
Authentication
POST /api/auth/register
POST /api/auth/login
Movies
GET    /api/movies              (paginated, filterable)
GET    /api/movies/trending
GET    /api/movies/{id}
POST   /api/movies              [ADMIN]
PUT    /api/movies/{id}         [ADMIN]
DELETE /api/movies/{id}         [ADMIN]
Theaters
GET    /api/theaters
GET    /api/theaters/{id}
POST   /api/theaters            [ADMIN]
PUT    /api/theaters/{id}       [ADMIN]
DELETE /api/theaters/{id}       [ADMIN]
Shows
GET    /api/shows
GET    /api/shows/{id}
GET    /api/shows/movie/{movieId}
POST   /api/shows               [ADMIN]
DELETE /api/shows/{id}          [ADMIN]
Seats
GET /api/seats/show/{showId}
GET /api/seats/show/{showId}/available
Bookings
POST   /api/bookings            [AUTH]
GET    /api/bookings/my-bookings [AUTH]
DELETE /api/bookings/{id}       [AUTH - cancel]
Admin
GET /api/admin/dashboard        [ADMIN]
GET /api/admin/users            [ADMIN]
GET /api/admin/bookings         [ADMIN]
PUT /api/admin/users/{id}/toggle-status [ADMIN]
Users
GET /api/users/me               [AUTH]
Package Structure
com.movieticket.booking/
├── config/          SecurityConfig, SwaggerConfig, DataLoader, AsyncConfig
├── controller/      AuthController, MovieController, TheaterController,
│                    ShowController, SeatController, BookingController,
│                    AdminController, UserController
├── dto/
│   ├── request/     RegisterRequest, LoginRequest, MovieRequest,
│   │                TheaterRequest, ShowRequest, BookingRequest
│   └── response/    AuthResponse, MovieResponse, TheaterResponse,
│                    ShowResponse, SeatResponse, BookingResponse,
│                    PaymentResponse, UserResponse, DashboardResponse
├── entity/          User, Movie, Theater, Screen, Show, Seat, Booking, Payment
│                    + enums: Role, MovieStatus, ShowStatus, SeatCategory,
│                             SeatStatus, BookingStatus, PaymentStatus, PaymentMethod
├── exception/       GlobalExceptionHandler, ResourceNotFoundException,
│                    BadRequestException, UnauthorizedException, ErrorResponse
├── repository/      UserRepository, MovieRepository, TheaterRepository,
│                    ScreenRepository, ShowRepository, SeatRepository,
│                    BookingRepository, PaymentRepository
├── security/        JwtUtil, JwtAuthenticationFilter, CustomUserDetailsService
└── service/         AuthService, MovieService, TheaterService, ShowService,
                     SeatService, BookingService, UserService, EmailService
Tests (JUnit 5 + Mockito)
mvn test
Test classes:

JwtUtilTest — JWT token generation/validation
AuthServiceTest — register/login
MovieServiceTest — CRUD operations
BookingServiceTest — booking flow
AuthControllerTest — controller layer
MovieControllerTest — controller layer
