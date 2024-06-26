# Simple Inventory

## Bemutatás

Az alkalmazás egy leltározó alkalmazás, amivel könnyedén nyilvántarthatja egy ember vagy akár egy kisvállalkozás a különböző fizikai eszközeit, kiegészítőit. A felhasználónak lehetősége van tételtípusokat létrehozni, és tétel bejegyzéseket létrehozni, ezenkívül vonalkódot generálni tételekhez és azt kamerával beolvasni a beazonosítás érdekében. Az adatok egy központi adatbázisban lesznek tárolva, ezáltal több felhasználó is eléri őket a telefonjára telepített alkalmazáson keresztül.

Az alkalmazás ötlete onnan jött, hogy több lakhelyem is van, a lakhelyeimen rengeteg cuccom van nekem/a családnak, és általában nehéz megtalálni dolgokat, vagy egyáltalán megállapítani, hogy van-e az adott típusú dologból valahol (pl. Displayport kábelt keresek, és nem tudom, van-e, illetve hol). Ez az alkalmazás megoldást adna ezekre a problémákra mind nekem, mind a családtagjaim számára, és később akár más használati esetekre is jó lesz.


## Főbb funkciók

Az alkalmazás adatai egy központi adatbázisban lesznek tárolva.

### Főképernyő
Indításkor az **alkalmazás főképernyője** látható. Amennyiben még nem jelentkezett be vagy nem regisztrált a felhasználó, egy **login screen** látható a főképernyő megjelenítése előtt, ahol ezt meg lehet tenni.

A főképernyőn a felhasználó **kedvenc tételei, tétel típusai** vannak listázva, amire kattintva megjelenik a **tétel/tételtípus részletei** nézet.

**Keresőmező** segítségével gyorsan lehet keresni az összes rögzített tétel és tétel típus között. Ezenkívül egyéb gyors műveleteket is lehet végezni: **új tétel rögzítését**, illetve **vonalkód beolvasást**.

A főképernyőről tovább lehet navigálni a **tétel típusok** vagy a **tételek** nézetre.


### Tétel típusok
Ebben a nézetben tétel típusokon lehet CRUD műveleteket végezni. A tétel típusok **hierarchiában** vannak, minden tétel típusnak lehet **egy szülője** (kategória), illetve **bármennyi gyereke**.

Listázva vannak azok a tétel típusok, amiknek nincs szülője (gyökér kategóriák), melyeket lehet **szerkeszteni**, **törölni**, **hozzáadni a kedvencekhez**. Törölni és kedvencekhez hozzáadni egyszerre több elemet is lehet, jelölőnégyzettel való kijelöléssel. Ezenkívül lehet **új tétel típust létrehozni**.

Tétel típus szerkesztésekor, vagy egy új létrehozásakor a **tétel típus részletek** nézet nyílik meg.


### Tétel típus részletek
Ebben a nézetben be lehet állítani a tétel típus **nevét**, **kategóriáját** (ez egy másik tétel típus példány).

Ebben a nézetben el lehet **nagiválni a szülő típus részleteire** (amennyiben van szülője), illetve listázva vannak a **gyerek tétel típusok, ezek részleteire is el lehet navigálni**. A listázás funkcionálisan megegyezik a tétel típusok nézetben található listázással.

Minden tételnek be lehet állítani a tétel típusát. A tétel típusok részletek nézetben listázva vannak azok a tételek, amiknek az éppen megjelenített tétel típus a típusa. Ezeknek **a tételeknek a részleteire is el lehet navigálni**. A listázás funkcionálisan megegyezik a tételek nézetben található listázással.

**Példa tétel típusok:**

Audio-video kábelek
- Név: A/V kábel
- Kategória: Kábel
- Altípusok: HDMI kábel, DP kábel, VGA kábel, 3xRCA M-M összekötő, ...
- Tételek: ismeretlen av kábel #1, ...

HDMI kábelek
- Név: HDMI kábel
- Kategória: A/V kábel
- Altípusok: -
- Tételek: HDMI kábel #1, HDMI kábel 3m, ...


### Tételek, tétel részletek
Ezek a nézetek nagyon hasonlóak a tétel típus/tétel típus részletek nézethez. A tételeken lehet ugyanazokat a műveleteket végezni, a tételek is ugyanolyan hierarchiában vannak. Azokat a tételeket, melyeknek vannak gyerek tételei, összetett tételnek nevezzük, ilyen lehet pl. egy doboz, egy polc, stb.

