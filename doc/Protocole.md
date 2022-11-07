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



|        Fonction         |   Paramètres   | Type de données récupérées |                             Description                              | Exemple |
|:------------------------|:---------------|:---------------------------|:---------------------------------------------------------------------|:--------|
| Lister tous les trajets |                |    Liste d'utilisateurs et de trajets | Récupère tous les trajets pour tous les utilisateurs      | [Exemple de données](#exemple-1) |
| Lister tous les trajets d'un utilisateur | utilisateur | Liste de trajets| Récupère tous les enregistrements pour un user donné              | [Exemple de données](#exemple-2) |
| Donner les informations pour un trajet   | trajet | Informations du trajet        | Récupère les informations d'un trajet donnée             | [Exemple de données](#exemple-3) |
| Ajouter un trajet pour un utilisateur    | utilisateur, trajet |                        | Ajoute un trajet pour un utilisateur               |
| Supprimer un trajet pour utilisateur     | utilisateur, trajet   |                            | Supprime un enregistrement donné pour un utilisateur donnée | |
| Modifier un trajet pour un utilisateur | champ à modifier, la nouvelle valeur | | Modifie un trajet selon les paramètres donnés              | |

# Exemples
## Exemple 1
```json
    json
    {
        "user_1" =>{
            "trajet_1" =>{
                "route" =>{
                    [0°N, 0°E],
                    [0°N, 0°E],
                    ...
                },
                viteses =>{
                    0,
                    0,
                    ...
                }
                temps => "0000s"
            },
            "trajet_2" =>{
                "route" =>{
                    [0°N, 0°E],
                    [0°N, 0°E],
                    ...
                },
                viteses =>{
                    0,
                    0,
                    ...
                }
                temps => "0000s"
            },
        },
        ...
    }
```

## Exemple 2
```json
    json
    {
        "user_1" =>{
            "trajet_1" =>{
                "route" =>{
                    [0°N, 0°E],
                    [0°N, 0°E],
                    ...
                },
                viteses =>{
                    0,
                    0,
                    ...
                }
                temps => "0000s"
            },
            "trajet_2" =>{
                "route" =>{
                    [0°N, 0°E],
                    [0°N, 0°E],
                    ...
                },
                viteses =>{
                    0,
                    0,
                    ...
                }
                temps => "0000s"
            },
        }
    }
```

## Exemple 3
```json
    json
    {
        "trajet_1"=>{
            "route" =>{
                    [0°N, 0°E],
                    [0°N, 0°E],
                    ...
            },
            "viteses" =>{
                0,
                0,
                ...
            },
            "temps" => "0000s"
        }
    }
