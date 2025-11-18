# PowerShell script for testing Rate Limiting
# Usage: .\test-rate-limit.ps1 -Token "your-jwt-token" -Endpoint "http://localhost:8080/api/books" -MaxCalls 25

param(
    [Parameter(Mandatory=$true)]
    [string]$Token,
    
    [Parameter(Mandatory=$true)]
    [string]$Endpoint,
    
    [Parameter(Mandatory=$false)]
    [int]$MaxCalls = 25
)

Write-Host "Testing Rate Limiting for $Endpoint" -ForegroundColor Cyan
Write-Host "Max calls: $MaxCalls" -ForegroundColor Cyan
Write-Host "---"

$success = 0
$failed = 0
$rateLimited = 0

$headers = @{
    "Authorization" = "Bearer $Token"
    "Content-Type" = "application/json"
}

for ($i = 1; $i -le $MaxCalls; $i++) {
    try {
        $response = Invoke-WebRequest -Uri $Endpoint -Method GET -Headers $headers -ErrorAction Stop
        
        if ($response.StatusCode -eq 200) {
            $success++
            Write-Host "[$i] ✅ Success (200)" -ForegroundColor Green
        } else {
            $failed++
            Write-Host "[$i] ❌ Failed ($($response.StatusCode))" -ForegroundColor Red
        }
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        
        if ($statusCode -eq 429) {
            $rateLimited++
            $errorBody = $_.Exception.Response | ConvertFrom-Json -ErrorAction SilentlyContinue
            Write-Host "[$i] ⚠️  Rate Limited (429) - $($errorBody.message)" -ForegroundColor Yellow
        } else {
            $failed++
            Write-Host "[$i] ❌ Failed ($statusCode) - $($_.Exception.Message)" -ForegroundColor Red
        }
    }
    
    # Small delay
    Start-Sleep -Milliseconds 100
}

Write-Host "---"
Write-Host "Results:" -ForegroundColor Cyan
Write-Host "  Successful: $success" -ForegroundColor Green
Write-Host "  Rate Limited: $rateLimited" -ForegroundColor Yellow
Write-Host "  Failed: $failed" -ForegroundColor Red

