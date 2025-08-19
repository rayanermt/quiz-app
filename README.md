# Quiz App — UFU (PDM)

Aplicativo Android de perguntas e respostas com autenticação via Google/Firebase, sincronização de questões e histórico local com Room. Desenvolvido em equipe para a disciplina de Programação para Dispositivos Móveis (PDM) — FACOM/UFU.

## 📦 Entregáveis

Os arquivos entregáveis estão na pasta deliverable/ na raiz do repositório:
```
deliverable
├── cronograma.pdf
├── quiz-app.apk
├── relatório.pdf
├── slides.pdf
└── vídeo.mp4
```

---

## 📱 Visão geral

* **Login e autenticação**: Google Sign-In (Firebase Auth).
* **Fonte de dados**: Cloud Firestore.
* **Armazenamento local**: Room (categorias, questões e sessões do usuário).
* **Sincronização**: download das categorias/questões do Firestore e *upsert* no Room; resultados de sessões salvos localmente e no Firestore.
* **Telas**: Login, Lista de Categorias, Quiz, Histórico, Ranking.
* **Arquitetura**: MVVM + Repositórios + Data Sources, com `AppContainer` para composição de dependências.

> **Requisitos atendidos** (resumo): autenticação individual, cache local das perguntas, execução de quiz com pontuação e tempo, histórico pessoal, UI com telas principais e ranking. Ver detalhes nas seções abaixo.

---

## 👥 Equipe e responsabilidades

* **UI/UX** — *Rayane Reis Motta*: layout das telas (Login, Categorias, Quiz, Histórico, Ranking) e experiência do usuário.
* **Comunicação com Firebase** — *Pedro Henrique Lopes Duarte*: integração com Firebase Auth e Cloud Firestore; estrutura das coleções e sincronização de dados remotos.
* **Arquitetura e Gerenciamento Local** — *Gabriel Augusto Paiva*: modelagem de dados, Room (entities/DAOs/DB), repositórios e integração MVVM.

---

## 🧱 Arquitetura

Camadas principais:

* **Presentation (MVVM)**

    * Activities e ViewModels:

        * `presentation/auth/LoginActivity`, `LoginViewModel`
        * `presentation/categories/MainActivity`, `CategoriesViewModel`
        * `presentation/quiz/QuizActivity`, `QuizViewModel`
        * `presentation/history/HistoryActivity`, `HistoryViewModel`
        * `presentation/ranking/RankingActivity`, `RankingViewModel`

* **Domain**

    * Modelos: `Category`, `Question`, `SessionItem`, `User`, `LeaderboardEntry`.
    * Contratos: `AuthRepository`, `QuestionsRepository`, `SessionsRepository`, `UsersRepository`.

* **Data**

    * **Local (Room)**

        * DB: `QuizDatabase`
        * Entidades: `CategoryEntity`, `QuestionEntity`, `QuizSessionEntity`
        * DAOs: `CategoryDao`, `QuestionDao`, `SessionDao`
    * **Remoto (Firestore/Auth)**

        * Data sources: `UsersRemoteDataSource`, `QuestionsRemoteDataSource`, `SessionsRemoteDataSource`, `FirebaseAuthDataSource`
    * **Repositórios**

        * `AuthRepositoryImpl`, `UsersRepositoryImpl`
        * `QuestionsRepositoryImpl` (sync + queries locais)
        * `SessionsRepositoryImpl` (persistência local e envio ao Firestore)

* **Injeção simples**

    * `AppContainer` inicializa `FirebaseAuth`, `FirebaseFirestore`, `Room` e expõe os repositórios para as Activities/ViewModels.

**Padrões adotados**

* MVVM com `StateFlow`/`ViewModelScope` para estados de UI.
* Mapeamento DTO ⇄ Entity ⇄ Domain centralizado em `data/mapper/EntityMappers.kt`.
* Sincronismo *pull-based* (ver "Sincronização de dados").

---

## 🗄️ Modelo de dados

### Firestore (estrutura sugerida/implementada)

```
/users/{uid}
  email, displayName, photoUrl, createdAtMs, updatedAtMs, totalScore
  /quiz_history/{sessionId}
    categoryId, quizName, correctCount, total, durationMs, completedAt (Timestamp)

/categories/{categoryId}
  name
  /questions/{questionId}
    text, options [String], correctIndex (Int), updatedAt (Timestamp)
```

### Room (local)

* **category** (`CategoryEntity`)

    * `id: String` (PK)
    * `name: String`
