# Temporarily Disable SSL Guide

This guide shows you how to temporarily disable SSL for development purposes.

## Quick Start

### Option 1: Use the SSL Switcher Script (Recommended)

```cmd
switch-ssl.bat
```

Then select option 2 to disable SSL.

### Option 2: Manual Commands

**To disable SSL (HTTP mode):**
```cmd
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=nossl
```

**To enable SSL (HTTPS mode):**
```cmd
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

## Configuration Details

### No-SSL Profile (`application-nossl.properties`)

- **SSL**: Disabled (`server.ssl.enabled=false`)
- **Port**: 8080 (standard HTTP port)
- **Protocol**: HTTP
- **URLs**: 
  - API: `http://localhost:8080`
  - WebSocket: `ws://localhost:8080/websocket`

### SSL Profile (`application-dev.properties`)

- **SSL**: Enabled (`server.ssl.enabled=true`)
- **Port**: 8443 (HTTPS port)
- **Protocol**: HTTPS
- **URLs**:
  - API: `https://localhost:8443`
  - WebSocket: `wss://localhost:8443/websocket`

## Client Configuration Changes

### When SSL is Disabled (HTTP Mode)

**JavaFX Client:**
```java
// No SSL configuration needed
HttpClient httpClient = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(30))
    .build();

// Use HTTP URLs
String serverUrl = "http://localhost:8080";
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create(serverUrl + "/users/test"))
    .GET()
    .build();
```

**WebSocket Client:**
```java
// Use WS instead of WSS
String wsUrl = "ws://localhost:8080/websocket";
```

**JavaScript/Web Client:**
```javascript
// HTTP API calls
fetch('http://localhost:8080/users/test')

// WebSocket connection
const socket = new WebSocket('ws://localhost:8080/websocket');
```

### When SSL is Enabled (HTTPS Mode)

**JavaFX Client:**
```java
// Use SSL helper for self-signed certificates
HttpClient httpClient = createSSLHttpClient();

// Use HTTPS URLs
String serverUrl = "https://localhost:8443";
```

**WebSocket Client:**
```java
// Use WSS instead of WS
String wsUrl = "wss://localhost:8443/websocket";
```

## Testing

### Test HTTP Mode (No SSL)

```cmd
# Test API endpoint
curl http://localhost:8080/users/test

# Test with browser
# Navigate to: http://localhost:8080/users/test
```

### Test HTTPS Mode (SSL)

```cmd
# Test API endpoint (with self-signed certificate)
curl -k https://localhost:8443/users/test

# Test with browser
# Navigate to: https://localhost:8443/users/test
# Click "Advanced" → "Proceed to localhost (unsafe)"
```

## Network Access

### HTTP Mode (No SSL)

- **Local**: `http://localhost:8080`
- **Network**: `http://YOUR_IP:8080`

### HTTPS Mode (SSL)

- **Local**: `https://localhost:8443`
- **Network**: `https://YOUR_IP:8443`

## Security Considerations

### When SSL is Disabled

⚠️ **WARNING**: HTTP is not secure and should only be used for development.

- **No encryption** of data in transit
- **Passwords and sensitive data** are transmitted in plain text
- **Man-in-the-middle attacks** are possible
- **Never use in production**

### When SSL is Enabled

- **Data is encrypted** in transit
- **Self-signed certificates** are used for development
- **Proper certificates** should be used in production

## Troubleshooting

### Common Issues

1. **Port already in use**
   ```cmd
   # Check what's using the port
   netstat -ano | findstr :8080
   netstat -ano | findstr :8443
   ```

2. **Certificate errors** (when SSL is enabled)
   - Use `-k` flag with curl
   - Accept self-signed certificate in browser
   - Configure JavaFX client to trust self-signed certificates

3. **Connection refused**
   - Check if server is running
   - Verify correct port is being used
   - Check firewall settings

### Debug Commands

```cmd
# Check server status
netstat -an | findstr :8080
netstat -an | findstr :8443

# Test connectivity
curl http://localhost:8080/users/test
curl -k https://localhost:8443/users/test
```

## Switching Between Modes

### Quick Switch

1. **Stop the server** (Ctrl+C)
2. **Run the switcher**: `switch-ssl.bat`
3. **Select the desired mode**

### Manual Switch

1. **Stop the server** (Ctrl+C)
2. **Start with desired profile**:
   ```cmd
   # For HTTP (no SSL)
   mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=nossl
   
   # For HTTPS (with SSL)
   mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
   ```

## Best Practices

1. **Use HTTP mode** for quick development and testing
2. **Use HTTPS mode** when testing SSL-related features
3. **Always use HTTPS** in production
4. **Test both modes** to ensure your client works in both scenarios
5. **Document which mode** your team should use for development 