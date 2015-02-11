# Workshop NoSQL

*Découverte de [MongoDB](http://www.mongodb.org/) et d'[Elasticsearch](http://www.elasticsearch.org/), par la pratique !*


## Prise en main de MongoDB

Quelques rappels avant de démarrer :

* MongoDB est une base de donnnées NoSQL, orientée documents.
* Le format des documents est JSON.
* Les documents sont stockés dans des collections.
* Une base de données MongoDB peut contenir plusieurs collections de documents.
* Il n'est pas possible d'effectuer de jointures entre collections (et ce n'est pas la philosophie).

Dans tous les cas, n'hésitez pas à vous référer à la [documentation officielle](http://docs.mongodb.org/manual/reference/).

### Installation

Téléchargez la dernière version stable de MongoDB sur [mongodb.org/downloads](https://www.mongodb.org/downloads). Ce workshop est basé sur la version 2.6.6 de MongoDB.

Dézippez le bundle dans le dossier de votre choix, par exemple `$HOME/progz/mongodb-2.6.6`.

Les exécutables nécessaires au fonctionnement de MongoDB se trouvent dans le dossier `$HOME/progz/mongodb-2.6.6/bin`.

Pour plus de facilités, vous pouvez ajouter ce dossier à votre `PATH`, afin que les commandes `mongod` et `mongo` soient directement accessibles.
Par exemple sous Linux, ajoutez les lignes suivantes à votre fichier `.profile` :

```bash
# Path to MongoDB binaries
PATH="$HOME/progz/mongodb-2.6.6/bin:$PATH"
export PATH
```

Par défaut, MongoDB stocke ses données dans le dossier `/data/db`. Cela peut être modifié via le paramètre `--dbpath`

Vous pouvez donc créer un dossier spécifique pour stocker les données du workshop, par exemple `$HOME/data/nosql-workshop` :

```bash
mkdir -p "$HOME/data/nosql-workshop"
```

Démarrez MongoDB à l'aide de la commande suivante :

```bash
mongod --dbpath="$HOME/data/nosql-workshop"
```

### Prise en main du shell

MongoDB propose un shell Javascript interactif permettant de se connecter à une instance (démarrée via la commande `mongod`, comme précédemment).

Pour lancer le shell :

```bash
mongo
```

Par défaut, le shell se connecte à l'instance `localhost` sur le port `27017`, sur la base `test` :

```
MongoDB shell version: 2.6.6
connecting to: test
```

Le shell met à disposition un objet Javascript `db` qui permet d'interagir avec la base de données. Par exemple pour obtenir de l'aide :

```javascript
db.help()
```

Pour visualiser les bases disponibles :

```
show dbs
```

Pour changer de base de données, par exemple `workshop` (MongoDB crée automatiquement la base si elle n'existe pas) :

```
use workshop
```

Pour insérer un document dans une collection (la collection est créée automatiquement si elle n'existe pas encore) :

```javascript
db.personnes.insert({ "prenom" : "Jean", "nom" : "DUPONT" })
```

Pour afficher un document :

```javascript
db.personnes.findOne()
```

MongoDB génère automtiquement un identifiant unique pour chaque document, dans l'attribut `_id`. Cet identifiant peut être défini manuellement :

```javascript
db.personnes.insert({ "_id" : "jdupont", "prenom" : "Jean", "nom" : "DUPONT" })
```

Pour voir la liste des collections d'une base de données :

```
show collections
```

### Opérations CRUD

#### Recherche : find()

La méthode `find()` possède deux paramètres (optionnels) :

* le critère de recherche
* la projection (les attributs à retourner)

Par exemple, pour rechercher toutes les personnes se nommant "DUPONT" :

```javascript
db.personnes.find({ "nom" : "DUPONT" })
```

Si vous ne souhaitez retourner que les noms et prénoms, sans l'identifiant :

```javascript
db.personnes.find({ "nom" : "DUPONT" }, {"_id" : 0, "nom" : 1, "prenom" : 1})
```

Il est également possible de renommer un attribut dans la projection. Par exemple pour renomme le "nom" en "nom_de_famille" :

```javascript
db.personnes.find({ "nom" : "DUPONT" }, {"_id" : 0, "nom_de_famille" : "$nom", "prenom" : 1})
```

#### Insertion : insert()

L'insertion d'un document se fait via la méthode `insert()`, comme vu précédemment lors de la prise en main du Shell.

Le Shell étant un interpréteur Javascript, il est possible d'insérer plusieurs documents à l'aide d'une boucle `for` :

```javascript
for (var i = 1 ; i <= 100 ; i++) {
    db.personnes.insert({ "prenom" : "Prenom" + i, "nom" : "Nom" + i, "age" : (Math.floor(Math.random() * 50) + 20) })
}
```

#### Mise à jour

La mise à jour de documents se fait via la méthode `update()`, qui possède plusieurs paramètres :

* le filtre permettant de sélectionner les documents à mettre à jour
* la requête de mise à jour
* des options (par exemple : `{"multi" : true}` pour mettre à jour tous les documents correspondant au filtre)

Par exemple, pour répartir les personnes dans deux catégories ("Master" pour les plus de 40 ans, "Junior pour les autres") :

```javascript
db.personnes.update({"age" : { "$gte" : 40 }}, {"$set" : {"categorie" : "Master"}}, {"multi" : true})
db.personnes.update({"age" : { "$lt" : 40 }}, {"$set" : {"categorie" : "Junior"}}, {"multi" : true})
```

Remarque : MongoDB a créé automatiquement l'attribut "categorie" qui n'existait pas auparavant !

#### Suppression

La méthode `remove()` permet de supprimer des documents étant donné un filtre :

```javascript
db.personnes.remove({ "nom" : "DUPONT" })
```

Pour supprimer une collection :

```javascript
db.personnes.drop()
```

### Tableaux

Il est possible d'utiliser des tableaux dans les documents. Par exemple, on peut socker les compétences des personnes de la manière suivante :

```javascript
{
    "_id": "jdupont",
    "prenom": "Jean",
    "nom": "DUPONT",
    "competences" : [
        "Java",
        "Javascript",
        "HTML"
    ]
}
```

Pour rechercher les personnes possédant la compétence "Java" :

```javascript
db.personnes.find({ "competences" : "Java" })
```

Pour ajouter une compétence :

```javascript
db.personnes.update({ "_id" : "jdupont" }, {"$push" : {"competences" : "CSS"}})
```

Pour éviter les doublons :

```javascript
db.personnes.update({ "_id" : "jdupont" }, {"$addToSet" : {"competences" : "CSS"}})
```

Pour enlever une compétence :

```javascript
db.personnes.update({ "_id" : "jdupont" }, {"$pull" : {"competences" : "CSS"}})
```

## Prise en main d'Elasticsearch

TODO

## Application Java

L'objectif est de développer une application manipulants des données relatives aux installations sportives de la région Pays de la Loire.

Les données sont issues de [http://data.paysdelaloire.fr](http://data.paysdelaloire.fr).

Trois jeux de données vont particulièrement nous intéresser et sont disponibles dans le projet (format CSV) :

* [Installations](http://data.paysdelaloire.fr/donnees/detail/equipements-sportifs-espaces-et-sites-de-pratiques-en-pays-de-la-loire-fiches-installations)
* [Equipements](http://data.paysdelaloire.fr/donnees/detail/equipements-sportifs-espaces-et-sites-de-pratiques-en-pays-de-la-loire-fiches-equipements)
* [Activités](http://data.paysdelaloire.fr/donnees/detail/equipements-sportifs-espaces-et-sites-de-pratiques-en-pays-de-la-loire-activites-des-fiches-equ)

Des liens existent entre les trois jeux de données :

* une installation possède un ou plusieurs équipements
* une ou plusieurs activités peuvent être pratiquées sur un équipement donné.

![model](assets/model.png)

### Import des données dans MongoDB

La première tâche consiste à créer la collection des installations sportives à partir des trois fichiers CSV, en utilisant le driver MongoDB natif Java.

Pour cela, recherchez les `TODO` dans le code du module `batch`, complétez le code pour obtenir des documents de cette forme :

```javascript
{
    "_id": "440390003",
    "nom": "La Pierre Tremblante",
    "adresse": {
        "numero": "",
        "voie": "Chemin des rives",
        "lieuDit": "",
        "codePostal": "44640",
        "commune": "Cheix-en-Retz"
    },
    "location": {
        "type": "Point",
        "coordinates": [
            -1.816274,
            47.181243
        ]
    },
    "multiCommune": false,
    "nbPlacesParking": 0,
    "nbPlacesParkingHandicapes": 0,
    "dateMiseAJourFiche": ISODate("2014-06-18T00:00:00Z"),
    "equipements": [
        {
            "numero": "191989",
            "nom": "La Pierre tremblante",
            "type": "Point d'embarquement et de débarquement isolé",
            "famille": "Site d'activités aquatiques et nautiques",
            "activites": [
                "Canoë de randonnée",
                "Pêche au coup en eau douce"
            ]
        }
    ]
}
```

### Services Java

TODO

### Recherche full-text

#### Avec MongoDB

On peut par exemple positionner un index de type "text" sur le nom de l'installation et sa commune, en mettant un poids plus important pour la commune :

```javascript
db.installations.ensureIndex(
    {
        "nom" : "text",
        "adresse.commune" : "text"
    },
    {
        "weights" : {
            "nom" : 3,
            "adresse.commune" : 10
        },
        "default_language" : "french"
    }
)
```

Ensuite, on peut par exemple rechercher les "Ports" de la ville de "Carquefou", en triant par pertinance et en ne conservant que les 10 premiers résultats :

```javascript
db.installations.find(
    {
        "$text": {
            "$search": "Port Carquefou",
            "$language" : "french"
        }
    },
    {
        "score": {"$meta": "textScore"}
    }
)
.sort({"score": {"$meta": "textScore"}})
.limit(10)
```

### Recherche géographique

TODO

### Import des données dans Elasticsearch

TODO
