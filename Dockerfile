FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . .
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Create startup script that clears logs
RUN echo '#!/bin/bash\n\
echo "Clearing logs..."\n\
rm -f logs/*.log 2>/dev/null || true\n\
echo "Starting application..."\n\
exec "$@"' > /start.sh && chmod +x /start.sh

CMD ["/start.sh", "/wait-for-it.sh", "db:5432", "--", "./mvnw", "spring-boot:run"]
