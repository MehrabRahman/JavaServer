# JavaServer

## Overview
Minimal HTTP server written in plain Java with a tiny, custom test runner. It demonstrates:
- A simple servlet-style routing mechanism (path → handler function)
- Basic HTTP request parsing and response writing
- A lightweight, annotation-based test framework (`JankUnit`) with example tests

## Contents
- `Server.java`: Main server executable and core classes
  - `Server`: Listens on port 8080, accepts connections, dispatches requests to registered servlets using a fixed thread pool (10 threads)
  - `Request`: Parses the HTTP request line and headers
  - `Response`: Writes a minimal HTTP/1.1 200 OK response with `Content-Length`
  - `Servlet` (interface): Functional interface for request handlers
- `JankUnit.java`: Tiny test framework
  - `@JTest`: Annotation to mark test methods
  - `JankUnit`: Discovers and runs methods annotated with `@JTest`
  - `UnitTests`: Example test class containing a couple of simple arithmetic tests
- `greeting`: Example text file containing a sample message (not wired into the server by default)

## Quick start
1) Compile
```
javac Server.java JankUnit.java
```

2) Run the server (listens on port 8080)
```
java Server
```

3) Try it out
```
curl -i http://localhost:8080/
curl -i http://localhost:8080/test
```

4) Run the tests
```
java JankUnit UnitTests
```

## How routing works
In `main` of `Server`, routes are registered via `addServlet(path, handler)`:
```
server.addServlet("/", (req, res) -> res.send("Hello"));
server.addServlet("/test", (req, res) -> res.send("Test"));
```
Add more routes by supplying additional path/handler pairs.

## Notes and limitations
- Educational example, not production-ready
- Very small subset of HTTP; request body handling and many headers are not implemented
- Minimal error handling; malformed requests may cause failures
- Single-process, fixed-size thread pool

## License
No license specified; assume personal/educational use unless you add one.
