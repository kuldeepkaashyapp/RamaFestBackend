package com.rama.fest.ramafest.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ClientIpResolver {

    private static final Pattern FORWARDED_FOR =
            Pattern.compile("(?i)(?:^|[,;])\\s*for\\s*=\\s*(\"[^\"]*\"|[^;,]+)");

    private final List<CidrRange> trustedProxies;

    public ClientIpResolver(
            @Value("${app.audit.trusted-proxies:127.0.0.1/32,::1/128}") String trustedProxyRanges) {
        this.trustedProxies = Arrays.stream(trustedProxyRanges.split(","))
                .map(String::trim)
                .filter(range -> !range.isEmpty())
                .map(this::parseRange)
                .filter(range -> range != null)
                .toList();
    }

    public String resolve(HttpServletRequest request) {
        String connectionIp = normalizeIp(request.getRemoteAddr());
        if (connectionIp == null || !isTrustedProxy(connectionIp)) {
            return request.getRemoteAddr();
        }

        List<String> forwardedIps = validIps(request.getHeader("X-Forwarded-For"));
        if (forwardedIps.isEmpty()) {
            forwardedIps = forwardedIps(request.getHeader("Forwarded"));
        }
        if (forwardedIps.isEmpty()) {
            String realIp = normalizeIp(request.getHeader("X-Real-IP"));
            if (realIp != null) {
                return realIp;
            }
            return request.getRemoteAddr();
        }

        String candidate = connectionIp;
        for (int index = forwardedIps.size() - 1; index >= 0; index--) {
            if (!isTrustedProxy(candidate)) {
                return candidate;
            }
            candidate = forwardedIps.get(index);
        }
        return candidate;
    }

    private List<String> forwardedIps(String header) {
        if (header == null || header.isBlank()) {
            return List.of();
        }

        List<String> ips = new ArrayList<>();
        Matcher matcher = FORWARDED_FOR.matcher(header);
        while (matcher.find()) {
            String ip = normalizeIp(matcher.group(1));
            if (ip != null) {
                ips.add(ip);
            }
        }
        return ips;
    }

    private List<String> validIps(String header) {
        if (header == null || header.isBlank()) {
            return List.of();
        }
        return Arrays.stream(header.split(","))
                .map(this::normalizeIp)
                .filter(ip -> ip != null)
                .toList();
    }

    private String normalizeIp(String value) {
        if (value == null) {
            return null;
        }

        String ip = value.trim().replace("\"", "");
        if (ip.startsWith("[") && ip.contains("]")) {
            ip = ip.substring(1, ip.indexOf(']'));
        } else if (ip.indexOf(':') == ip.lastIndexOf(':') && ip.contains(":")) {
            ip = ip.substring(0, ip.lastIndexOf(':'));
        }
        if (!ip.matches("[0-9A-Fa-f:.]+") || ip.isEmpty()) {
            return null;
        }

        try {
            return InetAddress.getByName(ip).getHostAddress();
        } catch (UnknownHostException exception) {
            return null;
        }
    }

    private boolean isTrustedProxy(String ip) {
        return trustedProxies.stream().anyMatch(range -> range.contains(ip));
    }

    private CidrRange parseRange(String value) {
        String[] parts = value.split("/", 2);
        String address = normalizeIp(parts[0]);
        if (address == null) {
            return null;
        }

        int prefixLength;
        try {
            prefixLength = parts.length == 2
                    ? Integer.parseInt(parts[1])
                    : InetAddress.getByName(address).getAddress().length * 8;
        } catch (NumberFormatException | UnknownHostException exception) {
            return null;
        }

        byte[] addressBytes;
        try {
            addressBytes = InetAddress.getByName(address).getAddress();
        } catch (UnknownHostException exception) {
            return null;
        }

        if (prefixLength < 0 || prefixLength > addressBytes.length * 8) {
            return null;
        }
        return new CidrRange(addressBytes, prefixLength);
    }

    private record CidrRange(byte[] network, int prefixLength) {
        private boolean contains(String value) {
            try {
                byte[] address = InetAddress.getByName(value).getAddress();
                if (address.length != network.length) {
                    return false;
                }
                int fullBytes = prefixLength / 8;
                int remainingBits = prefixLength % 8;
                for (int index = 0; index < fullBytes; index++) {
                    if (address[index] != network[index]) {
                        return false;
                    }
                }
                if (remainingBits == 0) {
                    return true;
                }
                int mask = 0xFF << (8 - remainingBits);
                return (address[fullBytes] & mask) == (network[fullBytes] & mask);
            } catch (UnknownHostException exception) {
                return false;
            }
        }
    }
}
