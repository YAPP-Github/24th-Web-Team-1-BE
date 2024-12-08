package email.config.properties

data class AwsEmailProviderProperties(
    var accessKey: String = "",
    var secretKey: String = "",
    var region: String = "",
)