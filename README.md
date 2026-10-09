# Training Compliance Dashboard

## Projektbeschreibung

Sandras Training Compliance Dashboard ist eine webbasierte Anwendung zur Überwachung von Mitarbeiterschulungen und deren Fälligkeiten.

Ziel des Projekts ist es, den aktuellen Schulungsstand übersichtlich darzustellen und Mitarbeitende mit überfälligen oder fehlenden Pflichttrainings zu erkennen.

Das Dashboard unterstützt Sandra bei der Auswertung der Trainingsdaten und bei der Entscheidung, wo Handlungsbedarf besteht.

## Herangehensweise
Ich habe zuerst die Daten untersucht, bevor ich etwas gebaut habe: Status- und
Datumsverteilung, Lücken, Verteilung der Überfälligen nach Training. Dabei sind
vier Dinge aufgefallen, die ein reiner "Überfällig-Zähler" verdeckt hätte:

- Ein einzelnes Training (Phishing) macht fast die Hälfte der überfälligen
  Zuweisungen aus (478 von 890 Pflicht-Überfälligen). Also kein Einzelfall, sondern eher ein Kampagnen-Problem.
- 19 AGG-Zuweisungen sind seit 2019–2023 offen und würden jede Liste dominieren (Altlasten)
- 26 Mitarbeitende, die seit Juni 2026 eingetreten sind, haben noch keine einzige
  Pflichtzuweisung. In einem rein zuweisungsbasierten Dashboard sind sie unsichtbar, da nichts überfällig werden könnte.
- Ein paar Datensätze sind widersprüchlich (abgeschlossen ohne Abschlussdatum,
  offen ohne Fälligkeit, uneinheitliche Abteilungsnamen).

Daraus ist das Dashboard entlang von Sandras Fragen entstanden: Wer ist überfällig,
wen eskaliere ich und wo, was kommt auf mich zu, was muss ich selbst (in Zukunft) anlegen.

## Wichtigste Entscheidungen

- **Fehlende Zuweisungen sind ein eigener Bereich ("Noch zuzuweisen").** Nur Sandra kann sie lösen, nicht die Mitarbeitenden oder Führungskräfte. Alle fehlenden Pflichtzuweisungen betreffen dieselben 26 Neueingestellten.
- **Der Status entscheidet vor dem Datum.**  Drei Zuweisungen sind "Abgeschlossen", haben aber kein Abschlussdatum. Ich zähle sie nicht als überfällig, sondern weise sie als Datenqualitätsproblem aus. Dadurch sind es 890 statt 893 Überfällige.
- **Altlasten (mehr als 365 Tage überfällig) werden gezählt und getrennt behandelt.**  In der Detailliste stehen Pflichttrainings zuerst und Altlasten am Ende, damit die 19 AGG-Fälle von 2019 die aktuellen Fälle nicht verdecken.

Weitere Entscheidungen: Pflichttrainings sind als Filter voreingestellt, weil freiwillige Trainings kein Audit-Risiko sind. Ablaufende Wiederholungen zähle ich mit 60 statt 30 Tagen Vorlauf, weil Sandra erst eine Zuweisung anlegen muss, bevor eine Frist läuft.

## Bewusst weggelassen

- Login, E-Mail-Versand und Eskalationsworkflow, nach Bereinigung der Listen sicherlich ein weiterer Punkt
- Datenbank: Bei rund 800 Mitarbeitenden und 6.000 Zuweisungen reichen CSV im Speicher.
- Zielgruppen "Staplerfahrer:innen" und "Benannte Ersthelfer:innen": In den Daten gibt
  es keine Spalte, aus der sich das ableiten ließe.
- Wiederholungen als Einzelliste: Das Dashboard zeigt aktuell nur die Anzahl.

## Als Nächstes

1. Ablaufende Wiederholungen (87, ohne Folgezuweisung) als Liste in "Noch zuzuweisen".Der Code zählt aktuell alle ablaufenden Abschlüsse und prüft noch nicht, ob bereits eine Folgezuweisung existiert. In den vorliegenden Daten trifft das auf alle 87 zu
2. Klick vom Training zu den betroffenen Mitarbeitenden, damit Sandra z.B. die
   Phishing-Kampagne gezielt ansprechen kann.
3. Zielgruppen für Staplerfahrer und Ersthelfer über zusätzliche Stammdaten.
4. Technisch: Spring-Dependency-Injection statt manuellem Erzeugen, Maps statt
   Linearsuche, Enum für die Kategorien, Tests für die Statuslogik.

   

## Anwendung starten

