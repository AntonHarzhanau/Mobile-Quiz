# MusicalQuiz : Rapport de Projet

---

## 1. Description des fonctionnalités de l'application

L'application **MusicalQuiz** offre les fonctionnalités suivantes :

- **Recherche de musique (pistes et albums)** avec affichage des résultats (pochette, titre, artiste).
- **Consultation des détails** sur les pistes et les albums, avec lecture d'extraits musicaux courts.
- **Création et gestion des playlists** avec possibilité d'ajouter des pistes.
- **Création et réalisation de quiz** basés sur les playlists personnelles.
### Captures d'écran de l'application : dans dossier screenshots
---

## 2. Architecture technique

L'application est construite selon le modèle **MVVM** (Model-View-ViewModel) :

```
MusicalQuiz (MVVM)
.
├── MainActivity (Activité unique)
│
├── Couche UI (Fragments)
│   ├── SearchFragment
│   ├── AlbumDetailsFragment
│   ├── TrackDetailsFragment
│   ├── PlayListFragment, CreatePlayListFragment, PlaylistDetailFragment
│   └── QuizListFragment, CreateQuizFragment, CreateQuestionFragment,
│       QuizDetailsFragment, QuizGameFragment, QuizResultFragment,HomeFragment
│
├── Couche ViewModel
│   ├── SearchViewModel
│   ├── PlayListViewModel
│   ├── CreateQuizViewModel, CreateQuestionViewModel
│   ├── QuizListViewModel, QuizDetailsViewModel, QuizGameViewModel
│
├── Couche Repository
│   ├── DeezerApi (Retrofit)
│   ├── QuizRepository, PlayListRepository, QuestionRepository, AnswerRepository (Room)
│
└── Couche de données
    ├── Base de données Room (Entités et DAOs)
    └── DTOs réseau et interfaces API (Deezer API)
```

### Décisions architecturales prises :
- Approche Single Activity avec des fragments pour simplifier la navigation.
- ViewModel et LiveData pour gérer l'état de l'interface utilisateur et sauvegarder l'état lors des changements de configuration.
- Room pour le stockage local et Retrofit avec Kotlin Coroutines pour les requêtes réseau asynchrones.

---

## 3. Problèmes techniques non résolus
- Comportement incorrect du MediaPlayer lors de la rotation de l'écran (erreurs d'état).
- Implémentation inachevée de la sélection d'images depuis la galerie.
- Absence de mise en cache du contenu multimédia (images et audio).
- Absence d'un véritable mode hors ligne.
- pour le homePage, navigation et actions de sbouttons
---

## Contribution des participants

**Harzhanau Anton** :
- Architecture du projet (MVVM)
- Gestion des appels réseau vers l'API Deezer (Retrofit, Coroutines)
- Implémentation de la base de données locale (Room)
- Logique des ViewModels et gestion de l'état
- Intégration du lecteur multimédia (MediaPlayer)

**Adamah Olivia** :
- Design et réalisation de l'interface utilisateur (Fragments, RecyclerView, adaptateurs, Layout)
- Implémentation de la navigation et interactions utilisateur
- Récupération des "Top Hits du moment" :Appele  d'un point de terminaison spécifique de l'API Deezer (chart pour pour obtenir une liste des chansons les plus populaires et tendance du moment). 
- Tests et débogage de l'application