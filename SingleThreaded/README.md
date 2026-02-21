# Single-Threaded Server

A basic blocking I/O server that handles one client at a time.

## Architecture

```
┌─────────────┐     ┌─────────────┐
│   Client    │────▶│   Server    │
└─────────────┘     │ (1 thread)  │
                    └─────────────┘
```

This is the simplest server architecture where a single thread:
1. Accepts a connection
2. Processes the request (simulated CPU work)
3. Sends the response
4. Closes the connection
5. Waits for the next client

## How It Works

- Uses `ServerSocket` for blocking TCP connections
- Processes clients **sequentially** - subsequent clients must wait
- Simulates CPU-intensive work with a loop summing 50 million numbers
- Returns the computed sum to the client

## Files

| File | Description |
|------|-------------|
| `Server.java` | Main server implementation |
| `Client.java` | Test client for connecting to the server |

## Running

**Start the server:**
```bash
javac Server.java
java Server
```

**Start the client:**
```bash
javac Client.java
java Client
```

## Configuration

- **Port**: 8090 (hardcoded)

## Limitations

- Can only handle **one client at a time**
- Blocked during CPU-intensive work
- Poor scalability
- Not suitable for production use

## Use Cases

- Learning basic socket programming
- Simple single-user applications
- Debugging and testing

## Performance

- **Throughput**: ~1 request at a time
- **Latency**: Depends on workload (high when busy)
- **Concurrency**: None - clients queue up
