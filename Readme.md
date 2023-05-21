[![Publish](https://img.shields.io/github/actions/workflow/status/ayley/decode_huffman/publish_gradle.yml?style=for-the-badge&label=Publish)][publish]

[![Releases](https://img.shields.io/nexus/maven-releases/me.kleidukos/huffman-decoder?label=Release&logo=Release&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)][release]

[publish]: https://github.com/ayley/decode_huffman/actions/workflows/publish_gradle.yml
[release]: https://eldonexus.de/#browse/browse:maven-releases:me%2Fkleidukos%2Fhuffman-decoder

# Dependency
```kotlin
repositories {
    maven("https://eldonexus.de/repository/maven-public")
}

dependencies {
    implementation("me.kleidukos", "huffman-decoder", "version")
}
```