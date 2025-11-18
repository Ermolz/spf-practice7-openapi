#!/bin/bash

# Script for testing Rate Limiting
# Usage: ./test-rate-limit.sh <token> <endpoint> <max_calls>
# Example: ./test-rate-limit.sh "your-jwt-token" "http://localhost:8080/api/books" 25

TOKEN=$1
ENDPOINT=$2
MAX_CALLS=${3:-25}

if [ -z "$TOKEN" ] || [ -z "$ENDPOINT" ]; then
    echo "Usage: $0 <token> <endpoint> [max_calls]"
    echo "Example: $0 'your-jwt-token' 'http://localhost:8080/api/books' 25"
    exit 1
fi

echo "Testing Rate Limiting for $ENDPOINT"
echo "Max calls: $MAX_CALLS"
echo "---"

SUCCESS=0
FAILED=0
RATE_LIMITED=0

for i in $(seq 1 $MAX_CALLS); do
    RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$ENDPOINT" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json")
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" -eq 200 ]; then
        SUCCESS=$((SUCCESS + 1))
        echo "[$i] ✅ Success (200)"
    elif [ "$HTTP_CODE" -eq 429 ]; then
        RATE_LIMITED=$((RATE_LIMITED + 1))
        echo "[$i] ⚠️  Rate Limited (429) - $BODY"
    else
        FAILED=$((FAILED + 1))
        echo "[$i] ❌ Failed ($HTTP_CODE) - $BODY"
    fi

    sleep 0.1
done

echo "---"
echo "Results:"
echo "  Successful: $SUCCESS"
echo "  Rate Limited: $RATE_LIMITED"
echo "  Failed: $FAILED"

sleep 10