**A különbségek:**
- Tételek nézetben lehet 1 vagy akár több tételhez **vonalkódot generálni**, ami egy **pdf fájlt exportál** a kiválasztott tételek vonalkódjával, ennek célja, hogy a felhasználó kinyomtassa és ráragassza az adott tételekre.
- Tételek nézetben lehet **vonalkódot beolvasni** egy termék beazonosítása érdekében.
- Tétel részletek nézetben be lehet állítani a tételnek a **nevét**, **tétel típusát**, **szülő tételét**, és hogy az adott tétel egy összetett tétel-e.
- Tétel részletek nézetben meg lehet tekinteni a tétel **vonalkódját**.
- Tétel részletek nézetben **listázva vannak a gyerek tételek**, amennyiben az adott tétel egy összetett tétel, és **el lehet navigálni a szülő tétel részleteire** illetve **a gyerek tételek részleteire**.

**Példa tételek:**

Doboz
- Név: Doboz #1
- Típus: Doboz
- Összetett: igen
- Szülő tétel: -
- Altételek: HDMI kábel #1, HDMI kábel 3m, DP kábel 5m

DP kábel
- Név: DP kábel 5m
- Típus: DP kábel
- Összetett: nem
- Szülő tétel: Doboz #1

## Választott technológiák:

- UI
- Fragmentek
- RecyclerView
- Perzisztens adattárolás: Firebase


# Dokumentáció

## Entitások kezelése, tárolása
Az alkalmazás 2 fő entitást kezel: **Item** (tétel) és **Category** (tétel típus).
- Category: A tétel típusokat reprezentálja. Minden tétel típusnak van neve, lehet szülő típusa, és lehet a kedvencek közé mentve.
- Item: A tétel típusokhoz tartozó tétel példányokat reprezentálja. Minden tételnek van neve, típusa, lehet szülő tétele, és lehet a kedvencek közé mentve. Ezenkívül van egy inkrementálisan generált vonalkód azonosítója.

A clean code elveket követve, készült egy absztrakt Repository osztály, ami elabsztrahálja az entitások tárolását. Ennek 2 implementációja született:
- **MemoryRepository**: Az entitásokat egy memóriában tárolt listában tárolja, így az alkalmazás bezárásával az adatok elvesznek. Ez az implementáció csak tesztelési célokra készült.
- **FirebaseRepository**: Az entitásokat a Firebase Realtime Database-ben tárolja, így az adatok nem vesznek el az alkalmazás bezárásával. Ezt az implementációt használja az alkalmazás.

Az entitásokat az alkalmazás Firebase adatbázisba menti, illetve onnan tölti be. A megvalósítás során a Firebase Realtime Database-t használtam, és alapvetően minden megjelenített adat listener-eken keresztül van megjelenítve, így mindig szinkronban van az adatbázis tartalmával, és ezáltal a többi felhasználó által létrehozott vagy módosított entitások azonnal megjelennek mindenkinél akinél éppen nyitva van az alkalmazás.

## Első indítás
Első indításkor a felhasználónak be kell jelentkeznie. Ha még nincs felhasználója, regisztrálhat egyet, ezt követően viszont nem tudja közvetlenül használni az alkalmazást, először a regisztrált felhasználónak jogokat kell adni az adatbázis tartalmának eléréséhez a Firebase konzolon, adatvédelmi okok miatt.

Az alkalmazás SharedPreferences-be eltárolja a bejelentkezési adatokat, így a későbbi megnyitásoknál már nem kell bejelentkezni, az automatikusan megtörténik.

## Navigation bar
Az alkalmazásban a képernyő alján egy NavigationBar található, amivel a felhasználó a Főmenü, a Tétel típusok lista és a Tételek lista Fragment-ek között navigálhat.

## Főmenü
Az alkalmazás indulásakor a főmenü jelenik meg, ahol a felhasználó a kedvenc tétel típusait és tételeit látja. A kedvenc tétel típusokat és tételeket a felhasználó a tétel típusok és tetelek listázó nézetekben tudja hozzáadni a kedvencekhez, egy kedvencet eltávolítani pedig akár a főmenüből is lehet.

