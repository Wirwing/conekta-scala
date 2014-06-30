package com

import org.apache.http.conn.ClientConnectionManager

package object conekta {
    var apiKey: String = ""
    var ApiBase: String = "https://api.conekta.io"
    var connectionManager: ClientConnectionManager = null
}
