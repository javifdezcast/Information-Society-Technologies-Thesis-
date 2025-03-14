param(
    [string]$ip
)

if (-not $ip) {
    Write-Host "No IP address provided."
    exit 1
}

$ruleName = "Blocked IP - $ip"

# Check if rule already exists
$existingRule = Get-NetFirewallRule -DisplayName $ruleName -ErrorAction SilentlyContinue
if ($existingRule) {
    Write-Host "IP $ip is already blocked."
    exit 0
}

# Block the IP (Inbound & Outbound)
New-NetFirewallRule -DisplayName $ruleName -Direction Inbound -Action Block -RemoteAddress $ip
New-NetFirewallRule -DisplayName $ruleName -Direction Outbound -Action Block -RemoteAddress $ip

Write-Host "Blocked IP: $ip"