**Voraussetzungen:** JDK 25 und Maven (oder IntelliJ IDEA mit Maven-Unterstützung).

1. Projekt in IntelliJ IDEA öffnen und die Maven-Abhängigkeiten laden.
2. Die Klasse `TrainingDashboardApplication` starten.
3. Im Browser `http://localhost:8080/` öffnen.

Alternativ als JAR:

```bash
mvn clean package
java -jar target/training-dashboard-1.0-SNAPSHOT.jar
```

Die CSV-Dateien liegen in `src/main/resources/data/` und werden beim Start einmal eingelesen. Änderungen daran erfordern einen Neustart.

# Technisches
## Verwendete Technologien

Das Projekt wurde mit folgenden Technologien umgesetzt:

- **Java** für die Anwendungslogik
- **Spring Boot** als Webframework
- **Thymeleaf** zur Darstellung der HTML-Seiten
- **HTML, CSS und JavaScript** für die Benutzeroberfläche
- **CSV-Dateien** als Datenquelle
- **IntelliJ IDEA** als Entwicklungsumgebung

Eine separate Datenbank wird für die aktuelle Anwendung nicht benötigt. 
## Datenbasis

Die Anwendung verarbeitet drei Arten von CSV-Daten:

- **Mitarbeitende:** Stammdaten wie Name, Abteilung, Standort und Führungskraft
- **Trainings:** Informationen zu den Schulungen, einschließlich der Kennzeichnung als Pflichttraining
- **Trainingszuordnungen:** Zuordnung von Mitarbeitenden zu Trainings mit Status- und Datumsinformationen

Die CSV-Dateien werden beim Start der Anwendung eingelesen und anschließend im Speicher verwendet.

**Wichtig:** Werden CSV-Dateien verändert, muss die Anwendung neu gestartet werden, damit die Änderungen
## Filter und Seitennavigation

Das Dashboard bietet Filtermöglichkeiten nach:

- Standort
- Abteilung
- Pflichttraining

Die Abteilungsnamen werden beim Einlesen vereinheitlicht, um unterschiedliche Schreibweisen möglichst zu vermeiden.

Die Tabellen verfügen über eine Seitennavigation, damit auch größere Datenmengen übersichtlich bleiben.

Ungültige Seitenzahlen werden automatisch auf den gültigen Seitenbereich begrenzt. Dadurch führt beispielsweise die Eingabe `?page=99` nicht zu einem Fehler, wenn weniger Seiten vorhanden sind.

Beim Anwenden von Filtern bleibt der ausgewählte Dashboard-Tab erhalten.

## Berechnungslogik

Die Berechnung der Trainingsstatus und Kennzahlen erfolgt überwiegend in der Klasse `SandrasService`.

Die Anwendung berücksichtigt unter anderem:

- den Status einer Trainingszuordnung
- das Fälligkeitsdatum
- das Abschlussdatum
- die Pflichttraining-Kennzeichnung
- die Gültigkeit von Wiederholungstrainings

Als festes Referenzdatum wird aktuell der **02.11.2026** verwendet.

Dadurch sind die Auswertungen für die verwendeten Beispieldaten reproduzierbar. Das Dashboard verwendet für diese Berechnungen nicht automatisch das aktuelle Tagesdatum.

## Aufbau des Projekts

Die wichtigsten Bestandteile sind:

| Bestandteil | Aufgabe |
|---|---|
| `DashboardController` | Verarbeitet Webanfragen, Filter und Seitennavigation und stellt Daten für die Oberfläche bereit |
| `DashboardDataLoader` | Lädt die benötigten CSV-Daten |
| `SandrasService` | Enthält die Berechnungen und Auswertungen |
| `Employee`, `Training`, `Assignment` | Repräsentieren die grundlegenden Daten |
| `ComplianceStatus` | Beschreibt die möglichen Compliance-Zustände |
| `dashboard.html` | Stellt das Dashboard mit Thymeleaf dar |

Weitere Klassen dienen der Aufbereitung der Daten für die einzelnen Tabellen.
## Screenshots 
# Die folgenden Screenshots dienen der Visualisierung des Dashboards
 
# Übersicht: 
![Dashboard - Übersicht] (screenshots/dashboarduebersicht1.png)

# Noch zuzuweisen:
![Dashboard - Noch zuzuweisen] (screenshots/dashboardnochzuzuweisen.png)

# Überfällig
![Dashboard - Überfällig] (screenshots/dashboardueberfaellig.png)

# Nach Manager
![Dashboard - Nach Manager] (screenshots/dashboardnachmanager.png)
 
