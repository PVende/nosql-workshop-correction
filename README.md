# Workshop NoSQL

*Découverte de [MongoDB](http://www.mongodb.org/) et d'[Elasticsearch](http://www.elasticsearch.org/), par la pratique !*

Au programme :

* [Prise en main de MongoDB](#prise-en-main-de-mongodb)
    * [Installation](#installation)
    * [Prise en main du shell](#prise-en-main-du-shell)
    * [Opérations de base](#op%C3%A9rations-de-base)
        * [Insertion d'un document](#insertion-dun-document)
        * [Mise à jour d'un document](#mise-%C3%A0-jour-dun-document)
* [Prise en main d'Elasticsearch](#prise-en-main-delasticsearch)
* [Application Java](#application-java)
    * [Import des données dans MongoDB](#import-des-donn%C3%A9es-dans-mongodb)
    * [Services Java](#services-java)
    * [Import des données dans Elasticsearch](#import-des-donn%C3%A9es-dans-elasticsearch)
    * [Recherche full-text](#recherche-full-text)
    * [Recherche géographique](#recherche-g%C3%A9ographique)
* [Solutions](#solutions)

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

Pour visualiser les bases disponibles :

```
show dbs
```

Pour changer de base de données, par exemple `workshop` (MongoDB crée automatiquement la base si elle n'existe pas) :

```
use workshop
```

Le shell met à disposition un objet Javascript `db` qui permet d'interagir avec la base de données. Par exemple pour obtenir de l'aide :

```javascript
db.help()
```

Pour voir les collections d'une base de données :

```
show collections
```

Pour accéder à une collection nommée `geeks`, et par exemple afficher ses statistiques :

```javascript
db.geeks.stats()
```

### Opérations de base

#### Insertion d'un document

L'insertion d'un document se fait via la méthode `insert()`. Par exemple, pour insérer un document dans la collection `personnes` :

```javascript
db.personnes.insert({ "prenom" : "Jean", "nom" : "DUPONT" })
```

Remarque : la collection est créée automatiquement si elle n'existe pas encore.

**Exercice** :

Insérez un "Geek" dans une collection nommée `geeks`, avec les attributs suivants :

* nom : Dorne
* prénom : Manuel
* âge : 32

#### Mise à jour d'un document


## Prise en main d'Elasticsearch

TODO

## Application Java

TODO description générale de l'application : équipements sportifs des Pays de la Loire (open data).

![model](assets/model.png)

### Import des données dans MongoDB

TODO

### Services Java

TODO

### Import des données dans Elasticsearch

TODO

### Recherche full-text

TODO

### Recherche géographique

TODO

## Solutions

### MongoDB - opérations de base

### Insertion d'un document

```javascript
db.geeks.insert({ "prenom" : "Manuel", "nom" : "Dorne", age : 32 })
```
