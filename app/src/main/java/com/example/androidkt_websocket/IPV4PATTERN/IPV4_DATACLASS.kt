package com.example.androidkt_websocket.IPV4PATTERN

data class IPV4_DATACLASS (
    val iPV4PATTERN: Regex =
        """^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$""".toRegex(),
    val portPATTERN: Regex = """^(10|[1-9][0-9]{1,3}|9999)$""".toRegex()
)