* **question** (`QuestionEntity`)

    * `id: String` (PK)
    * `categoryId: String`
    * `text: String`
    * `optionsJson: String` (lista serializada)
    * `correctIndex: Int`
    * `updatedAt: Long`
* **quiz\_session** (`QuizSessionEntity`)

    * `id: String` (PK)
    * `userId: String`
    * `categoryId: String`
    * `quizName: String`
    * `startedAtMs: Long`, `endedAtMs: Long`
    * `correctCount: Int`, `total: Int`, `durationMs: Long`

---

## 🔄 Sincronização de dados

* **Questões/Categorias → Local**

    * `QuestionsRepositoryImpl.syncAll()` baixa categorias e questões do Firestore e faz **upsert** no Room (`OnConflictStrategy.REPLACE`). O campo `updatedAt` é preservado para rastrear atualizações em perguntas.
    * Fluxo acionado no `CategoriesViewModel.refresh()` (primeira carga/atualização manual do usuário).

* **Resultados de Quiz**

    * Ao finalizar uma sessão, `SessionsRepository` salva a sessão localmente (`quiz_session`) e envia um resumo para `/users/{uid}/quiz_history` no Firestore.
    * O **ranking** consulta `/users` ordenando por `totalScore` e exibe os Top N.

> Observação: O app utiliza sincronismo **pull** mecânico (por evento de UI). Observadores em tempo real (snapshot listeners) podem ser incluídos como evolução, mas para este trabalho priorizamos a consistência offline-first via Room.

---

## 🧭 Fluxo de uso

1. **Login** (Google/Firebase Auth).
2. **Categorias**: sincroniza e lista categorias do banco local; o usuário escolhe uma (Cinema, Ciência, História).
3. **Quiz**: carrega 5 questões aleatórias da categoria (a partir do Room), controla tempo e pontuação.
4. **Resultado**: ao finalizar, persiste a sessão localmente e no Firestore.
5. **Histórico**: lista sessões do usuário em ordem decrescente de conclusão.
6. **Ranking**: mostra os usuários com maior `totalScore`.

---

## 🧰 Requisitos de build

* **Android Studio** Koala ou superior.
* **SDK**: `compileSdk = 36`, `targetSdk = 36`, `minSdk = 24`.
* **Kotlin** com corrotinas e Jetpack (ViewModel, Lifecycle, RecyclerView, etc.).
* **Dependências principais**: Firebase Auth/Firestore, Room, Gson.

### Configuração do Firebase

1. Criar o projeto no Firebase Console e habilitar **Authentication (Google)** e **Firestore**.
2. Baixar e colocar `app/google-services.json` no módulo `app/`.
3. No Google Cloud Console, criar o **OAuth Client ID** para Android e registrar os **SHA-1/SHA-256** da assinatura do app.
4. (Opcional para testes) Regras permissivas temporárias no Firestore apenas em desenvolvimento.

### Executar

* Sincronize o Gradle e execute em um dispositivo/emulador com Google Play Services.

### Gerar APK

* **Debug**: `Build > Build Bundle(s)/APK(s) > Build APK(s)`.
* **Release**: `Build > Generate Signed App Bundle / APK...` (use um keystore de teste).

---

## 🗂️ Estrutura de pastas (resumo)

```
app/
  src/main/java/com/pdm/quiz/
    app/ (QuizApp, AppContainer)
    data/
      local/ (entity, dao, db)
      remote/ (auth, firestore, dto)
      repository/impl
      mapper/
    domain/
      model/, repository/
    presentation/
      auth/, categories/, quiz/, history/, ranking/
    ui/theme/
  src/main/res/layout/ (telas XML)
```

---

## ✅ Checklist de funcionalidades

* [x] Login via Google (Firebase Auth)
* [x] Perfil de usuário persistido no Firebase (coleção `users`)
* [x] Download de categorias/questões do Firestore
* [x] Armazenamento local (Room) e uso **offline**
* [x] Execução do quiz com pontuação e tempo
* [x] Histórico por usuário (local) e envio de resultados para Firestore
* [x] Ranking (Top N por `totalScore`)
* [x] Arquitetura MVVM + Repositórios

---

## 📌 Decisões de projeto

* **Room como fonte única da verdade** para a camada de leitura em tempo de execução (performático e resiliente offline).
* **Sincronização por *pull*** acionada pela UI para simplificar estados e evitar inconsistências em modo demo.
* **`AppContainer`** como composição simples de dependências, sem DI framework (Hilt/Dagger), reduzindo curva de aprendizado.
