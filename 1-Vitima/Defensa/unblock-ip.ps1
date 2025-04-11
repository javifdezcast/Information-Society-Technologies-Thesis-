param(
    [string]$ip
)

if (-not $ip) {
    exit 1
}

$ruleNameInbound = "Blocked-$ip-inbound"
$ruleNameOutbound = "Blocked-$ip-outbound"

# Check if rule exists
$existingRuleInbound = Get-NetFirewallRule -DisplayName $ruleNameInbound -ErrorAction SilentlyContinue
$existingRuleOutnbound = Get-NetFirewallRule -DisplayName $ruleNameOutbound -ErrorAction SilentlyContinue

if (existingRuleInbound) {
    # Remove the firewall rule
    Remove-NetFirewallRule -DisplayName $ruleNameInbound
} 
if (existingRuleOutnbound) {
    # Remove the firewall rule
    Remove-NetFirewallRule -DisplayName $ruleNameOutbound
} 