# Aufgabe 3



## Minimum

### Kürzlich veröffentlichte Tweets



```
finwes@DESKTOP-FHCLAD8:~$ curl https://api.twitter.com/2/tweets/search/recent?query=Flensburg -H "Authorization: Bearer <Token>"
{"data":[{"id":"1330574895311450119","text":"#tierheim #tierheimflensburg #tierschutzflensburg #flensburg #katze\nHallo, \nheute stellen wir euch eine Katze vor. Sie ist kastriert, gechipt und geimpft.\nKontakt bei Interesse oder Fragen zu den Tieren bitte per Telefon (0461-51598) oder eMail (138@tierschutz-flensburg.de) https://t.co/PSoDU8RVJH"},{"id":"1330572778735603713","text":"Stuck in north Germany...COVID on the rise..my Mom and brither don't weant to celebrate with us...need a Christmas family here (close to Flensburg)...my son , husband and I always celebrated Christmas as a huge event..family ..friends #WWG1WGA"},{"id":"1330571596310908936","text":"#tierheim #tierheimflensburg #tierschutz #flensburg #kater\nHallo, \nheute stellen wir euch einen hübschen roten Kater vor. Er ist kastriert, gechipt und geimpft.\nKontakt bei Interesse oder Fragen zu den Tieren bitte per Telefon (0461-51598) oder eMail (138@tierschutz-flensburg.de) https://t.co/yekM7PmBRU"},{"id":"1330568519562502147","text":"RT @RasmusBoysen92: Viggo Kristjansson is on fire in Flensburg with 8 goals on 9 shots in the first half!\n\nAmazing story he is the 26-year-…"},{"id":"1330565878841307142","text":"#tierheim #tierheimflensburg #tierschutz #tierschutzflensburg #flensburg #kater\nHallo, \nheute stellen wir euch unseren Hulk vor. Hulk ist ein Fundkater aus Hürup.\nKontakt bei Interesse oder Fragen zu den Tieren per Telefon (0461-51598) oder eMail (138@tierschutz-flensburg.de) https://t.co/u7QVPd1xo1"},{"id":"1330563313554612224","text":"#tierheim #tierheimflensburg #tierschutzflensburg #flensburg #katze\nHallo, \nheute stellen wir euch unsere Milena vor. Milena ist kastriert, gechipt und geimpft.\nKontakt bei Interesse oder Fragen zu den Tieren bitte per Telefon (0461-51598) oder eMail (138@tierschutz-flensburg.de) https://t.co/5s7q3kFU9I"},{"id":"1330559925584531457","text":"Der 2172. Kunde hat die Nord-Ostsee Sparkasse, Flensburg-Schleswiger Straße empfohlen. Danke! https://t.co/OareR3yCnU"},{"id":"1330552128557834241","text":"SG Flensburg-Handewitt verdrängt TVB Stuttgart von Platz drei https://t.co/2s0YNQBgPF https://t.co/YN0Akreq2r"},{"id":"1330550245063798785","text":"Пешевски со само еден гол не успеа да го спаси Штутгарт од пораз кај Фленсбург https://t.co/Kd43a7GAoK https://t.co/8z7jeKz9w6"},{"id":"1330541716546465795","text":"RT @Meckerschwabe: Typisch für #Flensburg ist der dänische Beitrag zum Bildungssystem: Der Dänische Schulverein für Südschleswig, Dansk Sko…"}],"meta":{"newest_id":"1330574895311450119","oldest_id":"1330541716546465795","result_count":10,"next_token":"b26v89c19zqg8o3foserx00ko28knontyx06qgvrlvzb1"}}
```



## Standard

### Filtered Stream

#### Filter adden

##### Anfrage
```curl
finwes@DESKTOP-FHCLAD8:~$ curl -X POST 'https://api.twitter.com/2/tweets/search/stream/rules' \
> -H "Content-type: application/json" \
> -H "Authorization: Bearer <Token>" -d \
> '{
>   "add": [
>     {"value": "münster has:images", "tag": "münster images"}
>   ]
> }'
```

