# HTTPS Setup for OMG Platform Server

## Overview
The server is now configured to use HTTPS with a self-signed certificate for development.

## Configuration
- **Port**: 8443 (HTTPS)
- **Certificate**: Self-signed (keystore.p12)
- **Password**: omgplatform123

## Usage
- **HTTPS URL**: `https://localhost:8443`
- **WebSocket**: `wss://localhost:8443/game`
- **API**: `https://localhost:8443/api/**`

## Frontend Updates
Update your frontend to use HTTPS/WSS:
```javascript
// WebSocket
const socket = new WebSocket('wss://localhost:8443/game');

// API calls
fetch('https://localhost:8443/api/auth/login', {...});
```

## Browser Warning
Browsers will show a security warning for self-signed certificates. Click "Advanced" → "Proceed to localhost" for development.

## Configuration Details

### SSL Certificate
- **Certificate Type**: Self-signed certificate (for development)
- **Key Store**: `src/main/resources/certs/keystore.p12`
- **Key Store Password**: `omgplatform123`
- **Key Alias**: `omgplatform`
- **Validity**: 365 days

### Server Configuration
- **Protocol**: HTTPS
- **Address**: 0.0.0.0 (accessible from any network interface)

## Usage

### Starting the Server
The server will now start on HTTPS port 8443:
```bash
./mvnw spring-boot:run
```

### Accessing the Server
- **API Endpoints**: `https://localhost:8443/api/**`

### Browser Security Warning
Since this is a self-signed certificate, browsers will show a security warning. For development:
1. Click "Advanced" or "Show Details"
2. Click "Proceed to localhost (unsafe)" or similar option

## Frontend Configuration

### CORS Origins
The server is configured to accept requests from:
- `http://localhost:3000`
- `https://localhost:3000`
- `http://localhost:3001`
- `https://localhost:3001`

### WebSocket Connection
Update your frontend WebSocket connection to use WSS:
```javascript
const socket = new WebSocket('wss://localhost:8443/game');
```

### API Calls
Update your API calls to use HTTPS:
```javascript
fetch('https://localhost:8443/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(loginData)
});
```

## Production Deployment

### For Production
1. **Replace the self-signed certificate** with a proper SSL certificate from a trusted Certificate Authority
2. **Update the keystore** with your production certificate
3. **Change the keystore password** to a secure production password
4. **Update application.properties** with production certificate details

### Certificate Management
- Keep your production certificates secure and never commit them to version control
- Consider using environment variables for sensitive certificate information
- Regularly renew certificates before expiration

## Troubleshooting

### Certificate Issues
- If you get certificate errors, ensure the keystore file exists in `src/main/resources/certs/`
- Verify the keystore password matches in `application.properties`
- Check that the key alias is correct

### Port Issues
- Ensure port 8443 is not already in use
- Check firewall settings if accessing from external machines

### CORS Issues
- Verify your frontend origin is included in the CORS configuration
- Check that you're using the correct protocol (HTTP vs HTTPS) 