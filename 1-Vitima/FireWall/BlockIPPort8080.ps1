# Filename: BlockIPPort8080.ps1
param(
    [Parameter(Mandatory=$true)]
    [string]$BannedIP
)

# Validate the IP address format
if (-not ($BannedIP -match '^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)$')) {
    Write-Host "Invalid IP address format. Please provide a valid IP address." -ForegroundColor Red
    exit 1
}

# Define the firewall rule name
$RuleName = "Block HTTP from $BannedIP to Port 8080"

# Create the firewall rule to block traffic
try {
    New-NetFirewallRule -DisplayName $RuleName `
                        -Direction Inbound `
                        -Protocol TCP `
                        -LocalPort 8080 `
                        -RemoteAddress $BannedIP `
                        -Action Block
    Write-Host "Successfully blocked IP address $BannedIP from accessing port 8080." -ForegroundColor Green
} catch {
    Write-Host "An error occurred while creating the firewall rule: $_" -ForegroundColor Red
}