##### Antwort
```json
{"data":[{"value":"münster has:images","tag":"münster images","id":"1331265011306688512"}],"meta":{"sent":"2020-11-24T15:54:48.401Z","summary":{"created":1,"not_created":0,"valid":1,"invalid":0}}}
```

#### Filter überprüfen

##### Anfrage
```curl
finwes@DESKTOP-FHCLAD8:~$ curl -X GET 'https://api.twitter.com/2/tweets/search/stream/rules' \
> -H "Authorization: Bearer <Token>"
```

##### Antwort
```json
{"data":[{"id":"1331265011306688512","value":"münster has:images","tag":"münster images"}],"meta":{"sent":"2020-11-24T15:55:16.279Z"}}
```

#### Ergebnisse Abfragen

##### Anfrage

```curl
finwes@DESKTOP-FHCLAD8:~$ curl -X GET -H "Authorization: Bearer <Token>" "https://api.twitter.com/2/tweets/search/stream?tweet.fields=created_at&expansions=author_id&user.fields=created_at"
```

##### Antwort
```json
{"data":{"text":"Habe gehört münster ist ganz nett. https://t.co/zaepr3DAHt","created_at":"2020-11-24T16:10:16.000Z","id":"1331268908330913795","author_id":"602075911"},"includes":{"users":[{"name":"marcus|zero","created_at":"2012-06-07T18:00:29.000Z","username":"sucram0","id":"602075911"}]},"matching_rules":[{"id":1331265011306688512,"tag":"münster images"}]}

{"data":{"text":"Test Twitter API mit Inhalt münster https://t.co/xRzzrzJjU9","created_at":"2020-11-24T16:14:47.000Z","id":"1331270047512268802","author_id":"780789351210856448"},"includes":{"users":[{"name":"Finn Wessel","created_at":"2016-09-27T15:21:02.000Z","username":"wessel_finn","id":"780789351210856448"}]},"matching_rules":[{"id":1331265011306688512,"tag":"münster images"}]}
```


#### Filter löschen

##### Anfrage
```curl
finwes@DESKTOP-FHCLAD8:~$ curl -X POST 'https://api.twitter.com/2/tweets/search/stream/rules' \
>   -H "Content-type: application/json" \
>   -H "Authorization: Bearer <Token>" -d \
>   '{
>     "delete": {
>       "ids": [
>         "1331265011306688512"
>       ]
>     }
>   }'
```

##### Antwort

```json
{"meta":{"sent":"2020-11-24T16:18:31.896Z","summary":{"deleted":1,"not_deleted":0}}}
```



## Exzellenz


### CURL Anfragen

