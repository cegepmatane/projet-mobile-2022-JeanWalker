# Modèle de données Firebase
Référence de la documentation google : [modèles de données](https://firebase.google.com/docs/firestore/data-model)
---
Firebase utilise un modèle de données NoSQL se basant sur des documents. Chaque document est identifié par un nom (fonctionnant comme son id) et contient des données étiquettées. Ces documents se trouvent dans des collections, contenant des documents qui peuvent ne pas avoir tous les même champs.

La récupération de données se fait dans le code, via des `CollectionReference` pour récupérer une collection, et `DocumentReference` pour des documents. 
```java
// Exemple de récupération des données d'un document
DocumentReference docRef = db.collection("cities").document("SF");
docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
            } else {
                Log.d(TAG, "No such document");
            }
        } else {
            Log.d(TAG, "get failed with ", task.getException());
        }
    }
});
// Exemple pris de la documentation google du Cloud Firestore → Lire des données → Obtenir les données → Obtenir un document
```

# API

|        Fonction         |   Paramètre   | Type de données récupérées |                             Description                              |
|:------------------------|:--------------|:---------------------------|:---------------------------------------------------------------------|
| GetAllEnregistrements() |               |    `Collectionreference`   | Récupère tous les enregistrements de la collection 'enregistrements' |
| GetEnregistrementsForUser() | `String user` | `List<DocumentReference>`| Récupère tous les enregistrements pour un user donné               |
| GetEnregistrementInfo() | `String id`   | `DocumentReference`        | Récupère les informations d'un document précis                       |
| AddEnregistrement()     | `Enregistrement e`|                        | Ajoute un enregistrement dans la collection des enregistrements      |
| DeleteEnregistrement()  | `String id`   |                            | Supprime un enregistrement donné                                     |
