param(
    [string]$ip
)

if (-not $ip) {
    Write-Host "No IP address provided."
    exit 1
}

$ruleName = "Blocked IP - $ip"

# Check if rule exists
$existingRule = Get-NetFirewallRule -DisplayName $ruleName -ErrorAction SilentlyContinue
if ($existingRule) {
    # Remove the firewall rule
    Remove-NetFirewallRule -DisplayName $ruleName
    Write-Host "Unblocked IP: $ip"
} else {
    Write-Host "No existing rule found for IP: $ip"
}