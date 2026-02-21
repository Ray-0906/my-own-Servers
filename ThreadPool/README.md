# Thread Pool Server

A blocking I/O server using `ExecutorService` with a fixed-size thread pool.

## Architecture

```
┌─────────────┐                     ┌─────────────┐
│   Client 1  │────┐                │  Worker 1   │
└─────────────┘    │                └─────────────┘
                   │   ┌─────────┐
┌─────────────┐    │   │  Task   │  ┌─────────────┐
│   Client 2  │────┼──▶│  Queue  │─▶│  Worker 2   │
└─────────────┘    │   │         │  └─────────────┘
                   │   └─────────┘
┌─────────────┐    │                ┌─────────────┐
│   Client N  │────┘                │  Worker N   │
└─────────────┘                     └─────────────┘
                                    
                   └── ExecutorService (Fixed Pool) ──┘
```

This architecture uses a bounded thread pool to handle client connections efficiently.

## How It Works

1. Server initializes an `ExecutorService` with a fixed number of threads (2000)
2. Acceptor loop runs on the main thread
3. Each accepted connection is submitted to the executor
4. Worker threads from the pool handle clients:
   - Simulate CPU work (50M iterations)
   - Send response with computed sum
   - Close connection
5. Thread returns to pool for reuse

## Files

| File | Description |
|------|-------------|
| `Server.java` | Thread pool server implementation |

## Running

```bash
javac Server.java
java Server
```

## Configuration

- **Port**: 8090 (hardcoded)
- **Pool Size**: 2000 threads (configurable via constructor)

## Key Components

```java
ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
executorService.execute(() -> handleClient(client));
```

## Characteristics

### Advantages
- **Bounded resources**: Fixed number of threads prevents resource exhaustion
- **Thread reuse**: No overhead of creating/destroying threads per request
- **Task queuing**: Excess requests wait in queue instead of failing
- **Better than raw multithreading**: More predictable resource usage

### Disadvantages
- Still uses blocking I/O
- Queue can grow unbounded under sustained load
- Threads blocked on I/O are wasted
- Not optimal for high concurrency (10K+ connections)

## Use Cases

- Web applications with moderate traffic
- API servers with bounded concurrency
- Background job processing

## Performance

- **Throughput**: High, limited by pool size
- **Latency**: Low when pool has available threads
- **Concurrency**: Up to pool size simultaneous connections
- **Scalability**: Better than raw multithreading, but still limited

## Tuning

Pool size should be tuned based on:
- CPU cores (for CPU-bound work)
- Expected I/O wait time
- Memory constraints (~1MB per thread)

For I/O-bound work: `poolSize = cores * (1 + waitTime/computeTime)`
