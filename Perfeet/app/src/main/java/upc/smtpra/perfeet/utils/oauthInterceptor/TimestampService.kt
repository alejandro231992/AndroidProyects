package upc.smtpra.perfeet.utils.oauthInterceptor

interface TimestampService {
    val timestampInSeconds: String
    val nonce: String
}