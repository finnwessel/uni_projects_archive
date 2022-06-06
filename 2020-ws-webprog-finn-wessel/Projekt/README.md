## Anleitung (Docker)

Im Ordner `server` `.env.sample` Datei zu `.env` umbenennen und Twitter API-Token für `TWITTER_API_KEY` setzen. Die restlichen Variablen können unverändert bleiben.

`docker-compose up` Befehl im Projektordner ausführen.
Ein Stack bestehend aus dem `Client`, `Server` und `mariadb` wird automatisch erstellt und die Tweets Datenbank generiert.

Über `http://localhost:8080` kann auf das Frontend zugegriffen werden. Dabei muss zu  Beginn ein User im Frontend registriert werden.



### Alternative (nativ)

Alternativ können Frontend und Server auch manuell gestartet werden. Dafür muss zunächst der `TWITTER_API_KEY` und die mysql Datenbankverbindung über `DATABASE_*`  unter `server/.env` eingestellt werden. Danach wird zunächst der Server über `npm run start:dev` im `server` Verzeichnis ausgeführt gestartet. Der Client wird über den Command `npm run serve` im ``client` Verzeichnis gestartet.

Über `http://localhost:8080` kann auf das Frontend zugegriffen werden. Dabei muss zu  Beginn ein User im Frontend registriert werden.



Abgabenlink: https://we.tl/t-SrPdDtJMfW