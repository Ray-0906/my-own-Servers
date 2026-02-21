# Java Server Architectures

A collection of server implementations in Java demonstrating different architectural patterns and their performance characteristics.

## Overview

This repository contains multiple server implementations built from scratch to explore and understand various networking architectures in Java. Each implementation showcases a different approach to handling client connections, from simple blocking I/O to high-performance non-blocking NIO with multi-reactor patterns.

## Server Implementations

| Server | Architecture | Concurrency Model | Performance |
|--------|--------------|-------------------|-------------|
| [SingleThreaded](SingleThreaded/) | Blocking I/O | Single thread | Low - handles one client at a time |
| [Multithreaded](Multithreaded/) | Blocking I/O | Thread per connection | Medium - limited by thread overhead |
| [ThreadPool](ThreadPool/) | Blocking I/O | Fixed thread pool | Medium-High - bounded resource usage |
| [NioHttpServer](NioHttpServer/) | Non-blocking NIO | Multi-reactor pattern | **1000+ concurrent requests/sec** |
| [NioRpcServer](NioRpcServer/) | Non-blocking NIO | Multi-reactor pattern | **1000+ concurrent requests/sec** |
| [Server](Server/) | Modular NIO | Work in progress | - |

## Architecture Comparison

### Blocking I/O Servers
- **SingleThreaded**: Classic approach where one thread handles all clients sequentially
- **Multithreaded**: Creates a new thread for each incoming connection
- **ThreadPool**: Uses `ExecutorService` with a fixed pool to limit resource consumption

### Non-blocking NIO Servers
- **NioHttpServer**: HTTP/1.1 compliant server using Java NIO with selector-based event loop
- **NioRpcServer**: Binary RPC protocol server with length-prefixed messages

## Quick Start

### Running a Server

```bash
cd <ServerDirectory>
javac *.java
java Server
```

Server will start on port **8090** by default.

### Testing

**HTTP Servers (NioHttpServer):**
```bash
# Using curl
curl http://localhost:8090/ping
curl http://localhost:8090/time

# Using Postman
GET http://localhost:8090/ping
```

**RPC Server (NioRpcServer):**
Send a 4-byte length prefix followed by the message body (binary protocol).

## Performance

The NIO-based servers (NioHttpServer and NioRpcServer) utilize:
- **Multi-reactor pattern**: One selector per CPU core
- **Non-blocking I/O**: Single thread can handle thousands of connections
- **Zero-copy buffers**: Efficient ByteBuffer usage

These servers are capable of handling **1000+ concurrent requests per second**.

## Project Structure

```
webserver/
├── SingleThreaded/     # Basic single-threaded server
├── Multithreaded/      # Thread-per-connection model
├── ThreadPool/         # ExecutorService-based server
├── NioHttpServer/      # High-performance HTTP server
├── NioRpcServer/       # High-performance RPC server
└── Server/             # Modular server (WIP)
```

## Requirements

- Java 8 or higher
- No external dependencies

## Author

Built to learn and demonstrate Java networking concepts and server architectures.