Für weitere Anfragen an API-Schnittstellen habe ich folgende "Fake"-API benutzt: [jsonplaceholder.com](https://jsonplaceholder.typicode.com/)



#### GET

##### Anfrage

Mit dem unten aufgeführten Befehl wird der erste Post angefragt.

```curl
curl https://jsonplaceholder.typicode.com/posts/1
```



##### Antwort

```json
{
  "userId": 1,
  "id": 1,
  "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
  "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
}
```

---



#### PATCH

##### Anfrage

Dieser Post kann durch eine `PATCH` - Anfrage verändert werden.

```
finwes@DESKTOP-FHCLAD8:~$ curl -X PATCH https://jsonplaceholder.typicode.com/post/1 \
-H "Content-type: application/json" \
-d '{ "title": "Hello World!" }'
```



##### Antwort

Leider gibt die `PATCH` - Anfrage mittels CURL - Befehl in diesem Fall nur ein leeres Objekt zurück. Bei gleicher Abfrage mittels Postman wird folgende Antwort zurückgegeben. 

```json
{
    "userId": 1,
    "id": 1,
    "title": "Hello World",
    "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
}
```

---



#### POST


##### Anfrage 

Mittels des POST´s Befehls können zudem weitere Posts erstellt werden.

```curl
finwes@DESKTOP-FHCLAD8:~$ curl -X POST https://jsonplaceholder.typicode.com/posts \
-H "Content-type: application/json" \
-d "{
    \"title\": \"Hello World!\"
    \"body\": \"Inhalt des Posts!\"
	\"userId\": 42
}"
```



##### Antwort

```
{
  "userId": 42,
  "id": 101
}
```

---



#### GET mit Query

Zusätzlich zu der einfachen `GET` - Request können die Posts auch nach `userId` gefiltert werden.



##### Anfrage

```curl
finwes@DESKTOP-ASL3QSL:~$ curl https://jsonplaceholder.typicode.com/posts?userId=3
```



##### Antwort

Die Antwort enthält nun nur die Post, welche von dem Benutzer mit der ID 3 verfasst wurden.

```json
[
  {
    "userId": 3,
    "id": 21,
    "title": "asperiores ea ipsam voluptatibus modi minima quia sint",
    "body": "repellat aliquid praesentium dolorem quo\nsed totam minus non itaque\nnihil labore molestiae sunt dolor eveniet hic recusandae veniam\ntempora et tenetur expedita sunt"
  },
  {
    "userId": 3,
    "id": 22,
    "title": "dolor sint quo a velit explicabo quia nam",
    "body": "eos qui et ipsum ipsam suscipit aut\nsed omnis non odio\nexpedita earum mollitia molestiae aut atque rem suscipit\nnam impedit esse"
  },
  {
    "userId": 3,
    "id": 23,
    "title": "maxime id vitae nihil numquam",
    "body": "veritatis unde neque eligendi\nquae quod architecto quo neque vitae\nest illo sit tempora doloremque fugit quod\net et vel beatae sequi ullam sed tenetur perspiciatis"
  },
  {
    "userId": 3,
    "id": 24,
    "title": "autem hic labore sunt dolores incidunt",
    "body": "enim et ex nulla\nomnis voluptas quia qui\nvoluptatem consequatur numquam aliquam sunt\ntotam recusandae id dignissimos aut sed asperiores deserunt"
  },
  {
    "userId": 3,
    "id": 25,
    "title": "rem alias distinctio quo quis",
    "body": "ullam consequatur ut\nomnis quis sit vel consequuntur\nipsa eligendi ipsum molestiae et omnis error nostrum\nmolestiae illo tempore quia et distinctio"
  },
  {
    "userId": 3,
    "id": 26,
    "title": "est et quae odit qui non",
    "body": "similique esse doloribus nihil accusamus\nomnis dolorem fuga consequuntur reprehenderit fugit recusandae temporibus\nperspiciatis cum ut laudantium\nomnis aut molestiae vel vero"
  },
  {
    "userId": 3,
    "id": 27,
    "title": "quasi id et eos tenetur aut quo autem",
    "body": "eum sed dolores ipsam sint possimus debitis occaecati\ndebitis qui qui et\nut placeat enim earum aut odit facilis\nconsequatur suscipit necessitatibus rerum sed inventore temporibus consequatur"
  },
  {
    "userId": 3,
    "id": 28,
    "title": "delectus ullam et corporis nulla voluptas sequi",
    "body": "non et quaerat ex quae ad maiores\nmaiores recusandae totam aut blanditiis mollitia quas illo\nut voluptatibus voluptatem\nsimilique nostrum eum"
  },
  {
    "userId": 3,
    "id": 29,
    "title": "iusto eius quod necessitatibus culpa ea",
    "body": "odit magnam ut saepe sed non qui\ntempora atque nihil\naccusamus illum doloribus illo dolor\neligendi repudiandae odit magni similique sed cum maiores"
  },
  {
    "userId": 3,
    "id": 30,
    "title": "a quo magni similique perferendis",
    "body": "alias dolor cumque\nimpedit blanditiis non eveniet odio maxime\nblanditiis amet eius quis tempora quia autem rem\na provident perspiciatis quia"
  }
```



