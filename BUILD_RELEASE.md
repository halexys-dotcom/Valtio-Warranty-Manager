# Valtio — Build de Release (APK para Play Store)

## Pré-requisitos
- Android Studio Hedgehog+ (2023.1+)
- JDK 17 (Eclipse Adoptium)
- Android SDK 34

## Passos para Gerar a APK Assinada

### Opção 1: Android Studio (Recomendado)

1. Abrir o projeto: `File → Open → C:\Users\hah_c\Desktop\Valtio`
2. Aguardar a sincronização do Gradle (Gradle Sync)
3. Menu: `Build → Generate Signed Bundle / APK...`
4. Selecionar **APK**
5. Em **Key store path**: selecionar `valtio-release.jks` (na raiz do projeto)
6. Preencher:
   - Key store password: `Valtio2026`
   - Key alias: `valtio`
   - Key password: `Valtio2026`
7. Selecionar **release** como build variant
8. Marcar **V1 (Jar Signature)** e **V2 (Full APK Signature)**
9. Clicar **Finish**

A APK será gerada em:
```
app/release/app-release.apk
```

### Opção 2: Linha de Comandos (se tiver JDK + Android SDK)

```batch
set ANDROID_HOME=C:\Users\hah_c\AppData\Local\Android\Sdk
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot
gradlew assembleRelease
```

## Configuração de Assinatura (já configurada)

- **Keystore**: `valtio-release.jks` (raiz do projeto)
- **Alias**: `valtio`
- **Password**: `Valtio2026`
- **Validade**: 10.000 dias (≈ 27 anos)
- **Algoritmo**: RSA 2048 + SHA256withRSA

## Build Config (app/build.gradle.kts)

- `versionCode = 1`
- `versionName = "1.0.0"`
- `minSdk = 26` (Android 8.0)
- `targetSdk = 34` (Android 14)
- `isMinifyEnabled = true` (R8)
- `isShrinkResources = true`
- Assinatura: `signingConfigs.release`

## Play Store — Informações para Publicação

### Idiomas Suportados
- Português (pt)
- Inglês (en)
- Español (es)
- Français (fr)
- Deutsch (de)
- Italiano (it)
- Nederlands (nl)

### Metadata Play Store
As descrições curta e longa já estão nos ficheiros `strings.xml` de cada idioma.
Podem ser extraídas para o Google Play Console.

### Conteúdo da APK
- Tamanho estimado: ~9-12 MB (Compose + Room + Hilt + Coil + iText7)
- Permissões: POST_NOTIFICATIONS, READ_MEDIA_IMAGES, CAMERA
- Target audience: Geral
- Classificação etária: Everybody (sem conteúdo sensível)

## ⚠️ Importante

**GUARDE a keystore `valtio-release.jks` em local seguro e NUNCA a perca.**
Sem esta keystore, não será possível publicar atualizações da aplicação na Play Store.
Guarde também a password num gestor de passwords seguro.