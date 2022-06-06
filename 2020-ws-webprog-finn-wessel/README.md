**Name:** Finn Wessel

**Matrikelnummer:** XXXXXX

**Email-Adresse:** finn.wessel@stud.hs-flensburg.de

### Arbeitsblatt 2


#### Aufgabe 1

##### Minimal

- Textinhalt von Tweets darstellen :heavy_check_mark:

##### Standard

- Medieninhalte anzeigen :heavy_check_mark:

##### Exzellenzkriterien
- VueJs Framework genutzt :heavy_check_mark:
- Masonry Layout umgesetzt :heavy_check_mark:
- Videos anzeigen :heavy_check_mark:



#### Aufgabe 2

##### Minimal

- ENTFALLEN

##### Standard

- ENTFALLEN

##### Exzellenzkriterien

- Autorenprofile anzeigen :heavy_check_mark:
- Antworten auf Tweets anzeigen :no_entry:



### Arbeitsblatt 3

#### Aufgabe 1

##### Minimal

- Search Endpunkt :heavy_check_mark:

##### Standard

- Stream Endpunkt :heavy_check_mark:

##### Exzellenzkriterien

- Weitere Anfragen an die Twitter-Api  :heavy_check_mark:

#### Aufgabe 2

##### Minimal

- Search Tweets in der Datenbank speichern und cachen :heavy_check_mark:

##### Standard

- Stream Tweets in der Datenbank speichern :heavy_check_mark:

##### Exzellenzkriterien

- Stream (-Rules) dynamisch anpassen :heavy_check_mark:


### Arbeitsblatt 4

#### Aufgabe 1

##### Minimal

- Listen und Anmelde / Registrierung im Frontend, Suchen speichern :heavy_check_mark:

##### Standard

- Listeneinträge löschen und editieren im Frontend einbauen:heavy_check_mark:

##### Exzellenzkriterien

- Vuejs :heavy_check_mark:

#### Aufgabe 2

##### Minimal

- Einfache Anmeldung t :heavy_check_mark:

##### Standard

- fortgeschrittetene Authentifizierungsschemata  :heavy_check_mark:

##### Exzellenzkriterien

- komplexe Anmeldungen mit z.B. OAuth oder Amazon Cognito  :no_entry:

#### Aufgabe 2

##### Minimal

- Search Tweets in der Datenbank speichern und cachen :heavy_check_mark:

##### Standard

- Stream Tweets in der Datenbank speichern :heavy_check_mark:

##### Exzellenzkriterien

- Stream (-Rules) dynamisch anpassen :heavy_check_mark:

#### Aufgabe 3

##### Minimal

- Erstellen und Lesen von Listeneinträgen :heavy_check_mark:

##### Standard

- Endpunkte zum Löschen und Ändern  :heavy_check_mark:

##### Exzellenzkriterien

- Listen mit anderen Benutzer teilen :heavy_check_mark:



### Extra Leistungen

- Filter für Suche (mit Parametern)
- Suchparameter werden aufbereitet, so dass Groß und Kleinschreibung und Leerzeichen egal sind
- Speichern der Tweets aus Suchen und Stream in Listen
- Updaten der Titel und entfernen einzelner Tweets aus den Listen
- Api - Dokumentation (http://localhost:3000/api-documentation/#/)
- Docker
- Responsive Website
- Vuex State management 
- Listen können auf privat oder öffentlich gestellt werden
- Links und Mentions in Tweet anklickbar



## Quellen

### Fremdcode



#### v-tooltip

Zum darstellen des Tooltips der Tweet Karte habe ich `v-tooltip` verwendet.

Autor: akryum

URL: https://www.npmjs.com/package/v-tooltip 09.02.2021

Lizenz: MIT

##### Im Code

- `client/src/components/TweetCard/TweetCard.vue`
  - In Zeile 7, 32, 89, 90



#### vue-toast-notification

Um die Notifications darzustellen wurde `vue-toast-notification` genutzt.

Autor: ankurk91

URL: https://www.npmjs.com/package/vue-toast-notification 09.02.2021

Lizenz: MIT

##### Im Code

- `client/src/helper/notify.js`



#### Icons (vue-material-design-icons)

Alle verwendeten Icons stammen aus dem `Vue Material Design Icon Components` Paket

Autor: robcresswell

URL: https://www.npmjs.com/package/vue-material-design-icons 09.02.2021

Lizenz: MIT

##### Im Code

- `client/src/components/TweetCard/TweetCard.vue`
  - In Zeile 23-29, 37-42, 50-55, 63-68, 76-81
- `client/src/components/Form/Search.vue`
  - In Zeile 19-22, 26-28



#### DayJs

Zur Umrechnung in verschiedene Zeitformate habe ich `dayjs` verwendet. 

Autor: iamkun

URL: https://www.npmjs.com/package/dayjs 09.02.2021

Lizenz: MIT

##### Im code

- `client/src/components/TweetCard/TweetCard.vue`
  - In Zeile 161
- `client/src/views/Lists.vue`
  - In Zeile 96



#### kFormatter

Um Zahlen wie 100.000 als 100k darzustellen habe ich die Funktion kFormatter von StackOverflow übernommen.

Autor: [Jake Feasel](https://stackoverflow.com/users/808921/jake-feasel) und [Derk Jan Speelman](https://stackoverflow.com/users/6086226/derk-jan-speelman)

URL: https://stackoverflow.com/a/9461657/11356966 09.02.2021

Lizenz: Unbekannt

##### Im code

- `client/src/components/TweetCard/TweetCard.vue`
  - In Zeile 195-198



### Genutze Tutorials



#### Nestjs Authentication

Um zu lernen, wie in NestJs mittels JWT eine Authentifizierung durchgeführt werden kann, habe ich folgendes Tutorial genutzt.

https://medium.com/javascript-in-plain-english/nestjs-implementing-access-refresh-token-jwt-authentication-97a39e448007



##### Im Code

- `server/src/auth/auth.service.ts`
  - In Zeile 102-115