# Multithreaded Server

A blocking I/O server that creates a new thread for each client connection.

## Architecture

```
                    ┌─────────────┐
┌─────────────┐     │  Thread 1   │
│   Client 1  │────▶│  (Handler)  │
└─────────────┘     └─────────────┘
                    
┌─────────────┐     ┌─────────────┐
│   Client 2  │────▶│  Thread 2   │
└─────────────┘     │  (Handler)  │
                    └─────────────┘
        ▲
        │           ┌─────────────┐
        └───────────│   Acceptor  │
                    │   Thread    │
                    └─────────────┘
```

This architecture spawns a new thread for each incoming connection, allowing multiple clients to be served concurrently.

## How It Works

1. Main thread runs an acceptor loop waiting for connections
2. When a client connects, a new `Thread` is spawned
3. The spawned thread handles the client independently:
   - Reads from the client
   - Sends a welcome message
   - Closes the connection
4. Main thread immediately returns to accepting new connections

## Files

| File | Description |
|------|-------------|
| `Server.java` | Multithreaded server implementation |

## Running

```bash
javac Server.java
java Server
```

## Configuration

- **Port**: 8090 (hardcoded)

## Characteristics

### Advantages
- Simple to implement and understand
- True parallelism - each client runs independently
- Low latency for individual requests

### Disadvantages
- **Thread overhead**: Each thread consumes ~1MB stack memory
- **Scalability issues**: Thousands of connections = thousands of threads
- **Context switching**: High CPU overhead with many threads
- **Resource exhaustion**: Can run out of memory/threads under load

## Use Cases

- Low to moderate traffic applications
- Quick prototypes
- Educational purposes

## Performance

- **Throughput**: Limited by thread creation overhead
- **Latency**: Low per-request (parallel handling)
- **Concurrency**: High, but resource-intensive
- **Max connections**: Limited by OS thread limits (~10K typical)

## Comparison to ThreadPool

Unlike ThreadPool, this server creates **unbounded** threads. Under heavy load, this can crash the JVM with `OutOfMemoryError`.
