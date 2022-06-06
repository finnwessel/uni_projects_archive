Marcus Meyer | MatrikelNr: XXXXXX| eMail: marcus.meyer@stud.hs-flensburg.de

Finn Wessel | MatrikelNr: XXXXXX| eMail: finn.wessel@stud.hs-flensburg.de



## Exzellenz

- Fragments verwenden :+1: (B1 A2)
- Navigation Graph :+1: (B1 A3)
    - Fragments auf Navigation Graph umstellen :+1:
- Abmelde / Kick - Funktion :+1: (B2 A2)
    - Clients senden beim verlassen Team = -1 :+1:
    - Host sendet beim verlassen Game = end :+1:
    - Host kann andere Spieler kicken :+1:
- Genauigkeit anzeigen :+1: (B3 A1)
    - Roter Satellit oben rechts, wenn kein GPS, gelb wenn schlechte Genauigkeit :+1:
- scrollbare Karte :+1: (B3 A3
- Bewachenden eines Eroberungspunktes verwarnen :+1: (B4 A3)
    - Spieler wird verwarnt, wenn er mehr als x Sekunden am eigenen Punkt steht:+1:
    - Team verliert Punkt, wenn Spieler sich nicht weg bewegt :+1:
- Verwenden von Sounds :+1: (B4 A5)
    - Sound beim joinen der Lobby :+1:
    - Sound beim verlassen der Lobby :+1:
    - Sound beim Game Start :+1:
    - Sound beim Game Ende :+1:



### Anleitung

Wenn die App gestartet wird, hat der Benutzer die Möglichkeit entweder eine neue Lobby zu erstellen, oder einer bestehenden Lobby beizutreten.

In der Lobby hat der Admin die Möglichkeit über das Zahnrad oben rechts die Dauer des Spiels festzulegen. Zudem kann der Admin andere Teilnehmer aus der Lobby schmeißen und deren Teamzugehörigkeit verändern.

Alle anderen Teilnehmer können nur die eigene Teamzugehörigkeit ändern.

Wenn alle Teilnehmer ein Team zugewiesen haben und alle verbunden sind, kann der Host das Spiel starten. Alle Teilnehmer werden dann über den Start des Spiels benachrichtigt und sehen nun die Spielansicht.

Wenn sich ein Spieler nun einem Punkt nähert, welcher noch von keinem Team in Besitz genommen wurde kann dieser eingenommen werden.

Falls der Punkt dem Gegnerteam gehört, wird zunächst für 5 Sekunden nach Geräten in der Nähe gesucht. Anschließend wird überprüft, ob das eigene Team in der Überzahl ist. Ist dies der Fall, wird der Punkt eingenommen. Wenn aus beiden Teams gleich viele Spieler in der Nähe sind, ist der Punkt umkämpft und blinkt auf der Karte abwechselnd blau / rot.



Wenn ein Team alle Punkte eingenommen hat, oder die Zeit des Spiels abgelaufen ist endet das Spiel.
Die Spieler sehen dann, ob ihr Team gewonnen hat, wie lange sie gespielt haben, wie viele Meter zurückgelegt und wie viele Eroberungen durchgeführt wurden.

Anschließend können die Spieler zum Startbildschirm zurückkehren.



### Quellen

#### Audio

- Alle Audiodateien sind von der Seite https://mixkit.co/



#### TouchImageView

- Pfad: `ui/TouchImageView.java`
- https://stackoverflow.com/a/43647820

