# Workshop NoSQL

*Découverte de [MongoDB](http://www.mongodb.org/) et d'[Elasticsearch](http://www.elasticsearch.org/), par la pratique !*

<hr>

## Prise en main de MongoDB

Quelques rappels avant de démarrer :

* MongoDB est une base de donnnées NoSQL, orientée documents.
* Le format des documents est JSON.
* Les documents sont stockés dans des collections.
* Une base de données MongoDB peut contenir plusieurs collections de documents.
* Il n'est pas possible d'effectuer de jointures entre collections.

### Installation

Téléchargez la dernière version stable de MongoDB sur [mongodb.org/downloads](https://www.mongodb.org/downloads). Ce workshop est basé sur la version 2.6.6 de MongoDB.

Dézippez le bundle dans le dossier de votre choix, par exemple `$HOME/progz/mongodb-2.6.6`.

Les exécutables nécessaires au fonctionnement de MongoDB se trouvent dans le dossier `$HOME/progz/mongodb-2.6.6/bin`.
Pour plus de facilités, vous pouvez ajouter ce dossier à votre `PATH`, afin que les commandes `mongod` et `mongo` soient directement accessibles.
Par exemple sous Linux, ajoutez les lignes suivantes à votre fichier `.profile` :

    PATH="$HOME/progz/mongodb-2.6.6/bin:$PATH"
    export PATH


Créez un dossier pour stocker les données, par exemple `$HOME/data/mongo`.
Exemple de commande sous Linux :

    mkdir -p "$HOME/data/mongo"

Démarrez MongoDB à l'aide de la commande suivante :

    mongod --dbpath="$HOME/data/mongo"

### Prise en main du shell

### Opérations de base

<hr>

## Prise en main d'Elasticsearch

TODO

<hr>

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
