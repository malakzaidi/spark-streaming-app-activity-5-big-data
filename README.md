# Projet Spark Streaming avec Hadoop HDFS

## Table des matières

1. [Description du projet](#description-du-projet)
2. [Architecture](#architecture)
3. [Prérequis](#prérequis)
4. [Structure du projet](#structure-du-projet)
5. [Configuration](#configuration)
6. [Installation et démarrage](#installation-et-démarrage)
7. [Utilisation](#utilisation)
8. [Interfaces web](#interfaces-web)
9. [Résultats attendus](#résultats-attendus)
10. [Arrêt et nettoyage](#arrêt-et-nettoyage)
11. [Dépannage](#dépannage)

---

## Description du projet

Ce projet implémente une application de streaming en temps réel utilisant Apache Spark Structured Streaming pour traiter des fichiers CSV stockés dans HDFS. L'application lit des fichiers de commandes par batch, effectue des agrégations par produit et affiche les résultats en temps réel avec le mode de sortie "Update".

### Objectifs pédagogiques

- Comprendre le fonctionnement de Spark Structured Streaming
- Manipuler HDFS pour le stockage distribué
- Utiliser le mode "Update" pour les agrégations stateful
- Déployer une application Spark sur un cluster Standalone
- Monitorer les applications via les interfaces web

---

## Architecture

### Composants

Le projet utilise une architecture distribuée comprenant :

**Hadoop HDFS :**
- 1 NameNode (gestion des métadonnées)
- 1 DataNode (stockage des données)

**YARN (Yet Another Resource Negotiator) :**
- 1 ResourceManager (gestion des ressources cluster)
- 1 NodeManager (exécution des conteneurs)

**Apache Spark Standalone :**
- 1 Master (coordination du cluster Spark)
- 2 Workers (exécution des tâches)

### Flux de données

1. Les fichiers CSV sont copiés dans HDFS (dossier /data/staging/)
2. Les fichiers sont déplacés vers /data/stream/ pour simuler un flux
3. Spark Streaming détecte automatiquement les nouveaux fichiers
4. Traitement et agrégation des données par produit
5. Affichage des résultats en console toutes les 10 secondes

---

## Prérequis

### Logiciels requis

- Docker Desktop (Windows/Mac) ou Docker Engine (Linux)
- Docker Compose version 2.x ou supérieure
- Java Development Kit (JDK) 11 ou supérieur
- Apache Maven 3.6 ou supérieur
- Git (optionnel)

### Configuration système minimale

- CPU : 4 cores
- RAM : 8 GB minimum (16 GB recommandé)
- Espace disque : 10 GB libres

### Vérification des prérequis

```bash
# Vérifier Docker
docker --version

# Vérifier Docker Compose
docker-compose --version

# Vérifier Java
java -version

# Vérifier Maven
mvn -version
```

---

## Structure du projet

```
practical-activity-5-spark-streaming/
│
├── src/
│   └── main/
│       └── java/
│           └── org/bigdata/practicalactivity5sparkstreaming/
│               └── Main.java                    # Application Spark Streaming
│
├── data/
│   ├── orders1.csv                              # Fichier batch 1
│   ├── orders2.csv                              # Fichier batch 2
│   └── orders3.csv                              # Fichier batch 3
│
├── volumes/
│   └── namenode/                                # Données persistantes HDFS
│
├── docker-compose.yaml                          # Configuration des services
├── config                                       # Variables d'environnement Hadoop
├── pom.xml                                      # Configuration Maven
└── README.md                                    # Ce fichier
```

---

## Configuration

### Fichier docker-compose.yaml

Le fichier définit 7 services :

- **namenode** : Port 9870 (UI), 8020 (RPC)
- **datanode** : Stockage HDFS
- **resourcemanager** : Port 8088 (YARN UI)
- **nodemanager** : Exécution des conteneurs YARN
- **spark-master** : Port 8080 (Spark Master UI), 7077 (RPC), 4040 (Application UI)
- **spark-worker-1** : 2 cores, 2GB RAM
- **spark-worker-2** : 2 cores, 2GB RAM

### Application Spark (Main.java)

**Schéma des données :**
- order_id (Long)
- client_id (Long)
- client_name (String)
- product (String)
- quantity (Integer)
- price (Double)
- order_date (String)
- status (String)
- total (Double)

**Traitement :**
- Lecture en streaming depuis HDFS (hdfs://namenode:8020/data/stream)
- Agrégation par produit : sum(total) et count(order_id)
- Sortie en console avec mode Update
- Trigger : toutes les 10 secondes

---

## Installation et démarrage

### Étape 1 : Démarrer l'infrastructure

```powershell
# Nettoyer les anciens conteneurs (optionnel)
docker-compose down -v

# Démarrer tous les services
docker-compose up -d

# Attendre que les services démarrent (30-40 secondes)
Start-Sleep -Seconds 40

# Vérifier l'état des conteneurs
docker-compose ps
```

### Étape 2 : Créer les répertoires HDFS

```powershell
# Créer le dossier pour le streaming
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -mkdir -p /data/stream

# Créer le dossier staging
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -mkdir -p /data/staging

# Donner les permissions
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -chmod 777 /data/stream

# Vérifier la création
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -ls /data/
```

### Étape 3 : Préparer les fichiers CSV

```powershell
# Copier les fichiers depuis Windows vers le conteneur
docker cp data/orders1.csv practical-activity-5-spark-streaming-namenode-1:/data/orders1.csv
docker cp data/orders2.csv practical-activity-5-spark-streaming-namenode-1:/data/orders2.csv
docker cp data/orders3.csv practical-activity-5-spark-streaming-namenode-1:/data/orders3.csv

# Copier dans HDFS staging
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -put -f /data/orders1.csv /data/staging/
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -put -f /data/orders2.csv /data/staging/
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -put -f /data/orders3.csv /data/staging/

# Vérifier
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -ls /data/staging/
```

### Étape 4 : Compiler l'application

```powershell
# Compiler le projet Maven
mvn clean package

# Vérifier que le JAR a été créé
ls target/practical-activity-5-spark-streaming-0.0.1-SNAPSHOT.jar
```

### Étape 5 : Déployer l'application

```powershell
# Copier le JAR vers Spark Master
docker cp target/practical-activity-5-spark-streaming-0.0.1-SNAPSHOT.jar spark-master:/opt/spark/app.jar

# Vérifier la copie
docker exec spark-master ls -lh /opt/spark/app.jar
```

### Étape 6 : Lancer l'application Spark Streaming

```powershell
docker exec -it spark-master /opt/spark/bin/spark-submit `
  --class org.bigdata.practicalactivity5sparkstreaming.Main `
  --master spark://spark-master:7077 `
  --deploy-mode client `
  --conf spark.hadoop.fs.defaultFS=hdfs://namenode:8020 `
  --conf spark.executor.memory=1g `
  --conf spark.executor.cores=1 `
  --conf spark.driver.memory=1g `
  /opt/spark/app.jar
```

L'application est maintenant en attente de nouveaux fichiers dans /data/stream/

---

## Utilisation

### Alimenter le stream par batch

Ouvrir un nouveau terminal PowerShell et exécuter :

**Batch 1 :**
```powershell
Write-Host "=== Envoi du Batch 1: orders1.csv ===" -ForegroundColor Green
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -cp /data/staging/orders1.csv /data/stream/orders1.csv
Start-Sleep -Seconds 20
```

**Batch 2 :**
```powershell
Write-Host "=== Envoi du Batch 2: orders2.csv ===" -ForegroundColor Green
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -cp /data/staging/orders2.csv /data/stream/orders2.csv
Start-Sleep -Seconds 20
```

**Batch 3 :**
```powershell
Write-Host "=== Envoi du Batch 3: orders3.csv ===" -ForegroundColor Green
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -cp /data/staging/orders3.csv /data/stream/orders3.csv
```

### Observer les résultats

Les résultats s'affichent automatiquement dans le terminal où spark-submit s'exécute :

```
-------------------------------------------
Batch: 1
-------------------------------------------
+--------+-------------+-----------+
|product |total_revenue|order_count|
+--------+-------------+-----------+
|Laptop  |1500.0       |2          |
|Phone   |800.0        |1          |
+--------+-------------+-----------+

-------------------------------------------
Batch: 2
-------------------------------------------
+--------+-------------+-----------+
|product |total_revenue|order_count|
+--------+-------------+-----------+
|Laptop  |3500.0       |4          |
|Phone   |2100.0       |3          |
|Tablet  |600.0        |2          |
+--------+-------------+-----------+
```

---

## Interfaces web

### Spark Master UI
**URL :** http://localhost:8080

**Fonctionnalités :**
- Vue d'ensemble du cluster Spark
- Workers actifs et leurs ressources
- Applications en cours d'exécution
- Historique des applications

### Spark Application UI
**URL :** http://localhost:4040

**Fonctionnalités :**
- Onglet "Jobs" : jobs exécutés et leur statut
- Onglet "Stages" : détails des stages et DAG visualization
- Onglet "Streaming" : métriques temps réel (Input Rate, Processing Time, Scheduling Delay)
- Onglet "SQL" : requêtes exécutées avec plans d'exécution
- Onglet "Executors" : statistiques des executors

### HDFS NameNode UI
**URL :** http://localhost:9870

**Fonctionnalités :**
- Overview du cluster HDFS
- Utilities > Browse the file system : explorer les fichiers HDFS
- Datanodes : statut des DataNodes
- Espace de stockage utilisé

### YARN ResourceManager UI
**URL :** http://localhost:8088

**Fonctionnalités :**
- Cluster Metrics : métriques globales du cluster
- Applications : liste de toutes les applications YARN
- Nodes : NodeManagers actifs et leurs ressources
- Scheduler : allocation des ressources par queue

---

## Résultats attendus

### Comportement de l'application

1. **Batch 0** : Aucune donnée (table vide)
2. **Batch 1** : Agrégation des données du fichier orders1.csv
3. **Batch 2** : Agrégation cumulative (orders1 + orders2)
4. **Batch 3** : Agrégation cumulative (orders1 + orders2 + orders3)

### Mode Update

Le mode "Update" affiche uniquement les lignes modifiées à chaque batch, ce qui est efficace pour les agrégations stateful.

### Métriques de performance

Dans l'interface Streaming (http://localhost:4040), surveiller :
- **Input Rate** : nombre d'enregistrements par seconde
- **Processing Time** : temps de traitement d'un batch
- **Scheduling Delay** : délai avant le début du traitement

Valeurs normales :
- Processing Time : 1-5 secondes
- Scheduling Delay : < 1 seconde

---

## Arrêt et nettoyage

### Arrêter l'application Spark

Dans le terminal où spark-submit s'exécute :
```
Ctrl + C
```

Ou depuis un autre terminal :
```powershell
docker exec spark-master pkill -f spark-submit
```

### Nettoyer le stream pour un nouveau test

```powershell
# Supprimer les fichiers du stream
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -rm /data/stream/*

# Vérifier
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -ls /data/stream/
```

### Arrêter tous les services

```powershell
# Arrêter les conteneurs
docker-compose down

# Arrêter et supprimer les volumes (données HDFS perdues)
docker-compose down -v
```

---

## Dépannage

### Problème : Le JAR n'est pas trouvé

**Erreur :** `Local jar /opt/spark/app.jar does not exist`

**Solution :**
```powershell
# Recompiler
mvn clean package

# Recopier le JAR
docker cp target/practical-activity-5-spark-streaming-0.0.1-SNAPSHOT.jar spark-master:/opt/spark/app.jar

# Vérifier
docker exec spark-master ls -lh /opt/spark/app.jar
```

### Problème : Fichiers CSV non trouvés dans HDFS

**Erreur :** `No such file or directory`

**Solution :**
```powershell
# Vérifier les fichiers locaux
ls data/

# Copier vers le conteneur
docker cp data/orders1.csv practical-activity-5-spark-streaming-namenode-1:/data/

# Copier dans HDFS
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -put -f /data/orders1.csv /data/staging/
```

### Problème : HDFS n'est pas accessible

**Erreur :** `Connection refused`

**Solution :**
```powershell
# Redémarrer HDFS
docker-compose restart namenode datanode

# Attendre 30 secondes
Start-Sleep -Seconds 30

# Vérifier
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -ls /
```

### Problème : L'application ne détecte pas les fichiers

**Causes possibles :**
- Les fichiers ne sont pas dans /data/stream/
- Les permissions HDFS sont incorrectes
- Le trigger interval est trop long

**Solution :**
```powershell
# Vérifier les fichiers dans stream
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -ls /data/stream/

# Vérifier les permissions
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -chmod -R 777 /data/stream
```

### Problème : Out of Memory

**Solution :**
Augmenter les ressources dans docker-compose.yaml :
```yaml
spark-worker-1:
  environment:
    - SPARK_WORKER_MEMORY=4g  # Au lieu de 2g
```

### Logs utiles pour le débogage

```powershell
# Logs Spark Master
docker logs spark-master

# Logs NameNode
docker logs practical-activity-5-spark-streaming-namenode-1

# Logs YARN ResourceManager
docker logs practical-activity-5-spark-streaming-resourcemanager-1

# Logs d'un worker
docker logs spark-worker-1
```

---

## Commandes utiles

### Gestion HDFS

```powershell
# Lister les fichiers
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -ls /data/

# Afficher le contenu d'un fichier
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -cat /data/staging/orders1.csv

# Supprimer un fichier
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -rm /data/stream/orders1.csv

# Créer un répertoire
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -mkdir -p /data/test

# Copier un fichier local vers HDFS
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfs -put /data/file.csv /data/hdfs/
```

### Gestion Docker

```powershell
# Voir tous les conteneurs
docker ps -a

# Voir les logs d'un conteneur
docker logs -f <container_name>

# Entrer dans un conteneur
docker exec -it <container_name> bash

# Redémarrer un service
docker-compose restart <service_name>

# Voir l'utilisation des ressources
docker stats
```

### Vérification du cluster

```powershell
# Vérifier les nodes YARN
docker exec practical-activity-5-spark-streaming-resourcemanager-1 yarn node -list

# Vérifier les applications YARN
docker exec practical-activity-5-spark-streaming-resourcemanager-1 yarn application -list

# Voir le statut HDFS
docker exec practical-activity-5-spark-streaming-namenode-1 hdfs dfsadmin -report
```

---

## Auteur

Projet réalisé dans le cadre du cours de Big Data - Spark Streaming avec HDFS

## Licence

Ce projet est à usage éducatif uniquement.