A megjelenítés egy RecyclerView-el történik, amihez készült egy közös, tételt és tétel típust is megjeleníteni képes 'ListItemRecyclerViewAdapter' osztály. Maga a főmenü Fragment egy absztrakt 'ListItemFragmentBase' osztályt implementál, így a Recycler View és egyebek konfigurálását nem kell külön ugyanúgy leimplementálni a tétel típusok lista és tételek lista Fragment-ekben.

A főmenüben a felhasználó tud globális keresés végezni, azaz olyan keresést, ami minden tétel és tétel típus között keres.

A főmenüben lehet vonalkódot beolvasni, vonalkód beolvasásához első használatkor az alkalmazás engedélyt fog kérni a kamera használatára. Ezután, egy már adatbázisban szereplő vonalkód beolvasásakor megnyílik a beolvasott tétel részletek nézete.

Vonalkód beolvasáshoz a 'zxing-android-embedded' nevű külső könyvtárat vettem segítségül.

A főmenüben lehet új tételt rögzíteni is.

## Tételek és tétel típusok lista
Ez a 2 Fragment nagyon hasonló felépítésű. A 'SelectableListItemFragmentBase' nevű absztrakt Fragment osztályból származnak le, ami pedig főmenünél már említett 'ListItemFragmentBase'-ből származik.
A 'SelectableListItemFragmentBase' annyival tud többet, hogy enged entitásokat kijelölni a RecyclerView-ben (hosszan nyomvatartással, és húzással), és kijelölt állapotban az ActionBar-on mutatja, mennyi entitás lett kijelölve, illetve a rajtuk végezhető műveleteket menu itemek formájában.

A navigation bar-ból elérve ezek a fragmentek a szülő nélküli entitásokat listázzák, így lehetővé téve a fentről lefele bejárást.

Mindkettő Fragment-ben lehetséges keresni, illetve entitásokat kijelölve lehetséges a kijelölteket törölni, hozzáadni kedvencekhez, eltávolítani kedvencek közül, és kijelölni az összes listázott entitást vagy a kijelölést megszüntetni.

A tételek listában lehetséges ezenkívül a kijelölt tételek vonalkódjait exportálni, ami készít egy PDF fájlt a vonalkódokkal és azt megnyitja a telefon alapértelmezett PDF kezelő alkalmazásában.

Egy entitásra kattintva megnyílik annak részletes nézete (tételre kattintva a tétel részletek, tétel típusra kattintva a tétel típus részletek).

## Tétel típusok részletek
Ebben a Fragment-ben egy ViewPager és egy TabLayout található. Ezzel meg lett valósítva 3 tab:
- **Details**: A tétel típus nevét és szülőjét mutatja, ezeket lehet szerkeszteni is. Ha van szülője, akkor át is lehet navigálni a szülő részletek nézetére.
- **Categories**: A tétel típusok, amiknek a jelenleg nézett tétel típus a szülője. Ez egy teljes értékű tétel típusok lista fragment.
- **Items**: A tételek, amiknek a jelenleg nézett tétel típus a kategóriája. Ez egy teljes értékű tételek lista fragment.

Az ActionBar az éppen nézett tétel típus nevét mutatja, és lehetőséget ad a tétel típus törlésére.

## Tétel részletek
Ebben a Fragment-ben ugyanúgy egy ViewPager és egy TabLayout található. Ezzel meg lett valósítva 2 tab:
- **Details**: A tétel nevét, típusát, szülőjét és vonalkódját mutatja, ezeket lehet szerkeszteni is. Ha van szülője, akkor át is lehet navigálni a szülő részletek nézetére. Ezenkívül a kategória részletek nézetére is át lehet navigálni.
- **Items**: A tételek, amiknek a jelenleg nézett tétel a szülője. Ez egy teljes értékű tételek lista fragment.

Az ActionBar az éppen nézett tétel nevét mutatja, és lehetőséget ad a tétel törlésére, illetve vonalkódjának PDF-be exportálására.

## Egyéb funkciók
Az alkalmazás optimalizálva van világos és sötét témára is, a felhasználó a telefonján beállított témától függően fogja látni az alkalmazást.

A Firebase adatbázisnak köszönhetően, az alkalmazás offline is teljes értékűen tud működni, akár bezárás és újbóli megnyitás esetén is. Amint újra van internetkapcsolata, minden addigi módosítást szinkronizál mindkét irányban a központi adatbázissal.
