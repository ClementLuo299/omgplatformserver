# SSL Certificate Setup Guide

This guide covers how to set up public SSL certificates for the OMG Platform server.

## Option 1: Let's Encrypt (Recommended - Free)

### Prerequisites
- A domain name pointing to your server
- Certbot installed on your server

### Step 1: Install Certbot
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install certbot

# CentOS/RHEL
sudo yum install certbot

# Windows (using Chocolatey)
choco install certbot

# macOS
brew install certbot
```

### Step 2: Generate Certificate
```bash
# For a single domain
sudo certbot certonly --standalone -d yourdomain.com

# For multiple domains
sudo certbot certonly --standalone -d yourdomain.com -d www.yourdomain.com

# For wildcard certificate (requires DNS challenge)
sudo certbot certonly --manual --preferred-challenges=dns -d *.yourdomain.com
```

### Step 3: Convert to PKCS12 Format
```bash
# Convert the certificate to PKCS12 format
sudo openssl pkcs12 -export \
  -in /etc/letsencrypt/live/yourdomain.com/fullchain.pem \
  -inkey /etc/letsencrypt/live/yourdomain.com/privkey.pem \
  -out /path/to/your/server/src/main/resources/certs/production-keystore.p12 \
  -name omgplatform \
  -passout pass:your_secure_password
```

### Step 4: Set Environment Variable
```bash
# Set the keystore password as environment variable
export SSL_KEYSTORE_PASSWORD=your_secure_password
```

### Step 5: Auto-renewal Setup
```bash
# Test auto-renewal
sudo certbot renew --dry-run

# Add to crontab for automatic renewal
sudo crontab -e
# Add this line to run twice daily:
0 0,12 * * * /usr/bin/certbot renew --quiet
```

## Option 2: Commercial Certificate Authority

### Step 1: Generate CSR (Certificate Signing Request)
```bash
# Generate private key
openssl genrsa -out private.key 2048

# Generate CSR
openssl req -new -key private.key -out certificate.csr
```

### Step 2: Submit CSR to CA
Submit the `certificate.csr` file to your chosen Certificate Authority (e.g., DigiCert, GlobalSign, etc.)

### Step 3: Convert Received Certificate
```bash
# Convert the received certificate to PKCS12
openssl pkcs12 -export \
  -in certificate.crt \
  -inkey private.key \
  -out production-keystore.p12 \
  -name omgplatform \
  -passout pass:your_secure_password
```

## Option 3: Self-Signed Certificate (Development Only)

### Generate Self-Signed Certificate
```bash
# Generate self-signed certificate
keytool -genkeypair \
  -alias omgplatform \
  -keyalg RSA \
  -keysize 2048 \
  -validity 365 \
  -keystore src/main/resources/certs/keystore.p12 \
  -storetype PKCS12 \
  -storepass omgplatform123 \
  -dname "CN=localhost, OU=Development, O=OMG Platform, L=City, S=State, C=US"
```

## Profile Configuration

The server uses Spring Boot profiles to switch between development and production configurations:

### Development Profile (Self-signed)
```bash
# Run with development profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production Profile (Public Certificate)
```bash
# Run with production profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## Environment Variables

For production, set these environment variables:

```bash
# Required for production
export SSL_KEYSTORE_PASSWORD=your_secure_password

# Optional: Override default values
export SERVER_PORT=8443
export JWT_SECRET=your_super_secret_jwt_key
export DATABASE_URL=jdbc:postgresql://your-db-host:5432/your-db-name
export DATABASE_USERNAME=your_db_user
export DATABASE_PASSWORD=your_db_password
```

## Docker Deployment

### Dockerfile with Certificate
```dockerfile
# Copy your production certificate
COPY production-keystore.p12 /app/src/main/resources/certs/

# Set environment variables
ENV SSL_KEYSTORE_PASSWORD=your_secure_password
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.profiles=prod"]
```

### Docker Compose
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8443:8443"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SSL_KEYSTORE_PASSWORD=${SSL_KEYSTORE_PASSWORD}
    volumes:
      - ./certs:/app/src/main/resources/certs
```

## Security Best Practices

### 1. Certificate Management
- Store certificates securely, never commit to version control
- Use environment variables for sensitive data
- Regularly renew certificates before expiration
- Monitor certificate expiration dates

### 2. Password Security
- Use strong, unique passwords for keystores
- Rotate passwords regularly
- Use password managers for production environments

### 3. Network Security
- Configure firewall rules appropriately
- Use HTTPS redirect for all HTTP traffic
- Implement proper CORS policies

### 4. Monitoring
- Set up certificate expiration monitoring
- Monitor SSL/TLS configuration
- Log security events

## Troubleshooting

### Common Issues

1. **Certificate not trusted by browsers**
   - Ensure you're using a certificate from a trusted CA
   - Check certificate chain is complete
   - Verify domain name matches certificate

2. **Java SSL errors**
   - Check keystore password is correct
   - Verify keystore file exists and is readable
   - Ensure certificate alias matches configuration

3. **Port binding issues**
   - Check if port 8443 is already in use
   - Verify firewall settings
   - Check SELinux if on Linux

### Debug Commands
```bash
# Check certificate details
openssl pkcs12 -info -in production-keystore.p12 -noout

# Test SSL connection
openssl s_client -connect yourdomain.com:8443 -servername yourdomain.com

# Check Java keystore
keytool -list -keystore production-keystore.p12 -storepass your_password
```

## Client Configuration

### JavaFX Client
```java
// For production certificates, no special configuration needed
HttpClient client = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .build();

HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://yourdomain.com:8443/users/login"))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
    .build();
```

### WebSocket Client
```javascript
// WebSocket connection with public certificate
const socket = new WebSocket('wss://yourdomain.com:8443/websocket');
```

## Migration Checklist

- [ ] Obtain public SSL certificate
- [ ] Convert certificate to PKCS12 format
- [ ] Place certificate in `src/main/resources/certs/production-keystore.p12`
- [ ] Set `SSL_KEYSTORE_PASSWORD` environment variable
- [ ] Update client URLs to use your domain
- [ ] Test HTTPS connections
- [ ] Test WebSocket connections
- [ ] Configure auto-renewal (Let's Encrypt)
- [ ] Set up monitoring for certificate expiration
- [ ] Update documentation with new URLs 