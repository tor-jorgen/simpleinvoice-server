package org.simpleinvoice.server.config

class SecurityConfig(
    val clientId: String,
    val clientSecret: String,
    val allowHosts: List<String>,
) {
    fun allowHostsAndSchemas(): Map<String, MutableList<String>> {
        val hosts = mutableMapOf<String, MutableList<String>>()
        allowHosts.map {
            val parts = it.split("://")
            val protocol: String? = parts.getOrNull(0)
            val host: String? = parts.getOrNull(1)
            if (host != null && protocol != null) {
                if (!hosts.contains(host)) {
                    hosts[host] = mutableListOf()
                }
                hosts[host]?.add(protocol)
            }
        }
        return hosts.toMap()
    }
}
