# Activité pratique 5 Spark Streaming avec Hadoop HDFS

<img width="1536" height="1024" alt="ChatGPT Image Dec 21, 2025, 12_52_13 AM" src="https://github.com/user-attachments/assets/05e77720-1c98-4912-871b-3d0fc5e1b354" />


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

<img width="1770" height="770" alt="Screenshot 2025-12-20 193232" src="https://github.com/user-attachments/assets/98fbeaca-d9f1-4229-897f-c18359030776" />


### Étape 2 : Créer les répertoires HDFS

<img width="1738" height="166" alt="Screenshot 2025-12-20 193801" src="https://github.com/user-attachments/assets/ceca8d4b-cd11-477f-a64d-bbfac4a7fab1" />


### Étape 3 : Préparer les fichiers CSV

<img width="1766" height="896" alt="Screenshot 2025-12-20 193950" src="https://github.com/user-attachments/assets/a6763695-d07e-4a5b-8d98-3fb2a0d7da17" />


### Étape 4 : Compiler l'application
<img width="1239" height="307" alt="Screenshot 2025-12-20 163446" src="https://github.com/user-attachments/assets/adb9fc27-cb24-476c-a0e5-e0b6559ca6d1" />

### Étape 5 : Déployer l'application
<img width="1776" height="165" alt="Screenshot 2025-12-20 165342" src="https://github.com/user-attachments/assets/40828461-10e6-4916-b876-11471f0463ac" />


### Étape 6 : Lancer l'application Spark Streaming

<img width="1910" height="950" alt="Screenshot 2025-12-20 194320" src="https://github.com/user-attachments/assets/270c0181-fa2e-43d1-a669-168e53a063fb" />

L'application est maintenant en attente de nouveaux fichiers dans /data/stream/

---

## Utilisation

### Alimenter le stream par batch

Ouvrir un nouveau terminal PowerShell et exécuter :

### Observer les résultats

Les résultats s'affichent automatiquement dans le terminal où spark-submit s'exécute :

**Batch  0 :**
<img width="1783" height="707" alt="Screenshot 2025-12-20 171929" src="https://github.com/user-attachments/assets/a6771c8d-5dc5-480c-86d9-661f880e7067" />

<img width="1761" height="1015" alt="Screenshot 2025-12-20 171838" src="https://github.com/user-attachments/assets/8640745a-4d86-42ce-b202-6f5da14cf411" />


**Batch 1 :**
<img width="1771" height="917" alt="Screenshot 2025-12-20 171945" src="https://github.com/user-attachments/assets/6ee2ba32-bb28-4aad-9387-40df230bcba0" />

**Batch 2 :**

<img width="1783" height="1031" alt="Screenshot 2025-12-20 172049" src="https://github.com/user-attachments/assets/7fa91513-8a18-42d7-8a9b-101c0dd617fd" />


## Interfaces web

### Spark Master UI
**URL :** http://localhost:8080

<img width="1899" height="976" alt="Screenshot 2025-12-20 172229" src="https://github.com/user-attachments/assets/f7b99b70-0b82-45b2-96f1-c16f83190050" />


**Fonctionnalités :**
- Vue d'ensemble du cluster Spark
- Workers actifs et leurs ressources
- Applications en cours d'exécution
- Historique des applications

### HDFS NameNode UI

**URL :** http://localhost:9870

<img width="1898" height="922" alt="Screenshot 2025-12-20 172726" src="https://github.com/user-attachments/assets/d70117b2-8e82-4934-b174-823b970f02d4" />

<img width="1886" height="992" alt="Screenshot 2025-12-20 172750" src="https://github.com/user-attachments/assets/a4a05f1d-8493-4bd9-95c0-23ccee489177" />

<img width="1903" height="998" alt="Screenshot 2025-12-20 172816" src="https://github.com/user-attachments/assets/f6d2aca0-81de-49f9-adea-2e1e7cc28b8d" />


**Fonctionnalités :**
- Overview du cluster HDFS
- Utilities > Browse the file system : explorer les fichiers HDFS
- Datanodes : statut des DataNodes
- Espace de stockage utilisé

### YARN ResourceManager UI
**URL :**http://localhost:8088

<img width="1919" height="1020" alt="Screenshot 2025-12-20 172841" src="https://github.com/user-attachments/assets/794e10f4-179e-428b-9445-022182859676" />

**Fonctionnalités :**
- Cluster Metrics : métriques globales du cluster
- Applications : liste de toutes les applications YARN
- Nodes : NodeManagers actifs et leurs ressources
- Scheduler : allocation des ressources par queue

---

### Mode Update

Le mode "Update" affiche uniquement les lignes modifiées à chaque batch, ce qui est efficace pour les agrégations stateful.

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

Activité pratique réalisée dans le cadre du cours de Big Data - Spark Streaming avec HDFS

## Licence

Ce projet est à usage éducatif uniquement.
