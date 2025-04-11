param(
    [string]$ip
)

if (-not $ip) {
    exit 1
}

$ruleNameInbound = "Blocked-$ip-inbound"
$ruleNameOutbound = "Blocked-$ip-outbound"

# Check if rule already exists
$existingRuleInbound = Get-NetFirewallRule -DisplayName $ruleNameInbound -ErrorAction SilentlyContinue
if ($existingRule) {
    exit 0
}
$existingRuleOutbound = Get-NetFirewallRule -DisplayName $ruleNameOutbound -ErrorAction SilentlyContinue
if ($existingRule) {
    exit 1
}

# Block the IP (Inbound & Outbound)
New-NetFirewallRule -DisplayName $ruleNameInbound -Direction Inbound -Action Block -RemoteAddress $ip -LocalPort 8080 -Protocol TCP
New-NetFirewallRule -DisplayName $ruleNameOutbound -Direction Outbound -Action Block -RemoteAddress $ip -LocalPort 8080 -Protocol TCP

