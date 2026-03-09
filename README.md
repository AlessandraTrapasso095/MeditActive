# MeditActive CLI (Java)

Applicazione backend **Java da terminale (CLI)** per gestire obiettivi di meditazione, utenti e prenotazioni.

## Obiettivo del progetto
MeditActive aiuta gli utenti a:
- consultare gli obiettivi disponibili;
- impostare un obiettivo esistente;
- disdire una prenotazione;
- aggiungere un nuovo utente;
- esportare un CSV con gli obiettivi ancora disponibili.

I dati iniziali vengono caricati da file CSV all'avvio e vengono mantenuti sincronizzati su file dopo ogni modifica.

## Requisiti tecnici
- Java JDK 17+ (il progetto compila anche con versioni superiori)
- Apache Maven 3.9+

## Setup locale
1. Clona la repository:
```bash
git clone https://github.com/AlessandraTrapasso095/MeditActive.git
cd MeditActive
```

2. (Solo se necessario su macOS con Homebrew) assicurati che Java sia nel PATH:
```bash
export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"
```

## Compilazione
```bash
mvn clean package
```

## Esecuzione applicazione
Da JAR:
```bash
java -jar target/meditactive-1.0.0.jar
```

## Generazione file JAR
Il JAR viene generato con:
```bash
mvn clean package
```

Output:
- `target/meditactive-1.0.0.jar`

## Struttura dati CSV
File usati all'avvio:
- `data/utenti.csv`
- `data/obiettivi.csv`
- `data/prenotazioni.csv`

Separatore CSV: `;`

Relazioni:
- `prenotazioni.IDUtente` -> `utenti.ID`
- `prenotazioni.IDObiettivo` -> `obiettivi.ID`

## Comandi disponibili in CLI
- `1` Visualizzare tutti gli obiettivi nel sistema
- `2` Impostare un obiettivo esistente
- `3` Disdire la prenotazione di un obiettivo
- `4` Aggiungere nuovo utente
- `5` Esportare un file con gli obiettivi disponibili
- `0` Uscire dal programma

## Persistenza modifiche
Dopo i comandi che modificano i dati (`2`, `3`, `4`), l'app salva automaticamente i CSV in `data/`:
- aggiorna disponibilità obiettivi;
- aggiorna elenco prenotazioni;
- aggiorna elenco utenti.

## Export obiettivi disponibili
Il comando `5` crea un file CSV nella cartella `exports/` con nome:
- `obiettivi_dd_MM_yyyy.csv`

Contenuto export:
- `ID;Nome;Descrizione;Tipologia;Durata`

## Architettura (package)
- `com.meditactive.config` configurazioni e path applicativi
- `com.meditactive.model` entità dominio (`User`, `Goal`, `Booking`)
- `com.meditactive.repository` stato in memoria (`DataStore`)
- `com.meditactive.service` servizi tecnici (lettura/scrittura CSV, parser date, export)
- `com.meditactive.ui` gestione input/output terminale e ciclo menu

## GitHub 
- Repository: `https://github.com/AlessandraTrapasso095/MeditActive.git`

## Autore: Alessandra Trapasso 👩

